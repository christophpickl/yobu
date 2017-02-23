
# TODOs

## NOW
* WIP Question.shortLabel einfuehren, was in der StatActivity rendered wird
* cheatsheet v2: eigene grafiken machen fuer bo/yu punkte
* wenn in MainActivity back button drueckt soll app geschlossen werden (stat activity quasi nicht am stack)

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
* for testing anko UIs, do this: id = View.generateViewId()
* auto-release script (set version, tag on github, create apk and deploy somewhere (github release?!), increment version)
    * automate release: enter release + dev version, write to file, git commit+tag, build APK and store in local dir (extended: create release in github, deploy to playstore)
* travis build
* ORM:
** PultusORM: https://github.com/s4kibs4mi/PultusORM
** DBFlow: https://github.com/Raizlabs/DBFlow
