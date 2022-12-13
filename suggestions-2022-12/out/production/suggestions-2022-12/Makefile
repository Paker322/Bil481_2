
run: clean
	javac *.java
	mkdir -p logs
	cat input/System.java | java -Djava.util.logging.config.file=logging.properties SuggestionEngine getProper 3

run2: clean
	javac *.java
	mkdir -p logs
	cat input/String.java | java -Djava.util.logging.config.file=logging.properties SuggestionEngine toUpper 3


clean:
	rm -f *.class
