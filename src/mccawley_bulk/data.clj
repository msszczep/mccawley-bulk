(ns mccawley-bulk.data
  (:require [mccawley-bulk.web :as w]))


; TODO: De-duplicate code

(defn get-entities [pre-processed]
  (for [r (w/get-parsed-sentences pre-processed)]
    (->> (get-in r [:body :parsed-text])
         read-string
         (tree-seq map? :children)
         (filter #(contains? % :entity))
         (filter #(not= "O" (:entity %)))
         (map #(vector (:word %) (:entity %))))))


(defn get-sentiments [pre-processed]
  (for [r (w/get-parsed-sentences pre-processed)]
    (->> (get-in r [:body :parsed-text])
         read-string
         (tree-seq map? :children)
         (filter #(contains? % :entity))
         (filter #(not= "0" (:sentiment %)))
         (map #(vector (:word %) (:sentiment %))))))
