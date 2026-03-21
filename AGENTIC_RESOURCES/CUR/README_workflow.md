# CUR Workflow Master Guide

This document explains the orchestration of all CUR prompts in your bash-to-Java Swing transcoding project. Follow this workflow to ensure efficient agentic development.

---

## Workflow Overview Diagram

```
┌─────────────────┐
│  User Input     │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│  PHASE 1: INITIALIZATION                    │
│  ────────────────────────────────────────── │
│  • CR_ANY_to_journaling.md                  │
│    → Creates AGENTIC_RESOURCES/checklist.md          │
└─────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│  PHASE 2: EXECUTION LOOP                   │
│  ────────────────────────────────────────── │
│  • CU_complete_checklist.md                            │
│    → Find first unchecked item              │
│    → Complete task                          │
│    → Update checklist                       │
│  • R_git.md                                 │
│    → Commit after each item                 │
│  • U_troubleshooting.md                          │
│    → Log troubleshooting when issues occur  │
└─────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│  PHASE 3: DOCUMENTATION REFRESH             │
│  ────────────────────────────────────────── │
│  • U_refresh_doc.md                         │
│    → Run after first implementation pass    │
│    → Clean up obsolete documentation        │
└─────────────────────────────────────────────┘
```

---

## Phase 1: Initialization

### When to Use: `CR_ANY_to_journaling.md`

**Trigger**: New user input describing a feature, bug fix, or project goal.

**Purpose**: Convert unstructured user input into a structured use case checklist.

**Process**:
1. Read user input
2. Analyze overall goal and scope
3. Break down into numbered use cases with:
   - Implementation steps
   - Test steps
   - Characteristic information (goal, scope, preconditions, success/failure conditions)
   - Main success scenario
   - Extensions and sub-variations
4. Write output to `AGENTIC_RESOURCES/checklist.md`

**Success Criteria**:
- Each use case has at least one concrete action item
- All user input features map to at least one use case
- Checklist is sufficient to create the entire expected solution

**Next Step**: Proceed to Phase 2 (Execution Loop)

---

## Phase 2: Execution Loop

### Primary Tool: `CU_complete_checklist.md`

**Trigger**: Checklist exists at `AGENTIC_RESOURCES/checklist.md` with unchecked items.

**Purpose**: Execute checklist items sequentially until completion.

**Process**:
1. Open `AGENTIC_RESOURCES/checklist.md`
2. Find the first unchecked item
3. Complete the task
4. Verify coherence between specification and result
5. Update checklist for traceability:
   - Add new tasks if necessary and missing
   - Document blocking points as unchecked items
   - Mark completed and verified tasks as done
6. Continue while unchecked items remain

**Before Starting Each Task**:
- Consult `AGENTIC_RESOURCES/troubleshooting.json` for known issues

**After Completing Each Task**:
- Execute `R_git.md` workflow (see below)

### Integration: `R_git.md`

**Trigger**: After completing each checklist item in `CU_complete_checklist.md`.

**Purpose**: Maintain version control with traceable commits.

**Process**:
1. Verify no unsaved files exist
2. Use current checklist item label as commit message
3. Enrich commit message with current advancement details
4. Check staging area for items to add to `.gitignore`
5. Execute `git add .`
6. Commit
7. Push to origin
8. Stop and ask user for resolution if conflict occurs

**Git Branch Strategy**:
- Stay on the same branch always (user manages git flow)
- Consider you are the only user of the repository
- Linear git log expected (no branching strategy needed for normal commits)

**Task Fix Workflow** (if issue identified):
1. Note the commit hash associated with the checklist item
2. Create a `[task]_fix` branch from that commit
3. Attempt the fix
4. Merge if fix succeeds

### Integration: `U_troubleshooting.md`

**Trigger**: When encountering unexpected tool behavior or operational issues.

**Purpose**: Maintain troubleshooting knowledge base for long-term efficiency.

**Process**:
1. Check `AGENTIC_RESOURCES/troubleshooting.json` exists
2. If absent: `mkdir -p AGENTIC_RESOURCES/ && touch AGENTIC_RESOURCES/troubleshooting.json`
3. Add skill entry with:
   - `description`: High-level problem description
   - `detection`: How to detect the problem
   - `type`: Root cause category (methodology, planning, command line, tool usage, code design, etc.)
   - `solutioning`: Best mitigation approach
   - `frequency_encountered`: Counter (start at 1)
   - `last_updated`: ISO8601 timestamp
   - `related_skills`: Array of related skill IDs
4. Check for duplication before adding; merge similar skills
5. Aim for genericity over precision

**File Location**: `AGENTIC_RESOURCES/troubleshooting.json`

### Reference: `U_java_linux_environment.md`

**Trigger**: Consult throughout Phase 2 for technical alignment.

**Purpose**: Maintain consistency with user's technical preferences.

**Key Guidelines**:
- User is expert in Linux administration, bash scripting, and Java industrial development
- System is Debian
- Value simplicity over extreme performance
- Agentic development has different constraints than human development
- Clean Code principles apply
- Create scripts for agentic repetitive tasks in dedicated folder
- Use `//TODO` comments for open issues
- Prefer small files to limit tool failure impact
- Use compiler and unit tests extensively
- No descriptive comments (per Clean Code)
- Re-create code instead of refactoring if >50% of file needs changes
- Use design patterns
- Avoid external dependencies except high-quality ones (e.g., Apache Commons)

---

## Phase 3: Documentation Refresh

### When to Use: `U_refresh_doc.md`

**Trigger**: At the end of the first implementation pass, when user is satisfied with code but documentation may be outdated.

**Purpose**: Reduce volume of unneeded or confusing documentation to prevent functional regression.

**Process**:
1. Identify all documentation files in current project
2. Rename files by prefixing with `UNDER_REWORK-`
3. Plan documentation rework steps:
   - Identify semantic duplicates, synonyms, redundancy
   - Provide glossary of the project
4. Create checklist at `AGENTIC_RESOURCES/refresh_doc.md` containing:
   - Bullet points
   - Success and failure criteria
   - Expected value gains per action
   - Only create new files (do not overwrite `UNDER_REWORK-*` files)
5. Execute checklist until completion
6. Move all `UNDER_REWORK-*` files to `AGENTIC_RESOURCES/[YYYYMMDD]_documentation_refresh/`

**Important Note**: Code is the main criterion of truth. Documentation comes after and can be partially obsolete. The goal is to align documentation with actual code state to prevent introduction of functional regression.

---

## File Locations Summary

| File | Purpose | Location |
|------|---------|----------|
| Checklist | Use case breakdown | `AGENTIC_RESOURCES/checklist.md` |
| Troubleshooting | Problem solutions | `AGENTIC_RESOURCES/troubleshooting.json` |
| Doc Refresh Plan | Documentation cleanup tasks | `AGENTIC_RESOURCES/refresh_doc.md` |
| Archived Docs | Old documentation | `AGENTIC_RESOURCES/[YYYYMMDD]_documentation_refresh/` |

---

## Quick Reference Decision Tree

```
Start
  │
  ├─ New user input?
  │   └─ Yes → Use CR_ANY_to_journaling.md
  │
  ├─ Checklist has unchecked items?
  │   └─ Yes → Use CU_complete_checklist.md
  │
  ├─ Task completed?
  │   └─ Yes → Use R_git.md
  │
  ├─ Unexpected problem encountered?
  │   └─ Yes → Use U_troubleshooting.md
  │
  ├─ First implementation pass complete + docs outdated?
  │   └─ Yes → Use U_refresh_doc.md
  │
  └─ Need technical alignment?
      └─ Consult U_java_linux_environment.md
```

---

## Notes for Orchestration

1. **Sequential Execution**: Phases should be executed in order, though Phase 2 may cycle multiple times.

2. **State Persistence**: The checklist at `AGENTIC_RESOURCES/checklist.md` is the single source of truth for project advancement.

3. **Git Integration**: Every checklist item completion should trigger a git commit with the item label as the message.

4. **Troubleshooting First**: Before attempting any task, check `troubleshooting.json` for known solutions.

5. **Documentation as Secondary**: Code is the source of truth; documentation refresh should align docs with code, not vice versa.

6. **No Deletion Operations**: The CUR naming convention intentionally excludes Delete (D) operations. Re-creation is preferred over deletion.
