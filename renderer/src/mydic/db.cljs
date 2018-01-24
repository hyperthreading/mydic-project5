(ns mydic.db)

(defn gen-word []
  (let [test-words ["pig" "lion" "anteater" "tiger" "computer"
                    "astounding" "reagent"]]
    (for [r (range 15)]
      {:word (nth test-words (rand-int (count test-words)))
       :id r})))

(def word-history
  (gen-word))

"
word-search types are `:definition` `:usage` `:idiom`
list modes are `:history` `:completion` `:result`

"
(def word-def
  {})

(def default-db
  {:route              {:window  nil
                        :content :word-search}
   :contents           {:word-search {:type   :definition
                                      :list   {:mode       :history
                                               ;; mode가 바뀔 때 마다 초기화 될 것임
                                               :selected   nil
                                               :result     []
                                               :completion []
                                               :history    word-history}
                                      :word   "computer"
                                      :detail nil}}
   :search-and-command {:text ""}})
