(ns hydrogen.module.cljs.core
  (:require [duct.core :as core]
            [integrant.core :as ig]
            [hydrogen.module.cljs.util :as util]))

(defmulti ^:private duct-compiler-cljs
          (fn [environment _ _]
            environment))

(defn- externs-paths [options project-dirs]
  (or (:externs-paths options)
      [(format "src/%s/client/externs.js" project-dirs)]))

(defmethod duct-compiler-cljs :production
  [_ config options]
  (let [project-ns (util/project-ns config options)
        project-dirs (util/project-dirs config options)]
    {:builds
     [{:source-paths ["src/"]
       :build-options
       {:main (symbol (str project-ns ".client"))
        :output-to (format "target/resources/%s/public/js/main.js" project-dirs)
        :output-dir (format "target/resources/%s/public/js" project-dirs)
        :asset-path "/js"
        :closure-defines {:goog.DEBUG false}
        :aot-cache true
        :verbose true
        :externs (externs-paths options project-dirs)
        :optimizations :advanced}}]}))

(defmethod duct-compiler-cljs :development
  [_ _ _]
  {})

(defn- duct-server-figwheel
  [config options]
  (let [project-ns (util/project-ns config options)
        project-dirs (util/project-dirs config options)]
    {:css-dirs [(format "target/resources/%s/public/css" project-dirs)]
     :builds [{:id "dev"
               :figwheel {:on-jsload (format "%s.client/mount-root" project-dirs)}
               :source-paths ["dev/src" "src"]
               :build-options {:main (symbol (str project-ns ".client"))
                               :output-to (format "target/resources/%s/public/js/main.js" project-dirs)
                               :output-dir (format "target/resources/%s/public/js" project-dirs)
                               :asset-path "/js"
                               :closure-defines {'goog.DEBUG true
                                                 "re_frame.trace.trace_enabled_QMARK_" true}
                               :verbose false
                               :preloads ['devtools.preload
                                          'day8.re-frame-10x.preload]
                               :optimizations :none}}]}))

(defn- core-config [config options]
  (let [project-ns (util/project-ns config options)
        environment (util/get-environment config options)]
    (cond->
      {:duct.middleware.web/defaults {:security {:anti-forgery false}}
       :duct.handler/root {:middleware [(ig/ref :duct.middleware.web/format)]}
       :duct.middleware.web/format {}

       (keyword (str project-ns ".static/root")) {}
       (keyword (str project-ns ".api/example")) {}

       :duct.compiler/sass {:source-paths ["resources"]
                            :output-path "target/resources"}

       :duct.compiler/cljs (duct-compiler-cljs environment config options)}

      (= environment :development)
      (assoc :duct.server/figwheel
             (duct-server-figwheel config options)))))

(defmethod ig/init-key :hydrogen.module.cljs/core [_ options]
  (fn [config]
    (core/merge-configs config (core-config config options))))
