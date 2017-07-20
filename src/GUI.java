/**
 * Authors: Helen Joules, Anna Soboleva, Marko Lozajic, Jonas Biegert
 * Class which launches the GUI of KWIC search, a key word in context program,
 * which operates with files, URls, and wikipedia articles
 */

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.xml.stream.XMLStreamException;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import static javax.swing.BoxLayout.Y_AXIS;

public class GUI extends JPanel
{

    private static JFrame frame; // top window
    private JTextField urlField; // to enter URL
    private JTextField searchBox; // the thing to search
    private JComboBox<String> ngramList; // how many context words
    private JComboBox<String> posList; // the different POS tags
    private JList<String> sentenceList; // sentences word has been found in
    private JTable resultTable; // table displaying word, lemma, and POS tags
    // radio buttons for types of input
    private JRadioButton urlInput;
    private JRadioButton fileInput;
    private JRadioButton wikiInput;
    // languages available
    private static JRadioButton english;
    private static JRadioButton german;
    private JScrollPane scrollPane;
    private JTabbedPane helpPane;

    // need this to be an instance variable so we can access the instance
    // variables of keywordfinder class in the statistics
    private KeyWordFinder finder = new KeyWordFinder();
    private ArrayList<String> tagList = new ArrayList<>();

    // control booleans that are changed in the searchButtonHandler and used in the statsButtonHandler
    private boolean wordSearchDone = false;
    private boolean wordAndTagSearchDone = false;

    //number that gives the searchTime in the statsButton
    private double searchTime;

    // POS tags for english and german, for the jcombobox
    private String[] englishPOS = { "", "CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP",
            "NNPS", "PDT", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB", "VBD", "VBG", "VBN",
            "VBP", "VBZ", "WDT", "WP", "WP$", "WRB" };
    private String[] germanPOS = { "", "ADJA", "ADJD", "ADV", "APPR", "APPRART", "APPO", "APZR", "ART", "CARD", "FM", "ITJ",
            "KOUI", "KOUS", "KON", "KOKOM", "NN", "NE", "PDS", "PDAT", "PIS", "PIAT", "PIDAT", "PPER", "PPOSS",
            "PPOSAT", "PRELS", "PRELAT", "PRF", "PWS", "PWAT", "PWAV", "PAV", "PTKZU", "PTKNEG", "PTKVZ", "PTKANT",
            "PTKA", "TRUNC", "VVFIN", "VVIMP", "VVINF", "VVIZU", "VVPP", "VAFIN", "VAIMP", "VAINF", "VAPP", "VMFIN",
            "VMINF", "VMPP", "XY", "$,", "$.", "$(" };

    //so we can use relative dimensions throughout the program
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private double width = screenSize.getWidth();
    private double height = screenSize.getHeight();

    // constructor
    GUI()
    {
        // top window- set size and name of window
        frame = new JFrame("KWIC search");

        // main panel with borderlayout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // separators
        Dimension smallSep = new Dimension((int) width / 200, 0);
        Dimension bigSep = new Dimension((int) width / 130, 0);
        Dimension verSep = new Dimension(0, (int) height / 80);

        // northPanel with boxlayout
        JPanel northPanel = new JPanel();
        BoxLayout bl = new BoxLayout(northPanel, Y_AXIS);
        northPanel.setLayout(bl);

        // north part of north panel- flowlayout- for the searcb ar
        JPanel npNorth = new JPanel();
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        npNorth.setLayout(fl);
        // so the labels aren't directly on the side
        npNorth.add(Box.createRigidArea(new Dimension((int) width / 50, 0)));

        // URL
        JLabel urlLabel = new JLabel("URL/Filename:");
        urlLabel.setToolTipText("Where should we look for the search term?");
        npNorth.add(urlLabel);
        urlField = new JTextField((int) width / 50);

        // buttons to select if it's a URL or file or wiki
        urlInput = new JRadioButton("URL");
        urlInput.setToolTipText("e.g. https://docs.oracle.com/javase/tutorial/uiswing/components/tooltip.html");
        fileInput = new JRadioButton("File");
        fileInput.setToolTipText("e.g. C:\\Users\\User1\\Corpus\\AEcorpus.txt");
        wikiInput = new JRadioButton("Wiki");
        wikiInput.setToolTipText("an article you want to look for, e.g. programmer");
        //buttongroup so you can't select more than one button
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(urlInput);
        buttonGroup.add(fileInput);
        buttonGroup.add(wikiInput);
        npNorth.add(urlField);
        npNorth.add(urlInput);
        npNorth.add(fileInput);
        npNorth.add(wikiInput);
        //select URL input as default
        urlInput.setSelected(true);
        // add space
        npNorth.add(Box.createRigidArea(bigSep));

        // south panel of north panel- flowlayout
        JPanel npSouth = new JPanel();
        FlowLayout fL = new FlowLayout(FlowLayout.LEFT);
        npSouth.setLayout(fL);
        npSouth.add(Box.createRigidArea(new Dimension((int) width / 44, 0)));

        // Search term
        JLabel searchLabel = new JLabel("Search term:");
        searchLabel.setToolTipText("What would you like to search for?");
        npSouth.add(searchLabel);
        searchBox = new JTextField((int) width / 75);
        npSouth.add(searchBox);
        npSouth.add(Box.createRigidArea(smallSep));

        //POS box
        JLabel POSLabel = new JLabel("POS:");
        POSLabel.setToolTipText("Would you like to search for the word with a specific POS?");
        npSouth.add(POSLabel);
        posList = new JComboBox<>(englishPOS);
        npSouth.add(posList);
        npSouth.add(Box.createRigidArea(smallSep));

        //N-gram box
        JLabel ngramLabel = new JLabel("N-grams:");
        ngramLabel.setToolTipText("How many context words be displayed on either side of the search term?");
        npSouth.add(ngramLabel);
        String[] ngrams = { "sentence", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        ngramList = new JComboBox<>(ngrams);
        npSouth.add(ngramList);
        npSouth.add(Box.createRigidArea(smallSep));

        //search button
        JButton searchButton = new JButton("search");
        searchButton.setToolTipText("Let's find it!");
        searchButton.addActionListener(new SearchButtonHandler());
        npSouth.add(searchButton);
        npSouth.add(Box.createRigidArea(bigSep));

        // westPanel- vertical boxlayout for buttons
        JPanel westPanel = new JPanel();
        BoxLayout bL = new BoxLayout(westPanel, Y_AXIS);
        westPanel.setLayout(bL);
        westPanel.setBorder(BorderFactory.createLineBorder(new Color(223, 240, 255), 8));
        // language buttons- change POS tag list
        english = new JRadioButton("English");
        german = new JRadioButton("German");

        // edit list of pos tags based on language selected
        english.addActionListener(e ->
        {
            posList.removeAllItems();
            for (String s : englishPOS)
                posList.addItem(s);
        });
        german.addActionListener(e ->
        {
            posList.removeAllItems();
            for (String s : germanPOS)
                posList.addItem(s);
        });
        //so you can only select one button
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(english);
        buttonGroup1.add(german);
        westPanel.add(english);
        westPanel.add(german);
        westPanel.add(Box.createRigidArea(verSep));
        // statistics button
        JButton statsbutton = new JButton("statistics");
        statsbutton.addActionListener(new StatisticsButtonHandler());
        westPanel.add(statsbutton);
        westPanel.add(Box.createRigidArea(verSep));
        // Saving
        JButton saveButton = new JButton("save");
        saveButton.setToolTipText("Save results!");
        saveButton.addActionListener(new SaveButtonHandler());
        westPanel.add(saveButton);
        westPanel.add(Box.createRigidArea(verSep));
        // Clearing
        JButton clearButton = new JButton("clear all");
        clearButton.setToolTipText("Clear all results and search terms");
        westPanel.add(clearButton);
        clearButton.addActionListener(new ClearButtonHandler());
        westPanel.add(Box.createRigidArea(verSep));
        // Help button
        JButton helpButton = new JButton("help");
        clearButton.setToolTipText("Confused?");
        westPanel.add(helpButton);
        helpButton.addActionListener(new HelpButtonHandler());

        // centerPanel- boxlayout containing sentences and POS tags
        JPanel centerPanel = new JPanel();
        BoxLayout BL = new BoxLayout(centerPanel, BoxLayout.X_AXIS);
        centerPanel.setLayout(BL);

        // centerLeft- sentences containing word
        JPanel centerLeft = new JPanel();
        String[] defaultSentences = { "<html>Welcome to KWIC! Please click me</html>" };
        //can demo program on sentence
        sentenceList = new JList<>(defaultSentences);
        sentenceList.setPreferredSize(new Dimension((int) width / 4, (int) height / 5));
        sentenceList.setFixedCellHeight(24);
        sentenceList.setVisibleRowCount(24);
        sentenceList.addListSelectionListener(new SentenceListHandler());
        scrollPane = new JScrollPane(sentenceList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        centerLeft.add(scrollPane);

        // centerRight- word, lemma, pos tags
        JPanel centerRight = new JPanel();
        String[] columnNames = { "Word", "Lemma", "POS Tags" };
        Object[][] data = new Object[30][3];
        resultTable = new JTable(data, columnNames);
        TableColumn column = null;
        // for each column, set preferred width
        for (int i = 0; i < 3; i++)
        {
            column = resultTable.getColumnModel().getColumn(i);
            column.setPreferredWidth((int) (width / 15));
        }
        resultTable.setRowHeight((int) (height / 40));
        // so you can't edit it
        resultTable.setEnabled(false);
        resultTable.setPreferredScrollableViewportSize(new Dimension((int) width / 4, (int) (height / 1.95)));
        JScrollPane sP = new JScrollPane(resultTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JTableHeader tH = resultTable.getTableHeader(); // for assigning colours
        centerRight.add(sP);

        // add sub-panels to panels
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

        // AESTHETICS
        // fonts
        Font medFont = new Font("Serif", Font.PLAIN, 18);
        Font smallFont = new Font("Serif", Font.PLAIN, 14);
        saveButton.setFont(smallFont);
        clearButton.setFont(smallFont);
        statsbutton.setFont(smallFont);
        tH.setFont(smallFont);
        sentenceList.setFont(medFont);
        resultTable.setFont(medFont);
        helpButton.setFont(smallFont);
        // colours
        panel.setBackground(Color.white);
        npNorth.setBackground(new Color(223, 240, 255));
        npSouth.setBackground(new Color(223, 240, 255));
        westPanel.setBackground(new Color(223, 240, 255));
        centerPanel.setBackground(new Color(223, 240, 255));
        centerLeft.setBackground(new Color(223, 240, 255));
        centerRight.setBackground(new Color(223, 240, 255));
        urlInput.setBackground(new Color(223, 240, 255));
        fileInput.setBackground(new Color(223, 240, 255));
        wikiInput.setBackground(new Color(223, 240, 255));
        english.setBackground(new Color(223, 240, 255));
        german.setBackground(new Color(223, 240, 255));
        saveButton.setBackground(Color.white);
        posList.setBackground(Color.white);
        ngramList.setBackground(Color.white);
        searchButton.setBackground(Color.white);
        resultTable.setBackground(Color.white);
        tH.setBackground(Color.white);
        clearButton.setBackground(Color.white);
        helpButton.setBackground(Color.white);
        statsbutton.setBackground(Color.white);

        //so it fits the screen
        frame.pack();
    }

    /**
     * Private class to close stop the program when the window is closed
     */
    private class WindowListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    }

    /**
     * Private class to save the search results into an XML file of the user's choice
     */
    private class SaveButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //listModel in order to access all elements in JList
            ListModel model = sentenceList.getModel();

            if (model.getSize() == 0) { //if results were cleared previously
                JOptionPane.showMessageDialog(frame, "Oops! You can't save empty results", "Saving error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            //prompt for input
            String fileName = JOptionPane.showInputDialog(frame, "Please enter a filename:");

            //so that an exception isn't thrown when the user closes the dialog box
            if (fileName != null && !fileName.equals("")) {

                ArrayList<String> listContents = new ArrayList<>();

                // add all the elements in JList to model as strings (Object by default)
                for (int i = 0; i < model.getSize(); i++) {
                    listContents.add(model.getElementAt(i).toString());
                }

                //tags differently depending on english or german
                try {
                    if (english.isSelected()) {
                        Saving.saveToFile(listContents, fileName, "English");
                        Path p = Paths.get(fileName);
                        JOptionPane.showMessageDialog(frame, "File has been saved in " + p, "Save successful!",
                                JOptionPane.PLAIN_MESSAGE);
                    } else {
                        Saving.saveToFile(listContents, fileName, "German");
                        Path p = Paths.get(fileName);
                        JOptionPane.showMessageDialog(frame, "File has been saved in " + p, "Save successful!",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                } catch (XMLStreamException x1) {
                    JOptionPane.showMessageDialog(frame, "Error in serializing XML.", "Saving error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(frame, "There was an error saving the file.", "Saving error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Private class which carries out the search
     */
    private class SearchButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //see if there's a POSTag, what the keyword to search is, and where to search
            String POStag = posList.getSelectedItem().toString();
            String keyword = searchBox.getText();
            String toSearch = urlField.getText();
            //if empty, show an error message
            if (keyword.equals("")) {
                JOptionPane.showMessageDialog(frame,
                        "You did not enter a keyword!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            //how many context words to display
            int contextWords;
            if (ngramList.getSelectedItem().equals("sentence"))
            {
                contextWords = 100;
            } else
            {
                contextWords = Integer.parseInt((String) ngramList.getSelectedItem());
            }
            try
            {
                double startTime = System.nanoTime(); // mark point from which time can be measured
                String reader;
                List<String> wikiText;
                //fetch from different sources depending on button selected
                if (urlInput.isSelected())
                {
                    POSTagging.fetchFromUrl(toSearch);
                    reader = POSTagging.readSentencesFromFile("url.txt");
                } else if (wikiInput.isSelected()) {
                    if (english.isSelected())
                    {
                        wikiText = POSTagging.fetchFromWikipedia(toSearch, "English");
                    } else
                    {
                        wikiText = POSTagging.fetchFromWikipedia(toSearch, "German");
                    }
                    //if there's no identical match, get list of possible articles they could want
                    if(wikiText.size() != 0) {
                        String[] suggestedTerms = new String[wikiText.size()];
                        for (int i = 0; i < suggestedTerms.length; i++) {
                            suggestedTerms[i] = wikiText.get(i);
                        }
                        String selection = (String) JOptionPane.showInputDialog(frame, "Did you mean...?", "Disambiguation",
                                JOptionPane.PLAIN_MESSAGE, null, suggestedTerms, suggestedTerms[0]);

                        if((selection!=null) && (selection.length() > 0)) { // if item in list is not empty
                            urlField.setText(selection); // change text in urlField to correspond to selected item
                            startTime = System.nanoTime(); // overwrite startTime (measurement starts from here)
                            if(english.isSelected()){
                                POSTagging.fetchFromWikipedia(selection, "English");
                            }
                            else{ // if German is selected
                                POSTagging.fetchFromWikipedia(selection, "German");
                            }
                        }
                        else{
                            return; // do nothing if no item in list selected
                        }
                    }
                    reader = POSTagging.readSentencesFromFile("wiki.txt"); // file to store wiki output
                } else {
                    reader = POSTagging.readSentencesFromFile(toSearch);
                }

                String[] sents;

                // choose which opennlp models to use based on language selected
                if (english.isSelected())
                {
                    sents = POSTagging.sentenceDetector(reader, "models/en-sent.bin");
                } else {
                    sents = POSTagging.sentenceDetector(reader, "models/de-sent.bin");
                }
                ArrayList<String> sentsWithKeyword = finder.getSentencesWithKeyWord(sents, keyword);
                ArrayList<String> ngramsWithKeyword = finder.generateNgrams(sentsWithKeyword, keyword, contextWords);

                if (english.isSelected())
                {
                    tagList = finder.generateTagList(sentsWithKeyword, keyword, "models/en-token.bin",
                            "models/en-pos-maxent.bin");
                } else {
                    tagList = finder.generateTagList(sentsWithKeyword, keyword, "models/de-token.bin",
                            "models/de-pos-maxent.bin");
                }

                int maxWidth = 0;
                String[] filteredSentences;

                // If there is a POSTag we have to take that into consideration and search with it
                if (!POStag.isEmpty())
                {
                    ArrayList<String> ngramsWithTag;
                    if (english.isSelected())
                    {
                        ngramsWithTag = finder.getNgramsWithCorrectPOSTag(ngramsWithKeyword, tagList, keyword, POStag, "models/en-token.bin",
                                "models/en-pos-maxent.bin");
                    } else
                    {
                        ngramsWithTag = finder.getNgramsWithCorrectPOSTag(ngramsWithKeyword, tagList, keyword, POStag, "models/de-token.bin",
                                "models/de-pos-maxent.bin");
                    }

                    //search time
                    searchTime = (System.nanoTime() - startTime) * Math.pow(10, -9);

                    filteredSentences = new String[ngramsWithTag.size()];
                    // this array is used to figure out how wide the cells in the Jlist should be
                    int[] filteredSentencesLength = new int[ngramsWithTag.size()];
                    for (int i = 0; i < filteredSentences.length; i++)
                    {
                        filteredSentences[i] = "<html>" + ngramsWithTag.get(i) + "</html>";
                        filteredSentencesLength[i] = filteredSentences[i].length();
                    }

                    // find the biggest value in the int array
                    for (int aSentenceLength : filteredSentencesLength) {
                        if (aSentenceLength > maxWidth) {
                            maxWidth = aSentenceLength;
                        }
                    }

                    // set the control boolean to true
                    wordAndTagSearchDone = true;
                    wordSearchDone = false;

                } else
                {
                    filteredSentences = new String[ngramsWithKeyword.size()];
                    // this array is used to figure out how wide the cells in the Jlist should be
                    int[] filteredSentencesLength = new int[ngramsWithKeyword.size()];
                    for (int i = 0; i < filteredSentences.length; i++)
                    {
                        filteredSentences[i] = "<html>" + ngramsWithKeyword.get(i) + "</html>";
                        // add the length of each item into the int array
                        filteredSentencesLength[i] = filteredSentences[i].length();
                    }
                    searchTime = (System.nanoTime() - startTime) * Math.pow(10, -9);

                    // find the biggest value in the int array
                    for (int aSentenceLength : filteredSentencesLength) {
                        if (aSentenceLength > maxWidth) {
                            maxWidth = aSentenceLength;
                        }
                    }

                    // set the control boolean to true
                    wordSearchDone = true;
                    wordAndTagSearchDone = false;
                }

                // multiply longest sentence length by 9 and use that as cellwidth
                maxWidth *= 9;

                // re-initialise sentence list

                sentenceList = new JList<>(filteredSentences);
                sentenceList.setFont(new Font("Serif", Font.PLAIN, 18));
                sentenceList.setFixedCellHeight(24);
                sentenceList.setFixedCellWidth(maxWidth);
                sentenceList.setVisibleRowCount(24);
                sentenceList.addListSelectionListener(new SentenceListHandler());

                // replace old scrollpane with new results
                scrollPane.setViewportView(sentenceList);

            } catch (MalformedURLException m)
            {
                JOptionPane.showMessageDialog(frame,
                        "Text could not be fetched from website",
                        "URL error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IOException i)
            {
                JOptionPane.showMessageDialog(frame,
                        "Text could not be fetched from file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Private class to clear both output fields, as well as all text boxes, and reset the combo boxes, in the program
     */
    private class ClearButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            // confirmation the user wants to clear it
            int n = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete everything?", "Warning!",
                    JOptionPane.YES_NO_OPTION);
            //if yes
            if (n == JOptionPane.YES_OPTION)
            {
                // clears sentence list
                DefaultListModel model = new DefaultListModel();
                sentenceList.setModel(model);
                model.removeAllElements();
                // clears table
                for (int i = 0; i < resultTable.getRowCount(); i++)
                {
                    for (int j = 0; j < 3; j++)
                        resultTable.setValueAt("", i, j);
                }
                // clear text boxes
                urlField.setText("");
                searchBox.setText("");
                posList.setSelectedIndex(0);
                ngramList.setSelectedIndex(0);
                urlInput.setSelected(true);
                // set the control booleans for stats to false;
                wordSearchDone = false;
                wordAndTagSearchDone = false;
            }
            //if they close or say no to clearing, do nothing
            else if (n == JOptionPane.CLOSED_OPTION || n == JOptionPane.NO_OPTION)
            { }
        }
    }

    /**
     * Private class to launch the help button in a new window, reading the input from the different tabs from
     * html files (for formatting reasons)
     */
    private class HelpButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //set visibility, size, and make top frame non-resizable
            JFrame helpFrame = new JFrame("Help");
            helpFrame.setVisible(true);
            helpFrame.setSize((int) width / 3, (int) height / 2);
            helpFrame.setResizable(false);
            //tabbed pane within frame
            helpPane = new JTabbedPane();
            JPanel panel1 = new JPanel();
            //so it doesn't go outside of bounds
            panel1.setLayout(new GridLayout(1, 1));
            //inner pannel containing text box
            JPanel innPanel = new JPanel();
            //instructions on how to use the program
            JEditorPane helpInstructions = new JEditorPane("text/html", "");
            helpInstructions.setEditable(false);
            helpInstructions.setPreferredSize(new Dimension((int) (width / 3.5), (int) (height / 2.5)));
            try
            {
                helpInstructions.setText(POSTagging.readSentencesFromFile("help/helpText.html"));
            } catch (IOException i)
            {
                JOptionPane.showMessageDialog(frame,
                        "A problem occurred",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            JScrollPane sPane = new JScrollPane(helpInstructions, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            sPane.setVisible(true);
            innPanel.add(sPane);
            panel1.add(innPanel);
            helpPane.addTab("How to use the program", panel1);
            //second tab- containing POS meanings
            JPanel panel2 = new JPanel();
            panel2.setLayout(new GridLayout(1, 1));
            JPanel innerPanel = new JPanel();
            JEditorPane postagList = new JEditorPane("text/html", "");
            postagList.setEditable(false);
            postagList.setPreferredSize(new Dimension((int) (width / 3.5), (int) (height / 2.5)));
            try
            {
                postagList.setText(POSTagging.readSentencesFromFile("help/helpPOS.html"));
            } catch (IOException i)
            {
                JOptionPane.showMessageDialog(frame,
                        "A problem occurred",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            JScrollPane scrollPane = new JScrollPane(postagList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setVisible(true);
            innerPanel.add(scrollPane);
            panel2.add(innerPanel);
            helpPane.addTab("POS tag meanings", panel2);

            helpFrame.getContentPane().add(helpPane);

            //AESTHETICS
            helpPane.setBackground(new Color(223, 240, 255));
            panel1.setBackground(Color.white);
            panel2.setBackground(new Color(223, 240, 255));
            innerPanel.setBackground(new Color(223, 240, 255));
            innPanel.setBackground(new Color(223, 240, 255));
            frame.setBackground(Color.white);
        }
    }

    /**
     * Private class to generate the tags and lemmas for the table on the right
     */
    private class SentenceListHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            //if the sentence is empty, do nothing
            if (sentenceList.getModel().getSize() == 0)
            { } else
            {
                // clear the table before putting in new content
                for (int i = 0; i < resultTable.getRowCount(); i++)
                {
                    for (int j = 0; j < 3; j++)
                        resultTable.setValueAt("", i, j);
                }

                //remove the html tags that have been added for formatting earlier
                String theSentence = "";
                String listContent = sentenceList.getSelectedValue().substring(6,
                        sentenceList.getSelectedValue().length() - 7);
                String[] tmp1 = listContent.split("\\s+");
                String[] tmp2 = new String[tmp1.length];

                // find the marked keyword and remove the <b></b>
                for (int i = 0; i < tmp1.length; i++)
                {
                    if (tmp1[i].startsWith("<b>"))
                    {
                        tmp2[i] = tmp1[i].substring(3, tmp1[i].length() - 4);
                    } else
                    {
                        tmp2[i] = tmp1[i];
                    }
                }

                for (String item : tmp2)
                {
                    theSentence += item + " ";
                }
                theSentence = theSentence.substring(0, theSentence.length() - 1);
                try
                {
                    String[] tokens, tags, lemmas;
                    //tag differently depending on language
                    if (english.isSelected())
                    {
                        tokens = POSTagging.tokenizer(theSentence, "models/en-token.bin");
                        tags = POSTagging.postagger(tokens, "models/en-pos-maxent.bin");
                        lemmas = POSTagging.lemmatizer(tokens, tags, "models/en-lemmatizer.bin");
                    } else
                    {
                        tokens = POSTagging.tokenizer(theSentence, "models/de-token.bin");
                        tags = POSTagging.postagger(tokens, "models/de-pos-maxent.bin");
                        lemmas = POSTagging.lemmatizer(tokens, tags, "models/de-lemmatizer.bin");
                    }

                    //adjust the length of the table
                    String[] columnNames = { "Word", "Lemma", "POS Tags" };
                    DefaultTableModel model = new DefaultTableModel(tokens.length, 3);
                    model.setColumnIdentifiers(columnNames);
                    resultTable.setModel(model);

                    //fill in the tokens in the first column
                    for (int i = 0; i < tokens.length; i++)
                    {
                        resultTable.setValueAt(tokens[i], i, 0);
                    }
                    //fill in the lemmas in the second column
                    for (int i = 0; i < tokens.length; i++)
                    {
                        resultTable.setValueAt(lemmas[i], i, 1);
                    }
                    //fill in the tags in the third column
                    for (int i = 0; i < tokens.length; i++)
                    {
                        resultTable.setValueAt(tags[i], i, 2);
                    }
                } catch (IOException e1)
                {
                    JOptionPane.showMessageDialog(frame,
                            "Tokenisation failed :(",
                            "Tokenisation error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Private class which generates the statistics for a selected sentence
     */
    private class StatisticsButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String output = "";
            // this block alters the message in the messageDialog depending on whether there has been a word without
            // tag search, a word with tag search, or neither
            if (wordSearchDone)
            {
                output = "The word \"" + searchBox.getText() + "\" has been found " + finder.getKeyWordCount()
                        + " times in " + finder.getSentencesWithKeyWordCount() + " out of " + finder.getSentenceCount()
                        + " sentences." + "\n\n" + "POS Tag Distribution: \n";

                // make a new ArrayList and add the unique items in the tagList to it
                ArrayList<String> uniqueTags = new ArrayList<>();
                for (String item : tagList)
                {
                    if (!uniqueTags.contains(item))
                    {
                        uniqueTags.add(item);
                    }
                }

                // go through the list of unique tags and find the amount of times the tags occurs in tagList then
                // add this information to the output string
                for (String uniqueTagsItem : uniqueTags)
                {
                    int count = 0;
                    for (String tagListItem : tagList)
                    {
                        if (tagListItem.equals(uniqueTagsItem))
                        {
                            count++;
                        }
                    }
                    double percentage =  (double) count * 100 / tagList.size();
                    DecimalFormat df = new DecimalFormat("####0.00");
                    output += uniqueTagsItem + ": " + count + "/" + tagList.size() + " (" + df.format(percentage) + "%)" + "\n";
                }

                //add search time
                output +=  "\n\nThe search took " + searchTime + " seconds.";
            }
            else {
                if (wordAndTagSearchDone)
                {
                    output = "The word \"" + searchBox.getText() + "\" with the tag \""
                            + posList.getSelectedItem().toString() + "\" has been found " + finder.getKeyWordCount()
                            + " times in " + finder.getSentenceCount() + " sentences." + "\n\nThe search took " +
                            searchTime + " seconds.";
                } else {
                    output = "There is nothing to show statistics for!";
                }
            }
            // show a message dialog with the output string
            JOptionPane.showMessageDialog(frame, output, "Statistics", 1);
        }
    }

    // make a window
    public static void main(String[] args)
    {
        new GUI();
        //choose between english or german
        Object[] options = {"English", "German"};
        int n = JOptionPane.showOptionDialog(frame,
                "Please choose a language:",
                "Welcome to KWIC search!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);
        //if english, in english
        if (n == JOptionPane.YES_OPTION) {
            english.setSelected(true);
            german.setEnabled(false);
        }
        //if german, in german
        else if (n == JOptionPane.NO_OPTION) {
            german.setSelected(true);
            english.setEnabled(false);
        }
        //if they press x, exit program
        else {
            System.exit(0);
        }
    }

}
