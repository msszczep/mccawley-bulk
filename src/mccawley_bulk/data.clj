(ns mccawley-bulk.data
  (:require [mccawley-bulk.web :as w]))


(defn get-content [pre-processed-sentences key-to-use nil-value]
  (for [r (w/get-parsed-sentences pre-processed-sentences)]
    (->> (get-in r [:body :parsed-text])
         read-string
         (tree-seq map? :children)
         (filter #(contains? % :entity))
         (filter #(not= nil-value (key-to-use %)))
         (map #(vector (:word %) (key-to-use %))))))


(defn get-entities [pre-processed-sentences]
  (get-content pre-processed-sentences :entity "O"))


(defn get-sentiments [pre-processed-sentences]
  (get-content pre-processed-sentences :sentiment "0"))
