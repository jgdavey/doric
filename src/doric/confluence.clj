(ns doric.confluence
  (:require [clojure.string :as str]
            [doric.tabular :refer [tabular-renderer
                                   unaligned-td
                                   unaligned-th]]))

(defn escape [s]
  (.replaceAll (str s) "\\|" "&amp;#124;"))

(defn assemble [rows]
  (cons (str "||" (str/join "||" (first rows)) "||")
          (for [tr (rest rows)]
            (str "|" (str/join "|" tr) "|"))))

(def renderer (tabular-renderer {:th (comp escape unaligned-th)
                                 :td (comp escape unaligned-td)
                                 :assemble assemble
                                 :escape escape}))
