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
   :detail/definition  "http://alldic.daum.net/word/view.do?wordid=%s"})

(def caps
  {:completion true
   :search true
   :detailed-search true
   :definition true
   :usage true
   :related true})

(defn async-fetch
  [url]
  (let [ch (async/chan)]
    (-> (fetch url)
        (.then #(.text %))
        (.then #(go (>! ch %)
                    (close! ch))))
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

(defn word-summary
  [])

(defn word-usage
  [])

(defn word-related
  [])

