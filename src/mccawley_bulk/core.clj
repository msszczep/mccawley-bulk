(ns mccawley-bulk.core
  (:require [clj-http.client :as client]
            [clojure.string :as s]))


(defn get-parsed-sentences [txt]
  (let [t (-> (s/replace txt #"\,|\;|\/|\%" {"," "%2C", ";" "%3B",
                                             "/" "%2F", "%" "%25"})
              (client/url-encode-illegal-characters))]
    (-> (client/get (str "http://localhost:3000/parse-multi/" t))
        :body
        (s/replace #"\"body\":" ":body ")
        (s/replace #"\"parsed-text\":" ":parsed-text ")
        (s/replace #"\"num-tokens\":" ":num-tokens ")
        (s/replace #"\"num-nodes\":" ":num-nodes ")
        (s/replace #"\"num-props\":" ":num-props ")
        (s/replace #"\"max-depth\":" ":max-depth ")
        (s/replace #"\"top-five\":" ":top-five ")
        read-string)))


(defn j [n p]
  (s/join ", " (map #(get-in % [:body n]) p)))


(defn parse-comments-file [f]
  (doseq [pre-processed (line-seq (clojure.java.io/reader f))]
    (let [processed (get-parsed-sentences pre-processed)]
      (println (str pre-processed
                    "\n\n# Sentences: " (count processed)
                    "\n# Words: " (count (s/split pre-processed #"\s+"))
                    "\n# Nodes per sentence: " (j :num-nodes processed)
                    "\n# Tokens / sentence: " (j :num-tokens processed)
                    "\n# Propositions / sentence: " (j :num-props processed)
                    "\n# Tree depth / sentence: " (j :max-depth processed)
                    "\n# Top 5 nodes / sentence: " (j :top-five processed)
                    "\n===\n")))))


(defn -main [& args]
  (if (nth args 0)
    (parse-comments-file (nth args 0))
    (println "Please specify a text file of comments, one comment per line, as the first argument")))
