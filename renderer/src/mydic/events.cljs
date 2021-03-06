(ns mydic.events
  (:require [mydic.db :as mydb]
            [re-frame.core :as rf]))

(rf/reg-event-db
 :init
 (fn [_ _] mydb/default-db))

(def word-search-path
  [:contents :word-search])

(defn select-word [add-to-history]
  "Select word with specified result type and id"
  (fn [db [_ word type id]]
   (update-in db word-search-path
              (fn [word-search]
                (-> word-search
                    (assoc :word word)
                    (assoc :type type)
                    (assoc-in [:list :selected] id)
                    (cond-> add-to-history
                      (update-in [:list :history]
                                 #(conj (filter (fn [w]
                                                  (not= (:id w)
                                                        id))
                                                %)
                                        {:word word
                                         :id   id}))))))))

(rf/reg-event-db
 :word-search/select
 (select-word true))

(rf/reg-event-db
 :word-search/select-word-in-history
 (select-word false))

(rf/reg-event-db
 :word-search/on-receiving-definition
 (fn [db [_ detail]]
   (assoc-in db
             (conj word-search-path :detail)
             detail)))

(def word-list-path
  (conj word-search-path :list))

(rf/reg-event-db
 :word-search.list/on-mode-change
 (fn [db [_ mode]]
   (assoc-in db
             (conj word-list-path :mode)
             mode)))

(rf/reg-event-db
 :word-search/start-completion
 (fn [db _]
   (assoc-in db
             (conj word-list-path :mode)
             :completion)))

(rf/reg-event-db
 :word-search/on-completion
 (fn [db [_ comp-list]]
   (assoc-in db
             (conj word-list-path :completion)
             comp-list)))

(rf/reg-event-db
 :word-search/start-search
 (fn [db _]
   (assoc-in db
             (conj word-list-path :mode)
             :result)))

(rf/reg-event-db
 :word-search/on-search-result
 (fn [db [_ result]]
   (assoc-in db
             (conj word-list-path :result)
             result)))

(rf/reg-event-db
 :search-and-command/on-change
 (fn [db [_ text]]
   (assoc-in db [:search-and-command :text]
             text)))
          
