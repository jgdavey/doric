(ns doric.formatting
  (:require [clojure.string :as str]))

(defn- title-case-word [w]
  (if (zero? (count w))
    w
    (str (Character/toTitleCase (first w))
         (subs w 1))))

(defn title-case [s]
  (str/join " " (map title-case-word (str/split s #"\s"))))

(defn titleize [n]
  (title-case
   (.replaceAll ^String (name (if (number? n)
                                (str n)
                                n))
                "-" " ")))

(defn escape [s]
  (if (string? s)
    (str/escape s {\newline "\\n"
                   \tab     "\\t"})
    s))

(defn align-cell [col s align]
  (let [width (:width col)
        s (str s)
        s (cond (<= (count s) width) s
                (:ellipsis col) (str (subs s 0 (- width 3)) "...")
                :else (subs s 0 width))
        len (count s)
        pad #(apply str (take % (repeat " ")))
        padding (- width len)
        half-padding (/ (- width len) 2)]
    (case align
      :left (str s (pad padding))
      :right (str (pad padding) s)
      :center (str (pad (Math/ceil half-padding))
                   s
                   (pad (Math/floor half-padding))))))
