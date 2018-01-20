(ns mydic.views)

(defn command-completion []
  "Add completion to search-and-command component"
  )

(defn search-and-command []
  "User can search words and execute command"
  [:input.search-and-command {:type "text"
           :value "search words or commands you want to execute"}])

(defn word-history [{words :words}]
  [:ul.word-history (for [word words]
                      ^{:key word} [:li.word-history-word word])])

(defn word-definition [{word :word}]
  [:div.word-definition "I really need to find " [:strong word]])

(defn word-search []
  [:div.word-search
   [#'word-history {:words ["pig" "lion" "tiger" "anteater"]}]
   [#'word-definition {:word "computer"}]])

(defn content-view []
  "Select appropriate content according to the current state"
  [:div.content-view [#'word-search]])

(defn app []
  [:div.main
   [#'search-and-command]
   [#'content-view]])
