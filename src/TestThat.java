package that;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestThat {

	public static void main(String[] args) throws IOException {
    //public void fetchFromWikipedia(String article) throws IOException{
	Scanner keyboard = new Scanner(System.in);
	String article = keyboard.nextLine().replace(" ", "_");
	String sitename = "https://en.wikipedia.org/wiki/" + article;
	URL url = new URL(sitename);
	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	if(Integer.parseInt(Integer.toString(connection.getResponseCode()).substring(0,1))!=2){
		//this way we exclude
	System.out.println("The mistake was made and there is no connection with the page.");
	return;
	}
	Document doc = Jsoup.connect(sitename).get();
	String content = "";
	Elements checker = doc.select("a[title=Help:Disambiguation]");
	 if(checker.text().length() > 0){
		    Elements links = doc.select("a[title*="+article+"]:not([lang])");
		    for (Element link : links){
			String linky = link.text();
			content = content + linky + " ; ";
			}
		    content = content.substring(0, content.lastIndexOf(";"));
		    System.out.println(content);
			//System.out.println("Please pick one of the titles out of this list:");
			//System.out.println(linky);
	 }
		else{
	Elements lines = doc.select("p");
	PrintWriter writer = new PrintWriter(article.replace(" ","_") + ".txt");
	for (Element line : lines) {
	// write next line to file, without citations (e.g. "[56]" or "[citation needed]")
	writer.println(line.text().replaceAll("\\[\\d*\\]|\\[citation needed\\]" , ""));
	}
	writer.close();
	}
	}
	static void readfromUrl(String givenurl) throws MalformedURLException, IOException {
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
	PrintWriter writer = new PrintWriter(givenurl + ".txt");
	for (Element line : lines) {
	writer.println(line.text());
	}
	writer.close();
	}
	}