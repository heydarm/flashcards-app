# Flashcards App

To start app create an instance of Flashcards class with the following arguments:  
    `Scanner scanner` - scanner object,  
    `String[] args`   - command line arguments.  
And then run `start()` method.  

After starting it will ask for the action. There are following arguments:  
- `add`          - add card and it definition;  
- `remove`       - remove flashcard;  
- `import`       - import flashcards from file. File format: each card on new line (cardname:definition:numberOfWrongAnswers);  
- `export`       - export flashcards to specified file;  
- `ask`          - ask random flashcard;  
- `exit`         - exit the program;  
- `log`          - save all console inputs and outputs to specified file;  
- `hardest card` - show all flashcards with the biggest number of wrong answers;   
- `reset stats`  - reset all statistics about wrong answers.  
