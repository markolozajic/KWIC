import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by marko on 08.07.17.
 */
public class KWIC {
    public static void main(String[] args){
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec("wget -qO- https://en.wikipedia.org/wiki/Apparatchik");

            // apparently you can't do pipes in runtime.exec()...how to build a process that does the wget above
            // and the xpath/sed below?

            // xmllint --xpath "//p//text()" Apparatchik - (gets only text in html paragraph tags)
            // sed s/\[[0-9]*\]//g - (removes the citation numbers like "[2]")

            // another small thing to consider: remove [citation needed]

            InputStream inStream = p.getInputStream();
            InputStreamReader sReader = new InputStreamReader(inStream);
            BufferedReader buff = new BufferedReader(sReader);
            String nextLine = "";
            while((nextLine = buff.readLine()) != null) {
                System.out.print(nextLine + ", ");
            }
        } catch (IOException e) {
            System.out.println("Whoops! Something went wrong");
        }
    }
}
