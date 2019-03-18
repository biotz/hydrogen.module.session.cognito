(ns hydrogen.module.cljs.core
  (:require [duct.core :as core]
            [integrant.core :as ig]
            [hydrogen.module.cljs.util :as util]))

(defn- core-config [project-ns]
  {:duct.middleware.web/defaults {:security {:anti-forgery false}}
   :duct.handler/root {:middleware [(ig/ref :duct.middleware.web/format)]}
   :duct.middleware.web/format {}

   (keyword (str project-ns ".handler/root")) {}

   :duct.compiler/sass {:source-paths ["resources"]
                        :output-path "target/resources"}})

(defmethod ig/init-key :hydrogen.module.cljs/core [_ options]
  (fn [config]
    (let [project-ns (util/project-ns config options)]
      (core/merge-configs config (core-config project-ns)))))
