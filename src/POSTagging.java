import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class POSTagging {
	
	/** Method to scrape a site with given by user URL 
	 * 
	 * @param givenurl - String input by user
	 * @throws IOException, MalformedURLException
	 */
    static void fetchFromUrl(String givenurl) throws IOException {
        URL url = new URL(givenurl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        //checking for code of connection and keep going only in case of "2**"
        //another possibility was to do it with regex
        if(Integer.parseInt(Integer.toString(connection.getResponseCode()).substring(0,1))!=2){
            //throwing separate exception in case of bad formed URL
            throw new MalformedURLException();
        }

        Document doc = Jsoup.connect(givenurl).get();
        Elements lines = doc.select("p");
        //in program only text in <p> considered to be worth for scraping 
        PrintWriter writer = new PrintWriter("url.txt");

        //instead of saving every file separately, file is overwritten every time
        for (Element line : lines) {
            writer.println(line.text().replaceAll("\\[\\d*\\]|\\[citation needed\\]" , ""));
        }
        writer.close();
    }

    /** Method to scrape a Wiki-page with user search word.
     * 
     * @param article - the name of Wiki page, for which user is looking
     * @param language - have "German or "English" as content, depend on User's choice
     * @return List<String> TheList - list of Strings with names of possible Wiki-pages, in case of disambiguation, aList<String> - empty list
     * @throws IOException, MalformedURLException
     */
    static List<String> fetchFromWikipedia(String article, String language) throws IOException{
    	//the variables are initialized
        String sitename = "";
        String detect = "";
        if(language.equals("English")){
        	//depending on language (chosen by user earlier) Wiki links would be different
            sitename = "https://en.wikipedia.org/wiki/" + article.replace(" ", "_");
            detect = "a[title=Help:Disambiguation]";
            //<almost> all pages with disambiguation have a link with this title, this way it's possible to detect is it meaningful
        }
        else {
            sitename = "https://de.wikipedia.org/wiki/" + article.replace(" ", "_");
            detect = "a[title=Wikipedia:Begriffsklärung]";
            //in case page consists of links like zB "Chelsea"
        }
        //considering program support only two languages there is no need to specify that it is "German"
        URL url = new URL(sitename);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        //checking for code of connection and keep going only in case of "2**"
        //another possibility was to do it with regex
        if(Integer.parseInt(Integer.toString(connection.getResponseCode()).substring(0,1))!=2){
            //throwing separate exception in case of bad formed URL
            throw new MalformedURLException();
        }

        Document doc = Jsoup.connect(sitename).get();
        String content = "";
        //detect whether it is a disambiguation page
        Elements checker = doc.select(detect);
        
        // if yes, it is handled differently
        if(checker.text().length() > 0){
            Elements links = doc.select("a[title*="+article+"]:not([lang])");
            //getting all links for redirection to more specific pages ("Maria" - Maria (Blondie song))
            //if lang attribute won't be excluded will get names of languages like "Русский" etc
            //because links in wiki have this format <...> title="Maria – French" lang="fr" <...>Français</a>
            for (Element link : links){
                String tmp = link.text();
                content = content + tmp + " ; ";
                //use ; as delimiter because "," can actually be part of a link's name
            }
            content = content.substring(0, content.lastIndexOf(";"));
            // remove extra ";" in the end
            List<String> TheList = Arrays.asList(content.split(";"));
            //convert String to List <String> (splitting on ";")
            for (int i = 0;  i < TheList.size(); i++){
                String temp = TheList.get(i);
                if(temp.trim().equalsIgnoreCase(article))
                {
                    TheList.set(i,"");
                    //this loop for removing identical links to Wiki (if list has links to "Maria" which is identical to String article
                    //and will not give any result, this item is removed;
                }
            }
            return TheList;
        }
        else{
            List<String> aList = new ArrayList<>();
            //empty list to take care of return statement
            Elements lines = doc.select("p");
            //in program only text in <p> considered to be worth for scraping 
            PrintWriter writer = new PrintWriter("wiki.txt");
            //instead of saving every file separately, file is overwritten every time
            for (Element line : lines) {
                // write next line to file, without citations (e.g. "[56]" or "[citation needed]")
                writer.println(line.text().replaceAll("\\[\\d*\\]|\\[citation needed\\]" , ""));
            }
            writer.close();
            return aList;
        }
    }

    /** Method which reads from file and converting them to String
     * 
     * @param filename - name of the file
     * @return sentences - all sentences from given by user file
     * @throws IOException
     */
    static String readSentencesFromFile(String filename) throws IOException{

        BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        String nextLine = "";
        String sentences = "";

        while((nextLine = buff.readLine()) != null) {
            sentences += nextLine + "\n";
        }
        buff.close();
        return sentences;
    }

    /**
     *
     * @param input the text to split into sentences
     * @param sentenceModel differs depending on language
     * @return text separated into sentences
     * @throws IOException
     */
    static String[] sentenceDetector (String input, String sentenceModel) throws IOException {

        InputStream modelIn = new FileInputStream(sentenceModel);
        SentenceModel model = new SentenceModel(modelIn);
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        modelIn.close();

        return sentenceDetector.sentDetect(input);
    }


    /**
     *
     * @param sentences input text split by the sentence model
     * @param tokenizerModel differs depending on language
     * @return an array containing all the tokens
     * @throws IOException
     */
    static String[] tokenizer(String sentences, String tokenizerModel) throws IOException {

        InputStream modelIn = new FileInputStream(tokenizerModel);
        TokenizerModel model = new TokenizerModel(modelIn);
        Tokenizer tokenizer = new TokenizerME(model);
        modelIn.close();

        return tokenizer.tokenize(sentences);
    }

    /**
     *
     * @param tokenizedText array containing tokens found in input
     * @param taggerModel differs depending on language
     * @return an array containing all the POS tags for the input tokens, in the same order
     * @throws IOException
     */
    static String[] postagger(String[] tokenizedText, String taggerModel) throws IOException {

        InputStream modelIn = new FileInputStream(taggerModel);
        POSModel model = new POSModel(modelIn);
        POSTaggerME tagger = new POSTaggerME(model);
        modelIn.close();

        return tagger.tag(tokenizedText);
    }

    /**
     *
     * @param tokens array of tokens
     * @param postags array of POS tags, in the same order as the tokens
     * @param lemmatizerModel differs depending on language
     * @return array containing lemmas according to token/POS tag, in the same order as both
     * @throws IOException
     */
    static String[] lemmatizer(String[] tokens, String[] postags, String lemmatizerModel) throws IOException{
        InputStream modelIn = new FileInputStream(lemmatizerModel);
        LemmatizerModel model = new LemmatizerModel(modelIn);
        LemmatizerME lemmatizer = new LemmatizerME(model);
        modelIn.close();

        return lemmatizer.lemmatize(tokens, postags);
    }
}
