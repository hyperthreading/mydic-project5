(ns mydic.renderer)

(defn ^:export on-reload []
  (println "reloaded!"))

(let [elem (.getElementById js/document "app")]
  (set! (.-innerHTML elem) "Hello, Cider!"))

