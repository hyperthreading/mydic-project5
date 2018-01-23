(ns mydic.dict-api.daum
  (:require [hickory.core :as h]
            [hickory.select :as s]
            [clojure.string :as string]
            [clojure.core.async :as async]
            [goog.string :as gstring]
            [goog.string.format]))

(def fetch js/fetch)

(def urls
  {:search/general     "http://alldic.daum.net/search.do?type=eng&q=%s"
   :search/word        "http://alldic.daum.net/search_more.do?q=%s&dic=eng&t=word&page=%d"
   :completion/general "http://suggest-bar.daum.net/suggest?mod=json&code=utf_in_out&enc=utf&id=language&cate=eng&q=%s"
   :detail/definition  "http://alldic.daum.net/word/view.do?wordid=%s"})

(def caps
  {:completion true
   :search true
   :detailed-search true
   :definition true
   :usage true
   :related true})

(defn api-capability
  caps)

(defn completion->word-link
  [text]
  (let [s (string/split text #"\|")]
    {:word (second s)
     :definition (nth s 2)}))

(defn parse-completion
  [text]
  (let [m (js->clj (js/JSON.parse text))
        q (get m "q")
        items (get m "items")]
    {:query q
     :list  (map completion->word-link items)}))

(defn word-completion
  [prefix]
  (let [ch (async/chan)]
    (-> (fetch (gstring/format (:completion/general urls) prefix))
        (.then #(.text %))
        (.then #(async/go (async/>! ch (parse-completion %))
                          (async/close! ch))))
    ch))

(defn word-summary
  [])

(defn word-usage
  [])

(defn word-related
  [])

