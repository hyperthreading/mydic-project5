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

(defn word-summary
  [wordid]
  (daum/word-summary wordid))
