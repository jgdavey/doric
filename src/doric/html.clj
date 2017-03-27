(ns doric.html
  (:require [clojure.string :as str]
            [doric.tabular :refer [tabular-renderer
                                   unaligned-td
                                   unaligned-th]]))
(defn escape [^String s]
  (-> s
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")))

(defn assemble [rows]
  (concat ["<table>"
           (str "<tr>" (str/join (for [c (first rows)]
                               (str "<th>" c "</th>"))) "</tr>")]
          (for [tr (rest rows)]
            (str "<tr>" (str/join (for [c tr]
                                (str "<td>" c "</td>"))) "</tr>"))
          ["</table>"]))

(def renderer (tabular-renderer {:th unaligned-th
                                 :td unaligned-td
                                 :assemble assemble
                                 :escape escape}))
