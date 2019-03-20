(ns hydrogen.module.cljs.util
  (:require [clojure.string :as str]))

(defn project-ns [config options]
  (:project-ns options (:duct.core/project-ns config)))

(defn project-dirs [config options]
  (-> (project-ns config options)
      name
      (str/replace "-" "_")
      (str/replace "." "/")))

(defn get-environment [config options]
  (:environment options (:duct.core/environment config :production)))
