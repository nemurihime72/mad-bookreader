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
* Online pdf reader import
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
* Sorting of books in recycler view

# User guide

## Importing a book
### Click on the import button
![Import button](https://i.imgur.com/zJa9sul.jpg)

### Select the type of book to import
![Import button](https://i.imgur.com/QssRfJl.png)

### Select books (for pdf and epub books)
![Import button](https://i.imgur.com/HCUbgHp.png)

### Set title (for pdf and epub books)
![Import button](https://i.imgur.com/kZRv6eN.png)

### Import online pdf books
![Import button](https://i.imgur.com/UVnpanF.png)

## Editing and Deleting
### Edit or delete existing book title
![Import button](https://i.imgur.com/K03KEqa.png)

## Pdf/Online pdf guide
### Change scroll direction when reading or go to a certain page(For pdf and online pdf books)
![Import button](https://i.imgur.com/QlPgO3P.png)

## Epub guide
![Import button](https://i.imgur.com/il0LzTU.png)
### Changing to dark theme for epub books
###### Click on the moon icon at the bottom of the reader

### Going to another chapter for epub books
###### Click on the menu icon at the bottom of the reader
![Import button](https://i.imgur.com/LnxNbHA.png)

## Other features
### Delete all books/ Settings page / About page / Sorting of books
![Import button](https://i.imgur.com/tLORhC8.png)
### Dark mode as app theme
###### Click Settings
![Import button](https://i.imgur.com/u7TKWmR.png)
### Sort
![Import button](https://i.imgur.com/rkV3cEV.png)
#### Sort by title
![Import button](https://i.imgur.com/feWvReg.png)
#### Sort and filter by file type
![Import button](https://i.imgur.com/TqxYYr9.png)


# Future plans
## Main Activity
* Implement an EPUB reader (hopefully) (done)
* Implement a search bar ( done )
* Settings option (done)
* Edit the cover image of book in recyclerview (not planning to do)
* Sorting of books by name, last read (done)

## Bookread activity
* Screen rotation (not doing)
* Allows user to jump to a page they want( done )

## Settings activity
* Light/Dark mode ( done )
