(ns doric.tabular
  (:require [clojure.string :as str]
            [doric.protocols :refer :all]
            [doric.formatting :refer [align-cell]]))


(defn calculate-width
  ([col rows]
   (calculate-width col rows identity))
  ([{:keys [title name]} rows escape]
   (->> rows
        (map (comp escape str name))
        (cons (escape title))
        (map count)
        (apply max))))

(defn columns-with-widths [escape cols rows]
  (for [{:keys [width] :as col} cols]
    (assoc col
           :width (or width
                      (calculate-width col rows escape)))))

(defrecord TabularRender [th td assemble escape]
  Render
  (-render [this cols data]
    (str/join "\n"
              (-render-lazy this cols data)))
  (-render-lazy [_ cols data]
    (let [cols (columns-with-widths escape cols data)]
      (assemble
       (cons (for [col cols]
               (th col (escape (:title col))))
             (for [row data]
               (for [col cols]
                 (td col (escape (get row (:name col)))))))))))

;; table format helpers

;; unalighed-th and td are useful for whitespace immune formats, like
;; csv and html
(defn unaligned-th [_ data] data)
(defn unaligned-td [_ data] data)

;; aligned th and td are useful for whitespace sensitive formats, like
;; raw and org
(defn aligned-th [col cell-data]
  (align-cell col
              cell-data
              (:title-align col)))

(defn aligned-td [col cell-data]
  (align-cell col
              cell-data
             (:align col)))

(defn tabular-renderer [{:keys [td th assemble escape]}]
  (map->TabularRender {:td (or td unaligned-td)
                       :th (or th unaligned-th)
                       :escape (or escape identity)
                       :assemble assemble}))
