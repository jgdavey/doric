(ns doric.protocols
  (:require [clojure.string :as str]
            [doric.formatting :refer [escape]]))

(defprotocol Render
  (-render-lazy [_ cols data])
  (-render [_ cols data]))

(defrecord TabularRender [th td assemble]
  Render
  (-render [this cols data]
    (str/join "\n"
              (-render-lazy this cols data)))
  (-render-lazy [_ cols data]
    (assemble
     (cons (for [col cols]
             (th col (escape (:title col))))
           (for [row data]
             (for [col cols]
               (td col (escape (get row (:name col))))))))))

(defn render-lazy [renderer cols data]
  (-render-lazy renderer cols data))

(defn render [renderer cols data]
  (-render renderer cols data))

(defn tabular-renderer [{:keys [td th assemble] :as fns}]
  (map->TabularRender fns))
