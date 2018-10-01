# twse-captcha-machine-learning
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/coldnew/twse-captcha-solver-dl4j/master/LICENSE)

A [deeplearning4j](https://deeplearning4j.org/) based captcha training tool for for Taiwan Stock Exchange's [website](http://bsr.twse.com.tw/bshtm/).

## Usage

In `src/main/resources`, there are three folders, each one should have different datasets:

- test

  Copy some images from `train` set, this set is mainly to determine whether it's good that the model fited traing data.

- train

  Copy most of your captcha images here, this datasets is used for train model.

- valid

  This is varification set, put the captcha images not in `traing` set to determine the model is good or bad. 

After build your own set, just run

```
gradle run
```

And wait for the training result. You may see result like belows:

```
validate result : sum count =1346 correct count=1295
```

Copy the `out/model.zip` to [twse-captcha-solver](https://github.com/coldnew/twse-captcha-solver-dl4j/tree/master/twse-captcha-solver)'s resource directory and build your solver.

## UI Server

You can connect to http://localhost:9000 to sea the training result

![ui server 1](https://raw.githubusercontent.com/coldnew/twse-captcha-solver-dl4j/master/screenshots/dl4j_ui1.png)
![ui server 2](https://raw.githubusercontent.com/coldnew/twse-captcha-solver-dl4j/master/screenshots/dl4j_ui2.png)


## License

Copyright © 2018 Yen-Chin, Lee <<coldnew.tw@gmail.com>>

Distributed under the [MIT License](http://opensource.org/licenses/MIT).

