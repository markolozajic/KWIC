import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class Saving {
    static void saveToFile(ArrayList<String> ngrams, String addressFile, String tokenizerModel,
                           String taggerModel, String lemmatizerModel) throws IOException {
        FileWriter fw = new FileWriter(new File(addressFile));
        generateXML(ngrams, fw, tokenizerModel, taggerModel, lemmatizerModel);
        fw.close();
    }

    static void generateXML(ArrayList<String> ngrams, Writer w, String tokenizerModel,
                            String taggerModel, String lemmatizerModel) {

        // defines a factory API that enables applications to obtain XML writers
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        // defines the interface for creating instances of XMLEvents
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();

        try {
            String s = "";
            for(String ngram : ngrams){
                s += ngram + " %b "; // as with POStag search method, "%b" marks end of ngram
            }
            s = s.replaceAll("<b>|</b>|<html>|</html>",""); // remove html tags used to bolden keyword
            s = s.substring(0,s.length()-3); // deletes last "%b ", don't want to generate extra empty ngram tag

            String[] tokens = POSTagging.tokenizer(s, tokenizerModel);
            String[] tags = POSTagging.postagger(tokens, taggerModel);
            String[] lemmas = POSTagging.lemmatizer(tokens, tags, lemmatizerModel);

            // defines the interface to write XML documents, use writer passed as parameter
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(w);

            eventWriter.add(eventFactory.createStartDocument()); // mark start of document
            eventWriter.add(eventFactory.createCharacters("\n")); // newline
            // create opening addressbooktag
            eventWriter.add(eventFactory.createStartElement("", "", "results"));

            eventWriter.add(eventFactory.createCharacters("\n\t"));
            // open sentence tag
            eventWriter.add(eventFactory.createStartElement("", "", "ngram"));


            for (int i = 0; i<tokens.length; i++) { // for all addresses found in the address book

                if(tokens[i].equals("%b")){
                    eventWriter.add(eventFactory.createCharacters("\n\t"));
                    eventWriter.add(eventFactory.createEndElement("", "", "ngram"));
                    eventWriter.add(eventFactory.createCharacters("\n\t"));
                    eventWriter.add(eventFactory.createStartElement("", "", "ngram"));
                    continue;
                }

                eventWriter.add(eventFactory.createCharacters("\n\t\t"));
                eventWriter.add(eventFactory.createStartElement("", "", "token"));

                eventWriter.add(eventFactory.createAttribute("lemma", lemmas[i]));
                eventWriter.add(eventFactory.createAttribute("pos_tag", tags[i]));
                eventWriter.add(eventFactory.createCharacters(tokens[i]));
                eventWriter.add(eventFactory.createEndElement("", "", "token"));

            }
            eventWriter.add(eventFactory.createCharacters("\n\t"));
            eventWriter.add(eventFactory.createEndElement("", "", "ngram"));
            eventWriter.add(eventFactory.createCharacters("\n"));
            // close address tag
            eventWriter.add(eventFactory.createEndElement("", "", "results"));
            eventWriter.add(eventFactory.createEndDocument()); // mark end of document
            eventWriter.close(); // close writer

        } catch (XMLStreamException e) {
            e.printStackTrace(); // TODO handle exceptions here or in the GUI?
        }
        catch (IOException i) {
            i.printStackTrace();
        }
    }
}
