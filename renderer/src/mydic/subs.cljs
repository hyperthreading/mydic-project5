(ns mydic.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :search-and-command/text
 (fn [db _]
   (get-in db [:search-and-command :text])))


(rf/reg-sub
 :word-search/word-history
 (fn [db _]
   (get-in db [:contents :word-search :word-history])))

(rf/reg-sub
 :word-search/timestamp
 (fn [db _]
   (get-in db [:contents :word-search :timestamp])))

(rf/reg-sub
 :word-search/word
 (fn [db _]
   (get-in db [:contents :word-search :word])))
