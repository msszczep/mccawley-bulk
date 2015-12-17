(ns mccawley-bulk.c-command)

(use 'clojure.pprint)

;; The definition of c-command is based partly on the relationship of dominance:
;; Node N1 dominates node N2 if N1 is above N2 in the tree and one can trace a
;; path from N1 to N2 moving only downwards in the tree (never upwards); that is,
;; if N1 is a parent, grandparent, etc. of N2.

;; Based upon this definition of dominance, node A c-commands node B if and only if:

;; 1. A does not dominate B,
;; 2. B does not dominate A, and
;; 3. The first (i.e. lowest) branching node that dominates A also dominates B.[2]


(def p pprint)

(def t "(ROOT (S (NP (PRP I)) (VP (MD can) (RB not) (VP (VB stand) (NP (NNS rabbits)))) (. .)))")

(def t3 "(ROOT (S (NP (NNP Jesus)) (VP (VBD wept)) (. .)))")

(def t-cleaned (clojure.string/replace t #"\([A-Z\.]+ " "("))

(def t3-cleaned (clojure.string/replace t3 #"\([A-Z\.]+ " "("))

(tree-seq seq? identity (read-string t-cleaned))

; 1. Find node A.
; 2. Go up two steps.
; 3. See if node B is in the result.

;(read-string t)
;(zipmap (range) (tree-seq seq? identity (read-string t-cleaned)))

(def zz (clojure.zip/seq-zip (read-string t-cleaned)))

(def zz3 (clojure.zip/seq-zip (read-string t3-cleaned)))


(->> zz
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
;; This is where "not" is found
        clojure.zip/up
        clojure.zip/up
;; Go up two nodes
        first
        str
        (re-find #"\(rabbits\)")
        nil?
        false?
     )

(->> zz3
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/next
     clojure.zip/end?)


(defn c-command [zz node-a node-b]
  (loop [to-check zz]
    (cond
      (-> to-check clojure.zip/end?) nil
      (= node-a (str (first to-check)))
        (->> to-check
             clojure.zip/up
             clojure.zip/up
             first
             str
             (re-find (re-pattern (str "\\(" node-b "\\)")))
             nil?
             false?)
      :else (recur (->> to-check clojure.zip/next)))))


(c-command zz "not" "rabbits")
