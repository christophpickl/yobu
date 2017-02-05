# yobu
Simple Shiatsu quiz app for checking your knowledge of bo and yu points

* spielprinzip: KO prinzip, soviele fragen solang richtig, dann geht counter hoch, und max counter ist highscore


# TODOs

## NOW
* app auf handy zum laufen bekommen

## High
* answers shufflen
* persistenz layer (highscore, fuer advanced question algorithm)
* start riddle immediately (no home screen)
* add progress bar for highscore indicator at bottom of screen

## Med
* travis build
* fragenkatalog in JSON/XML (oder evtl codegen? oder evtl from web, damit nicht neue app version deployen muss)
* mehrere falsche antworten, aber nur beschraenkte anzahl zeigen (random)
* display artifact version in app

## Low
* DE und EN
* fragengruppen machen: anatomie, meridian, punkte (leicht/mittel/schwer)
* auto-release script (set version, tag on github, create apk and deploy somewhere (github release?!), increment version)
