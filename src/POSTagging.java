
import java.io.*;

public class POSTagging {
    public static void main (String[] args) throws IOException{
            File testfile = new File("testfile.txt");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec("~/IdeaProjects/KWIC/opennlp_master/bin/opennlp");

            InputStream inStream = p.getInputStream();
            InputStreamReader sReader = new InputStreamReader(inStream);
            BufferedReader buff = new BufferedReader(sReader);
            String nextLine = "";
            while((nextLine = buff.readLine()) != null) {
                System.out.println(nextLine);
            }
        } catch (IOException e) {
            System.out.println("Whoops! Something went wrong");
        }

//            ProcessBuilder test = new ProcessBuilder("ls");
//            test.redirectOutput(new File("list.txt"));
//            test.start();
//
//            ProcessBuilder builder = new ProcessBuilder("opennlp_master/bin/opennlp","TokenizerME","models/en-token.bin");
//            builder.redirectInput(testfile);
//            builder.redirectOutput(new File("tokenizedfile.txt"));
//            builder.start();
    }

}