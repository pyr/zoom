(defproject spootnik/zoom "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljsbuild "1.1.0"]]
  :dependencies [[org.clojure/clojure       "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]
                 [org.clojure/core.async    "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om             "0.9.0"]]
  :cljsbuild {:builds [{:source-paths ["src"]
                        :compiler {:main "zoom.examples"
                                   :asset-path "/js"
                                   :output-to  "resources/public/js/app.js"
                                   :output-dir "resources/public/js"
                                   :optimizations :none
                                   :source-map true}}]}
  :clean-targets ^{:protect false} ["target" "resources/public/js"])
