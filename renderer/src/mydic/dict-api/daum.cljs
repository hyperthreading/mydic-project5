(ns mydic.dict-api.daum
  (:require [hickory.core :as h]
            [hickory.select :as s]
            [clojure.string :as string]))

(def fetch js/fetch)

(def url "http://alldic.daum.net/word/view.do?wordid=%s")

(defn word-completion
  [])

(defn word-summary
  [])

(defn word-usage
  [])

(defn word-related
  [])

