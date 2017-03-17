(ns doric.test.core
  (:refer-clojure :exclude [format name when])
  (:use [doric.core]
        [clojure.test]
        [doric.org :only [th td render]]))

(deftest test-column-defaults
  (is (= "foo" (:title (columnize {:title "foo"}))))
  (is (= "Foo" (:title (columnize {:name "foo"}))))
  (is (= "Foo Bar" (:title (columnize {:name "foo bar"}))))
  (is (= "Foo" (:title (columnize :foo)))))

(deftest test-when
  (is (re-find #"Foo" (table [{:name :foo}] [{:foo :bar}])))
  (is (re-find #"bar" (table [{:name :foo}] [{:foo :bar}])))
  (is (re-find #"Foo" (table [{:name :foo :when true}] [{:foo :bar}])))
  (is (re-find #"bar" (table [{:name :foo :when true}] [{:foo :bar}])))
  (is (not (re-find #"Foo" (table [{:name :foo :when false}] [{:foo :bar}]))))
  (is (not (re-find #"bar" (table [{:name :foo :when false}] [{:foo :bar}])))))

(deftest test-width
  (is (= 5 (width {:width 5})))
  (is (= 5 (width {:width 5 :name :foobar})))
  (is (= 7 (width {:name :foobar} ["foobar2"]))))

(deftest test-format-cell
  (is (= 2 (format-cell {:format inc} 1))))

(deftest test-th
  (is (= "Title  " (th {:title "Title" :width 7 :title-align :left})))
  (is (= " Title " (th {:title "Title" :width 7 :title-align :center})))
  (is (= "  Title" (th {:title "Title" :width 7 :title-align :right}))))

(deftest test-td
  (is (= ".  " (td {:name :t :width 3 :align :left} {:t "."})))
  (is (= " . " (td {:name :t :width 3 :align :center} {:t "."})))
  (is (= "  ." (td {:name :t :width 3 :align :right} {:t "."}))))

;; TODO (deftest test-header)

;; TODO (deftest test-body)

(deftest test-render
  (let [rendered (render [["1" "2"]["3" "4"]])]
    (is (.contains rendered "| 1 | 2 |"))
    (is (.contains rendered "| 3 | 4 |"))
    (is (.contains rendered "|---+---|"))))

;; TODO embiggen these tests
(deftest test-table
  (let [rendered (table [{:1 3 :2 4}])]
    (is (.contains rendered "| 1 | 2 |"))
    (is (.contains rendered "| 3 | 4 |"))
    (is (.contains rendered "|---+---|"))))

(deftest test-table*-laziness
  (let [calls (atom 0)
        inc #(do (swap! calls inc) %)]
    (testing "formats are not lazy"
      (let [seq (table* [{:name :1 :format inc}
                         {:name :2 :format inc}]
                        [{:1 3 :2 4}])]
        (is (= 2 @calls))))
    (reset! calls 0)
    (testing "unless you provide widths"
      (let [seq (table* [{:name :1 :format inc :width 10}
                         {:name :2 :format inc :width 10}]
                        [{:1 3 :2 4}])]
        (is (= 0 @calls))))
    (reset! calls 0)
    (testing "even for formats that should be automatically lazy, like csv"
      (let [seq (table* ^{:format csv}
                        [{:name :1 :format inc :width 0}
                         {:name :2 :format inc :width 0}]
                        [{:1 3 :2 4}])]
        (is (= 0 @calls))))))

(deftest test-empty-table
  (let [empty-table "|--|\n|  |\n|--|\n|--|"]
    (is (= empty-table (table [])))
    (is (= empty-table (table nil)))
    (is (= empty-table (table [] [])))
    (is (= empty-table (table [] nil)))
    (is (= empty-table (table nil [])))
    (is (= empty-table (table nil nil)))))
