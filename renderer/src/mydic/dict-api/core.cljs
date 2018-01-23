(ns mydic.dict-api.core
  (:require [mydic.dict-api.daum :as daum]
            [re-frame.core :as rf]
            [clojure.core.async :as async :refer [go <!]]))

;; 우선 다음 api로만 구현해놓고 추후에 네이버 등의 사전을 추가하기로 하자

(defn api-capability
  []
  (daum/api-capability))

(defn word-completion
  [prefix]
  (daum/word-completion prefix))

(defn word-detailed-search
  [query]
  (daum/word-detailed-search query :definition))

;; 가끔 먼저 보낸 요청이 나중에 오는 경우를 걸러줘야 함
(def last-word-comp (atom ""))

(defn get-word-completion
  [prefix]
  (reset! last-word-comp prefix)
  (rf/dispatch [:word-search/start-completion])
  (go (let [result (<! (word-completion prefix))]
        (when (= @last-word-comp (:query result))
          (rf/dispatch [:word-search/on-completion (:list result)])))))

(defn search-word
  [query]
  (rf/dispatch [:word-search/start-search])
  (go (let [result            (<! (word-detailed-search query))
            words             (:result result)
            {:keys [word id]} (first words)]
        (rf/dispatch [:word-search/on-search-result words])
        (rf/dispatch [:word-search/select word :definition id]))))

(defn get-word-summary
  [{:keys [word definition id]}]
  (rf/dispatch [:word-search/select word :definition id]))
