# A basic method suggestion engine. 

Given a Java class and a word, it returns method suggestions.

- Step 1: Builds a parse tree for the Java class
- Step 2: Collects a set of names of public methods
- Step 3: Computes Levenshtein distance between the word and each method name
- Step 4: Returns the method names the least distant to the given word


# Example run:

```
make run
```

The output is:

```
rm -f *.class
javac *.java
mkdir -p logs
cat input/System.java | java -Djava.util.logging.config.file=logging.properties SuggestionEngine getProper 3

Suggestions are found:
getProperty: 2.0
getProperties: 4.0
clearProperty: 6.0
```

The numbers above correspond to the distance of each method name to the given word. 3 suggestions are returned as requested.


# Example run2:

```
make run2
```

The output is:

```
rm -f *.class
javac *.java
mkdir -p logs
cat input/String.java | java -Djava.util.logging.config.file=logging.properties SuggestionEngine toUpper 3
Suggestions are found:
toUpperCase: 4.0
compare: 5.0
isEmpty: 6.0
```

# Details
SuggestionEngine takes 2 arguments: The first one is the word and the second one is the number of suggestions we request.
The logging location is set up in logging.properties file.

You can examine the log lines by executing:

```
tail -f logs/debug.log
```
