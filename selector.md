

word-definition(daum = http://alldic.daum.net/word/view_supword.do?wordid=ekw000165573&supid=eku001479454&suptype=)
```clojure
;뜻 갯수 따오기
(filter #(not= nil %)(map (comp first :content) (s/select (s/and (s/tag :span)
                 (s/class :num_item)) site-htree)))
;한글 뜻 따오기

(map :content (s/select (s/descendant
                        (s/and (s/tag :div)
                               (s/class :fold_ex))
                        (s/and (s/tag :p)
                               (s/class :desc_item)))
              site-htree))

(defn decoder [vecs]
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
  (filter #(not= "" %) (for [vec vecs]
    (apply str (map #(apply str %) (map d2s vec))))))

;품사 종류
(map (comp first :content) (s/select (s/and (s/tag :strong)
                 (s/class :tit_ex)) site-htree))

;예문 가져오기
(filter #(not= "" %)
  (map
    (comp #(apply str %)
          #(map (fn [x] (str x " ")) %)
          #(filter string? %)
          #(map (comp first :content) %)
           :content)
    (s/select (s/and (s/tag :p)
                     (s/class :desc_ex))
               site-htree)))


(defn word-def [site-htree]
  (let  [class (map (comp first :content) (s/select (s/and (s/tag :strong)
                   (s/class :tit_ex)) site-htree))
         text (decoder (map :content (s/select (s/descendant
                          (s/and (s/tag :div)
                                 (s/class :fold_ex))
                          (s/and (s/tag :p)
                                 (s/class :desc_item)))
                site-htree)))
        usage (filter #(not= "" %)
          (map
            (comp #(apply str %)
                  #(map (fn [x] (str x " ")) %)
                  #(filter string? %)
                  #(map (comp first :content) %)
                   :content)
                 (s/select (s/descendant
                            (s/and (s/tag :ul)
                                  (s/class :item_example))
                            s/first-child
                            (s/and (s/tag :p)
                                   (s/class :desc_ex)))
                            site-htree)))
        num (filter #(not= nil %)
              (map
                (comp first :content) (s/select (s/and (s/tag :span)
                                      (s/class :num_item))
                                      site-htree)))]

  (if (not= (count text) (count num))
    (def text (drop 1 text)))
  (def c (atom 0))
  (for [i (range (/ (count usage) 2))]
    {:class (if (= (nth usage @c) "1.")
                  (if (= @c 0) (nth class @c)
                                (nth class (swap! c inc)))
                  (nth class @c))
     :text (nth text i)
     :usage [{:text (nth usage (* 2 i))
              :translation (nth usage (+ (* 2 i) 1))}]
      })))



;발음 기호 (us, uk)
(map
  (comp first :content)
  (s/select (s/and (s/tag :span)
                  (s/class :txt_pronounce))
            site-htree))
;발음 (us, uk)
(map
  (comp :data-url :attrs)
  (s/select (s/descendant
                  (s/and (s/tag :span)
                         (s/class :listen_voice))
                  (s/nth-child :even))
            site-htree))

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
                :sound-url (second pron-url)}
          }      ))
;유의어 찾기 (영어)
(map
  (comp first :content)
  (s/select (s/descendant
             (s/id :SIMILAR_WORD)
             (s/and (s/tag :a)
                   (s/class :txt_emph1)))
            site-htree))    
;복합어 찾기 (영어)
(map
  (comp first :content)
  (s/select (s/descendant
             (s/id :INCLUDE_WORD)
             (s/and (s/tag :a)
                   (s/class :txt_emph1)))
            site-htree))
;유의어 찾기 (뜻)
(map
  (comp #(apply str %)
        #(map (fn [x] (if (string? x)
                     x
                     ((comp first :content) x))) %))
  (map :content (s/select (s/descendant
                           (s/id :SIMILAR_WORD)
                           (s/and (s/tag :span)
                                 (s/class :mean_info))
                           )
          site-htree)))
;복합어 찾기 (뜻)
(map
  (comp #(apply str %)
        #(map (fn [x] (if (string? x)
                     x
                     ((comp first :content) x))) %))
  (map :content (s/select (s/descendant
                           (s/id :INCLUDE_WORD)
                           (s/and (s/tag :span)
                                 (s/class :mean_info))
                           )
          site-htree)))
;유의어 찾기 (링크) -앞에 alldic.daum.net 붙여야함
(map
  (comp :href :attrs)
  (s/select (s/descendant
             (s/id :SIMILAR_WORD)
             (s/and (s/tag :a)
                   (s/class :txt_emph1))
             )
         site-htree))
;복합어 찾기 (링크)
(map
 (comp :href :attrs)
 (s/select (s/descendant
            (s/id :INCLUDE_WORD)
            (s/and (s/tag :a)
                  (s/class :txt_emph1))
            )
        site-htree))

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
                           (s/class :txt_emph1))
                     )
                 site-htree))
        mean (map
          (comp #(apply str %)
                #(map (fn [x] (if (string? x)
                             x
                             ((comp first :content) x))) %))
          (map :content (s/select (s/descendant
                                   (s/id :SIMILAR_WORD)
                                   (s/and (s/tag :span)
                                         (s/class :mean_info))
                                   )
                  site-htree))) ]
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
                           (s/class :txt_emph1))
                     )
                 site-htree))
        mean (map
          (comp #(apply str %)
                #(map (fn [x] (if (string? x)
                             x
                             ((comp first :content) x))) %))
          (map :content (s/select (s/descendant
                                   (s/id :INCLUDE_WORD)
                                   (s/and (s/tag :span)
                                         (s/class :mean_info))
                                   )
                  site-htree))) ]
  (for [i (range (count word))]
    {:word (nth word i)
      :id (nth url i)
      :definition (nth mean i)})))

(map (comp  
         #(apply str (map first (map d2s %)))
         :content)
    (s/select (s/and (s/tag :span)
                (s/class :txt_ex)) site-htree))
(defn word-usage [site-htree]
  (let [usage (map (comp  
           #(apply str (map first (map d2s %)))
           :content)
      (s/select (s/and (s/tag :span)
                  (s/class :txt_ex)) site-htree))]
  (for [i (range (/ (count usage) 2))]
      {:text (nth usage (* 2 i))
        :translation (nth usage (+ 1 (* 2 i)))})))
```
