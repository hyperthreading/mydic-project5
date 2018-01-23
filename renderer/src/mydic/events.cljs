(ns mydic.events
  (:require [mydic.db :as mydb]
            [re-frame.core :as rf]))

(rf/reg-event-db
 :init
 (fn [_ _] mydb/default-db))

(defn select-word [add-to-history]
  "Select word with specified result type and id"
  (fn [db [_ word type id]]
   (update-in db [:contents :word-search]
              (fn [word-search]
                (-> word-search
                    (assoc :word word)
                    (assoc :type type)
                    (assoc-in [:list :selected] id)
                    (cond-> add-to-history
                      (update-in [:list :history]
                              #(conj % {:word word
                                        :id   id}))))))))

(rf/reg-event-db
 :word-search/select
 (select-word true))

(rf/reg-event-db
 :word-search/select-word-in-history
 (select-word false))

(rf/reg-event-db
 :word-search.list/on-mode-change
 (fn [db [_ mode]]
   (assoc-in db [:contents :word-search :list :mode]
             mode)))

(rf/reg-event-db
 :word-search/on-completion
 (fn [db [_ comp-list]]
   (update-in db
              [:contents :word-search :list]
              (fn [list-db]
                (-> list-db
                    (assoc-in [:completion] comp-list)
                    (assoc-in [:mode] :completion))))))

(rf/reg-event-db
 :search-and-command/on-change
 (fn [db [_ text]]
   (assoc-in db [:search-and-command :text]
             text)))
          
