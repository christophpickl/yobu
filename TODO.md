
# TODOs

## NOW
* question choose algorithm; store in DB, each question needs an ID, store in table:
** question: id, count_right, count_wrong, last_right:Date?, last_wrong:Date? => create complex SQL query translating into points/weighting
* recreate state properly
* question catalog LAGE fuer alle punkte

## High
* mehr als 3 falsche antwort ermoeglichen aber nur 3 random nehmen/limitten
* answers jedesmal beim question aufruf shufflen
* mehrere falsche antworten, aber nur beschraenkte anzahl zeigen (random)

## Med
* neuer frage typ: mit bildchen, wo man sagen muss welcher bo/yu punkt das ist auf grafik eingezeichnet
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