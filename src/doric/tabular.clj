(ns doric.tabular
  (:require [clojure.string :as str]
            [doric.protocols :refer :all]
            [doric.formatting :refer [escape align-cell]]))

(defn- col-data [{:keys [name]} rows]
  (map (comp escape str name) rows))

(defn width [{:keys [title escape width]} cells]
  (or width
      (->> cells
           (cons (escape title))
           (map count)
           (apply max))))

(defn columns-with-widths [cols rows]
  (for [col cols]
    (merge col
           {:width (width col (col-data col rows))})))

(defrecord TabularRender [th td assemble]
  Render
  (-render [this cols data]
    (str/join "\n"
              (-render-lazy this cols data)))
  (-render-lazy [_ cols data]
    (let [cols (columns-with-widths cols data)]
      (assemble
       (cons (for [col cols]
               (th col (escape (:title col))))
             (for [row data]
               (for [col cols]
                 (td col (escape (get row (:name col)))))))))))

(defn tabular-renderer [{:keys [td th assemble] :as fns}]
  (map->TabularRender fns))

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
