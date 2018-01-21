(ns mydic.styles.core
  (:require [garden.core :as g :refer [css]]
            [garden.units :as u :refer [percent px]]
            [garden.selectors :as s]))
            

(def dark-grey "#212529")
(def light-blue "#e2e6ea")
(def sky-blue "#81beff")
(def dark-blue "#4198f6")

(def word-history-width (percent 20))

(def main-css
  (list
   [:body {:overflow "hidden"}]

   [:.btn
    {:transition ["color .15s ease-in-out"
                  "background-color .15s ease-in-out"]}]

   [:input.search-and-command {:width (percent 100)}]

   [:.word-history-container
    {:display "inline-block"
     :width word-history-width
     :min-width (px 75)
     :position "absolute"
     :top (px 16)
     :right 0
     ;; 0으로 두면 맨 밑 가려져서 안보임
     :bottom (px 16)
     :left 0}]

   [:.word-history-list
    {:width (percent 100)
     :height (percent 100)
     :padding 0
     :list-style "none"
     :overflow-y "scroll"}]

   ; Use string literal to avoid token error
   [".word-history-list::-webkit-scrollbar"
    {:display "none"}]
   
   [:.word-history-word
    {:padding "10px"
     :border-bottom "1px solid #ededed"
     :box-shadow "inset 0 -2px 1px rgba(0,0,0,0.03)"}

    ;.word-history-word:hover
    [:&:hover {:color dark-grey
               :background-color light-blue}]

    ;:.word-history-word.word-selected:hover
    [:&.word-selected:hover {:background-color dark-blue}]]

   [:.word-selected
    {:background-color sky-blue}]

   [:.word-definition
    {:display "inline-block"
     :width "77%"
     :height "100%"
     :margin-left word-history-width
     :padding "8px"
     :vertical-align "top"}]))

