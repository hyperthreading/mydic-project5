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

(rf/reg-event-db
 :time
 (fn [db [_ new-time]]
   (assoc db :time new-time)))

(rf/reg-event-db
 :change-time-color
 (fn [db [_ new-color]]
   (assoc db :time-color new-color)))

(rf/reg-sub
 :name
 (fn [db _]
   (:name db)))

(rf/reg-sub
 :time
 (fn [db _]
   (:time db)))

(rf/reg-sub
 :time-color
 (fn [db _]
   (:time-color db)))

(defn name-caller [name]
  [:h2 {:style {:display "inline"}} name "!"])

(defn hello []
  [:div "Hello, " [#'name-caller @(rf/subscribe [:name])]])

(defn timer []
  [:h1 {:style {:color @(rf/subscribe [:time-color])}}
   "This is now " (.toTimeString @(rf/subscribe [:time]))])

(defn timer-color-picker []
  [:input {:type "text"
           :value @(rf/subscribe [:time-color])
           :on-change #(rf/dispatch [:change-time-color (-> % .-target .-value)])}])

(defn app []
  [:div
   [#'hello]
   [#'timer]
   [#'timer-color-picker]])

(defn render []
  (reagent/render
   [#'app]
   (.getElementById js/document "app")))

(defn ^:export run []
  (rf/dispatch-sync [:init])
  (render))

(defn ^:export on-reload []
  (println "reloaded!"))

(defonce timer-update (js/setInterval #(rf/dispatch [:time (js/Date.)]) 1000))
