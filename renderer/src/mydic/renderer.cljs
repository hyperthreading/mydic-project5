(ns mydic.renderer
  (:require [mydic.views :as views]
            [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn mount []
  (reagent/render
   [#'views/app]
   (.getElementById js/document "app")))

(defn ^:export run []
  (rf/dispatch-sync [:init])
  (mount))

(defn ^:export on-reload []
  (println "reloaded!"))

(mount)
