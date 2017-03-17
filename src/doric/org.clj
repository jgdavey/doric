(ns doric.org
  (:require [clojure.string :as str]
            [doric.formatting :refer [aligned-th aligned-td]]))

(def th aligned-th)

(def td aligned-td)

(defn render [table]
  (let [spacer (str "|-"
                    (str/join "-+-"
                          (map #(apply str (repeat (.length %) "-"))
                               (first table)))
                    "-|")]
    (concat [spacer
             (str "| " (str/join " | " (first table)) " |")
             spacer]
            (for [tr (rest table)]
              (str "| " (str/join " | " tr) " |"))
            [spacer])))
