(ns mydic.dict-api.daum
  (:require [hickory.core :as h]
            [hickory.select :as s]
            [clojure.string :as string]
            [clojure.core.async :as async :refer [go >! <! close!]]
            [goog.string :as gstring]
            [goog.string.format]))

(def fetch js/fetch)

(def urls
  {:search/general     "http://alldic.daum.net/search.do?type=eng&q=%s"
   :search/word        "http://alldic.daum.net/search_more.do?q=%s&dic=eng&t=word&page=%d"
   :completion/general "http://suggest-bar.daum.net/suggest?mod=json&code=utf_in_out&enc=utf&id=language&cate=eng&q=%s"
   :detail/summary     "http://alldic.daum.net/word/view.do?wordid=%s"
   :detail/definition  "http://alldic.daum.net/word/view_supword.do?wordid=%s&supid=%s"})

(def caps
  {:completion true
   :search true
   :detailed-search true
   :definition true
   :usage true
   :related true})

;; to be replaced with something more efficient
(def simple-cache (atom {}))

(defn async-fetch
  [url]
  (let [ch     (async/chan)
        cached (get @simple-cache url)]
    (if cached
      (go (>! ch cached))
      (-> (fetch url)
          (.then #(.text %))
          (.then #(go (swap! simple-cache
                            (fn [cache]
                              (assoc cache url %)))
                      (>! ch %)
                      (close! ch)))))
    ch))

(defn api-capability []
  caps)

(defn completion->word-link
  [text]
  (let [s    (string/split text #"\|")
        word (second s)
        def  (nth s 2)]
    {:word       word
     :id         word
     :definition (nth s 2)}))

(defn parse-completion
  [text]
  (let [m (js->clj (js/JSON.parse text))
        q (get m "q")
        items (get m "items")]
    {:query q
     :list  (map completion->word-link items)}))

(defn word-completion
  [prefix]
  (let [ch (async/chan)
        url (gstring/format (:completion/general urls) prefix)]
    (go (->> (async-fetch url)
             <!
             parse-completion
             (>! ch))
        (close! ch))
    ch))

(defn word-search
  [])

(def word-selector-in-result
  (s/and (s/tag :div)
         (s/class :search_type)))
"word def selector: div.search_type span.txt_search"
(def word-def-selector-in-result
  (s/and (s/tag :span)
         (s/class :txt_search)))

"word link selector: div.search_type a.txt_searchword"
(def word-link-selector
  (s/class :txt_searchword))

(defn word-id-from-url
  [url]
  (last (re-find #"wordid=([\w\d]+)" url)))

(defn extract-content
  "It'll try to extract and leave only content from hickory HTML format"
  [element]
  (if (map? element)
    (map extract-content (:content element))
    element))

(defn extract-text
  "Simply pull out text from text using some transformation"
  [element]
  (->> element
       extract-content
       flatten
       string/join))

(defn word-links-from-search
  [html]
  (let [words (->> html h/parse h/as-hickory (s/select word-selector-in-result))]
    (map (fn [word]
           (let [title       (first (s/select word-link-selector word))
                 definitions (s/select word-def-selector-in-result word)]
             {:word (->> title
                         :content
                         (map extract-content)
                         flatten
                         string/join)
              :id   (->  title
                         :attrs
                         :href
                         word-id-from-url)
              :definition (->> definitions
                               (map (comp string/join flatten extract-content))
                               (interpose ", ")
                               string/join)}))
         words)))

(defn word-links-excluding-dupe
  [links]
  (loop [links links
         acc   []]
    (let [link (first links)]
      (if (seq links)
        (recur (filter #(not= (:id %)
                              (:id link))
                       links)
               (conj acc link))
        acc))))

(defn word-detailed-search
  "Types are `:definition` `:usage` `:idioms`"
  [query type]
  (let [ch   (async/chan)
        page 1
        url  (gstring/format (:search/word urls) query page)]
    (go (>! ch
            {:query  query
             :type   type
             :result (-> (<! (async-fetch url))
                         word-links-from-search
                         word-links-excluding-dupe)
             :page   page})
        (close! ch))
    ch))

(defn d2s [vec]
  (cond
    (string? vec) (list vec)
    (string? ((comp first :content) vec)) (list ((comp first :content) vec))
    :else
    (for [v (:content vec)]
      (cond
        (string? v) v
        (string? ((comp first :content) v))  ((comp first :content) v)
        :else ((comp first :content first :content) v)))))

(defn decoder [vecs]
  (filter #(not= "" %)
          (for [vec vecs] (apply str (map #(apply str %) (map d2s vec))))))

(defn definition-reducer
  [class defs elem]
  (let [text (extract-text elem)]
    (if (re-find #"\d+\." text)
      (conj defs {:text '()
                  :class class})
      (conj (vec (drop-last defs))
            (-> (last defs)
                (update :text
                        #(->> text
                              string/trim
                              (conj %)))
                (assoc :class class))))))

(defn word-def
  [htree]
  (let [classes (s/select (s/class :fold_ex) htree)]
    (flatten
     (for [class-wrap classes]
       (let [class-name (->> class-wrap
                             (s/select (s/class :tit_ex))
                             first
                             :content
                             first)
             defs (s/select (s/class :wrap_ex)
                            class-wrap)]
         (reduce (partial definition-reducer class-name)
                 []
                 defs))))))

(defn word-pronounce [site-htree]
  (let [symbol (map
                (comp first :content)
                (s/select (s/and (s/tag :span)
                                 (s/class :txt_pronounce))
                          site-htree))
        pron-url (map
                  (comp :data-url :attrs)
                  (s/select (s/descendant
                             (s/and (s/tag :span)
                                    (s/class :listen_voice))
                             (s/nth-child :even))
                            site-htree))]
    {
     :us {:text (first symbol)
          :sound-url (first pron-url)}
     :uk {:text (second symbol)
          :sound-url (second pron-url)}}))



(defn word [site-htree]
  ((comp first :content first) (s/select (s/and (s/tag :span)
                                                (s/class :txt_cleanword)) site-htree)))


(defn summary-def [site-htree]
  (vec (map (comp
             #(apply str %)
             #(map (fn [x] (if (string? x)
                             x
                             ((comp first :content) x))) %)
             :content)
            (s/select (s/and (s/tag :span)
                             (s/class :txt_mean)) site-htree))))

(defn word-related [site-htree]
  (let [word (map
              (comp first :content)
              (s/select (s/descendant
                         (s/id :SIMILAR_WORD)
                         (s/and (s/tag :a)
                               (s/class :txt_emph1)))
                        site-htree))
        url (map
             (comp :href :attrs)
             (s/select (s/descendant
                        (s/id :SIMILAR_WORD)
                        (s/and (s/tag :a)
                              (s/class :txt_emph1)))

                    site-htree))
        mean (map
              (comp #(apply str %)
                    #(map (fn [x] (if (string? x)
                                   x
                                   ((comp first :content) x))) %))
              (map :content (s/select (s/descendant
                                       (s/id :SIMILAR_WORD)
                                       (s/and (s/tag :span)
                                             (s/class :mean_info)))

                             site-htree)))]
   (for [i (range (count word))]
     {:word (nth word i)
       :id (nth url i)
       :definition (nth mean i)})))

(defn word-idiom [site-htree]
  (let [word (map
              (comp first :content)
              (s/select (s/descendant
                         (s/id :INCLUDE_WORD)
                         (s/and (s/tag :a)
                               (s/class :txt_emph1)))
                        site-htree))
        url (map
             (comp :href :attrs)
             (s/select (s/descendant
                        (s/id :INCLUDE_WORD)
                        (s/and (s/tag :a)
                              (s/class :txt_emph1)))

                    site-htree))
        mean (map
              (comp #(apply str %)
                    #(map (fn [x] (if (string? x)
                                   x
                                   ((comp first :content) x))) %))
              (map :content (s/select (s/descendant
                                       (s/id :INCLUDE_WORD)
                                       (s/and (s/tag :span)
                                             (s/class :mean_info)))

                             site-htree)))]
   (for [i (range (count word))]
     {:word (nth word i)
       :id (nth url i)
       :definition (nth mean i)})))

(defn word-usage [site-htree]
  (let [usage (map (comp #(apply str (map first (map d2s %)))
                         :content) (s/select (s/and (s/tag :span)
                                                    (s/class :txt_ex)) site-htree))]
    (for [i (range (/ (count usage) 2))]
      {:text        (nth usage (* 2 i))
       :translation (nth usage (+ 1 (* 2 i)) nil)})))

(defn extract-first-supid
  [html-src]
  (let [hick (-> html-src
                 h/parse
                 h/as-hickory)]
    (-> (s/select (s/attr :data-supid) hick)
        first
        :attrs
        :data-supid)))

(def word-summary
  (memoize (fn
             [wordid]
             (let [url-sum (gstring/format (urls :detail/summary) wordid)]
               (go (let [at-sum    (<! (async-fetch url-sum))
                         supid     (extract-first-supid at-sum)
                         url-def   (gstring/format (urls :detail/definition) wordid supid)
                         at-def    (<! (async-fetch url-def))
                         sum-htree (-> at-sum h/parse h/as-hickory)
                         def-htree (-> at-def h/parse h/as-hickory)]
                     {:word               (word sum-htree)
                      :definition-summary (seq (summary-def sum-htree))
                      :definition         (seq (word-def def-htree))
                      :pronounce          (word-pronounce sum-htree)
                      :usage              (seq (word-usage sum-htree))
                      :idiom              (seq (word-idiom sum-htree))
                      :related            (seq (word-related sum-htree))}))))))
