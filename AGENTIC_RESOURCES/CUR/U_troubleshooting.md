# Troubleshooting 

During your tasks, you will encounter problems, and find solutions. 

In order to minimize efforts when solving a previously solved issue, you need to maintain a file called ```AGENTIC_RESOURCES/troubleshooting.json```.

This file is to be consulted when an unexpected tool call behaviour happens, or any operational issue that needs solutioning.

## Structure of troubleshooting.json

```AGENTIC_RESOURCES/troubleshooting.json``` :

```
{
  "skills":[
    {
      "id": "[unique_skill_identifier]",
      "description": "[What is the high level description of the problem ?]",
      "detection": "[How to detect the problem happened or is happening ?]",
      "type": "[Is the problem caused by unadapted methodology, wrong planning, wrong command line, wrong tool usage, bad code design (...) ? ]",
      "solutioning": "[What is the best way to mitigate this problem ?]",
      "frequency_encountered": 0,
      "last_updated": "[ISO8601 timestamp]",
      "related_skills": ["skill_id_1", "skill_id_2"]
    }
  ]
}
```

## troubleshooting.json maintenance

In order to keep the troubleshooting methodology efficient :
* If ```AGENTIC_RESOURCES/troubleshooting.json``` is absent : ```mkdir -p AGENTIC_RESOURCES/ && touch AGENTIC_RESOURCES/troubleshooting.json```
* When adding a ```skill``` in ```skills```, always check for duplication : if a similar skill exists with minor differences, merge the existing one with the new. Aim at genericity more than precision. Keep the ```skill``` concise, this methodology is a long term effort for efficient problem solving.
* Increment ```frequency_encountered``` each time a skill is referenced or reused.
* Update ```last_updated``` with current ISO8601 timestamp when modifying a skill.
* Populate ```related_skills``` with IDs of skills that are contextually related to help with cross-referencing solutions.
