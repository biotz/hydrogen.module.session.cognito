(ns hydrogen.module.cljs.util)

(defn project-ns [config options]
  (:project-ns options (:duct.core/project-ns config)))
