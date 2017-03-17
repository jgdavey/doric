(ns doric.raw
  (:require [clojure.string :as str]
            [doric.tabular :refer [tabular-renderer
                                   aligned-th
                                   aligned-td]]))

(defn assemble [rows]
  (cons (str/join " " (first rows))
        (for [tr (rest rows)]
          (str/join " " tr))))

(def renderer (tabular-renderer {:th aligned-th
                                 :td aligned-td
                                 :assemble assemble}))
