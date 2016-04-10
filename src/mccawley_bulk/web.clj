(ns mccawley-bulk.web
  (:require [clj-http.client :as client]
            [clojure.string :as s]))


(defn get-parsed-sentences [txt]
  (let [t (-> (s/replace txt #"\,|\;|\/|\%" {"," "%2C", ";" "%3B",
                                             "/" "%2F", "%" "%25"})
              (client/url-encode-illegal-characters))]
    (-> (client/get (str "http://localhost:3000/parse-multi/" t))
        :body
        (clojure.string/replace #"\":" "\"")
        read-string
        clojure.walk/keywordize-keys)))
