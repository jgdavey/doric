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

(defn width [col data]
  (or (:width col)
      (apply max (map count (cons (:title col)
                                  (map str data))))))

(defn bar [x]
  (apply str (repeat x "#")))

(defn header [th cols]
  (for [col cols
        :when (:when col)]
    (th col)))

(defn body [td cols rows]
  (for [row rows]
    (for [col cols
          :when (:when col)]
      (td col row))))

(defn format-rows [cols rows]
  (for [row rows]
    (reduce
     (fn [m {:keys [name] :as col}]
       (assoc m name
              ((:format col) (get row name))))
     {}
     cols)))

(defn- col-data [col rows]
  (map #(get % (:name col)) rows))

(defn columns-with-widths [cols rows]
  (for [col cols]
    (merge col
           {:width (width col (col-data col rows))})))

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

(defn conform
  "Given an optional colspec and a sequence of maps, returns tuple
  of [conformed-columns formatted-rows]"
  ([rows]
   (conform nil rows))
  ([cols rows]
   (let [rows (mapify rows)
         cols (map columnize (or cols
                                 (keys (first rows))))
         rows (format-rows cols rows)
         cols (columns-with-widths cols rows)]
     [cols rows])))

(defn table*
  {:arglists '[[rows]
               [opts rows]
               [cols rows]
               [opts cols rows]]}
  [& args]
  (let [rows (last args)
        [opts cols] (case (count args)
                      1 [nil nil]
                      2 (if (map? (first args))
                          [(first args) nil]
                          [nil (first args)])
                      3 [(first args) (second args)])
        format (or (:format opts) org)
        _ (require format)
        th (ns-resolve format 'th)
        td (ns-resolve format 'td)
        render (ns-resolve format 'render)
        [cols rows] (conform cols rows)]
    (render (cons (header th cols) (body td cols rows)))))

(defn table
  {:arglists '[[rows]
               [opts rows]
               [cols rows]
               [otps cols rows]]}
  [& args]
  (apply str (str/join "\n" (apply table* args))))
