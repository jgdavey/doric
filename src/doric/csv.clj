(ns doric.csv
  (:require [clojure.string :as str]
            [doric.formatting :refer [unaligned-th unaligned-td]]))

(def th unaligned-th)

(def td unaligned-td)

(defn escape [s]
  (let [s (.replaceAll (str s) "\"" "\"\"")]
    (if (re-find #"[,\n\"]" s)
      (str "\"" s "\"")
      s)))

(defn render [table]
  (cons (str/join "," (map escape (first table)))
        (for [tr (rest table)]
          (str/join "," (map escape tr)))))
