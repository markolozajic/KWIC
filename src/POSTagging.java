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
import java.util.Scanner;


public class POSTagging {

    private static String fetchFromWikipedia() throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a wikipedia article:");
        String sitename = "https://en.wikipedia.org/wiki/" + scanner.nextLine().replace(" ", "_");
        Document doc = Jsoup.connect(sitename).get();

        Elements lines = doc.select("p");
        String content = "";
        for (Element line : lines) {
            content = content + line.text().replaceAll("\\[\\d*\\]|\\[citation needed\\]" , "");
        }
        return content;
    }

    private static String readSentencesFromFile(String filename) throws IOException{
        String[] anArray = sentenceDetector(filename);

        String sentences = "";

        for (String str : anArray) {
            sentences += str + "\n";
        }

        return sentences;
    }

    private static String[] sentenceDetector (String filename) throws IOException {

        BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        String nextLine = "";
        String bop = "";
        while ((nextLine = buff.readLine()) != null) {
            bop += nextLine + " ";
        }

        InputStream modelIn = new FileInputStream("models/en-sent.bin");
        SentenceModel model = new SentenceModel(modelIn);
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        modelIn.close();

        return sentenceDetector.sentDetect(bop);
    }

    private static String[] tokenizer(String sentences) throws IOException {

        InputStream modelIn = new FileInputStream("models/en-token.bin");
        TokenizerModel model = new TokenizerModel(modelIn);
        Tokenizer tokenizer = new TokenizerME(model);
        modelIn.close();

        return tokenizer.tokenize(sentences);
    }

    private static String[] postagger(String[] tokenizedText) throws IOException {

        InputStream modelIn = new FileInputStream("models/en-pos-maxent.bin");
        POSModel model = new POSModel(modelIn);
        POSTaggerME tagger = new POSTaggerME(model);
        modelIn.close();

        return tagger.tag(tokenizedText);
    }

    public static void main (String[] args) throws IOException{
        String contents = fetchFromWikipedia();
        String[] tokenized = tokenizer(contents);
        String[] tags = postagger(tokenized);
        for(int i = 0; i<tokenized.length; i++){
            System.out.println(tokenized[i] + " " + tags[i]);
        }
    }
}
