
import java.io.*;

public class POSTagging {
    public static void main (String[] args) throws IOException{

            File testfile = new File("testfile.txt");

            ProcessBuilder sentences = new ProcessBuilder("opennlp-master/bin/opennlp", "SentenceDetector", "opennlp-master/models/en-sent.bin");
            sentences.redirectInput(testfile);
            sentences.redirectOutput(new File("sentences.txt"));
            sentences.start();

            ProcessBuilder tokenizer = new ProcessBuilder("opennlp-master/bin/opennlp","TokenizerME","opennlp-master/models/en-token.bin");
            tokenizer.redirectInput(new File("sentences.txt"));
            tokenizer.redirectOutput(new File("tokenized.txt"));
            tokenizer.start();

            ProcessBuilder posTagger = new ProcessBuilder("opennlp-master/bin/opennlp","POSTagger","opennlp-master/models/en-pos-maxent.bin");
            posTagger.redirectInput(new File("tokenized.txt"));
            posTagger.redirectOutput(new File("tagged.txt"));
            posTagger.start();
    }
}