
# TODOs

## NOW
* get sure in build that DEVELOPMENT is disabled
* mehr (Bo) fragen, ausfeilen
* (semi) automate release: in constants.kt and build.gradle

## High
* cheatsheet v2: eigene grafiken machen fuer bo/yu punkte
* fine tuned distribution a la zettel: ad BoPunctDistributionItem
* mehrere falsche antworten, aber nur beschraenkte anzahl zeigen (random); nuetzlich fuer custom (non-generated) questions

## Med
* MainActivity has leaked window
* settings menu entry: reset data, immediate response, (sequential questions?)
* add progress bar for highscore indicator at bottom of screen
* new activity: bo / yu punct table (info grafik; schummler)
* BoPunctDistributionItem: introduce special distribution type: use same meridian as "except" instance but different point
* display artifact version in app (global menu thingy)
* neuer frage typ: mit bildchen, wo man sagen muss welcher bo/yu punkt das ist auf grafik eingezeichnet

## Low
* @question generation: generate the random answers each time question is displayed, not only once at app startup!
* swipe left/right for cheatsheets (images with bo/yu points)
* DEV action to execute a specific quesiton (by id/list to select it)
* new question type: freetext (dynamic question renderer pro type)
* menu entry: about (version)
* menu entry for development: display all questions (including statistics for it)
* support DE und EN
* could display the back button in top panel where the context menu is in main activity

## Tech
* introduce kodein for DI
* auto-release script (set version, tag on github, create apk and deploy somewhere (github release?!), increment version)
* check for DI framework in kotlin+android => testability of rand stuff
* travis build
* investigate anko (persistence, intents, programmatic UI; including anko androidstudio plugin)
* ORM:
** PultusORM: https://github.com/s4kibs4mi/PultusORM
** DBFlow: https://github.com/Raizlabs/DBFlow