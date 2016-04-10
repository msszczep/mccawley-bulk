(ns mccawley-bulk.basic
  (:require [clojure.string :as s]
            [mccawley-bulk.web :as w]))


(defn j [n p]
  (s/join ", " (map #(get-in % [:body n]) p)))


(defn show-basic-stats [pre-processed]
  (let [processed (w/get-parsed-sentences pre-processed)]
    (println (str pre-processed
                  "\n\n# Sentences: " (count processed)
                  "\n# Words: " (count (s/split pre-processed #"\s+"))
                  "\n# Nodes per sentence: " (j :num-nodes processed)
                  "\n# Tokens / sentence: " (j :num-tokens processed)
                  "\n# Propositions / sentence: " (j :num-props processed)
                  "\n# Tree depth / sentence: " (j :max-depth processed)
                  "\n# Top 5 nodes / sentence: " (j :top-five processed)
                  "\n=============\n"))))
