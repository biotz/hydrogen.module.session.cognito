(ns hydrogen.module.cljs.core
  (:require [duct.core :as core]
            [integrant.core :as ig]
            [hydrogen.module.cljs.util :as util]))

(defn- externs-paths [options environment]
  {:post [(vector? %)
          (every? string? %)]}
  (when-let [externs-paths (:externs-paths options)]
    (if (map? externs-paths)
      (get externs-paths environment) externs-paths)))

(defn- compiler-config
  [config options]
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
        :externs (or (externs-paths options :production)
                     [(format "src/%s/client/externs.js" project-dirs)])
        :optimizations :advanced}}]}))

(defn- duct-server-figwheel-build-options [config options]
  (let [project-ns (util/project-ns config options)
        project-dirs (util/project-dirs config options)
        externs (externs-paths options :development)]
    (cond->
     {:main (symbol (str project-ns ".client"))
      :output-to (format "target/resources/%s/public/js/main.js" project-dirs)
      :output-dir (format "target/resources/%s/public/js" project-dirs)
      :asset-path "/js"
      :closure-defines {'goog.DEBUG true
                        "re_frame.trace.trace_enabled_QMARK_" true}
      :verbose false
      :preloads ['devtools.preload
                 'day8.re-frame-10x.preload]
      :optimizations :none}
      externs (assoc :externs externs))))

(defn- figwheel-config
  [config options]
  (let [project-dirs (util/project-dirs config options)]
    {:css-dirs [(format "target/resources/%s/public/css" project-dirs)]
     :builds [{:id "dev"
               :figwheel {:on-jsload (format "%s.client/mount-root" project-dirs)}
               :source-paths ["dev/src" "src"]
               :build-options (duct-server-figwheel-build-options config options)}]}))

(defn- core-config-base [project-ns]
  {:duct.middleware.web/defaults {:security {:anti-forgery false}}
   :duct.handler/root {:middleware [(ig/ref :duct.middleware.web/format)]}
   :duct.middleware.web/format {}

   (keyword (str project-ns ".static/root")) {}

   :duct.compiler/sass {:source-paths ["resources"]
                        :output-path "target/resources"}})

(defn- core-config [config options]
  (let [project-ns (util/project-ns config options)
        environment (util/get-environment config options)]
    (cond->
     (core-config-base project-ns)

      (:add-example-api? options)
      (assoc (keyword (str project-ns ".api/example")) {})

      (= environment :development)
      (assoc :duct.server/figwheel
             (figwheel-config config options))
      (= environment :production)
      (assoc :duct.compiler/cljs (compiler-config config options)))))

(defmethod ig/init-key :hydrogen.module.cljs/core [_ options]
  (fn [config]
    (core/merge-configs config (core-config config options))))
