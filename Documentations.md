# mad-bookreader
A book reader for an assignment.

Created by Kwong Ming Wei,Ryan and Arrick from Ngee Ann Poly

Used barkteksc PdfViewer v1.6.0: 
implementation 'com.github.barteksc:android-pdf-viewer:1.6.0'

-Week 3:
-
Planning of activities to be added into the book reader
· Library of books
· Read books
· Give ratings
· Set custom tags
· Search
· Advanced search (by tags etc)
· Most popular books read (by category?)
· Recently read
· Synopsis
· Sync
· Bookmark/Save book
· Social media sharing(like share a link to friends via whatsapp)
· Light/Dark mode

-Week 4:
-

-Division of features between part 1 and 2

-What to use to make this app possible: A few options include epublib, skyepub and epub3reader. For now, epublib and skyepub are more attractive, with documentation to help development. For now, epublib will be used. More information will be added when we understand more about the epub libraries for android.

Implementation done:
-Title bar
-Recycler view on main page
-CardView to store each book item
-Book Class

-Week 5:
-

-Attempted to import epublib files and an activity for reading the files but failed

Implementations done:
-Decided on using pdf files for our book reader instead
-import button created on title bar
-Failed attempt at importing files to assets folder of the application

-Week 6
-

Implementations done:
-PdfViewer from barkteksc working
-Continue reading the pdf book from previously left off (have not implemented database to store it yet)
-Importing of files from device into the app using Uri (have not implemented database to store it yet)
-Prompt users using AlertDialog with a EditText to manually enter the title of the book to display at the cardview

-Week 7
-

Implementations done:
-Added onclicklisteners to bring users to the pdf reader activity with intent
-Sqlite created to store Uri of files, title of book, image of book and last page read
-top app bar modified
