(ns mccawley-bulk.core
  (:require [clj-http.client :as client]))


(defn get-parsed-sentences [txt]
  (let [t (-> (clojure.string/replace txt #"\,|\;|\/|\%"
                                      {"," "%2C", ";" "%3B",
                                       "/" "%2F", "%" "%25"})
              (client/url-encode-illegal-characters))]
    (-> (client/get (str "http://localhost:3000/parse-multi/" t))
        :body
        (clojure.string/replace #"\"body\":" ":body ")
        (clojure.string/replace #"\"parsed-text\":" ":parsed-text ")
        (clojure.string/replace #"\"num-tokens\":" ":num-tokens ")
        (clojure.string/replace #"\"num-nodes\":" ":num-nodes ")
        (clojure.string/replace #"\"num-props\":" ":num-props ")
        (clojure.string/replace #"\"max-depth\":" ":max-depth ")
        (clojure.string/replace #"\"top-five\":" ":top-five ")
        read-string)))


(defn help [n p]
  (clojure.string/join ", " (map #(get-in % [:body n]) p)))


(defn parse-comments-file [f]
  (doseq [pre-processed (line-seq (clojure.java.io/reader f))]
    (let [processed (get-parsed-sentences pre-processed)]
      (println (str pre-processed
                    "\n\n# Sentences: " (count processed)
                    "\n# Nodes per sentence: " (help :num-nodes processed)
                    "\n# Tokens / sentence: " (help :num-tokens processed)
                    "\n# Propositions / sentence: " (help :num-props processed)
                    "\n# Tree depth / sentence: " (help :max-depth processed)
                    "\n# Top 5 nodes / sentence: " (help :top-five processed)
                    "\n===\n")))))


(defn -main [& args]
  (if (nth args 0)
    (parse-comments-file (nth args 0))
    (println "Please specify a text file of comments, one comment per line, as the first argument")))
