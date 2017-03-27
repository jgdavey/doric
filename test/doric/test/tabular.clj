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

(deftest test-calculate-width
  (is (= 9 (calculate-width {:title "TitleCase" :name :a} ["hi"])))
  (is (= 8 (calculate-width {:title "Title" :name :a} [{:a "whatever"}
                                                                {:a "is"}
                                                                {:a "largest"}])))
  (is (= 7 (calculate-width {:name :foobar} [{:foobar "foobar2"}]))))
