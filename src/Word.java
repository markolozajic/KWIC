
public class Word
{
	private String word;
	private String posTag;
	private String lemma;

	/**
	 * Default constructor
	 */
	public Word()
	{
		word = "";
		posTag = "";
		lemma = "";
	}

	/**
	 * Constructor with all 3 instance variables
	 * 
	 * @param aWord
	 * @param aPosTag
	 * @param aLemma
	 */
	public Word(String aWord, String aPosTag, String aLemma)
	{
		this.word = aWord;
		this.posTag = aPosTag;
		this.lemma = aLemma;
	}

	/**
	 * Constructor with just word and tag
	 * 
	 * @param aWord
	 * @param aPosTag
	 */
	public Word(String aWord, String aPosTag)
	{
		this.word = aWord;
		this.posTag = aPosTag;
	}

	/**
	 * Constructor with just word
	 * 
	 * @param aWord
	 * @param aPosTag
	 */
	public Word(String aWord)
	{
		this.word = aWord;
	}

	/**
	 * toString method
	 */
	public String toString()
	{
		return "Word:" + word + ", POS Tag:" + posTag + ", Lemma:" + lemma + "]";
	}

	/**
	 * Method to retrieve the word variable
	 * 
	 * @return the word of this word
	 */
	public String getWord()
	{
		return word;
	}

	/**
	 * Method to set the word variable
	 * 
	 * @param word
	 *            -
	 */
	public void setWord(String word)
	{
		this.word = word;
	}

	/**
	 * Method to retrieve the POSTag variable
	 * 
	 * @return the POSTag of this word
	 */
	public String getPosTag()
	{
		return posTag;
	}

	/**
	 * Method to set the word variable
	 * 
	 * @param posTag
	 */
	public void setPosTag(String posTag)
	{
		this.posTag = posTag;
	}

	/**
	 * Method to retrieve the lemma variable
	 * 
	 * @return the lemma of this word
	 */
	public String getLemma()
	{
		return lemma;
	}

	/**
	 * Method to set the lemma variable
	 * 
	 * @param lemma
	 */
	public void setLemma(String lemma)
	{
		this.lemma = lemma;
	}

}
