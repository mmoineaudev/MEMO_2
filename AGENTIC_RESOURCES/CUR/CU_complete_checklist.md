# Main task

In current project directory there is a `AGENTIC_RESOURCES/checklist.md` file that contains specifications and current advancement of the project. 

* Your task is to find the first unchecked item in the checklist and proceed to it's completion
  * Once the item has been realized, verify carefully the coherence between specification and result produced. 
  * update `AGENTIC_RESOURCES/checklist.md` for tracability of advancement
    * New tasks are to be added if they are necessary and missing
    * blocking points should also be documented as unchecked items. 
    * Completed and verified tasks are to be updated as completed
    * Add the commit hash to any list item change for allowing targetted git operations
* Continue as long as they are still unchecked items.

## Task Fix Workflow

If an issue is identified with a completed checklist item:
1. Note the commit hash associated with the checklist item
2. Create a `[task]_fix` branch from that commit
3. Attempt the fix
4. Merge if fix succeeds
5. Document the fix in the checklist with the new commit hash
