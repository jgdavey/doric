(ns doric.csv
  (:require [clojure.string :as str]
            [doric.protocols :refer [tabular-renderer]]
            [doric.formatting :refer [unaligned-th unaligned-td]]))


(defn escape [s]
  (let [s (.replaceAll (str s) "\"" "\"\"")]
    (if (re-find #"[,\n\"]" s)
      (str "\"" s "\"")
      s)))

(defn assemble [rows]
  (cons (str/join "," (first rows))
        (for [tr (rest rows)]
          (str/join "," tr))))

(def renderer (tabular-renderer {:th (comp escape unaligned-th)
                                 :td (comp escape unaligned-td)
                                 :assemble assemble}))
