# Lezhin Comics Downloader

![GitHub release (latest by date)](https://img.shields.io/github/v/release/imsejin/lezhin-comics-downloader) ![jdk](https://img.shields.io/badge/jdk-8-orange) ![GitHub](https://img.shields.io/github/license/imsejin/lezhin-comics-downloader)

This is downloader that helps you to login and downloads the specified comic for all lezhin-comics even adults.

※ *The user is responsible for everything that happens using this program.*

<br>

### Preview

![preview.gif](https://user-images.githubusercontent.com/46176032/86532430-d73b8d00-bf04-11ea-9442-064af11aea9c.gif)

<br>

### Usage

1. Checks if chrome browser was installed in your device or downloads it [here](https://www.google.com/chrome).

2. Checks your <ins>chrome browser version</ins> with this URI [chrome://version](chrome://version).

   (The first line is the version. e.g. 83.0.4103.116)

3. Downloads the chrome driver that matches <ins>the version</ins> and your device OS [here](https://chromedriver.chromium.org/downloads) and decompresses it.

4. Checks if JRE(or JDK) version is greater than or equal to 8 or installs it.

5. Downloads the latest released lezhin-comics-downloader [here](https://github.com/ImSejin/lezhin-comics-downloader/releases).

6. Places two files in the same path.

7. Uses the following command to run the downloader.



```cmd
java -jar {JAR filename} {id} {password} {language} {comic name} [{episode range}]
```

- *<ins>id</ins>, <ins>password</ins> (required)*: your lezhin comics account, not account of third party platform.
- *<ins>language (required)</ins>*: the platform of lezhin comics you want to see.
  - **ko** : korean
  - **en** : english
  - **ja** : japanese
- *<ins>comic name</ins> (required)*: the webtoon name you want to download.
- *<ins>episode range</ins> (optional)*
  - __skipped__ : all episodes
  - __n~__ : from ep.N to the end of the episode
  - __~n__ : from the beginning of the episode to ep.N
  - __m~n__ : from ep.M to ep.N

<br>

### Command examples

```cmd
java -jar lezhin-comics-downloader.jar test@gmail.com test123 appetite
```

Downloads all episodes of the comic named appetite.

<br>

```cmd
java -jar lezhin-comics-downloader.jar test@gmail.com test123 appetite 8~
```

Downloads the episodes of the comic named appetite from ep.8 to the end.

<br>

```cmd
java -jar lezhin-comics-downloader.jar test@gmail.com test123 appetite ~25
```

Downloads the episodes of the comic named appetite from the beginning to ep.25.

<br>

```cmd
java -jar lezhin-comics-downloader.jar test@gmail.com test123 appetite 1~10
```

Downloads the episodes of the comic named appetite from ep.1 to ep.10.

<br>

### Dependencies

- Lombok
- Selenium
- Gson
- Progress Bar


