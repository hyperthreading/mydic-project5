(ns mydic.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :route/content
 (fn [db _]
   (get-in db [:route :content])))

(rf/reg-sub
 :search-and-command/text
 (fn [db _]
   (get-in db [:search-and-command :text])))

(rf/reg-sub
 :word-search/list
 (fn [db _]
   (get-in db [:contents :word-search :list])))

(rf/reg-sub
 :word-search.list/mode
 (fn [db _]
   (get-in db [:contents :word-search :list :mode])))
 

(rf/reg-sub
 :word-search/word
 (fn [db _]
   (get-in db [:contents :word-search :word])))

(rf/reg-sub
 :word-search/detail
 (fn [db _]
   (get-in db [:contents :word-search :detail])))


