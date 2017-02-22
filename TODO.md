
# TODOs

## NOW
* investigate google play store
* automate release: enter release + dev version, write to file, git commit+tag, build APK and store in local dir (extended: create release in github, deploy to playstore)
* get sure in build that DEVELOPMENT is disabled
* mehr (Bo) fragen, ausfeilen

## High
* cheatsheet v2: eigene grafiken machen fuer bo/yu punkte
* fine tuned distribution a la zettel
* mehrere falsche antworten, aber nur beschraenkte anzahl zeigen (random); nuetzlich fuer custom (non-generated) questions
* Question.shortLabel einfuehren, was in der StatActivity rendered wird
* wenn in MainActivity back button drueckt soll app geschlossen werden (stat activity quasi nicht am stack)

## Med
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
* DE und EN

## Tech
* for testing anko UIs, do this: id = View.generateViewId()
* auto-release script (set version, tag on github, create apk and deploy somewhere (github release?!), increment version)
* check for DI framework in kotlin+android => testability of rand stuff
* travis build
* investigate anko (persistence, intents, programmatic UI; including anko androidstudio plugin)
* ORM:
** PultusORM: https://github.com/s4kibs4mi/PultusORM
** DBFlow: https://github.com/Raizlabs/DBFlow