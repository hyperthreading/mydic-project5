(ns mydic.renderer
  (:require [mydic.views :as views]
            [mydic.db]
            [mydic.events]
            [mydic.subs]
            [mydic.ipc-renderer]
            [mydic.dict-api.core]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [re-frame.db :as rfdb]))

(defn init-db []
  (rf/dispatch-sync [:init]))

(defn mount []
  (reagent/render
   [#'views/app]
   (.getElementById js/document "app")))

(defn ^:export run []
  (init-db)
  (mount))

(defn ^:export on-reload []
  (mount)
  (println "reloaded!"))

;; to refresh every component in reagent
(defonce initialize-db
  (run))

(enable-console-print!)

(def log (.log js/console))
