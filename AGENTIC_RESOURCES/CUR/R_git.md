Each time you open a checklist file, you should save current advancement in git. Stay on the same branch always, the user is responsible for the git flow : consider you are the only user of the repository. 

* Before committing: verify no unsaved files exist - all changes must be saved before proceeding
* Use the current checklist item label as the commit message, and enrich the commit message with the current advancement for this item. 
* Check the staging area for items to be added in .gitignore, add them to .gitignore if you find some.
* ```git add .``` should stage all documentation and code changes
* commit
* push to origin
* Stop in case you encounter a conflict and ask the user for resolution.