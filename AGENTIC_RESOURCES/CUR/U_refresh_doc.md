# Anti-pollution phase : documentation

**Trigger**: This task should be run at the end of the first implementation pass, in order to clarify the context for the next evolution.

The goal of this task is to reduce the volume of uneeded or confusing documentation. In order to do this there is a procedure : 

**Important**: The code is considered the main criteria of truth. Documentation comes after and can be partially obsolete. If the user is satisfied with the code but documentation is not up to date with the changes, reading the documentation will lead to introduction of functional regression. This task should consider the code as primary source when refreshing documentation.

* 1 Identify all documentation files in the current project :
* 2 Rename those file by prefixing them with ```UNDER_REWORK-```
* 3 Plan carefully what are the necessary steps to be done for grouping documentation parts. he user wants improved readability and reduced volume.
  * Identify semantic duplicates, synonyms, redundancy. 
  * Provide a glossary of the project 
  * This plan of documentation rework is to be described in a checklist named ```AGENTIC_RESOURCES/refresh_doc.md``` : it contains bullet points, success and failure criterias, expected value gains per action. It should mention only creating new files instead of overwriting ```UNDER_REWORK-*``` files.
* 4 Attend this checklist until completion. 
* 5 After completion, all ```UNDER_REWORK-*``` files should be moved in a ```AGENTIC_RESOURCES/[YYYYMMDD]_documentation_refresh/``` directory.
