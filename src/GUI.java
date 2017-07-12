import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import static javax.swing.BoxLayout.Y_AXIS;


public class GUI extends JPanel {

    private JFrame frame; //top window
    private JTextField urlField; //to enter URL
    private JTextField saveField; //to save file
    private JTextField searchBox;
    private JComboBox<String> searchTerm;
    private JTextField ngramBox;
    private JList<String> sentenceList;
    private JScrollPane scrollPane;
    private JTextArea resultArea;

        //constructor
    GUI() {
        Font listFont = new Font("Serif", Font.PLAIN, 18); //if I could do this with original font it would be better
        Dimension size = new Dimension(95, 30); //how is this with size??

        //top window- set size and name of window
        frame = new JFrame("KWIC search");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double windowWidth = width*0.7;
        double heightWidth = height*0.7;

        //set window size relative to machine
        frame.setSize((int) windowWidth, (int) heightWidth);
        //main panel with borderlayout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        //in west, have a vertical box layout- add URL label to the top- jlabel, and then space, then buttons below? createrigidarea?

        Dimension sep = new Dimension(5, 0);

        //northPanel
        JPanel northPanel = new JPanel();
        BoxLayout bl = new BoxLayout(northPanel, Y_AXIS);
        northPanel.setLayout(bl);

        //north part of north panel
        JPanel npNorth = new JPanel();
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        npNorth.setLayout(fl);
        npNorth.add(Box.createRigidArea(new Dimension(68, 0)));
        //URL label
        JLabel urlLabel = new JLabel("URL:");
        urlLabel.setToolTipText("Where should we look for the search term?");
        npNorth.add(urlLabel);
        //space to enter URL
        urlField = new JTextField(45);;
        npNorth.add(urlField);
        //add a separator
        npNorth.add(Box.createRigidArea(new Dimension(290, 0)));
        JLabel fileLabel = new JLabel("Filename:");
        fileLabel.setToolTipText("What would you like to name the file to save the results into?");
        npNorth.add(fileLabel);
        saveField = new JTextField(23); //default suggestion when you hover???
        npNorth.add(saveField);
        //add a separator
        npNorth.add(Box.createRigidArea(sep));

        //south panel of north panel
        JPanel npSouth = new JPanel();
        FlowLayout fL = new FlowLayout(FlowLayout.LEFT);
        npSouth.setLayout(fL);
        npSouth.add(Box.createRigidArea(new Dimension(20, 0)));
        JLabel searchLabel = new JLabel("Search term:");
        searchLabel.setToolTipText("What are you looking for?");
        npSouth.add(searchLabel);
        searchBox = new JTextField(30);
        npSouth.add(searchBox);
        //separator
        npSouth.add(Box.createRigidArea(sep));
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setToolTipText("Are you searching for a word, lemma, or POS tag?");
        npSouth.add(typeLabel);
        String[] searchTerms = {"Word", "POS tag", "Lemma"};
        searchTerm = new JComboBox<>(searchTerms);
        searchTerm.setSize(10, 30);
        npSouth.add(searchTerm);
        //separator
        npSouth.add(Box.createRigidArea(sep));
        JLabel ngramLabel = new JLabel("N-gram length:");
        ngramLabel.setToolTipText("How many context words be displayed on either side of the search term?");
        npSouth.add(ngramLabel);
        ngramBox = new JTextField(5); //hover to show
        npSouth.add(ngramBox);
        //separator
        npSouth.add(Box.createRigidArea(sep));
        JButton searchButton = new JButton("search");
        searchButton.setToolTipText("Let's find it!");
        searchButton.addActionListener(new SearchButtonHandler());
        searchButton.setSize(size);
        npSouth.add(searchButton);

        npSouth.add(Box.createRigidArea(new Dimension(234, 0)));
        JButton saveButton = new JButton("save");
        saveButton.setToolTipText("Save results into an XML file with the given filename");
        saveButton.addActionListener(new SaveButtonHandler());
        saveButton.setSize(size);
        npSouth.add(saveButton);
        npSouth.add(Box.createRigidArea(new Dimension(10, 0)));
        JButton clearButton = new JButton("clear all");
        clearButton.setToolTipText("Clear all results and search terms");
        saveButton.setSize(size);
        npSouth.add(clearButton);
        clearButton.addActionListener(new ClearButtonHandler());


        //westPanel
        JPanel westPanel = new JPanel();
        BoxLayout bL = new BoxLayout(westPanel, Y_AXIS);
        westPanel.setLayout(bL);
        Dimension verSep = new Dimension(0, 20);
        westPanel.add(Box.createRigidArea(verSep));
        //add fun buttons here
        JButton but1 = new JButton("fun button");
        but1.addActionListener(new FunButton1Handler());
        westPanel.add(but1);
        westPanel.add(Box.createRigidArea(verSep));
        JButton but2 = new JButton("fun button");
        but2.addActionListener(new FunButton2Handler());
        westPanel.add(but2);
        westPanel.add(Box.createRigidArea(verSep));
        JButton but3 = new JButton("fun button");
        but3.addActionListener(new FunButton3Handler());
        westPanel.add(but3);
        westPanel.setBorder(BorderFactory.createLineBorder(new Color(223, 240, 255), 7));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));

        //centerLeft
        JPanel centerLeft = new JPanel();
        String[] defaultSentences = {"Welcome to KWIC! Please click me"};
        // if we use the same "sentenceList" as in the listSelectionListener, we could show the magic of our program
        // on the default sentence!
        sentenceList = new JList<>(defaultSentences);
        sentenceList.setFont(listFont);
        sentenceList.setFixedCellHeight(24);
        sentenceList.setFixedCellWidth(700);
        sentenceList.setVisibleRowCount(24);
        sentenceList.addListSelectionListener(new SentenceListHandler());
        scrollPane = new JScrollPane(sentenceList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        centerLeft.add(scrollPane);

        //centerRight- for
        JPanel centerRight = new JPanel();
        resultArea = new JTextArea(24, 30);
        resultArea.setFont(listFont);
        resultArea.setEditable(false);
        centerRight.add(new JScrollPane(resultArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.PAGE_START);


        northPanel.add(npNorth);
        northPanel.add(npSouth);
        centerPanel.add(centerLeft, BorderLayout.CENTER);
        centerPanel.add(centerRight, BorderLayout.EAST);
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(westPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel);
        frame.addWindowListener(new WindowListener());
        frame.setVisible(true);
        frame.setResizable(false);


        //colours of main box and sub-box
        panel.setBackground(Color.white);
        npNorth.setBackground(new Color(223, 240, 255));
        npSouth.setBackground(new Color(223, 240, 255));
        westPanel.setBackground(new Color(223, 240, 255));
        centerPanel.setBackground(new Color(223, 240, 255));
        centerLeft.setBackground(new Color(223, 240, 255));
        centerRight.setBackground(new Color(223, 240, 255));

        //buttons another colour
        saveButton.setBackground(Color.white);
        searchTerm.setBackground(Color.white);
        searchButton.setBackground(Color.white); //maybe make this one a fancy colour
        clearButton.setBackground(Color.white);
        but1.setBackground(Color.white);
        but2.setBackground(Color.white);
        but3.setBackground(Color.white);
        //make fun buttons fun colours
    }

    //so that when you close a window the window actually closes
    private class WindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    //actionlisteners
    private class SaveButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String fileName = saveField.getText();

            //methods
            //save to xml file
            //get sentences and tags and ???
            //message for invalid file names

        }
    }

    private class SearchButtonHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            //methods
            //search for toSearch in url webpage/file
            //return correct number of ngram words either side within sentence
            //look at type to search for
            //list of sentences and make it bold
            //return things into
            //message for invalid file names/websites
            //or if text not found/access denied/??

            String toSearch = searchBox.getText();
            String url = urlField.getText();
            String type = searchTerm.getSelectedItem().toString();
            try {
                int contextWords = Integer.parseInt(ngramBox.getText()); // throws exception if there is no text
            }
            catch (NumberFormatException n){
                // TODO there's definitely a better way to deal with the ngrams
                ngramBox.setText("100"); // this number appears in the ngram box if field left empty
                int contextWords = Integer.parseInt(ngramBox.getText());
                // to be continued
            }
            try {
                /* I'm using the url field here, but it's very simple to reassign this to another one if need be.
                   What it's doing at the moment is expecting a simple string as input (e.g. "helen keller"), and
                   attempts to find an English wikipedia article with such a title. If not found, an exception is
                   thrown.
                   Note: for now we only have a general IOException, maybe we could consider making more specific ones
                   for different failures (FileNotFound, URL not found) so we could inform the user more precisely what
                   went wrong?
                */
                POSTagging.fetchFromWikipedia(url); // look for topic on wikipedia, save text to file
                // readSentencesFromFile method has to make sure to read from the file the previous method just created,
                // so the long parameter string is an attempt to predict what the filename will look like
                String reader = POSTagging.readSentencesFromFile(url.replaceAll(" ","_") + ".txt");
                String[] sents = POSTagging.sentenceDetector(reader);

                // the following block of code is just repeating what is already written above (look for "centerLeft")
                // surely there is a way to avoid this?
                sentenceList = new JList<>(sents);
                sentenceList.setFont(new Font("Serif", Font.PLAIN, 18));
                sentenceList.setFixedCellHeight(24);
                sentenceList.setFixedCellWidth(700);
                sentenceList.setVisibleRowCount(24);
                sentenceList.addListSelectionListener(new SentenceListHandler());

                scrollPane.setViewportView(sentenceList); // replace old scrollpane

            } catch (IOException i) {
                // show error message in scrollpane
                String[] errorMessage = {"Something unfortunate just happened"};
                JList<String> whoops = new JList<>(errorMessage);
                scrollPane.setViewportView(whoops);
            }
        }
    }

    private class ClearButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            //pop up are you sure box
            //clear both boxes and all fields
            //call a reset method??

        }
    }


    private class SentenceListHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {

            String theSentence = sentenceList.getSelectedValue();
            try {
                String[] tokenized = POSTagging.tokenizer(theSentence);
                String[] tagged = POSTagging.postagger(tokenized);
                resultArea.setText("");
                for (int i = 0; i<tokenized.length; i++){
                    resultArea.append("Word: " + tokenized[i] + "   POS Tag: " + tagged[i] + "\n");
                }
            } catch (IOException e1) {
                System.out.println("Cannot tokenize for some reason");
            }

            // maybe format somehow, table or at least separate columns could look neater?
            // word, pos tags and lemma in a table (if that's a thing)

        }
    }

    private class FunButton1Handler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //whatever this button is actually gonna do
        }
    }

    private class FunButton2Handler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //whatever this button is actually gonna do
        }
    }

    private class FunButton3Handler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //whatever this button is actually gonna do
        }
    }

    //make a window
    public static void main(String[] args) { new GUI(); }
}