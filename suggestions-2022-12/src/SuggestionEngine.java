import com.sun.javafx.image.impl.IntArgb;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.*;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SuggestionEngine extends Java8BaseListener {
	class Candidate implements Comparable<Candidate> {
		String methodName;
		double distance;
		public Candidate(String methodName, double distance) {
			this.methodName = methodName;
			this.distance = distance;
		}

		public int compareTo(Candidate c) {
			int difference = (int) (this.distance - c.distance);
			if (difference == 0 && !methodName.equals(c.methodName)) {
				return 1;
			}
			return difference;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Candidate) {
				return ((Candidate)o).methodName.equals(
					this.methodName);
			}
			return super.equals(o);
		}

		@Override
		public int hashCode() {
			return methodName.hashCode();
		}
	}

	private final static Logger LOGGER = Logger.getLogger(SuggestionEngine.class.getName());

	public static void main(String[] args) throws IOException {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Info Log");
		SuggestionEngine se = new SuggestionEngine();
		InputStream code = System.in;
		//String word = args[0];
		String word ="getProper";
		//int topK = Integer.valueOf(args[1]);
		int topK =3;
		TreeSet<Candidate> suggestions = se.suggest(code, word, topK);

		System.out.println("Suggestions are found:");
		while (!suggestions.isEmpty()) {
			Candidate c = suggestions.pollFirst();
			System.out.println(c.methodName + ": " + c.distance);
		}
	}

	List<String> mMethods;

	public TreeSet<Candidate> suggest(InputStream code,
					  String word,
					  int topK)
	throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(System.in);
		Java8Lexer lexer = new Java8Lexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Java8Parser parser = new Java8Parser(tokens);
		LOGGER.info("Building the parse tree...");
		long start = System.nanoTime();
		ParseTree tree = parser.compilationUnit();
		long elapsedNano = System.nanoTime() - start;
		long elapsedSec =TimeUnit.SECONDS.convert(elapsedNano, TimeUnit.NANOSECONDS);
		LOGGER.info(String.format("Built the parse tree...(took %d seconds)", elapsedSec));
		ParseTreeWalker walker = new ParseTreeWalker();
		mMethods = new ArrayList<>();


		LOGGER.info("Collecting the public method names...");
		walker.walk(this, tree);
		LOGGER.info("Collected the public method names...");
		LOGGER.info(mMethods.toString());

		LOGGER.info("Finding the suggestions...");
		return getTopKNeighbor(word, topK);
	}

	// Access methodModifier and examine all modifiers to see if there is "public".
	// If the method is public, then get the method name from the methodDeclarator's Identifier.
	// Add the method name to mMethods variable.
	@Override
	public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
		String a = ctx.methodModifier().get(0).getText();

		String lastMethod = ctx.methodHeader().methodDeclarator().Identifier().getText();
		if(a.equals("public"))
			mMethods.add(lastMethod);


	}
	// - Go through all methods in mMethods and compute the distance of the method name to the word.
	// - Use
	//   double distance = Levenshtein.distance(word, methodName);
	// to compute the distance.
	// - Use the minHeap to keep track of K methods with the least distance.
	// - Make sure that there is at least K elements in the heap.
	// - You can use
	//	LOGGER.info("my message")
	// to add your log lines for debugging.
	private TreeSet<Candidate> getTopKNeighbor(String word, int K) {
		TreeSet<Candidate> minHeap = new TreeSet<>();
		for (int j = 0 ;j<K;j++){
			String mehod_name = mMethods.get(j);
			double distance =  Levenshtein.distance(word,mMethods.get(j));
			Candidate yeni = new Candidate(mehod_name,distance);
			minHeap.add(yeni);
		}
		for(int i = K ;i<mMethods.size();i++) {
			String mehod_name = mMethods.get(i);
			double distance = Levenshtein.distance(word, mMethods.get(i));
			Candidate yeni = new Candidate(mehod_name, distance);
			if (minHeap.last().distance > distance && !minHeap.contains(yeni)) {
				minHeap.pollLast();
				minHeap.add(yeni);
			}
		}
		return minHeap;
	}
}
