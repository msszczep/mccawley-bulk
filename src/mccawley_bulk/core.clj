(ns mccawley-bulk.core
  (:require [clj-http.client :as client]
            [mccawley-bulk.stats :as s]))


(defn get-parsed-sentences [txt]
  (let [t (-> (clojure.string/replace txt #"\,|\;|\/|\%"
                                      {"," "%2C", ";" "%3B",
                                       "/" "%2F", "%" "%25"})
              (client/url-encode-illegal-characters))]
    (-> (client/get (str "http://localhost:3000/parse-multi/" t))
        :body
        (clojure.string/replace #"-text\":" "-text\"")
        read-string
        vals
        first)))


(defn parse-comments-file [f]
  (doseq [pre-processed-sentences (line-seq (clojure.java.io/reader f))]
    (let [processed-sentences (get-parsed-sentences pre-processed-sentences)
          p-stats (s/get-stats processed-sentences)]
      (println (str pre-processed-sentences "\n\n" processed-sentences
                    "\n\n# Sentences: " (p-stats :num-of-sentences)
                    "\n# Nodes per sentence: " (p-stats :num-of-nodes)
                    "\n# Parsed words / sentence: " (p-stats
                                                       :num-of-parsed-words)
                    "\n# Propositions / sentence: " (p-stats
                                                       :num-of-propositions)
                    "\n# Tree depth / sentence: " (p-stats :max-depth)
                    "\n# Top five nodes / sentence: " (p-stats :top-five-nodes)
                    "\n===\n")))))


(defn -main [& args]
  (if (nth args 0)
    (parse-comments-file (nth args 0))
    (println "Please specify a text file of comments, one comment per line, as the first argument")))
