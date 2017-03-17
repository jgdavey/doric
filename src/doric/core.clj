(ns doric.core
  (:require [doric.formatting :refer [titleize escape]]
            [doric.protocols :refer [render render-lazy]]
            [clojure.string :as str]
            [doric.org]
            [doric.raw]
            [doric.html]
            [doric.csv]
            [doric.json]))

(defn column-defaults [col]
  (merge col
         {:align (keyword (get col :align :left))
          :format (or (:format col)
                      identity)
          :escape (or (:escape col)
                      escape)
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

(defn width [{:keys [title escape width]} data]
  (or width
      (apply max (map count (cons title
                                  (map (comp escape str) data))))))

(defn bar [x]
  (apply str (repeat x "#")))

(defn format-rows [cols rows]
  (for [row rows]
    (reduce
     (fn [m {:keys [name format] :as col}]
       (assoc m name
              (-> row
                  (get name)
                  format)))
     {}
     cols)))

(defn- col-data [col rows]
  (map #(get % (:name col)) rows))

(defn columns-with-widths [cols rows]
  (for [col cols]
    (merge col
           {:width (width col (col-data col rows))})))

;; table formats
(def renderers {:csv doric.csv/renderer
                :html doric.html/renderer
                :org doric.org/renderer
                :raw doric.raw/renderer
                :json (doric.json/make-renderer)
                :json-pretty (doric.json/make-renderer true)})

(defn mapify [rows]
  (let [example (first rows)]
    (cond (map? rows) (for [k (sort (keys rows))]
                        {:key k :val (rows k)} )
          (vector? example) (for [row rows]
                              (into {}
                                    (map-indexed (fn [i x] [i x]) row)))
          (map? example) rows)))

(defn conform
  "Given an optional colspec and a sequence of maps, returns map with
  keys :cols, :rows"
  ([rows]
   (conform nil rows))
  ([cols rows]
   (let [rows (mapify rows)
         cols (map columnize (or cols
                                 (keys (first rows))))
         rows (format-rows cols rows)
         cols (columns-with-widths cols rows)]
     {:cols cols, :rows rows})))

(defn -parse-args
  [args]
  (let [rows (last args)
        [opts cols] (case (count args)
                      1 [nil nil]
                      2 (if (map? (first args))
                          [(first args) nil]
                          [nil (first args)])
                      3 [(first args) (second args)])
        format (or (:format opts) :org)
        renderer (renderers format format)
        cols-rows (conform cols rows)]
    (merge {:renderer renderer} cols-rows)))

(defn table*
  {:arglists '[[rows]
               [opts rows]
               [cols rows]
               [opts cols rows]]}
  [& args]
  (let [{:keys [renderer cols rows]} (-parse-args args)]
    (render-lazy renderer cols rows)))

(defn table
  {:arglists '[[rows]
               [opts rows]
               [cols rows]
               [otps cols rows]]}
  [& args]
  (let [{:keys [renderer cols rows]} (-parse-args args)]
    (render renderer cols rows)))
