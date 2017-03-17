(ns doric.core
  (:refer-clojure :exclude [format name join split])
  (:require [doric.formatting :refer [titleize]]
            [clojure.string :as str]))

(defn column-defaults [col]
  (merge col
         {:align (keyword (get col :align :left))
          :format (or (:format col)
                      identity)
          :title  (or (:title col)
                      (titleize (:name col)))
          :title-align (keyword (or (:title-align col)
                                    (:align col)
                                    :center))
          :when (:when col true)}))

(defn column-map [col]
  (if (map? col)
    col
    {:name col}))

(def columnize (comp column-defaults column-map))

(defn width [col & [data]]
  (or (:width col)
      (apply max (map count (cons (:title col)
                                  (map str data))))))

(defn bar [x]
  (apply str (repeat x "#")))

(defn format-cell [col s]
  ((:format col) s))

(defn header [th cols]
  (for [col cols
        :when (:when col)]
    (th col)))

(defn body [td cols rows]
  (for [row rows]
    (for [col cols
          :when (:when col)]
      (td col row))))

(defn- col-data [col rows]
  (map #(get % (:name col)) rows))


(defn- format-rows [cols rows]
  (for [row rows]
    (into {}
          (for [col cols :let [name (:name col)]]
            [name (format-cell col (row name))]))))

(defn- column2 [col & [data]]
  {:width (width col data)})

(defn- columns2 [cols rows]
  (for [col cols]
    (merge col
           (column2 col (col-data col rows)))))

;; table formats
(def csv 'doric.csv)
(def html 'doric.html)
(def org 'doric.org)
(def raw 'doric.raw)

(defn mapify [rows]
  (let [example (first rows)]
    (cond (map? rows) (for [k (sort (keys rows))]
                        {:key k :val (rows k)} )
          (vector? example) (for [row rows]
                              (into {}
                                    (map-indexed (fn [i x] [i x]) row)))
          (map? example) rows)))

(defn table*
  {:arglists '[[rows]
               [opts rows]
               [cols rows]
               [opts cols rows]]}
  [& args]
  (let [rows (mapify (last args))
        [opts cols] (case (count args)
                      1 [nil nil]
                      2 (if (map? (first args))
                          [(first args) nil]
                          [nil (first args)])
                      3 [(first args) (second args)])
        cols (or cols (keys (first rows)))
        format (or (:format opts) org)
        _ (require format)
        th (ns-resolve format 'th)
        td (ns-resolve format 'td)
        render (ns-resolve format 'render)
        cols (map columnize cols)
        rows (format-rows cols rows)
        cols (columns2 cols rows)]
    (render (cons (header th cols) (body td cols rows)))))

(defn table
  {:arglists '[[rows]
               [opts rows]
               [cols rows]
               [otps cols rows]]}
  [& args]
  (apply str (str/join "\n" (apply table* args))))
