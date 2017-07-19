import java.io.IOException;
import java.util.ArrayList;

public class KeyWordFinder
{
	// sentenceCount is used to say how many sentences the document contains in
	// the statistics
	private static int sentenceCount;
	private static int keyWordCount;
	private static int sentencesWithKeyWordCount;

	static int getSentenceCount()
	{
		return sentenceCount;
	}

	static int getKeyWordCount()
	{
		return keyWordCount;
	}

	static int getSentencesWithKeyWordCount()
	{
		return sentencesWithKeyWordCount;
	}

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
	static ArrayList<String> getSentencesWithKeyWord(String[] sentences,
													 String keyWord)
	{
		sentenceCount = sentences.length;
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
				if (words[j].equals(keyWord) && (!rval.contains(sentences[i])))
				{
					rval.add(sentences[i]);
				}
			}
		}

		sentencesWithKeyWordCount = rval.size();

		return rval;
	}

	/**
	 * Method that takes an ArrayList of the sentences containing a words and a
	 * given n-gram and generates an ArrayList with those n-grams of the keyword
	 *
	 * @param sentences
	 *            - the ArrayList to get n-grams of
	 * @param keyWord
	 *            - the keyword
	 * @param ngram
	 *            - the number of words to each side of the keyword
	 * @return the n-gram of the keyword specified
	 */
	static ArrayList<String> generateNgrams(ArrayList<String> sentences,
											String keyWord, int ngram)
	{
		ArrayList<String> rval = new ArrayList<String>();
		// boolean to check whether we actually found the keyword
		boolean keyWordFound = false;

		for (String item : sentences)
		{
			if (item.equals("Sorry, could not find the word with that tag in the text!"))
			{
				rval.add(item);
				keyWordFound = true;
				break;
			}
			// split the ArrayList content by whitespace
			String[] words = item.split("\\s+");
			for (int i = 0; i < words.length; i++)
			{
				// if you find the keyword
				if (words[i].equals(keyWord)
						|| words[i]
						.matches("[\"'(\\[]" + keyWord + "[\")'\\]]")
						|| words[i].matches(keyWord + "[?;,.!:\"')\\]]")
						|| words[i].matches("[\"'(\\[]" + keyWord))
				{
					keyWordFound = true;
					// this block adds the specified amount of words around the
					// keyword + the keyword to the ArrayList that is to be
					// returned
					int keyWordIndex = i;
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
							if (words[j].equals(words[i]) && j == keyWordIndex)
							{
								toAdd += "<b>" + words[j] + "</b>" + " ";
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
							if (words[j].equals(words[i]) && j == keyWordIndex)
							{
								toAdd += "<b>" + words[j] + "</b>" + " ";
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
							if (words[j].equals(words[i]) && j == keyWordIndex)
							{
								toAdd += "<b>" + words[j] + "</b>" + " ";
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
							if (words[j].equals(words[i]) && j == keyWordIndex)
							{
								toAdd += "<b>" + words[j] + "</b>" + " ";
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
			keyWordCount = 0;
		} else
		{
			keyWordCount = rval.size();
		}
		return rval;
	}

	/**
	 * method that generates a list of tags for the keyword
	 *
	 * @param sentences
	 *            - sentences to search for the keyword in
	 * @param keyWord
	 *            - the keyword
	 * @param tokenizerModel
	 *            - tokenizermodel for language change
	 * @param taggerModel
	 *            - taggerModel for language change
	 * @return a list of the tags of the keyword
	 * @throws IOException
	 */
	static ArrayList<String> generateTagList(ArrayList<String> sentences,
											 String keyWord, String tokenizerModel, String taggerModel)
			throws IOException
	{
		ArrayList<String> tagList = new ArrayList<String>();

		// Turn the array of sentences into a string
		String sentencesToString = "";
		for (String item : sentences)
		{
			sentencesToString += item + " ";
		}

		// remove special characters for word detection
		sentencesToString = sentencesToString.replaceAll("[,-.\"\';:]", "");

		String[] tokens = POSTagging.tokenizer(sentencesToString,
				tokenizerModel); // array with tokenized sentences
		String[] tags = POSTagging.postagger(tokens, taggerModel); // array with
		// tags made
		// from
		// tokenized
		// array

		// go through the array, if the token is the keyword add its tag to the
		// list that's returned
		for (int i = 0; i < tokens.length; i++)
		{
			if (tokens[i].equals(keyWord))
			{
				tagList.add(tags[i]);
			}
		}

		return tagList;
	}

	/**
	 * Method that takes the result of getSentencesWithKeyWord + generateNgrams
	 * + generate tagList and filters it so it returns a new ArrayList that only
	 * contains those ngrams in which the keyword has the correct POSTag
	 *
	 * @param ngrams
	 *            - the sentences to go through
	 * @param keyWord
	 *            - the keyword to look for
	 * @param tag
	 *            - the tag to look for
	 * @return - those sentences that contain the keyword with the given POS-Tag
	 * @throws IOException
	 */
	static ArrayList<String> getNgramsWithCorrectPOSTag(
			ArrayList<String> ngrams, ArrayList<String> tags, String keyWord,
			String tag, String tokenizerModel, String taggerModel)
			throws IOException
	{
		sentencesWithKeyWordCount = 0;
		ArrayList<String> rval = new ArrayList<String>();

		boolean found = false;
		int tagsIndex = -1;

		for (String item : ngrams)
		{
			System.out.println(item);
			// split the ArrayList content by whitespace
			String[] words = item.split("\\s+");
			// if you find the keyword
			for (int i = 0; i < words.length; i++)
			{
				if (words[i].equals("<b>" + keyWord + "</b>")
						|| words[i]
						.matches("<b>" + "[\"'(\\[]" + keyWord + "[\")'\\]]" + "</b>")
						|| words[i].matches("<b>" + keyWord + "[?;,.!:\"')\\]]" + "</b>")
						|| words[i].matches("<b>" + "[\"'(\\[]" + keyWord + "</b>"))
				{
					tagsIndex++;
					//if the keyword has the correct tag
					if (tags.get(tagsIndex).equals(tag))
					{
						if (!rval.contains(item))
						{
							sentencesWithKeyWordCount++;
						}

						//add the ngram to the return value
						rval.add((item));

						found = true;
					}
				}
			}
		}

		if (!found)
		{
			rval.add("Sorry, tag not found for given word!");
			keyWordCount = 0;
		} else
		{
			keyWordCount = rval.size();
		}

		return rval;
	}
}