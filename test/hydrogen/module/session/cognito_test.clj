(ns hydrogen.module.session.cognito-test
  (:require [clojure.test :refer :all]
            [duct.core :as core]
            [duct.core.env :as env]
            [hydrogen.module.session.cognito :as sut]
            [integrant.core :as ig]))

(core/load-hierarchy)

(def base-config
  {:duct.profile/base {:duct.core/project-ns 'foo.bar}
   :hydrogen.module/session.cognito {}})

(deftest module-test
  (testing "blank config"
    (is (= {:duct.core/project-ns 'foo.bar
            :foo.bar.api/config
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
             :authfn (ig/ref :magnet.buddy-auth/jwt-oidc)}}
           (core/build-config base-config))))

  (testing "add example api config"
    (let [config (core/build-config (merge base-config
                                           {:hydrogen.module/session.cognito {:add-example-api? true}}))]
      (is (some-> config
                  (get :foo.bar.api/example)
                  (= {:auth-middleware (ig/ref :duct.middleware.buddy/authentication)}))))))