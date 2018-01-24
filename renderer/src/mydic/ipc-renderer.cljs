(ns mydic.ipc-renderer
  (:require [mydic.commands :as commands]
            [re-frame.core :as rf]))

(def electron (js/require "electron"))
(def ipcRenderer (.-ipcRenderer electron))

(doto ipcRenderer
  (.removeAllListeners #js ["find-word"])
  (.on "find-word"
       (fn [event word]
         (commands/search-word word))))
         
  
