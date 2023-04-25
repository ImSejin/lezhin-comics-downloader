# Table of Contents

- [v3.0.5](#v305): 2023-04-25
- [v3.0.4](#v304): 2023-04-24
- [v3.0.3](#v303): 2023-01-03
- [v3.0.2](#v302): 2022-12-18
- [v3.0.1](#v301): 2022-12-16
- [v3.0.0](#v300): 2022-12-14
- [v2.9.0](#v290): 2022-02-20

# v3.0.5

## Dependencies

- ♻️ Replace: build dependency `maven-assembly-plugin` with `maven-shade-plugin`

## Troubleshooting

- 🐞 Fix: IllegalArgumentException ... Unknown HttpClient factory jdk-http-client

# v3.0.4

## Dependencies

- ⬆️ Upgrade: dependency `selenium-java` from `4.6.0` to `4.9.0`
- ⬆️ Upgrade: dependency `lombok` from `1.18.24` to `1.18.26`
- ⬆️ Upgrade: dependency `logback-classic` from `1.4.5` to `1.4.7`
- ⬆️ Upgrade: dependency `slf4j-api` from `2.0.6` to `2.0.7`
- ⬆️ Upgrade: dependency `annotations` from `23.1.0` to `24.0.1`
- ⬆️ Upgrade: dependency `progressbar` from `0.9.4` to `0.9.5`
- ⬆️ Upgrade: dependency `mapstruct-plugin` from `1.5.3.Final` to `1.5.5.Final`
- ⬆️ Upgrade: test dependency `junit5` from `5.9.1` to `5.9.2`
- ⬆️ Upgrade: test dependency `assertj-core` from `3.23.1` to `3.24.2`
- ⬆️ Upgrade: test dependency `mockito-inline` from `4.11.0` to `5.2.0`
- ⬆️ Upgrade: build dependency `maven-compiler-plugin` from `3.10.1` to `3.11.0`
- ⬆️ Upgrade: build dependency `maven-assembly-plugin` from `3.3.0` to `3.5.0`
- ⬆️ Upgrade: build dependency `jacoco-maven-plugin` from `0.8.8` to `0.8.9`

## Troubleshooting

- 🐞 Fix: IOException ... Invalid Status code=403 text=Forbidden
- 🐞 Fix: SessionNotCreatedException ... Could not start a new session. Response code 500

# v3.0.3

## Dependencies

- ⬆️ Upgrade: dependency `common-utils` from `0.13.0` to `0.14.0`
- ⬆️ Upgrade: dependency `annotations` from `23.0.0` to `23.1.0`
- ⬆️ Upgrade: dependency `slf4j-api` from `2.0.4` to `2.0.6`
- ⬆️ Upgrade: dependency `logback-classic` from `1.3.5` to `1.4.5`
- ⬆️ Upgrade: test dependency `mockito-inline` from `4.10.0` to `4.11.0`
- ⬆️ Upgrade: build dependency `gmavenplus-plugin` from `1.13.1` to `2.1.0`

## Troubleshooting

- 🐞 Fix: termination of application
- 🐞 Fix: failure to get image count on locale KOREA

# v3.0.2

## Dependencies

- ➕ Add: test dependency `mockito-inline`

## Troubleshooting

- 🐞 Fix: creation of directory containing invalid character

# v3.0.1

## Dependencies

- ⬆️ Upgrade: dependency `slf4j-api` from `1.7.32` to `2.0.4`

## Troubleshooting

- 🐞 Fix: downloading image of lower resolution than manually
- 🐞 Fix: conflict dependency version of logging 

# v3.0.0

## Modification

- ♻️ Refactor: entire application architecture
- ♻️ Change: base package from `io.github.imsejin.lzcodl` to `io.github.imsejin.dl.lezhin`
- ⬆️ Upgrade: java version from `8` to `11`
- 🔥 Remove: model class `Arguments`
- 🔥 Remove: common classes `CommandParser`, `URLFactory`, `UsagePrinter`
- 🔥 Remove: core classes `Crawler`, `Downloader`, `LoginHelper`

## Dependencies

- ⬆️ Upgrade: dependency `progressbar` from `0.9.3` to `0.9.4`
- ⬆️ Upgrade: dependency `common-utils` from `0.7.1` to `0.13.0`
- ⬆️ Upgrade: dependency `lombok` from `1.18.22` to `1.18.24`
- ⬆️ Upgrade: dependency `selenium-java` from `4.1.2` to `4.6.0`
- ⬆️ Upgrade: dependency `logback-classic` from `1.2.10` to `1.3.5`
- ⬆️ Upgrade: test dependency `junit5` from `5.8.2` to `5.9.1`
- ⬆️ Upgrade: test dependency `assertj-core` from `3.22.0` to `3.23.1`

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
