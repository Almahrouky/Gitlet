# Gitlet
- Gitlet is a version-control system that mimics some of the basic features of the popular system Git.
## The main functionality that Gitlet supports is:
1. Saving the contents of the entire directory of files. In Gitlet this is called committing.
2. Restoring a version of one or more files. In Gitlet this is called checkout.
3. Viewing the history of your backups. In Gitlet this is called log.
## The supported commands:
- init  <=== Creates a new Gitlet version-control system in the current directory.
- add   <=== Adds a copy of the file as it currently exists to the staging area.
- commit    <=== Saves a snapshot of tracked files in the current commit and staging area.
- log without merging   <=== Displays information about each commit starting with the current head commit and ends with the initial commit.
- checkout -- [file name]   <=== Takes the version of the file as it exists in the head commit and puts it in the working directory.
- checkout [commit id] -- [file name]   <=== Takes the version of the file as it exists in the commit with the given id and puts it in the working directory.
## File hierarchy structure of the .gitlet directory:
```
.
|--- HEAD                    <=== file contain the current branch name
|--- cur_commit              <=== file contain the sha1 of the current commit
|--- stagingArea             <=== file contain object collects the tracked files
|--- objects                 <=== directory contain objects like commit
|    |--- sha1 (6 digits)    <=== directory has the commit which has the sha1 starts withe the 6 digits
|         |--- sha1          <=== file called the hole sha1 of the commit and has the object inside
|--- branches                <=== directory has the branches name
|    |--- master             <=== file contain the lastest commit in the branch called master as an example
```
## Source:
- CS 61B Data Structures course by UC Berkeley.