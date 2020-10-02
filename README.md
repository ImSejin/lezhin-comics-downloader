<p align="center" width="40%">
	<img alt="Lezhin Comics Downloader" src="./src/main/resources/lezhin-comics-downloader-main-image.png">
</p>



<p align="center">
    <img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/imsejin/lezhin-comics-downloader?label=github">
    <img alt="jdk8" src="https://img.shields.io/badge/jdk-8-orange">
	<img alt="GitHub" src="https://img.shields.io/github/license/imsejin/lezhin-comics-downloader">
	<img alt="GitHub All Releases" src="https://img.shields.io/github/downloads/imsejin/lezhin-comics-downloader/total">
	<img alt="GitHub Releases" src="https://img.shields.io/github/downloads/imsejin/lezhin-comics-downloader/latest/total">
</p>

This is downloader that helps you to login and downloads the specified comic for all lezhin-comics even adults.

※ *The user is responsible for everything that happens using this program.*

<br><br>

## Preview

![preview.gif](https://user-images.githubusercontent.com/46176032/93713243-81869080-fb95-11ea-8c70-031f26c8ebfc.gif)

<br><br>

## Getting started

### Pre-requirements

1. Check if chrome browser was installed in your device or download it [here](https://www.google.com/chrome).

2. Check your <ins>chrome browser version</ins> with this URI [chrome://version](chrome://version).

   (The first line is the version. e.g. 83.0.4103.116)

3. Download the chrome driver that matches <ins>the version</ins> and your device OS [here](https://chromedriver.chromium.org/downloads) and decompress it.

4. Check if JRE(or JDK) version is greater than or equal to 8 or install it.

5. Download the latest released lezhin-comics-downloader [here](https://github.com/ImSejin/lezhin-comics-downloader/releases).

6. Download config.ini [here](https://raw.githubusercontent.com/ImSejin/lezhin-comics-downloader/master/config.ini) and write your account in the file.

7. Place three files in the same path.

8. Use the following command to run the downloader.

<br><br>

### Usage

```cmd
java -jar {JAR filename} -l=<language> -n=<comic name> [-r=<episode range>]
```

- *<ins>id</ins>, <ins>password</ins> (required)*: your lezhin comics account, not account of third party platform.
- *<ins>language (required)</ins>*: language of lezhin platform you want to see.
  - **ko** : korean
  - **en** : english
  - **ja** : japanese
- *<ins>comic name</ins> (required)*: webtoon name you want to download.

<img width="350" alt="comic-name" src="https://user-images.githubusercontent.com/46176032/86545858-88c1d900-bf6c-11ea-9c14-64692abbee3a.png">

- *<ins>episode range</ins> (optional)*: range of episodes you want to download.
  - __skipped__ : all episodes
  - __n~__ : from ep.N to the last episode
  - __~n__ : from the first episode to ep.N
  - __m~n__ : from ep.M to ep.N

<br><br>

## Examples

```cmd
java -jar lezhin-comics-downloader.jar -l=en -n=appetite
```

Downloads all episodes of the comic named appetite.

<br>

```cmd
java -jar lezhin-comics-downloader.jar -l=en -n=appetite -r=8~
```

Downloads the episodes of the comic named appetite from ep.8 to the last.

<br>

```cmd
java -jar lezhin-comics-downloader.jar -l=en -n=appetite -r=~25
```

Downloads the episodes of the comic named appetite from the first to ep.25.

<br>

```cmd
java -jar lezhin-comics-downloader.jar -l=en -n=appetite -r=1~10
```

Downloads the episodes of the comic named appetite from ep.1 to ep.10.

<br><br>

## Dependencies

- Lombok
- Selenium
- Gson
- Progress Bar
- Ini4j
- Apache Commons CLI
