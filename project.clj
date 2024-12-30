(defproject kelveden/clj-wiremock "1.10.0-SNAPSHOT"
  :description "Clojure bindings for WireMock"
  :url "https://github.com/zougue-mpms/clj-wiremock"
  :license {:name "Eclipse Public License - v 1.0"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.13.0"]
                 [clj-http "3.13.0"]
                 [com.github.tomakehurst/wiremock "2.27.2"]
                 [org.clojure/clojure "1.11.0"]
                 [org.clojure/tools.logging "1.3.0"]
                 [org.slf4j/slf4j-api "2.0.16"]]
  :jvm-opts ["-Dclojure.spec.check-asserts=true"]
  :profiles {:dev {:dependencies [[metosin/ring-http-response "0.9.4"]
                                  [org.slf4j/slf4j-simple "2.0.16"]
                                  [slingshot "0.12.2"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}}})
