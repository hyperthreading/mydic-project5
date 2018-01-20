(ns mydic.events
  (:require [mydic.db :as mydb]
            [re-frame.core :as rf]))

(rf/reg-event-db
 :init
 (fn [_ _] mydb/default-db))

(defn find-word [add-to-history]
  (fn [db [_ word type timestamp]]
   (update-in db [:contents :word-search]
              (fn [word-search]
                (-> word-search
                    (assoc :word word)
                    (assoc :type type)
                    (assoc :timestamp timestamp)
                    (cond-> add-to-history
                      (update :word-history
                              #(conj % {:word word
                                        :timestamp timestamp}))))))))

(rf/reg-event-db
 :word-search/find
 (find-word true))

(rf/reg-event-db
 :word-search/find-word-in-history
 (find-word false))


(rf/reg-event-db
 :search-and-command/on-change
 (fn [db [_ text]]
   (assoc-in db [:search-and-command :text]
             text)))
