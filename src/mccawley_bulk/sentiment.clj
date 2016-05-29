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


(defn get-z-expression [parsed-sentence]
  (->> (get-in parsed-sentence [:body :s-expression])
       read-string
       z/seq-zip))


(defn get-all-noun-phrases [parsed-sentence]
  (->> (get-z-expression parsed-sentence)
       (s/get-all-zipper-nodes)
       (map first)
       (map str)
       (filter #(re-matches #"^\(NP .*\)$" %))))


(defn get-all-words-beneath-node [node-as-string]
  (->> (re-seq #" [\w\d]+\)" node-as-string)
       (map #(clojure.string/replace % #"[\)\s]" ""))
       (clojure.string/join " ")))


(defn connect-sentiments-to-entities [pre-processed-sentences]
  (for [parsed-sentence (w/get-parsed-sentences pre-processed-sentences)]
     (let [entity-sentiment-pairs
            (combo/cartesian-product (get-entities parsed-sentence)
                                     (get-sentiments parsed-sentence))
           z-expression (->> (get-in parsed-sentence [:body :s-expression])
                             read-string
                             z/seq-zip)]
      (reduce
        (fn [coll pair]
          (if (s/c-command z-expression (first pair) (last pair))
                (conj coll [(:word (first pair))
                            (Integer/parseInt (:sentiment (last pair)))])
                coll))
        []
        entity-sentiment-pairs))))


(defn connect-sentiments-to-nps [pre-processed-sentences]
  (for [parsed-sentence (w/get-parsed-sentences pre-processed-sentences)]
     (let [np-sentiment-pairs
            (combo/cartesian-product (get-sentiments parsed-sentence)
                                     (get-all-noun-phrases parsed-sentence))
           z-expression (->> (get-in parsed-sentence [:body :s-expression])
                             read-string
                             z/seq-zip)]
       np-sentiment-pairs
      #_(reduce
        (fn [coll pair]
          (if (s/c-command z-expression (first pair) (last pair))
                (conj coll [(get-all-words-beneath-node (first pair))
                            (Integer/parseInt (:sentiment (last pair)))])
                coll))
        []
        np-sentiment-pairs))))
