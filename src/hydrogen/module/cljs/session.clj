(ns hydrogen.module.cljs.session
  (:require [duct.core :as core]
            [duct.core.env :as env]
            [integrant.core :as ig]
            [hydrogen.module.cljs.util :as util]))

(defn- session-config [project-ns]
  {[:duct/const :magnet.aws/credentials]
   {:access-key-id (env/env '["AWS_ACCESS_KEY_ID" Str])
    :secret-access-key (env/env '["AWS_SECRET_ACCESS_KEY" Str])
    :default-region (env/env '["AWS_DEFAULT_REGION" Str :or "eu-west-1"])}

   (keyword (str project-ns ".handler/api"))
   {:auth-middleware (ig/ref :duct.middleware.buddy/authentication)}

   (keyword (str project-ns ".handler/config"))
   {:iss (env/env '["OIDC_ISSUER_URL" Str])
    :client-id (env/env '["OIDC_AUDIENCE" Str])}

   :magnet.buddy-auth/jwt-oidc
   {:claims
    {:iss (env/env '["OIDC_ISSUER_URL" Str])
     :aud (env/env '["OIDC_AUDIENCE" Str])}
    :jwks-uri (env/env '["OIDC_JWKS_URI" Str])}

   :duct.middleware.buddy/authentication
   {:backend :token
    :token-name "Bearer"
    :authfn (ig/ref :magnet.buddy-auth/jwt-oidc)}})

(defmethod ig/init-key :hydrogen.module.cljs/session [_ options]
  (fn [config]
    (let [project-ns (util/project-ns config options)]
      (core/merge-configs config (session-config project-ns)))))
