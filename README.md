## NOTE: This repo got deprecated. We migrated to [hydrogen.duct-template](https://github.com/magnetcoop/hydrogen.duct-template)

# hydrogen.module.session.cognito for Duct

It implements a module for [Duct](https://github.com/duct-framework/duct).
`hydrogen.module.session.cognito` further enriches config provided by 
[hydrogen.module.core](https://github.com/magnetcoop/hydrogen.module.core)
 to support AWS Cognito User Pools-based session management.

## Installation

[![Clojars Project](https://img.shields.io/clojars/v/hydrogen/module.session.cognito.svg)](https://clojars.org/hydrogen/module.session.cognito)

## Usage
 
```edn
{:hydrogen.module/core {
  ;; core hydrogen config
}
 :hydrogen.module/session.cognito {}}
```

##### This module will need those env variables to be set in order to work properly:
1. OIDC_ISSUER_URL
2. OIDC_AUDIENCE
3. OIDC_JWKS_URI

The module merges the following Integrant keys to system configuration:

1. `:project-ns.api/config`: it provides the above env variables values to the front-end
2. `:magnet.buddy-auth/jwt-oidc`: it provides a function that implements `:duct.middleware.buddy/authentication` compatible JWT token validation for OpenID Connect ID Tokens.
3. `:duct.middleware.buddy/authentication`: it provides a Ring-compatible middleware that enables authentication using OpenID Connect ID Tokens.

You will need to reference the `:duct.middleware.buddy/authentication` key from the routes' handlers keys where you want to use authentication. E.g.:

### Additional options

- This module is used by Hydrogen CE and by [Hydrogen duct template](https://github.com/magnetcoop/hydrogen.duct-template).
For this reason it usually starts with `:add-example-api? true` option to make running demo more effortless. The default for this option is `false` so there's probably nothing for you to care about :)
 
## License

Copyright (c) 2019 Magnet S Coop.

The source code for the library is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
