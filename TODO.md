
# TODOs

## NOW
* recreate state properly
* question choose algorithm; store in DB, each question needs an ID, store in table:
** question: id, count_correct, count_wrong, last_correct:Date?, last_wrong:Date? => create complex SQL query translating into points/weighting
* question catalog LAGE fuer alle punkte

## High
* mehr als 3 falsche antwort ermoeglichen aber nur 3 random nehmen/limitten
* answers jedesmal beim question aufruf shufflen
* mehrere falsche antworten, aber nur beschraenkte anzahl zeigen (random)

## Med
* new activity: bo / yu punct table (info grafik)
* display artifact version in app (global menu thingy)
* travis build
* check for DI framework in kotlin+android => testability of rand stuff
* add progress bar for highscore indicator at bottom of screen

## Low
* load questions catalog from web
* new question type: freetext (dynamic question renderer pro type)
* DE und EN
* fragengruppen machen: anatomie, meridian, punkte (leicht/mittel/schwer)
* auto-release script (set version, tag on github, create apk and deploy somewhere (github release?!), increment version)
``