(ns mccawley-bulk.core
  (:require [mccawley-bulk.web :as w]
            [mccawley-bulk.basic :as b]
            [mccawley-bulk.sentiment :as s]))


(defn get-data-from-file [f]
  (line-seq (clojure.java.io/reader f)))


(defn get-sentiment-info [f]
  (doseq [datum-from-file (get-data-from-file f)]
    (println (s/connect-sentiments-to-entities datum-from-file))))


(defn get-basic-info [f]
  (doseq [datum-from-file (get-data-from-file f)]
    (b/show-basic-stats datum-from-file)))


(defn manager [file action]
  (condp = action
    "basic" (get-basic-info file)
    "sentiment" (get-sentiment-info file)
    (println "Nothing.  There is no third thing.")))


;; TODO: Fix IndexOutOfBoundsException

(defn -main [& args]
  (if (and (nth args 0) (nth args 1))
    (manager (nth args 0) (nth args 1))
    (println "Please specify a text file of comments, one comment per line, as the first argument
              and .")))
