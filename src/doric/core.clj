(ns doric.core
  (:require [doric.formatting :refer [titleize]]
            [doric.protocols :refer [render render-lazy Render]]
            [clojure.string :as str]
            [doric.org]
            [doric.raw]
            [doric.html]
            [doric.csv]
            [doric.confluence]
            [doric.json]))

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

;; table formats
(def renderers {:csv doric.csv/renderer
                :html doric.html/renderer
                :org doric.org/renderer
                :raw doric.raw/renderer
                :confluence doric.confluence/renderer
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
         cols (filter :when
                      (map columnize (or cols
                                         (keys (first rows)))))
         rows (format-rows cols rows)]
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
        format (:format opts)
        renderer (if (satisfies? Render format)
                   format
                   (renderers (or format :org)))
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
