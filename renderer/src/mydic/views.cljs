(ns mydic.views
  (:require [re-frame.core :as rf]
            [mydic.dict-api.core :as dict-api]
            [clojure.string :as string]))

(defn command-completion []
  "Add completion to search-and-command component")


(defn search-and-command []
  "User can search words and execute command"
  [:input.search-and-command
   {:type      "text"
    :value     @(rf/subscribe [:search-and-command/text])
    :on-change (fn [e]
                 (let [content @(rf/subscribe [:route/content])
                       prefix  (-> e .-target .-value)]
                   (case content
                     :word-search
                     (if (not= prefix "")
                       (dict-api/get-word-completion prefix)
                       (rf/dispatch [:word-search.list/on-mode-change :history]))
                     nil)
                   (rf/dispatch
                    [:search-and-command/on-change prefix])))
    :placeholder "search words or commands you want to execute"}])

(defn word-list []
  (let [word-list         @(rf/subscribe [:word-search/list])
        {:keys [mode
                selected
                result
                completion
                history]} word-list]
    [:div.word-list-container
     [:ul.word-list
      (let [word-links (case mode
                         :history    history
                         :result     result
                         :completion completion)]
        (for [{:keys [word id]} word-links]
          ^{:key id} [:li.btn.word-list-item
                      {:class    (if (= id selected)
                                   "word-selected")
                       :on-click #(rf/dispatch
                                   [:word-search/select-word-in-history
                                    word
                                    :definition
                                    id])}
                      word]))]]))

(defn kr-mean [word]
  [:div.kr-mean
   [:p {:style {:fontSize "29px"}} word]])

(defn en-mean [word]
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

(defn ex-sen [sen]
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
   [#'word-list]
   [#'word-definition]])

(defn content-view []
  "Select appropriate content according to the current state"
  [:div.content-view [#'word-search]])

(defn app []
  [:div.main
   [#'search-and-command]
   [#'content-view]])
