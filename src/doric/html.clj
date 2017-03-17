(ns doric.html
  (:use [clojure.string :as str]
        [doric.formatting :refer [unaligned-th unaligned-td]]))

(def th unaligned-th)

(def td unaligned-td)

(defn render [table]
  (concat ["<table>"
           (str "<tr>" (str/join (for [c (first table)]
                               (str "<th>" c "</th>"))) "</tr>")]
          (for [tr (rest table)]
            (str "<tr>" (str/join (for [c tr]
                                (str "<td>" c "</td>"))) "</tr>"))
          ["</table>"]))
