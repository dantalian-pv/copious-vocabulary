Copious Vocabulary
===================

# Description
An application for your daily vocabulary training

# Software requirements
To run the application `jvm` (`java`, `jre`) version 8.0 or higher is needed

# Building from source

## Requirements

* `java` version at least 1.8 https://java.com/ru/download/
* `maven` version at least 3.5 https://maven.apache.org/download.cgi
* `git` - (optional) to download sources

## Compilation steps

* Download source by one of the ways
  * `$> git clone https://github.com/dantalian-pv/copious-vocabulary.git`
  * Or download using any web-browser from https://github.com/dantalian-pv/copious-vocabulary/archive/master.zip
* Go into `copious-vocabulary` directory
* Run `$> mvn clean package -Pprod`

## Running application locally

For local use after successful compilation copy archive from `copious-vocabulary/ui/target/copious-vocabulary-ui-0.0.1.tar.gz` to any directory and extract it. The go into extracted directory and run 
`bin/copious-vocabulary-ui` for Linux or `bin/copious-vocabulary-ui.bat` for Windows.

After initialization process a web-page should be opened in a default web-browser, if it does not happen just open `https://localhost:8443` in your favorite web-browser.

Confirm (or add) SSL certificate exception, if needed.

Default username and password are user:user.

## Usage

* Login with username and password (default user:user)
* Click "Add Vocabulary" button on top of the page
* In appeared form enter vocabulary name and select source and target languages
* Then click "Save". Just created vocabulary will appear in the list
* Click on name of just created vocabulary
* Click "Edit Cards" on opened vocabulary
* Click "Add Card"
* Fill up the form. The application **will offer you list of examples** while you are typing to fill up the form automatically, which should simplify the process
* When you finished with editing card go back to vocabulary by clicking "Back to <Vocabulary name>"
* Then you can click "Show Cards" or "Training"

### Show Cards

On this page you can list an memorize cards. If you forgot translation of a certain word just click on "Show Answer" to see a prompt.

### Training

On training page you can check yourself. On this page you will see a word and an example and you need to type a traslation in "Answer" filed and click "Check". If your answer was correct, next word will be shown.
