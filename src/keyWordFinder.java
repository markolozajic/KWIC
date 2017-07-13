import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import java.util.ArrayList;
import java.io.*;

public class keyWordFinder
{

	private int count;
	// if we want to keep "private int count" here, we should figure out a way to make
	// the method sentencesWithKeyword non-static (or a different way of keeping track of count)

	/**
	 * takes an array of strings and returns the keyword (if found) with the
	 * specified n-gram
	 * 
	 * @param sentences
	 *            - the original array
	 * @param keyWord
	 *            - the keyword we're looking for
	 * @param ngram
	 *            - the amount of words you want to display next to the keyboard
	 *            to either side
	 * @return - an ArrayList of the Strings that are the keyword + n-gram of
	 *         the original sentence
	 */
	ArrayList<String> sentencesWithKeyWord(String[] sentences, String keyWord, int ngram)
	{
		count = 0;
		ArrayList<String> rval = new ArrayList<String>();
		ArrayList<String> buffer = new ArrayList<String>();
		// boolean to check whether we actually found the keyword
		boolean keyWordFound = false;

		// outer for-loop that goes through the sentences array
		for (int i = 0; i < sentences.length; i++)
		{
			// split the current array slot by word boundaries;
			String[] words = sentences[i].split("\\b");
			// for-loop that searches through the result of the previous
			// operation and checks if it can find the keyword
			for (int j = 0; j < words.length; j++)
			{
				// if you find the keyword in this sentence, add it to the
				// buffer ArrayList, increment count and
				// set the control boolean to true
				if (words[j].equals(keyWord))
				{
					if (!buffer.contains(sentences[i]))
					{
						buffer.add(sentences[i]);
					}
					keyWordFound = true;
					count++;
				}
			}
		}

		// Now we have an ArrayList that contains all the sentences that contain
		// the keyword

		for (String item : buffer)
		{
			// split the ArrayList content by whitespace
			String[] words = item.split("\\s+");
			for (int i = 0; i < words.length; i++)
			{
				// if you find the keyword
				if (words[i].equals(keyWord) || words[i].matches("[\"'(\\[]" + keyWord + "[\")'\\]]")
						|| words[i].matches(keyWord + "[?;,.!:\"')\\]]") || words[i].matches("[\"'(\\[]" + keyWord))
				{
					// this block adds the specified amount of words around the
					// keyword + the keyword to the ArrayList that is to be
					// returned
					int startIndex = i - ngram;
					int endIndex = i + ngram;

					if (startIndex <= 0 && endIndex >= (words.length - 1))
					{
						rval.add(item);
					}

					if (startIndex > 0 && endIndex >= (words.length - 1))
					{
						String toAdd = "";
						for (int j = startIndex; j < words.length; j++)
						{
							toAdd += words[j] + " ";
						}
						toAdd = toAdd.substring(0, toAdd.length() - 1);
						rval.add(toAdd);
					}

					if (startIndex <= 0 && endIndex < (words.length - 1))
					{
						String toAdd = "";
						for (int j = 0; j <= endIndex; j++)
						{
							toAdd += words[j] + " ";
						}
						toAdd = toAdd.substring(0, toAdd.length() - 1);
						rval.add(toAdd);
					}

					if (startIndex > 0 && endIndex < (words.length - 1))
					{
						String toAdd = "";
						for (int j = startIndex; j <= endIndex; j++)
						{
							toAdd += words[j] + " ";
						}
						toAdd = toAdd.substring(0, toAdd.length() - 1);
						rval.add(toAdd);
					}

				}
			}
		}

		// if the keyword wasn't found we display a notification
		if (!keyWordFound)
		{
			rval.add("Sorry, could not find the keyword in the text!");
		}

		return rval;
	}

	static String readSentencesFromFile(String filename) throws IOException
	{

		BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String nextLine = "";
		String sentences = "";

		while ((nextLine = buff.readLine()) != null)
		{
			sentences += nextLine + "\n";
		}

		buff.close();

		return sentences;
	}

	static String[] sentenceDetector(String input) throws IOException
	{

		InputStream modelIn = new FileInputStream("models/en-sent.bin");
		SentenceModel model = new SentenceModel(modelIn);
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		modelIn.close();

		return sentenceDetector.sentDetect(input);
	}

	static String[] tokenizer(String sentences) throws IOException
	{

		InputStream modelIn = new FileInputStream("models/en-token.bin");
		TokenizerModel model = new TokenizerModel(modelIn);
		Tokenizer tokenizer = new TokenizerME(model);
		modelIn.close();

		return tokenizer.tokenize(sentences);
	}

	static String[] postagger(String[] tokenizedText) throws IOException
	{

		InputStream modelIn = new FileInputStream("models/en-pos-maxent.bin");
		POSModel model = new POSModel(modelIn);
		POSTaggerME tagger = new POSTaggerME(model);
		modelIn.close();

		return tagger.tag(tokenizedText);
	}

	public static void main(String[] args)
	{
		String sentence1 = "This is a sentence.";
		String sentence2 = "(I am just) making examples.";
		String sentence3 = "So \"I\" can test my shit.";
		String sentence4 = "I guess I] need another one.";
		String[] array = { sentence1, sentence2, sentence3, sentence4 };

		keyWordFinder asdf = new keyWordFinder();
		ArrayList<String> toPrint = asdf.sentencesWithKeyWord(array, "I", 2);
		for (String item : toPrint)
		{
			System.out.println(item);
		}

	}

}
