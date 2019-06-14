(defproject hydrogen/module.session.cognito "0.1.7"
  :description "Duct module for OIDC-based session management in Hydrogen app"
  :url "https://github.com/magnetcoop/hydrogen.module.session.cognito"
  :license {:name "Mozilla Public Licence 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.439"]
                 [duct/core "0.7.0"]
                 [integrant "0.7.0"]]
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]]
  :profiles
  {:dev          [:project/dev :profiles/dev]
   :profiles/dev {}
   :project/dev  {:plugins [[lein-cljfmt "0.6.2"]
                            [jonase/eastwood "0.3.4"]]}})
