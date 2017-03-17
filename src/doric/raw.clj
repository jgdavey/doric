(ns doric.raw
  (:require [clojure.string :as str]
            [doric.protocols :refer [tabular-renderer]]
            [doric.formatting :refer [aligned-th aligned-td]]))

(defn assemble [rows]
  (cons (str/join " " (first rows))
        (for [tr (rest rows)]
          (str/join " " tr))))

(def renderer (tabular-renderer {:th aligned-th
                                 :td aligned-td
                                 :assemble assemble}))
