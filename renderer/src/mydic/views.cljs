(ns mydic.views
  (:require [re-frame.core :as rf]))

(defn command-completion []
  "Add completion to search-and-command component"
  )

(defn search-and-command []
  "User can search words and execute command"
  [:input.search-and-command
   {:type "text"
    :value @(rf/subscribe [:search-and-command/text])
    :on-change #(rf/dispatch [:search-and-command/on-change
                              (-> % .-target .-value)])
    :placeholder "search words or commands you want to execute"}])

(defn word-history []
  (let [word-hist @(rf/subscribe [:word-search/word-history])]
    [:div.word-history
     [:ul.word-history-list
      (for [{:keys [word timestamp]} word-hist]
        ^{:key timestamp} [:li.btn.word-history-word
                           {:on-click #(rf/dispatch
                                        [:word-search/find-word-in-history
                                         word
                                         :definition
                                         (.getTime (js/Date.))])}
                           word])]]))

(defn word-definition []
  (let [aword @(rf/subscribe [:word-search/word])]
    [:div.word-definition "I really need to find " [:strong aword]]))

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
