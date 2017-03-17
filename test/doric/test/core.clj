(ns doric.test.core
  (:refer-clojure :exclude [format name when])
  (:use [doric.core]
        [clojure.test]
        [doric.org :only [assemble]]))

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
  (is (= 5 (width {:width 5} ["no matter what"])))
  (is (= 9 (width {:title "TitleCase"} ["hi"])))
  (is (= 8 (width {:title "Title"} ["whatever" "is" "largest"])))
  (is (= 7 (width {:name :foobar} ["foobar2"]))))


;; TODO (deftest test-header)

;; TODO (deftest test-body)

(deftest test-assemble
  (let [rendered (assemble [["1" "2"]["3" "4"]])]
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
      (let [seq (table* {:format :csv}
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
