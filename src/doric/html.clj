(ns doric.html
  (:require [clojure.string :as str]
            [doric.protocols :refer [tabular-renderer]]
            [doric.formatting :refer [unaligned-th unaligned-td]]))

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
                                 :assemble assemble}))
