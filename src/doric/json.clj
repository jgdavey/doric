(ns doric.json
  (:require [cheshire.core :as json]
            [doric.protocols :as proto]))

(defrecord JSONRenderer [cheshire-opts]
  proto/Render
  (-render-lazy [_ cols data]
    (map #(json/generate-string % cheshire-opts) data))
  (-render [_ cols data]
    (json/generate-string data cheshire-opts)))

(defn make-renderer
  ([]
   (make-renderer false))
  ([pretty]
   (->JSONRenderer {:pretty pretty})))
