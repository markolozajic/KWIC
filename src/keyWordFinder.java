
import java.io.IOException;
import java.util.ArrayList;

public class keyWordFinder
{
	/**
	 * takes an array of strings and returns the keyword (if found) with the
	 * specified n-gram
	 * 
	 * @param sentences
	 *            - the original array
	 * @param keyWord
	 *            - the keyword we're looking for
	 * @return - an ArrayList of the Strings that are the keyword + n-gram of
	 *         the original sentence
	 */
	public ArrayList<String> getSentencesWithKeyWord(String[] sentences, String keyWord)
	{
		ArrayList<String> rval = new ArrayList<String>();

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
					if (!rval.contains(sentences[i]))
					{
						rval.add(sentences[i]);
					}
				}
			}
		}

		return rval;
	}

	/**
	 * Method that takes the result of getSentencesWithKeyWord and filters it so
	 * it returns a new ArrayList that only contains those sentences in which
	 * the keyword has the correct POSTag
	 * 
	 * @param sentences
	 *            - the sentences to go through
	 * @param keyWord
	 *            - the keyword to look for
	 * @param tag
	 *            - the tag to look for
	 * @return - those sentences that contain the keyword with the given POS-Tag
	 * @throws IOException
	 */
	public ArrayList<String> getSentencesWithCorrectPOSTag(ArrayList<String> sentences, String keyWord, String tag)
			throws IOException
	{
		POSTagging tagger = new POSTagging();
		ArrayList<String> rval = new ArrayList<String>();

		// go through the entirety of the sentences
		for (String item : sentences)
		{
			// generate the tokens and tags for the given sentence
			String[] tokens = tagger.tokenizer(item);
			String[] tags = tagger.postagger(tokens);

			// go through the tokens array
			for (int i = 0; i < tokens.length; i++)
			{
				// if you find the keyword, check its tag
				if (tokens[i].equals(keyWord))
				{
					// if the tag is the one we want, add the sentence to the
					// new ArrayList
					if (tags[i].equals(tag))
					{
						rval.add(item);
					}
				}
			}
		}
		return rval;
	}

	/**
	 * Method that takes an ArrayList of the sentences containing a words and a
	 * given n-gram and generates an ArrayList with those n-grams of the keyword
	 * 
	 * @param keyWord
	 *            - the keyword
	 * @param buffer
	 *            - the ArrayList to get n-grams of
	 * @param ngram
	 *            - the number of words to each side of the keyword
	 * @return the n-gram of the keyword specified
	 */
	public ArrayList<String> generateNgrams(ArrayList<String> sentences, String keyWord, int ngram)
	{
		ArrayList<String> rval = new ArrayList<String>();
		// boolean to check whether we actually found the keyword
		boolean keyWordFound = false;

		for (String item : sentences)
		{
			// split the ArrayList content by whitespace
			String[] words = item.split("\\s+");
			for (int i = 0; i < words.length; i++)
			{
				// if you find the keyword
				if (words[i].equals(keyWord) || words[i].matches("[\"'(\\[]" + keyWord + "[\")'\\]]")
						|| words[i].matches(keyWord + "[?;,.!:\"')\\]]") || words[i].matches("[\"'(\\[]" + keyWord))
				{
					keyWordFound = true;
					// this block adds the specified amount of words around the
					// keyword + the keyword to the ArrayList that is to be
					// returned
					int startIndex = i - ngram;
					int endIndex = i + ngram;

					// if the n-gram given would start and end outside of the
					// sentence, just add the whole sentence
					if (startIndex <= 0 && endIndex >= (words.length - 1))
					{
						String toAdd = "";
						for (int j = 0; j < words.length; j++)
						{
							// mark up the keyword so the GUI knows which one to
							// change later
							if (words[j].equals(words[i]))
							{
								toAdd += "!@#" + words[j] + " ";
							} else
							{
								toAdd += words[j] + " ";
							}
						}
						toAdd = toAdd.substring(0, toAdd.length() - 1);
						rval.add(toAdd);
					}

					// if the n-gram given would start inside the sentence and
					// end outside of it, add the sentence beginning with where
					// the n-gram starts
					if (startIndex > 0 && endIndex >= (words.length - 1))
					{
						String toAdd = "";
						for (int j = startIndex; j < words.length; j++)
						{
							// mark up the keyword so the GUI knows which one to
							// change later
							if (words[j].equals(words[i]))
							{
								toAdd += "!@#" + words[j] + " ";
							} else
							{
								toAdd += words[j] + " ";
							}

						}
						toAdd = toAdd.substring(0, toAdd.length() - 1);
						rval.add(toAdd);
					}

					// if the n-gram given would start outside of the sentence
					// and end inside of it add the sentence starting at the
					// start of it and ending at the given n-gram
					if (startIndex <= 0 && endIndex < (words.length - 1))
					{
						String toAdd = "";
						for (int j = 0; j <= endIndex; j++)
						{
							// mark up the keyword so the GUI knows which one to
							// change later
							if (words[j].equals(words[i]))
							{
								toAdd += "!@#" + words[j] + " ";
							} else
							{
								toAdd += words[j] + " ";
							}
						}
						toAdd = toAdd.substring(0, toAdd.length() - 1);
						rval.add(toAdd);
					}

					// if the n-gram given is entirely inside the sentence add
					// the actual n-gram of the keyword
					if (startIndex > 0 && endIndex < (words.length - 1))
					{
						String toAdd = "";
						for (int j = startIndex; j <= endIndex; j++)
						{
							// mark up the keyword so the GUI knows which one to
							// change later
							if (words[j].equals(words[i]))
							{
								toAdd += "!@#" + words[j] + " ";
							} else
							{
								toAdd += words[j] + " ";
							}
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
}
