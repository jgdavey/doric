(ns doric.unicode
  (:require [clojure.string :as str]
            [doric.formatting :refer [escape]]
            [doric.tabular :refer [tabular-renderer
                                   aligned-th
                                   aligned-td]]))

(defn assemble [rows]
  (let [spacer (fn [l c r]
                 (str l
                      (str/join c
                                (map #(apply str (repeat (.length %) "─"))
                                     (first rows)))
                      r))]
    (concat [(spacer "┌─"  "─┬─"  "─┐")
             (str "│ " (str/join " │ " (first rows)) " │")
             (spacer "├─"  "─┼─"  "─┤")]
            (for [tr (rest rows)]
              (str "│ " (str/join " │ " tr) " │"))
            [(spacer "└─"  "─┴─"  "─┘")])))

(def renderer (tabular-renderer {:th aligned-th
                                 :td aligned-td
                                 :assemble assemble
                                 :escape escape}))
