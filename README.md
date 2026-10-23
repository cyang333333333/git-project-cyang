# git-project-cyang
hi mr theiss

For stretch goal 1-1 edge cases I considered if the file did not exist for cleanup then I wouldn't need to cleanup that file


Bugs I found (5.1)
- no newline in tree files and from rebuildindex, used .trim()
- I implemented checkout in git.java instead of gitwrapper so i could call static method in verify2 and gitwrapper, didnt change the code
- first commit had parent: as empty line, added the check
- parentDirectory in createtree and from buildroottree did not work because it would be null for the root and not path of "", so i added that check
- buildroottree() had infinite loop, because it would try to get it all to one folder for some reason and checked for only one folder but if you have multiple folders in the root directory it would break my computer because it kept making files. there was also no check for it to be the maxdepth in buildroottree, so i made it where it calls creatree from the root ("" directory). It broke my computer multiple times, with infinite text files


5.2
- changed init to create git/refs/ and git/refs/heads/, and writes ref: refs/heads/main into HEAD
- i made a helper method getCurrentCommit which just looks in head and looks a the thing after refs: which is the path and looks thru that path file and reads that for the hash of the commit, if its empty just returns "" becaues there is no commit yet, also checks if head is detatched then it just writes that hash
- in commit, i made it so parent comes from this getcurrentcommit, if getcurrentcommit is "" then no parent line, i also added it so it writes the new hash into the branch file that HEAD names, creating the file if needed, if its detatched again it will just write the hash
- i added createbranch, the name basically it just creates the branch in git/refs/heads
- in checkout, i changed the commitHash arg to input because it can either be a branch or commit, it checks for branch or commit if they exist, and if its a branch i write to the head, if commit its just the hash
    -  i also did the stretch goal and what i did was when it was neither a existing branch or commit i made a new branch for the current commit and set head to ref: refs heads and the input, then it doesnt do anything after that