# mydic-project5

Electron 사전 프로젝트

## Language/ Framework
Electron + ClojureScript
Re-frame

## Build/ Preprocessor
leiningen, cljsbuild, garden (css)

## development
figwheel과 lein-garden 이용함

### garden autobuild
mydic.styles.core의 main-css를
resources/public/css/style.css로 자동 빌드
```sh
lein garden auto
```

### figwheel

3가지 방법이 있음
1. Figwheel 실행
```sh
lein figwheel main-dev renderer-dev
```

2. nrepl + figwheel
editor에서 nrepl을 활용하기 위함
```sh
lein repl
```
```clojure
(start-figwheel!)
(autobuild "renderer-dev")
(fg/cljs-repl "renderer-dev")
```

3. CIDER
Emacs CIDER에서 자동으로 실행하는 방법

### Production

추가 예정...

# LICENSE
MIT
