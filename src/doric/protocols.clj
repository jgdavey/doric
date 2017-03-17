(ns doric.protocols)

(defprotocol Render
  (-render-lazy [_ cols data])
  (-render [_ cols data]))

(defn render-lazy [renderer cols data]
  (-render-lazy renderer cols data))

(defn render [renderer cols data]
  (-render renderer cols data))
