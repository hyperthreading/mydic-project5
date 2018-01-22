(ns mydic.ipc-renderer
  (:require [re-frame.core :as rf]))

(def electron (js/require "electron"))
(def ipcRenderer (.-ipcRenderer electron))

(doto ipcRenderer
  (.removeAllListeners #js ["find-word"])
  (.on "find-word"
       (fn [event, word]
         (println word)
         (rf/dispatch [:word-search/find
                       word
                       :definition
                       (.getTime (js/Date.))]))))
  
