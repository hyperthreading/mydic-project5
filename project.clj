(defproject
  mydic-project5 "0.1.0"
  :description ""
  :url ""
  :license {:name ""}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]

                 [org.clojure/core.async "0.4.474"]
                 [re-frame "0.10.3-rc2"]
                 [hickory "0.7.1"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.14"]
            [lein-garden "0.3.0"]]

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.9"]
                                  [garden "1.3.3"]
                                  [figwheel-sidecar "0.5.14"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [proto-repl "0.3.1"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["app/src" "renderer/src" "dev"]
                   ;; for CIDER
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   ;; need to add the compliled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                     "resources/out"
                                                     :target-path]}}

  :garden
  {:builds [{:source-paths ["renderer/src"]
             :stylesheet mydic.styles.core/main-css
             :compiler {:output-to "resources/public/css/style.css"}}]}

  :figwheel {:server-port 3450
             :css-dirs ["resources/public/css"]}

  :cljsbuild {:builds [{:id "main-dev"
                        :source-paths ["app/src"]
                        :incremental true
                        :assert true
                        :figwheel true
                        :compiler {:output-to "resources/out/main.js"
                                   :output-dir "resources/out"
                                   :main mydic.main
                                   :target :nodejs
                                   :optimizations :none
                                   :source-map true}}

                       {:id "renderer-dev"
                        :source-paths ["renderer/src"]

                        :figwheel
                        {:on-jsload "mydic.renderer/on-reload"}

                        :compiler {:main mydic.renderer
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/renderer.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true
                                   ;; To console.log CLJS data-structures make sure you enable devtools in Chrome
                                   ;; https://github.com/binaryage/cljs-devtools
                                   :preloads [devtools.preload]}}]})
