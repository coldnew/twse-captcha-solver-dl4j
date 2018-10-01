# twse-captcha-downloader
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/coldnew/twse-captcha-solver-dl4j/master/LICENSE)

A simple captcha image downloader based on [selenium](https://www.seleniumhq.org/) and [chromedriver](https://sites.google.com/a/chromium.org/chromedriver/).

## Requirement

- OpenJDK 8
- [chromedriver](https://sites.google.com/a/chromium.org/chromedriver/)

## Usage

Just type following command, and captcha images will be saved to `download` folder.

```
./gradlew run
```

After downloader finished, you will see the `download` folder conains some images

```
coldnew@gentoo ~/twse-captcha-solver-dl4j/twse-captcha-downloader $ ls download/
unknown_2ee43351-a386-44b9-bc6a-fa4545abebdf.png  unknown_d2fd9ee9-8f51-4e6a-9919-fb5f4bf08f08.png
unknown_3d5d92eb-c750-469d-8a74-d0bbbac134fc.png  unknown_df475b28-76e3-40d4-ad6c-3e6ac4903f6e.png
unknown_7d78192c-287c-49cc-921e-c6501c08a3de.png  unknown_eb0d1b23-a067-452f-a3a1-31ff105c5ac7.png
unknown_9915dada-c614-4bda-b13a-a16f11a7876a.png  unknown_f716ff47-ddd1-450d-bcad-e570aa2f7bc8.png
```
Rename these image to their captcha value.

Take `unknown_2ee43351-a386-44b9-bc6a-fa4545abebdf.png` as example, since it's value is `F4NLC`, you should rename it to `F4NLC.png`.

```
$ mv unknown_2ee43351-a386-44b9-bc6a-fa4545abebdf.png F4NLC.png
```

## Hint

This project is just an example on how to download the captcha. To download the captcha images more quickly, you can based on this project with [twse-captcha-solver-java](https://github.com/coldnew/twse-captcha-solver-java) to build a more efficiently downloader.

## License

Copyright © 2018 Yen-Chin, Lee <<coldnew.tw@gmail.com>>

Distributed under the [MIT License](http://opensource.org/licenses/MIT).
