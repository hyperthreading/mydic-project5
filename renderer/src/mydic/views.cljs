(ns mydic.views
  (:require [re-frame.core :as rf]
            [hickory.core :as h]
            [hickory.select :as s]
            [clojure.string :as string]))


(defn command-completion []
  "Add completion to search-and-command component")


(defn search-and-command []
  "User can search words and execute command"
  [:input.search-and-command
   {:type "text"
    :value @(rf/subscribe [:search-and-command/text])
    :on-change #(rf/dispatch [:search-and-command/on-change
                              (-> % .-target .-value)])
    :placeholder "search words or commands you want to execute"}])

(defn word-history []
  (let [word-hist @(rf/subscribe [:word-search/word-history])
        word-sel @(rf/subscribe [:word-search/timestamp])]
    [:div.word-history-container
     [:ul.word-history-list
      (for [{:keys [word timestamp]} word-hist]
        ^{:key timestamp} [:li.btn.word-history-word
                           {:class (if (= timestamp word-sel)
                                     "word-selected")
                            :on-click #(rf/dispatch
                                        [:word-search/find-word-in-history
                                         word
                                         :definition
                                         timestamp])}
                           word])]]))



(defn kr-mean[word]
  [:div.kr-mean
   [:p {:style {:fontSize "29px"}} word]])

(defn en-mean[word]
  [:div.en-mean
   [:p {:style {:fontSize "25px"}} word]])

(def ex-sentences
  ["1st example sentances",
   "2nd example sentances",
   "3rd example sentances",
   "4th example sentances",
   "5th example sentances",
   "6th example sentances",
   "7th example sentances",
   "8th example sentances",
   "9th example sentances",
   "10th example sentances",
   "11th example sentances",
   "12th example sentances",])

(defn ex-sen[sen]
  [:div.ex-sen
   [:ul.ex-sen-list
    (for [sentence sen]
      [:li sentence])]])


(defn word-definition []
  (let [aword @(rf/subscribe [:word-search/word])]
    [:div.word-definition
     [:strong {:style {:fontSize "70px" :color "#0000cd"} } aword]
     [kr-mean "한글 뜻!"]
     [en-mean "english mean"]
     [ex-sen ex-sentences]]))

(defn word-search []
  [:div.word-search
   [#'word-history]
   [#'word-definition]])

(defn content-view []
  "Select appropriate content according to the current state"
  [:div.content-view [#'word-search]])

(defn app []
  [:div.main
   [#'search-and-command]
   [#'content-view]])
