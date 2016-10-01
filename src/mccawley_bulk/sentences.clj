(ns mccawley-bulk.sentences
  (:require [mccawley-bulk.web :as w]))


(defn show-sentence-stats [raw]
  (let [pre-processed (clojure.string/split raw #"\t") ; HOLT et al.
        processed (w/get-parsed-single-sentence (last pre-processed))]
    (println (str (first pre-processed)
                  "\t" (last pre-processed)
                  "\t" (:num-words processed)
                  "\t" (:num-nodes processed)
                  "\t" (:num-tokens processed)
                  "\t" (:num-props processed)
                  "\t" (:max-depth processed)
                  "\t" (:s-expression processed)))))
