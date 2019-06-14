(ns hydrogen.module.session.cognito
  (:require [duct.core :as core]
            [duct.core.env :as env]
            [integrant.core :as ig]))

(defn project-ns [config options]
  (:project-ns options (:duct.core/project-ns config)))

(defn- session-config-base [project-ns]
  {(keyword (str project-ns ".api/config"))
   {:cognito
    {:iss (env/env '["OIDC_ISSUER_URL" Str])
     :client-id (env/env '["OIDC_AUDIENCE" Str])}}

   :magnet.buddy-auth/jwt-oidc
   {:claims
    {:iss (env/env '["OIDC_ISSUER_URL" Str])
     :aud (env/env '["OIDC_AUDIENCE" Str])}
    :jwks-uri (env/env '["OIDC_JWKS_URI" Str])}

   :duct.middleware.buddy/authentication
   {:backend :token
    :token-name "Bearer"
    :authfn (ig/ref :magnet.buddy-auth/jwt-oidc)}})

(defn- session-config [options project-ns]
  (cond->
   (session-config-base project-ns)

    (:add-example-api? options)
    (assoc (keyword (str project-ns ".api/example"))
           {:auth-middleware (ig/ref :duct.middleware.buddy/authentication)})))

(defmethod ig/init-key :hydrogen.module/session.cognito [_ options]
  (fn [config]
    (let [project-ns (project-ns config options)]
      (core/merge-configs config (session-config options project-ns)))))

