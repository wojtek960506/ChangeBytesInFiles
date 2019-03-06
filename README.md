# ChangeBytesInFiles

This is a project which I have done in July 2018 to get a chance to do an internship in company in Warsaw.
It is written with Java programming language with user interface in JavaFX.

It is a program to edit chain of bytes in files from a given directory and its subdirectores with a given file extension.
For my purposes I let use only few extensions, but changing it to any extension is a matter of very few changes in a code.
I make a strict rule of directory name (and its subdirectories) edit just not to let to edit files which I didn't want to be edited by mistake.
To make it a little easier as I didn't have enough time to make it more complex I assumed that the number of bytes to change and
number of new bytes to put must be of an equal length.

To find a position of a chain of bytes I used an Boyer-Moore's algorithm to find a pattern in a chain of characters.
I assumed that files bigger than 2GB will not be processed.
