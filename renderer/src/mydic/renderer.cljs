(ns mydic.renderer
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(rf/reg-event-db
 :init
 (fn [_ _]
   {:name "Jaeyoung"
    :time (js/Date.)
    :time-color "#f88"}))

(rf/reg-event-db
 :name
 (fn [db [_ new-name]]
   (assoc db :name new-name)))

(rf/reg-sub
 :name
 (fn [db _]
   (:name db)))

(defn name-caller [name]
  [:h2 {:style {:display "block"}} name "!"])

(defn hello []
  [:div "Hello, " [#'name-caller @(rf/subscribe [:name])]])

(defn app []
  [#'hello])

(defn ^:export run []
  (let [elem (.getElementById js/document "app")]
    (rf/dispatch-sync [:init])
    (reagent/render
     [#'app]
     elem)))

(defn ^:export on-reload []
  (println "reloaded!"))

