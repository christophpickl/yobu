
# TODOs

## NOW
* cheatsheet v2: eigene grafiken machen fuer bo/yu punkte

## High
* add progress bar for highscore indicator at bottom of screen
* TECH: rewrite MainActivity with anko

## Med
* swipe left/right for cheatsheets (images with bo/yu points)
* mehrere falsche antworten, aber nur beschraenkte anzahl zeigen (random); nuetzlich fuer custom (non-generated) questions
* new question type: mit bildchen, wo man sagen muss welcher bo/yu punkt das ist auf grafik eingezeichnet
* new question type: freetext (dynamic question renderer pro type)

## Low
* MainActivity has leaked window
* support DE und EN
* @question generation: generate the random answers each time question is displayed, not only once at app startup!
* menu entry: about (version)
* menu entry for development: display all questions (including statistics for it)
* could display the back button in top panel where the context menu is in main activity
* on back for MainActivity -> exit app

## Tech
* auto-release script (set version, tag on github, create apk and deploy somewhere (github release?!), increment version)
* travis build
* ORM:
** PultusORM: https://github.com/s4kibs4mi/PultusORM
** DBFlow: https://github.com/Raizlabs/DBFlow
