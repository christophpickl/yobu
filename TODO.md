
# TODOs

## NOW
* context menu entries: about (version number), show historic data
* swipe left/right for cheatsheets (images with bo/yu points)
* @BoPunctGenerator: when generating 1..3, each time the except list should grow (see also down below), otherwise there will be duplicates!
* @BoPunctGenerator: generate the random answers each time question is displayed!
* get sure in build that DEVELOPMENT is disabled

## High
* @QuestionsLoader: mehr BO fragen
* mehrere falsche antworten, aber nur beschraenkte anzahl zeigen (random); nuetzlich fuer custom (non-generated) questions
* add progress bar for highscore indicator at bottom of screen

## Med
* new activity: bo / yu punct table (info grafik; schummler)
* BoPunctDistributionItem: introduce special distribution type: use same meridian as "except" instance but different point
* display artifact version in app (global menu thingy)
* neuer frage typ: mit bildchen, wo man sagen muss welcher bo/yu punkt das ist auf grafik eingezeichnet

## Low
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