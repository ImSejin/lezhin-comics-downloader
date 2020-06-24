# Lezhin Comics Downloader

![GitHub release (latest by date)](https://img.shields.io/github/v/release/imsejin/lezhin-comics-downloader) ![jdk](https://img.shields.io/badge/jdk-8-orange) ![GitHub](https://img.shields.io/github/license/imsejin/lezhin-comics-downloader)

This is downloader that helps login process and downloads the specified comic for all lezhin comics even adult.

※ *The user is responsible for everything that happens using this program.*



### Preview

![preview.gif](https://user-images.githubusercontent.com/46176032/82747023-5ef38f00-9dd0-11ea-9f42-18f744fb50a9.gif)



### Usage

This is an executable JAR package. To run it, use the following command.

```cmd
java -jar lezhin-comics-downloader.jar {id} {password} {comic name} [{episode range}]
```

- *id*, *password*: your lezhin comics account, not account of third party platform.
- *comic name*: the webtoon name you want to download.
- *episode range*
  - **skipped** : all of episodes
  - ***** : all of episodes
  - **8~** : from ep.8 to the end of the episode
  - **~25 **: from the beginning of the episode to ep.25
  - **1~10** : from ep.1 to ep.10



### Dependencies

- Lombok
- Selenium
- Gson
- Progress Bar
- Junit





