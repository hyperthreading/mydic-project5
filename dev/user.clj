(ns user
  (:require [figwheel-sidecar.repl-api :as fg]))

(defn start []
  (fg/start-figwheel!))

(defn stop []
  (fg/stop-figwheel!))

(defn autobuild [& ids]
  (apply fg/start-autobuild ids))

(defn cljs []
  (fg/cljs-repl))
