# Table of Contents

- [v3.0.0](#v300): 2022-12-14
- [v2.9.0](#v290): 2022-02-20

# v3.0.0

- ♻️ Refactor: entire application architecture
- ♻️ Change: base package from `io.github.imsejin.lzcodl` to `io.github.imsejin.dl.lezhin`
- 🔥 Remove: model class `Arguments`
- 🔥 Remove: common classes `CommandParser`, `URLFactory`, `UsagePrinter`
- 🔥 Remove: core classes `Crawler`, `Downloader`, `LoginHelper`

# v2.9.0

## Modification

- ⚡️ Improve: parsing `application.properties`
- ⚡️ Improve: validation using assertion
- ♻️ Change: type of `Arguments#language` from `String` to `Language`
- 🚚 Rename: method `of(String)` to `from(String)` in `EpisodeRange`
- 🔧 Update: build script

## New features

- ⚡️ Add: chrome driver options
- ✨ Add: method `getVersion()` in `ChromeBrowser`
- 👷 Add: github actions CI

## Dependencies

- ⬆️ Upgrade: dependency `progressbar` from `0.9.2` to `0.9.3`
- ⬆️ Upgrade: dependency `common-utils` from `0.7.0` to `0.7.1`
- ⬆️ Upgrade: dependency `lombok` from `1.18.20` to `1.18.22`
- ⬆️ Upgrade: dependency `selenium-java` from `3.141.59` to `4.1.2`
- ⬆️ Upgrade: dependency `commons-cli` from `1.4` to `1.5.0`
- ⬆️ Upgrade: dependency `slf4j-api` from `1.7.31` to `1.7.36`
- ⬆️ Upgrade: dependency `logback-classic` from `1.2.3` to `1.2.10`
- ⬆️ Upgrade: test dependency `junit5` from `5.7.2` to `5.8.2`
- ⬆️ Upgrade: test dependency `assertj-core` from `3.20.2` to `3.22.0`
- ⬆️ Upgrade: build dependency `maven-assembly-plugin` from `2.6` to `3.3.0`
- ➖ Remove: useless build dependency `maven-dependency-plugin`

## Troubleshooting

- 🐞 Fix: not found hostname(`cdn.lezhin.com` -> `ccdn.lezhin.com`)
- 🐞 Fix: `301 Moved Permanently` with changing to http/s protocol

```html
<html>
  <head><title>301 Moved Permanently</title></head>
  <body bgcolor="white">
    <center><h1>301 Moved Permanently</h1></center>
    <hr><center>CloudFront</center>
  </body>
</html>
```

- 🐞 Fix: mis-computation of parsing `EpisodeRange`
- 🐞 Fix: invocation `Object#equals(Object)` comparing incomparable types `Enum<Languages>` and `String`
