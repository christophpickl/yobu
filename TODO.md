
# TODOs

## NOW
* BoPunkt questions automatisch generieren (daten modell fuer jeden MainMeridian inklusive PunctCoordinate dafuer)
* question catalog LAGE fuer alle punkte
* context menu entries: reset, about (version)

## High
* answers jedesmal beim question aufruf shufflen

## Med
* mehrere falsche antworten, aber nur beschraenkte anzahl zeigen (random)
* new activity: bo / yu punct table (info grafik; schummler)
* display artifact version in app (global menu thingy)
* travis build
* check for DI framework in kotlin+android => testability of rand stuff
* add progress bar for highscore indicator at bottom of screen

## Low
* neuer frage typ: mit bildchen, wo man sagen muss welcher bo/yu punkt das ist auf grafik eingezeichnet
* load questions catalog from web
* new question type: freetext (dynamic question renderer pro type)
* DE und EN
* fragengruppen machen: anatomie, meridian, punkte (leicht/mittel/schwer)
* auto-release script (set version, tag on github, create apk and deploy somewhere (github release?!), increment version)


## Tech
* investigate anko (persistence, intents, programmatic UI; including anko androidstudio plugin)
* ORM:
** PultusORM: https://github.com/s4kibs4mi/PultusORM
** DBFlow: https://github.com/Raizlabs/DBFlow