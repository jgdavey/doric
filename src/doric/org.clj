(ns doric.org
  (:require [clojure.string :as str]
            [doric.protocols :refer [tabular-renderer]]
            [doric.formatting :refer [aligned-th aligned-td]]))

(defn assemble [rows]
  (let [spacer (str "|-"
                    (str/join "-+-"
                              (map #(apply str (repeat (.length %) "-"))
                                   (first rows)))
                    "-|")]
    (concat [spacer
             (str "| " (str/join " | " (first rows)) " |")
             spacer]
            (for [tr (rest rows)]
              (str "| " (str/join " | " tr) " |"))
            [spacer])))

(def renderer (tabular-renderer {:th aligned-th
                                 :td aligned-td
                                 :assemble assemble}))
