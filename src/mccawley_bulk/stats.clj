(ns mccawley-bulk.stats)


(defn get-num-of-sentences [txt]
  (count txt))


(defn get-num-of-nodes [processed-sentence]
  (count (re-seq #" \(" processed-sentence)))


(defn get-num-of-parsed-words [processed-sentence]
  (count (re-seq #"\(.+? .+?\)" processed-sentence)))


(defn get-num-of-propositions [processed-sentence]
  (->> (re-seq #"\((.+?) " processed-sentence)
       (map second)
       (filter #{"JJ" "JJR" "JJS" "VB" "VBD" "VBG"
                 "VBN" "VBP" "VBZ" "TO" "RB" "RBR" "RBS"})
       count))


(defn get-max-depth [processed-sentence]
  (->> (loop [input-seq (->> (re-seq #"[\(\)]" processed-sentence)
                             (map #(clojure.string/replace %
                                  #"\(|\)"
                                  {"(" "1" ")" "-1"}))
                             (map #(Integer. %)))
              output-seq [0]]
         (if (empty? input-seq)
           output-seq
           (recur (rest input-seq)
                  (conj output-seq
                        (+ (last output-seq)
                           (first input-seq))))))
       (apply max)
       dec
       dec))


(defn get-top-five [processed-sentence]
  (->> (re-seq #"\((.+?) " processed-sentence)
       (map second)
       rest
       frequencies
       (sort-by val)
       reverse
       (take 5)
       (map #(clojure.string/join ": " %))
       (clojure.string/join ", ")))


(defn get-stats [processed-sentences]
  (let [p processed-sentences]
    (letfn [(j [s] (clojure.string/join ", " s))]
      (hash-map
       :num-of-sentences (get-num-of-sentences p)
       :num-of-nodes (j (map get-num-of-nodes p))
       :num-of-parsed-words (j (map get-num-of-parsed-words p))
       :num-of-propositions (j (map get-num-of-propositions p))
       :max-depth (j (map get-max-depth p))
       :top-five-nodes (clojure.string/join " -- " (map get-top-five p))))))
