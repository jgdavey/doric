(defproject doric "0.10.0-SNAPSHOT"
  :description "Clojure table layout"
  :url "https://github.com/joegallo/doric"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.7.0"]]
  :profiles {:1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :dev {:dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                                  [org.apache.poi/poi "3.10.1"]]}}
  :aliases {"all" ["with-profile" "dev,1.6:dev,1.7:dev,1.8:dev"]})
