# mad-bookreader

Used barkteksc PdfViewer v1.6.0: 
implementation 'com.github.barteksc:android-pdf-viewer:1.6.0'

# Stage 1
## All implementation problem faced
* epub (pushed to stage 2)
* Page change effect was not what we wanted (solved by downgrading the pdfviewer to 1.6.0)
* Scrolling direction issues (solved by reloading the pdfviewer every time the "Change scroll direction" option is selected)
* Grid layout items displayed per row 
(solved by making a function to calculate screen width and dividing by cardview size to determine number of items per row)
* Impossible to import files to "Assets" folder of the app (solved by getting and storing the uri of the file instead)
* Scrolling direction was not being saved properly i.e. exiting the app and re entering and loading up a pdf would not use the saved scrolling direction (solved)
* Last page read was loading incorrectly on change of the scrolling direction (solved)
* Certain intents did not put the extra string before sending to another activity
* Could not get bitmap of first page for thumbnail (solved by using pdfiumcore)
* App bar cannot hide and show on touch or click
* Delete book alert dialog would show edit book dialog (solved)
* Uri was not persistent throughout sessions, closing the app and reopening meant books could not be opened again (solved by making Uri persistent throughout reboots of phone)
* Could not delete entry from recycler view and database if app found that file no longer exists and cannot open to read it (due to the file path differences between Ryan and Ming Wei's and Arrick's)

## Week 3:
### Planning of activities to be added into the book reader
* Library of books
* Read books
* Give ratings
* Set custom tags
* Search
* Advanced search (by tags etc)
* Most popular books read (by category?)
* Recently read
* Synopsis
* Sync
* Bookmark/Save book
* Social media sharing(like share a link to friends via whatsapp)
* Light/Dark mode

## Week 4:
### Planning
* Division of features between part 1 and 2
* What to use to make this app possible: A few options include epublib, skyepub and epub3reader. For now, epublib and skyepub are more attractive, with documentation to help development. For now, epublib will be used. More information will be added when we understand more about the epub libraries for android.
### Decision of features to implement for stage 1
* Import function to import epublib books
* Recyclerview to display all the books
* Database to store book information
* Last read page saved
* Custom title bar with dropdown menu
* Scrolling methods
### Implementation done:
* Title bar
* Book Class

## Week 5:
### Implementations done:
* Recycler view on main page
* CardView to store each book item
* Attempted to import epublib files and an activity for reading the files but failed
* import button created on title bar

## Week 6:
### Decision:
* Failed attempt at importing files to assets folder of the application, therefore decided to use Uri instead
* Decided on using pdf files for our book reader instead since we had issues with using epublib
### Implementations done:
* PdfViewer from barkteksc working
* Continue reading the pdf book from previously left off (have not implemented database to store it yet)
* Importing of files from device into the app using Uri (have not implemented database to store it yet)
* Prompt users using AlertDialog with a EditText to manually enter the title of the book to display at the cardview

## Week 7:
### Implementations done:
* Added onclicklisteners to bring users to the pdf reader activity with intent
* Sqlite created to store Uri of files, title of book, image of book and last page read
* top app bar created for bookreadActivity.java
* Created 2 different methods to scroll when reading book, horizontal and vertical
* Created a page for settings and a new custom app bar
* Made grid view display number of items in a row depending on the width of the device
* Edit feature to edit title of book
* Deleting feature to delete book from the recyclerview
* Delete all books

## Week 8:
### Implemetations done:
* Presentation and submission of stage 1

# Stage 2
## All implementation problem faced
* Online pdf reader unable to get first page of the pdf as bitmap to display when import ( Decided to use a default image for all online pdf import)


## Week 9
### Implementations done:
* RecyclerView Title background color change when currently being read
* Online pdf reader implemented
* App icon

## Week 10
### Implementations done:
* Search option
* Go to page option

## Week 11
### Implementations done:
* Fixed small database error

## Week 12
### Implementations done:
* Completed online pdf reader with "go to page" and "page swipe direction" function with use of database
* Redid database functions

## Week 13
### Implementations done:
* Epub reader
* Redid page swipe direction into checkbox
* Sorting of books in recycler view by filetype filtering and title ascending/descending
* Fixing of app features
* Online pdf internet connected and internet connectivity checking

## Week 14
### Implementations done:
* About page
* Submission to google play
