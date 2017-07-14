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


public class POSTagging {


    static void fetchFromWikipedia(String article) throws IOException{

        String sitename = "https://en.wikipedia.org/wiki/" + article.replace(" ", "_");
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

    static String[] sentenceDetector (String input) throws IOException {

        InputStream modelIn = new FileInputStream("models/en-sent.bin");
        SentenceModel model = new SentenceModel(modelIn);
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        modelIn.close();

        return sentenceDetector.sentDetect(input);
    }

    static String[] tokenizer(String sentences) throws IOException {

        InputStream modelIn = new FileInputStream("models/en-token.bin");
        TokenizerModel model = new TokenizerModel(modelIn);
        Tokenizer tokenizer = new TokenizerME(model);
        modelIn.close();

        return tokenizer.tokenize(sentences);
    }

    static String[] postagger(String[] tokenizedText) throws IOException {

        InputStream modelIn = new FileInputStream("models/en-pos-maxent.bin");
        POSModel model = new POSModel(modelIn);
        POSTaggerME tagger = new POSTaggerME(model);
        modelIn.close();

        return tagger.tag(tokenizedText);
    }

    static String[] lemmatizer(String[] tokens, String[] postags) throws IOException{
        InputStream modelIn = new FileInputStream("models/en-lemmatizer.bin");
        LemmatizerModel model = new LemmatizerModel(modelIn);
        LemmatizerME lemmatizer = new LemmatizerME(model);
        return lemmatizer.lemmatize(tokens, postags);
    }


    // use this part if you're curious what kind of output you get from the above methods

    public static void main (String[] args) throws IOException{
        String article = "helen keller";
        fetchFromWikipedia(article);
        String contents = readSentencesFromFile(article.replaceAll(" ","_") + ".txt");
        String[] tokens = tokenizer(contents);
        String[] tags = postagger(tokens);
        String[] lemmas = lemmatizer(tokens, tags);
        for(int i = 0; i<tokens.length; i++){
            System.out.println(tokens[i] + " " + tags[i] + " " + lemmas[i]);
        }
    }
}
