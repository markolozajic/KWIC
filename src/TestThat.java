import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestThat {

	    static List<String> fetchFromWikipedia(String article, String language) throws IOException{
	        String sitename = "";
	        String detect = "";
			if(language.equals("English")){
	            sitename = "https://en.wikipedia.org/wiki/" + article.replace(" ", "_");
	            detect = "a[title=Help:Disambiguation]";
	          //all pages with disambgiuation have a link with this title, this way it's possible to detect is it meaningful
	        }
	        else {
	            sitename = "https://de.wikipedia.org/wiki/" + article.replace(" ", "_");
	            detect = "a[title=Wikipedia:Begriffsklärung]";
	            //in case page consists of links like zB "Chelsea"
	        }
	        //considering program support only two languages there is no need to specify
	        URL url = new URL(sitename);
	    	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	    	if(Integer.parseInt(Integer.toString(connection.getResponseCode()).substring(0,1))!=2){
	    		throw new MalformedURLException();
	    	}
	    	Document doc = Jsoup.connect(sitename).get();
	    	String content = "";
	    	Elements checker = doc.select(detect);
	    	 if(checker.text().length() > 0){
	    		 // this way we check does this thing exist or not (
	    		    Elements links = doc.select("a[title*="+article+"]:not([lang])");
	    		    //if lang attribute won't be excluded will get names of languages like "Русский" etc
	    		    //because links in wiki have this format <...> title="Maria – French" lang="fr" <...>Français</a>
	    		    for (Element link : links){
	    			String linky = link.text();
	    			content = content + linky + " ; ";
	    			//use ; as delimiter because "," can actually be part of a link's name
	    			}
	    		    content = content.substring(0, content.lastIndexOf(";"));
	    		    List<String> TheList = Arrays.asList(content.split(";"));
	    		    for (int i = 0;  i < TheList.size(); i++){
	    		    	String temp = TheList.get(i);
	    		    	if(temp.trim().equalsIgnoreCase(article))
	    		    	{
	    		    		TheList.set(i,"");
	    		    	}
	    	        }
	    		    return TheList;
	    	 }
	    		else{
	    			List<String> aList = new ArrayList();
	      	Elements lines = doc.select("p");
	    	PrintWriter writer = new PrintWriter(article.replace(" ","_") + ".txt");
	    	for (Element line : lines) {
	    	// write next line to file, without citations (e.g. "[56]" or "[citation needed]")
	    	writer.println(line.text().replaceAll("\\[\\d*\\]|\\[citation needed\\]" , ""));
	    	}
	    	writer.close();
	    	return aList;
	    	}
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
	}
