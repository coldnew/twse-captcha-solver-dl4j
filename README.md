# twse-captcha-solver-dl4j
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/coldnew/twse-captcha-solver-dl4j/master/LICENSE)

A [deeplearning4j](https://deeplearning4j.org/) based captcha solver for for Taiwan Stock Exchange's [website](http://bsr.twse.com.tw/bshtm/).

Compared with my another project [twse-captcha-solver-java](https://github.com/coldnew/twse-captcha-solver-java), which was based on OCR, this captcha solver will have more accurate result.

-----

## Usage

This project is built with three subprojects:

- [twse-captcha-downloader](https://github.com/coldnew/twse-captcha-solver-dl4j/tree/master/twse-captcha-downloader)

  A simple captcha image downloader based on [selenium](https://www.seleniumhq.org/) and [chromedriver](https://sites.google.com/a/chromium.org/chromedriver/). This is the first step to build your own datasets.

- [twse-captcha-machine-learning](https://github.com/coldnew/twse-captcha-solver-dl4j/tree/master/twse-captcha-machine-learning)

  After collect enough captcha images, feed these datasets to this project and get the training model.

- [twse-captcha-solver](https://github.com/coldnew/twse-captcha-solver-dl4j/tree/master/twse-captcha-solver)

  Use the training model generated from `twse-captcha-machine-learning` and create your own captcha solver.

For more info, please see each project's README.

## Q&A

#### Q: Is this project really work ?

Yes, this project is part of my side project. However, you should build your own dataset to training the captcha solver.

#### Q: Will you share your training datasets?

No, the datasets I have will only be released public after Taiwan Stock Exchange's [website](http://bsr.twse.com.tw/bshtm/) change their's captcha algorithm.
Currently I have no plan to release these datasets, if you really can't build your own, you can ask for `commercial support` :).

#### Q: Why opensource this project ?

Just for fun :P

## License

Copyright © 2018 Yen-Chin, Lee <<coldnew.tw@gmail.com>>

Distributed under the [MIT License](http://opensource.org/licenses/MIT).
