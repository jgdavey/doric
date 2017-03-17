(ns doric.test.formatting
  (:require [doric.formatting :refer :all]
            [clojure.test :refer :all]))

(deftest test-title-case
  (is (= "Foo" (title-case "foo")))
  (is (= "Foo-bar" (title-case "foo-bar")))
  (is (= "Foo Bar" (title-case "foo bar")))
  (is (= "Foo  Bar" (title-case "foo  bar"))))

(deftest test-align-cell
  (is (= "." (align-cell {:width 1} "." :left)))
  (is (= "." (align-cell {:width 1} "." :center)))
  (is (= "." (align-cell {:width 1} "." :right)))
  (is (= ".  " (align-cell {:width 3} "." :left)))
  (is (= " . " (align-cell {:width 3} "." :center)))
  (is (= "  ." (align-cell {:width 3} "." :right)))
  (is (= ".   " (align-cell {:width 4} "." :left)))
  (is (= "  . " (align-cell {:width 4} "." :center)))
  (is (= "   ." (align-cell {:width 4} "." :right))))

