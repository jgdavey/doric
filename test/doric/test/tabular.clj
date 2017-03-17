(ns doric.test.tabular
    (:require [doric.tabular :refer :all]
              [clojure.test :refer :all]))

(deftest test-aligned-th
  (is (= "Title  " (aligned-th {:width 7 :title-align :left} "Title")))
  (is (= " Title " (aligned-th {:width 7 :title-align :center} "Title")))
  (is (= "  Title" (aligned-th {:width 7 :title-align :right} "Title"))))

(deftest test-aligned-td
  (is (= ".  " (aligned-td {:width 3 :align :left} ".")))
  (is (= " . " (aligned-td {:width 3 :align :center} ".")))
  (is (= "  ." (aligned-td {:width 3 :align :right} "."))))

(deftest test-width
  (is (= 5 (width {:width 5} ["no matter what"])))
  (is (= 9 (width {:title "TitleCase" :escape identity} ["hi"])))
  (is (= 8 (width {:title "Title" :escape identity} ["whatever" "is" "largest"])))
  (is (= 7 (width {:name :foobar :escape identity} ["foobar2"]))))
