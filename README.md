# git-project-cyang
hi mr theiss

For stretch goal 1-1 edge cases I considered if the file did not exist for cleanup then I wouldn't need to cleanup that file


Bugs I found (5.1)
- no newline in tree files and from rebuildindex, used .trim()
- I implemented checkout in git.java instead of gitwrapper so i could call static method in verify2 and gitwrapper, didnt change the code
- first commit had parent: as empty line, added the check
- parentDirectory in createtree and from buildroottree did not work because it would be null for the root and not path of "", so i added that check
- buildroottree() had infinite loop, because it would try to get it all to one folder for some reason and checked for only one folder but if you have multiple folders in the root directory it would break my computer because it kept making files. there was also no check for it to be the maxdepth in buildroottree, so i made it where it calls creatree from the root ("" directory). It broke my computer multiple times before I figured the issue out