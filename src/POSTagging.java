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


public class POSTagging {

    static void fetchFromUrl(String givenurl) throws MalformedURLException, IOException {
        //was thinking about regex, but it works bad with matching long links (like "https://stackoverflow.com/questions/161738/what-is-the-best-r.." will not work for example
        URL url = new URL(givenurl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        if(Integer.parseInt(Integer.toString(connection.getResponseCode()).substring(0,1))!=2){
            return;
            //throw new MalformedURLException();
            //2** means that URL ok, 4** - bad bad bad, 3** - redirection - do we try to handle it or just put with 4?
        }
        Document doc = Jsoup.connect(givenurl).get();
        Elements lines = doc.select("p");
        PrintWriter writer = new PrintWriter("url.txt");
        for (Element line : lines) {
            writer.println(line.text().replaceAll("\\[\\d*\\]|\\[citation needed\\]" , ""));
        }
        writer.close();
    }

    static void fetchFromWikipedia(String article, String language) throws IOException{

        String sitename = "";

        if(language.equals("English")){
            sitename = "https://en.wikipedia.org/wiki/" + article.replace(" ", "_");
        }
        else if(language.equals("German")){
            sitename = "https://de.wikipedia.org/wiki/" + article.replace(" ", "_");
        }

        Document doc = Jsoup.connect(sitename).get();
        Elements lines = doc.select("p");

        // so the search "helen keller" would have its output saved to file "helen_keller.txt"
        PrintWriter writer = new PrintWriter(article.replace(" ","_") + ".txt");

        for (Element line : lines) {
            // write next line to file, without citations (e.g. "[56]" or "[citation needed]")
            writer.println(line.text().replaceAll("\\[\\d*\\]|\\[citation needed\\]" , ""));
        }
        writer.close();
    }

    static String readSentencesFromFile(String filename) throws IOException{

        BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        String nextLine = "";
        String sentences = "";

        while((nextLine = buff.readLine()) != null) {
            sentences += nextLine + "\n";
        }

        return sentences;
    }

    static String[] sentenceDetector (String input, String sentenceModel) throws IOException {

        InputStream modelIn = new FileInputStream(sentenceModel);
        SentenceModel model = new SentenceModel(modelIn);
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        modelIn.close();

        return sentenceDetector.sentDetect(input);
    }

    static String[] tokenizer(String sentences, String tokenizerModel) throws IOException {

        InputStream modelIn = new FileInputStream(tokenizerModel);
        TokenizerModel model = new TokenizerModel(modelIn);
        Tokenizer tokenizer = new TokenizerME(model);
        modelIn.close();

        return tokenizer.tokenize(sentences);
    }

    static String[] postagger(String[] tokenizedText, String taggerModel) throws IOException {

        InputStream modelIn = new FileInputStream(taggerModel);
        POSModel model = new POSModel(modelIn);
        POSTaggerME tagger = new POSTaggerME(model);
        modelIn.close();

        return tagger.tag(tokenizedText);
    }

    static String[] lemmatizer(String[] tokens, String[] postags, String lemmatizerModel) throws IOException{
        InputStream modelIn = new FileInputStream(lemmatizerModel);
        LemmatizerModel model = new LemmatizerModel(modelIn);
        LemmatizerME lemmatizer = new LemmatizerME(model);
        modelIn.close();

        return lemmatizer.lemmatize(tokens, postags);
    }
}
