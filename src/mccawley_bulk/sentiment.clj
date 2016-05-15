(ns mccawley-bulk.sentiment
  (:require [mccawley-bulk.web :as w]
            [mccawley-bulk.squirrel :as s]
            [clojure.math.combinatorics :as combo]
            [clojure.zip :as z]))


(defn get-content [parsed-sentence key-to-use nil-value]
  (->> (get-in parsed-sentence [:body :parsed-text])
       read-string
       (tree-seq map? :children)
       (filter #(contains? % :entity))
       (filter #(not= nil-value (key-to-use %)))))


(defn get-entities [parsed-sentence]
  (get-content parsed-sentence :entity "O"))


(defn get-sentiments [parsed-sentence]
  (get-content parsed-sentence :sentiment "0"))


(defn connect-sentiments-to-entities [pre-processed-sentences]
  (for [parsed-sentence (w/get-parsed-sentences pre-processed-sentences)]
     (let [entity-sentiment-pairs
            (combo/cartesian-product (get-entities parsed-sentence)
                                     (get-sentiments parsed-sentence))
           s-expression (->> (get-in parsed-sentence [:body :s-expression])
                             read-string)
           z-expression (z/seq-zip s-expression)]
      (reduce
        (fn [coll pair]
          (if (s/c-command z-expression (first pair) (last pair))
                (conj coll [(:word (first pair))
                            (Integer/parseInt (:sentiment (last pair)))])
                coll))
        []
        entity-sentiment-pairs))))
