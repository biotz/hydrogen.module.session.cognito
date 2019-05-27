# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/).

## [0.1.3] - 2019-05-27

### Added
- You can configure this module to provide specific paths for extern files by using
`:extern-paths ["x/y/extern1.js" "x/y/extern2.js"]`. If not provided, it will assume that there's one
extern file in location `src/<project-dirs>/client/externs.js`.

### Changed
- **Breaking change** - Now by default this module doesn't provide example api integrant key.
To retain it, you have to add `:add-example-api? true` to the config options.

[0.1.3]: https://github.com/magnetcoop/hydrogen.module.cljs/releases/tag/v0.1.3
