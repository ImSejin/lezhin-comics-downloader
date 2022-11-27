<p align="center">
    <img alt="Lezhin Comics Downloader" src="./src/main/resources/assets/lezhin-comics-downloader-logo.png" width="20%">
</p>

<h1 align="center">Lezhin Comics Downloader</h1>

<p align="center">Downloader for lezhin comics</p>

<p align="center">
    <a href="https://lgtm.com/projects/g/ImSejin/lezhin-comics-downloader/context:java">
        <img alt="Lgtm grade" src="https://img.shields.io/lgtm/grade/java/github/ImSejin/lezhin-comics-downloader.svg?logo=&logoWidth=&label=lgtm%3A%20code%20quality&&style=flat-square"/>
    </a>
    <a href="https://app.codacy.com/gh/ImSejin/lezhin-comics-downloader/dashboard">
        <img alt="Codacy grade" src="https://img.shields.io/codacy/grade/1a2400c31a8346ddbf108fb3ac78f481?label=codacy%3A%20code%20quality&style=flat-square">
    </a>
    <img alt="jdk8" src="https://img.shields.io/badge/jdk-8-orange?style=flat-square">
    <br/>
    <img alt="GitHub All Releases" src="https://img.shields.io/github/downloads/imsejin/lezhin-comics-downloader/total?style=flat-square">
    <img alt="GitHub Releases" src="https://img.shields.io/github/downloads/imsejin/lezhin-comics-downloader/latest/total?style=flat-square">
    <a href="https://github.com/ImSejin/lezhin-comics-downloader/releases/latest">
        <img alt="Latest release" src="https://img.shields.io/github/v/release/ImSejin/lezhin-comics-downloader?color=orange&style=flat-square">
    </a>
</p>

# Preview

<img alt="preview" src="./src/main/resources/assets/preview.gif">

<p align="center">This is downloader that helps you to login and downloads the specified comic for all lezhin-comics even adults.</p>
<p align="center">※ <i>The user is responsible for everything that happens using this program.</i></p>
<br><br>

# Getting started

## Pre-requirements

1. Check if chrome browser was installed in your device or download it [here](https://www.google.com/chrome).

2. Check your <ins>chrome browser version</ins> with this URI `chrome://version`.

   (The first line is the version. e.g. 83.0.4103.116)

3. Download the `chrome driver` that matches <ins>its version</ins> and your device OS [here](https://chromedriver.chromium.org/downloads) and decompress it.

4. Check if JRE(or JDK) version is greater than or equal to 8 or install it.

5. Download the latest released `lezhin-comics-downloader.jar` [here](https://github.com/ImSejin/lezhin-comics-downloader/releases).

6. Download `config.ini` [here](https://raw.githubusercontent.com/ImSejin/lezhin-comics-downloader/master/config.ini) and write your account in the file.

7. Place three files in the same path.

8. Use the following command to run the downloader.

<br><br>

## Usage

```bash
java -jar {JAR filename} -l=<locale_language> -n=<content_name> [-r=<episode_range> -j -d]
```

- *<ins>locale language</ins> (required)*: language of lezhin platform you want to download the webtoon on.
  
  - **ko** : korean
  - **en** : english
  - **ja** : japanese
  
- *<ins>content name</ins> (required)*: webtoon name you want to download.

<p>
    <img alt="comic name" src="./src/main/resources/assets/comic-name.png">
</p>

- *<ins>episode range</ins> (optional)*: range of episodes you want to download.
  - __skipped__ : all episodes
  - __n~__ : from ep.N to the last episode
  - __~n__ : from the first episode to ep.N
  - __m~n__ : from ep.M to ep.N
- <ins>jpg</ins> (optional): save images as JPEG format (default: WEBP format).
- <ins>debug</ins> (optional): enables debugging mode.

<br><br>

# Examples

```bash
java -jar lezhin-comics-downloader.jar -l=en -n=appetite
```

Downloads all episodes of the comic named appetite.

<br>

```bash
java -jar lezhin-comics-downloader.jar -l=en -n=appetite -r=8~
```

Downloads the episodes of the comic named appetite from ep.8 to the last.

<br>

```bash
java -jar lezhin-comics-downloader.jar -l=en -n=appetite -r=~25
```

Downloads the episodes of the comic named appetite from the first to ep.25.

<br>

```bash
java -jar lezhin-comics-downloader.jar -l=en -n=appetite -r=1~10
```

Downloads the episodes of the comic named appetite from ep.1 to ep.10.

<br>

<br>

# Build

```bash
./mvnw package
```

Then you will get a file `lezhin-comics-downloader-{version}.jar`.

