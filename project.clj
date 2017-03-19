(defproject glittershark/pubmethod "0.1.0"
  :description "Multimethods with support for multiple subscribers"
  :url "https://github.com/glittershark/pubmethod"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/clojurescript "1.9.494"]]
  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-doo "0.1.6"]]
  :test-paths ["test"]
  :cljsbuild
  {:builds {:test {:id "test"
                   :source-paths ["src" "test"]
                   :compiler {:output-to "target/js/pubmethod-test.js"
                              :main glittershark.pubmethod.runner
                              :optimizations :none
                              :pretty-print true
                              :source-map false
                              :warnings {:munged-namespace false}}}}})
