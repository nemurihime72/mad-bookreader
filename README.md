# Team members and roles
## MAD class T02, group 5
### Leader: 
* Ryan Lee Rui Yuan (S10195732)
### Member: 
* Kwong Ming Wei (S10195172)
* Arrick Tee Ren Yi (S10198673)

# Description of app

A book reader that imports pdf file of books and allows user to import and gather all their pdf books into one place which makes it more convenient for the user. 

# Contribution

## Kwong Ming Wei: 
* Pdf viewer
* App bar for the bookreadActivity.java
* Recyclerview
* Java class for each book
* CardView for each book item
* pdfViewer will open the book at the page last read
* Scrolling directions the book reader, allows users to change the direction of scrolling
* Grid layout, number of items per row depends on screen width
* Edit title of book after import
* Delete book
* Color change to green at recyclerview when book is currently being read
* Online pdf reader with import
* App icon
* Search option with search bar
* Go to page option
* Dark/Light mode

## Ryan Lee:
* Import function with alertdialog to manually enter title
* Importing of pdf files using Uri
* Importing of uri files
* Import pdf files with function to automatically retrieve and set cover image
* Import function automatically retrieves title from filename
* Importing book will set the first page of the file as cover page on the recyclerview

## Arrick:
* Settings page app bar
* All the sqlite
* Intent activities and onClickListeners

# User guide

## Importing a book
### Click on the import button
![Import button](https://i.imgur.com/dAQ1rRh.jpg)

### Select the pdf file
![Import button](https://i.imgur.com/JS9yrRk.jpg)

### Set title
![Import button](https://i.imgur.com/aN2KtBH.jpg)

## Editing and Deleting
### Edit or delete existing book title
![Import button](https://i.imgur.com/WOUft9m.jpg)
### Delete all books(To be updated)
![Import button]()

## Change scroll direction when reading(To be updated)
![Import button]()

# Future plans
## Main Activity
* Implement an EPUB reader (hopefully)
* Implement a search bar ( done )
* Settings option 
* Edit the cover image of book in recyclerview
* Sorting of books by name,date added, last read

## Bookread activity
* Screen rotation
* Allows user to jump to a page they want( done )

## Settings activity
* Light/Dark mode ( done )
