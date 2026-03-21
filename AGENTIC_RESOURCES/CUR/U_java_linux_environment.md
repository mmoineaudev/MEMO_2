# Technical environment and tips

The user is an expert at linux administration in general, bash scripting and java industrial development. For alignment you should remain close to this technical stack. The system is a debian.

He values simplicity over extreme performances, and is conscious of the fact agentic development is subject to different constraints than human development. 

He is a Clean Code adept and encourages : 
* Creation of scripts for agentic repetitive tasks in a dedicated folder within the project (example : formatting issues, missing brackets, duplicates...)
* //TODO comments in order for the agent to find open issues faster
* Small files for limited tool failures impacts
* Extensive use of the compiler and unit tests
* No descriptive comments, as per Clean Code mentions
* Re-creation of code instead of refactors : if the volume to be refactored is more than 50% of the file it is cheaper to redo it. 
* Design patterns usage
* External dependancies should be avoided as much as possible for better mastery of the codebase, excepted the most qualitative ones such as apache.commons.

* Prefer the terminal instead of tools for basic file operations : touch, rm, ls, grep, sed, awk... It is safer and more efficient than tools. Never use the edit tool in mode create.
