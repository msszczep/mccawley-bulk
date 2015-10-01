(ns mccawley-bulk.c-command)


(def t "(ROOT (S (NP (PRP I)) (VP (MD can) (RB not) (VP (VB stand) (NP (NNS rabbits)))) (. .)))")

(read-string t)

(tree-seq seq? identity t)

(def zz (clojure.zip/seq-zip (read-string t)))

(-> zz
    clojure.zip/down
    clojure.zip/right
        clojure.zip/left
;    clojure.zip/down
    )

(defn dominate? [a b sentence]
  ()
  )


(defn c-command? [a b sentence]
  )
