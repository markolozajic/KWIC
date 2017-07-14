import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import static javax.swing.BoxLayout.Y_AXIS;

/**
 * Created by Joules on 10/07/2017.
 * GUI for
 */
public class GUI extends JPanel {

    private JFrame frame; //top window
    private JTextField urlField; //to enter URL
    private JTextField saveField; //to save file
    private JTextField searchBox; //the thing to search
    private JComboBox<String> ngramList; //how many context words
    private JTextField POSBox;
    private JList<String> sentenceList; //sentences word has been found in
    private JTable resultTable; //table displaying word, lemma, and POS tags
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton fileInput;
    private JRadioButton urlInput;
    private JScrollPane scrollPane;

    //constructor
    GUI() {

        //top window- set size and name of window
        frame = new JFrame("KWIC search");
        //get the size of the screen it's on, so that everything is relative to screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double windowWidth = width*0.7;
        double windowHeight = height*0.7;
        //set window size relative to screen
        frame.setSize((int) windowWidth, (int) windowHeight);

        //main panel with borderlayout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        //separators
        Dimension sep = new Dimension((int)width/100, 0);
        Dimension borderSep = new Dimension((int) width/50, 0);
        Dimension verSep = new Dimension(0, (int) height/80);

        //northPanel with boxlayout
        JPanel northPanel = new JPanel();
        BoxLayout bl = new BoxLayout(northPanel, Y_AXIS);
        northPanel.setLayout(bl);

        //north part of north panel- flowlayout
        JPanel npNorth = new JPanel();
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        npNorth.setLayout(fl);
        //so the labels aren't directly on the side
        npNorth.add(Box.createRigidArea(borderSep));

        //URL
        JLabel urlLabel = new JLabel("URL/Filename:");
        urlLabel.setToolTipText("Where should we look for the search term?");
        npNorth.add(urlLabel);
        urlField = new JTextField((int) width/50);

        //if the box has text in it, keep it- if not, give example URL
        urlField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                urlField.setText("");
            }
            public void focusLost(FocusEvent e) {
                urlField.setForeground(Color.black);
                while (urlField.getText().equals("")) {
                    urlField.setText("en.wikipedia.org/wiki/Java_(programming_language)");
                    urlField.setForeground(Color.gray); } }
        });

        //buttons to select if it's a URL or file
        urlInput = new JRadioButton("URL");
        fileInput = new JRadioButton("File");
        buttonGroup.add(urlInput);
        buttonGroup.add(fileInput);
        npNorth.add(urlField);
        npNorth.add(urlInput);
        npNorth.add(fileInput);

        //space between them
        npNorth.add(Box.createRigidArea(new Dimension((int) (width/6.65), 0)));

        //Filename for saving
        JLabel fileLabel = new JLabel("Filename:");
        fileLabel.setToolTipText("What would you like to name the file to save the results into?");
        npNorth.add(fileLabel);
        saveField = new JTextField((int) width/75);
        //example filename for saving the results
        saveField.setText("defaultFile.xml");
        npNorth.add(saveField);

        //south panel of north panel- flowlayout
        JPanel npSouth = new JPanel();
        FlowLayout fL = new FlowLayout(FlowLayout.LEFT);
        npSouth.setLayout(fL);
        npSouth.add(Box.createRigidArea(new Dimension((int) width/44, 0)));

        //Search term
        JLabel searchLabel = new JLabel("Search term:");
        searchLabel.setToolTipText("What would you like to search for?");
        npSouth.add(searchLabel);
        searchBox = new JTextField((int) width/75);
        npSouth.add(searchBox);

        JLabel POSLabel = new JLabel("POS:");
        POSLabel.setToolTipText("What part of speech is the word you're looking for?");
        npSouth.add(POSLabel);
        POSBox = new JTextField((int) width/400);
        npSouth.add(POSBox);
        npSouth.add(Box.createRigidArea(sep));

        JLabel ngramLabel = new JLabel("N-grams:");
        ngramLabel.setToolTipText("How many context words be displayed on either side of the search term?");
        npSouth.add(ngramLabel);
        String[] ngrams = {"sentence", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        ngramList = new JComboBox<>(ngrams);
        npSouth.add(ngramList);
        npSouth.add(Box.createRigidArea(sep));

        JButton searchButton = new JButton("search");
        searchButton.setToolTipText("Let's find it!");
        searchButton.addActionListener(new SearchButtonHandler());
        npSouth.add(searchButton);

        npSouth.add(Box.createRigidArea(new Dimension((int) (width/5.2), 0)));

        //Saving
        JButton saveButton = new JButton("save");
        saveButton.setToolTipText("Save results into an XML file with the given filename");
        saveButton.addActionListener(new SaveButtonHandler());
        npSouth.add(saveButton);
        npSouth.add(Box.createRigidArea(sep));

        //Clearing
        JButton clearButton = new JButton("clear all");
        clearButton.setToolTipText("Clear all results and search terms");
        npSouth.add(clearButton);
        clearButton.addActionListener(new ClearButtonHandler());

        //westPanel- vertical boxlayout for buttons
        JPanel westPanel = new JPanel();
        BoxLayout bL = new BoxLayout(westPanel, Y_AXIS);
        westPanel.setLayout(bL);

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

        //centerPanel- boxlayout containing main two boxes
        JPanel centerPanel = new JPanel();
        BoxLayout BL = new BoxLayout(centerPanel, BoxLayout.X_AXIS);
        centerPanel.setLayout(BL);

        //centerLeft- sentences containing word
        JPanel centerLeft = new JPanel();
        String[] defaultSentences = {"Welcome to KWIC! Please click me"};
        // if we use the same "sentenceList" as in the listSelectionListener, we could show the magic of our program
        // on the default sentence!
        sentenceList = new JList<>(defaultSentences);
        sentenceList.setFixedCellHeight(24);
        sentenceList.setFixedCellWidth(700);
        sentenceList.setVisibleRowCount(24);
        sentenceList.addListSelectionListener(new SentenceListHandler());
        scrollPane = new JScrollPane(sentenceList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        centerLeft.add(scrollPane);

        //centerRight- word, lemma, pos tags
        JPanel centerRight = new JPanel();
        String[] columnNames = {"Word", "Lemma", "POS Tags"};
        Object[][] data = {{"This", "x", "y"}, {"is", "x", "y"}, {"an", "x", "y"}, {"example", "x", "y"}, {"sentence", "x", "y"},
                {"This", "x", "y"}, {"is", "x", "y"}, {"an", "x", "y"}, {"example", "x", "y"}, {"sentence", "x", "y"},
                {"This", "x", "y"}, {"is", "x", "y"}, {"an", "x", "y"}, {"example", "x", "y"}, {"sentence", "x", "y"},
                {"This", "x", "y"}, {"is", "x", "y"}, {"an", "x", "y"}, {"example", "x", "y"}, {"sentence", "x", "y"}, {"This", "x", "y"}, {"is", "x", "y"}, {"an", "x", "y"}, {"example", "x", "y"}, {"sentence", "x", "y"},
                {"This", "x", "y"}, {"is", "x", "y"}, {"an", "x", "y"}, {"example", "x", "y"}, {"sentence", "x", "y"},
                {"This", "x", "y"}, {"is", "x", "y"}, {"an", "x", "y"}, {"example", "x", "y"}, {"sentence", "x", "y"}};
        resultTable = new JTable(data, columnNames);
        TableColumn column = null;
        //for each column, set preferred width
        for (int i = 0; i < 3; i++) {
            column = resultTable.getColumnModel().getColumn(i);
            column.setPreferredWidth((int) (width/15));}
        resultTable.setRowHeight((int) (height/40));
        //so you can't edit it
        resultTable.setEnabled(false);
        resultTable.setPreferredScrollableViewportSize(new Dimension((int) width/4, (int) (height/1.95)));
        JScrollPane sP = new JScrollPane(resultTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JTableHeader tH = resultTable.getTableHeader(); //for assigning colours later
        centerRight.add(sP);

        //add sub-panels to panels
        northPanel.add(npNorth);
        northPanel.add(npSouth);
        centerPanel.add(centerLeft);
        centerPanel.add(centerRight);
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(westPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel);
        frame.addWindowListener(new WindowListener());
        frame.setVisible(true);
        frame.setResizable(false);


        //AESTHETICS
        //fonts
        Font medFont = new Font("Serif", Font.PLAIN, 18);
        Font smallFont = new Font("Serif", Font.PLAIN, 14);
        but1.setFont(smallFont);
        but2.setFont(smallFont);
        but3.setFont(smallFont);
        tH.setFont(smallFont);
        sentenceList.setFont(medFont);
        resultTable.setFont(medFont);
        //colours
        panel.setBackground(Color.white);
        npNorth.setBackground(new Color(223, 240, 255));
        npSouth.setBackground(new Color(223, 240, 255));
        westPanel.setBackground(new Color(223, 240, 255));
        centerPanel.setBackground(new Color(223, 240, 255));
        centerLeft.setBackground(new Color(223, 240, 255));
        centerRight.setBackground(new Color(223, 240, 255));
        urlInput.setBackground(new Color(223, 240, 255));
        fileInput.setBackground(new Color(223, 240, 255));
        saveButton.setBackground(Color.white);
        ngramList.setBackground(Color.white);
        searchButton.setBackground(Color.white);
        resultTable.setBackground(Color.white);
        tH.setBackground(Color.white);
        clearButton.setBackground(Color.white);
        but1.setBackground(Color.white);
        but2.setBackground(Color.white);
        but3.setBackground(Color.white);
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
            int contextWords =0;
            if(ngramList.getSelectedItem().equals("sentence")){
                contextWords = 100;
            }
            else
            {
                contextWords = (int) ngramList.getSelectedItem();
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
                keyWordFinder finder = new keyWordFinder();
                ArrayList<String> tmp1 = finder.getSentencesWithKeyWord(sents, toSearch);
                ArrayList<String> tmp2 = finder.generateNgrams(tmp1, toSearch, contextWords);
                String[] filteredSentences = new String[tmp2.size()];
                for(int i = 0; i<filteredSentences.length; i++){
                    filteredSentences[i] = tmp2.get(i);
                }
                // the following block of code is just repeating what is already written above (look for "centerLeft")
                // surely there is a way to avoid this?
                sentenceList = new JList<>(filteredSentences);
                sentenceList.setFont(new Font("Serif", Font.PLAIN, 18));
                sentenceList.setFixedCellHeight(24);
                sentenceList.setFixedCellWidth(700);
                sentenceList.setVisibleRowCount(24);
                //Had to comment this out till it is fixed
                //sentenceList.addListSelectionListener(new SentenceListHandler());

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