(ns mydic.db)

(defn gen-word []
  (let [test-words ["pig" "lion" "anteater" "tiger" "computer"
                    "astounding" "reagent"]]
    (for [r (range 15)]
      {:word (nth test-words (rand-int (count test-words)))
       :timestamp r})))

(def word-history
  (gen-word))

(defonce default-db
  {:route    {:window  nil
              :content :word-search}
   :contents {:word-search {:word-history
                            word-history
                            :type :definition
                            :word "computer"}}
   :search-and-command {:text ""}})
