(ns mydic.views
  (:require [mydic.commands :as commands]
            [re-frame.core :as rf]
            [clojure.string :as string]))

(defn mode-indicator
  []
  [:div.mode-indicator
   [:span (case @(rf/subscribe [:word-search.list/mode])
            :history    "history"
            :completion "completion"
            :result     "result")]])

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
                       (commands/get-word-completion prefix)
                       (rf/dispatch [:word-search.list/on-mode-change :history]))
                     nil)
                   (rf/dispatch
                    [:search-and-command/on-change prefix])))
    :on-key-down (fn [e]
                   (->
                    (case (.-key e)
                      "ArrowUp"
                      nil
                      "ArrowDown"
                      nil
                      "Enter"
                      (commands/search-word-and-select (-> e .-target .-value))
                      true)
                    (when-not
                        (.preventDefault e))))
    :placeholder "search words or commands you want to execute"}])

(defn handle-word-click
  [{:keys [word id definition] :as word-link} mode]
  (case mode
    :history
    (do
      (rf/dispatch
       [:word-search/select-word-in-history word :definition id])
      (commands/get-word-summary word-link))
    :completion
    (commands/search-word-and-select word)
    :result
    (do
      (rf/dispatch [:word-search/select word :definition] id)
      (commands/get-word-summary word-link))))
  

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
        (for [{:keys [word
                      id
                      definition] :as word-link} word-links]
          ^{:key id} [:li.btn.word-list-item
                      {:class    (if (= id selected)
                                   "word-selected")
                       :on-click (when-not (= id selected)
                                   #(handle-word-click word-link mode))}
                      word
                      (if definition
                        (list ^{:key 0} [:br]
                              ^{:key definition}
                              [:span.word-small-definition
                               definition]))]))]]))

(def sound-element (atom nil))

(defn pronounce-handler
  [audio-url]
  (fn []
    (when @sound-element
      (.pause @sound-element))
    (reset! sound-element (js/Audio. audio-url))
    (.play @sound-element)))

(defn word-pronounce
  [{:keys [text sound-url]}]
  [:span text
   [:i.material-icons.pronounce-play
    {:on-click (pronounce-handler sound-url)}
    "play_arrows"]])

(defn word-display
  [word pronounce]
  [:div
   [:strong.big-word word]
   [:div {:style {:display "inline-block"}}
    [:span.pronounce "US " [#'word-pronounce (:us pronounce)]]
    [:span.pronounce "UK " [#'word-pronounce (:uk pronounce)]]]])

(defn kr-mean [means]
  [:div.kr-mean
   [:ul.mean-list
    (map (fn [index mean]
           [:li index ". " mean])
         (range)
         means)]])

(defn en-mean [word]
  [:div.en-mean
   [:p {:style {:fontSize "25px"}} word]])

(defn detailed-definitions
  [definitions]
  (let [def-groups (group-by :class definitions)]
    [:div
     (for [[class defs] def-groups]
       [:div
        [:strong class]
        (for [{text :text} defs]
          [:p text])])]))

(defn ex-sen [sen]
  [:div.ex-sen
   [:strong "예문"]
   [:ul.ex-sen-list
    (for [{:keys [text translation]} sen]
      [:li
       [:p.usage-text text]
       [:p.usage-trans translation]])]])


(defn word-definition []
  (let [aword  @(rf/subscribe [:word-search/word])
        detail @(rf/subscribe [:word-search/detail])]
    [:div.word-definition
     [#'word-display aword (:pronounce detail)]
     [#'kr-mean (:definition-summary detail)]
     [#'en-mean ""]
     [#'detailed-definitions (:definition detail)]
     [#'ex-sen (:usage detail)]]))

(defn word-search []
  [:div.word-search
   [#'word-list]
   [#'word-definition]])

(defn content-view []
  "Select appropriate content according to the current state"
  [:div.content-view [#'word-search]])

(defn app []
  [:div.main
   [:div.commandbox
    [#'search-and-command]
    [#'mode-indicator]]
   [#'content-view]])
