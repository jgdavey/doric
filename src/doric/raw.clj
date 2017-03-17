(ns doric.raw
  (:require [clojure.string :as str]
            [doric.formatting :refer [aligned-th aligned-td]]))

(def th aligned-th)

(def td aligned-td)

(defn render [table]
  (cons (str/join " " (first table))
        (for [tr (rest table)]
          (str/join " " tr))))
