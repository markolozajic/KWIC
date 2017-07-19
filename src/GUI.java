import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

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

    private JFrame frame; // top window
    private JTextField urlField; // to enter URL
    private JTextField searchBox; // the thing to search
    private JComboBox<String> ngramList; // how many context words
    private JComboBox<String> posList; // the different POS tags
    private JList<String> sentenceList; // sentences word has been found in
    private JTable resultTable; // table displaying word, lemma, and POS tags
    // types of input
    private JRadioButton urlInput;
    private JRadioButton fileInput;
    private JRadioButton wikiInput;
    // languages
    private JRadioButton english;
    private JScrollPane scrollPane;
    private JTabbedPane helpPane;

    // need this to be an instance variable so i can access the instance
    // variables of the keywordfinder class in the statistics
    private KeyWordFinder finder = new KeyWordFinder();
    private ArrayList<String> tagList = new ArrayList<>();

    // these are control booleans that are changed in the searchButtonHandler
    // and used in the statsButtonHandler
    private boolean wordSearchDone = false;
    private boolean wordAndTagSearchDone = false;

    //number that gives the searchTime in the statsButton
    private double searchTime;

    // POS tags for english and german
    private String[] englishPOS = { "", "CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP",
            "NNPS", "PDT", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB", "VBD", "VBG", "VBN",
            "VBP", "VBZ", "WDT", "WP", "WP$", "WRB" };
    private String[] germanPOS = { "", "ADJA", "ADJD", "ADV", "APPR", "APPRART", "APPO", "APZR", "ART", "CARD", "FM", "ITJ",
            "KOUI", "KOUS", "KON", "KOKOM", "NN", "NE", "PDS", "PDAT", "PIS", "PIAT", "PIDAT", "PPER", "PPOSS",
            "PPOSAT", "PRELS", "PRELAT", "PRF", "PWS", "PWAT", "PWAV", "PAV", "PTKZU", "PTKNEG", "PTKVZ", "PTKANT",
            "PTKA", "TRUNC", "VVFIN", "VVIMP", "VVINF", "VVIZU", "VVPP", "VAFIN", "VAIMP", "VAINF", "VAPP", "VMFIN",
            "VMINF", "VMPP", "XY", "$,", "$.", "$(" };

    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private double width = screenSize.getWidth();
    private double height = screenSize.getHeight();

    // constructor
    GUI()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        // top window- set size and name of window
        frame = new JFrame("KWIC search");
        // get the size of the screen it's on, so that everything is relative to
        // screen size
        double windowWidth = width * 0.7;
        double windowHeight = height * 0.7;
        frame.setSize((int) windowWidth, (int) windowHeight);

        // main panel with borderlayout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // separators
        Dimension smallSep = new Dimension((int) width / 200, 0);
        Dimension bigSep = new Dimension((int) width / 130, 0);
        Dimension borderSep = new Dimension((int) width / 50, 0);
        Dimension verSep = new Dimension(0, (int) height / 80);

        // northPanel with boxlayout
        JPanel northPanel = new JPanel();
        BoxLayout bl = new BoxLayout(northPanel, Y_AXIS);
        northPanel.setLayout(bl);

        // north part of north panel- flowlayout
        JPanel npNorth = new JPanel();
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        npNorth.setLayout(fl);
        // so the labels aren't directly on the side
        npNorth.add(Box.createRigidArea(borderSep));

        // URL
        JLabel urlLabel = new JLabel("URL/Filename:");
        urlLabel.setToolTipText("Where should we look for the search term?");
        npNorth.add(urlLabel);
        urlField = new JTextField((int) width / 50);
        urlField.setForeground(Color.black);

        // buttons to select if it's a URL or file
        urlInput = new JRadioButton("URL");
        urlInput.setToolTipText("e.g. https://docs.oracle.com/javase/tutorial/uiswing/components/tooltip.html");
        fileInput = new JRadioButton("File");
        fileInput.setToolTipText("e.g. C:\\Users\\User1\\Corpus\\AEcorpus.txt");
        wikiInput = new JRadioButton("Wiki");
        wikiInput.setToolTipText("an article you want to look for, e.g. programmer");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(urlInput);
        buttonGroup.add(fileInput);
        buttonGroup.add(wikiInput);
        npNorth.add(urlField);
        npNorth.add(urlInput);
        npNorth.add(fileInput);
        npNorth.add(wikiInput);
        urlInput.setSelected(true);

        // space between them
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

        JLabel POSLabel = new JLabel("POS:");
        POSLabel.setToolTipText("Would you like to search for the word with a specific POS?");
        npSouth.add(POSLabel);
        posList = new JComboBox<>(englishPOS);
        npSouth.add(posList);
        npSouth.add(Box.createRigidArea(smallSep));

        JLabel ngramLabel = new JLabel("N-grams:");
        ngramLabel.setToolTipText("How many context words be displayed on either side of the search term?");
        npSouth.add(ngramLabel);
        String[] ngrams = { "sentence", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        ngramList = new JComboBox<>(ngrams);
        npSouth.add(ngramList);
        npSouth.add(Box.createRigidArea(smallSep));

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
        // language buttons
        english = new JRadioButton("English");
        JRadioButton german = new JRadioButton("German");
        english.setSelected(true);
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
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(english);
        buttonGroup1.add(german);
        westPanel.add(english);
        westPanel.add(german);
        westPanel.add(Box.createRigidArea(verSep));
        // add fun buttons here
        JButton but1 = new JButton("statistics");
        but1.addActionListener(new StatisticsButtonHandler());
        westPanel.add(but1);
        westPanel.add(Box.createRigidArea(verSep));
        JButton but2 = new JButton("fun button");
        but2.addActionListener(new FunButton2Handler());
        westPanel.add(but2);
        westPanel.add(Box.createRigidArea(verSep));
        JButton but3 = new JButton("fun button");
        but3.addActionListener(new FunButton3Handler());
        westPanel.add(but3);
        westPanel.add(Box.createRigidArea(new Dimension(0, (int) height / 20)));
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

        // centerPanel- boxlayout containing main two boxes
        JPanel centerPanel = new JPanel();
        BoxLayout BL = new BoxLayout(centerPanel, BoxLayout.X_AXIS);
        centerPanel.setLayout(BL);

        // centerLeft- sentences containing word
        JPanel centerLeft = new JPanel();
        String[] defaultSentences = { "<html>Welcome to KWIC! Please click me</html>" };
        // if we use the same "sentenceList" as in the listSelectionListener, we
        // could show the magic of our program
        // on the default sentence!
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
        but1.setFont(smallFont);
        but2.setFont(smallFont);
        but3.setFont(smallFont);
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
        but1.setBackground(Color.white);
        but2.setBackground(Color.white);
        but3.setBackground(Color.white);
    }

    // so that when you close a window the window actually closes
    private class WindowListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    }

    // actionlisteners
    private class SaveButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String fileName = JOptionPane.showInputDialog(frame, "Please enter a filename:");
            if (fileName != null && !"".equals(fileName)) {

                ArrayList<String> listContents = new ArrayList<>();

                ListModel model = sentenceList.getModel(); // have to make listModel
                // to access all
                // elements in JList

                // add all the elements in JList to model as strings (Object by
                // default)

                for (int i = 0; i < model.getSize(); i++) {
                    listContents.add(model.getElementAt(i).toString());
                }

                try {
                    if (english.isSelected()) {
                        Saving.saveToFile(listContents, fileName, "models/en-token.bin", "models/en-pos-maxent.bin",
                                "models/en-lemmatizer.bin");
                        Path p = Paths.get(fileName);
                        JOptionPane.showMessageDialog(frame, "File has been saved in " + p, "Save successful!",
                                JOptionPane.PLAIN_MESSAGE);
                    } else {
                        Saving.saveToFile(listContents, fileName, "models/de-token.bin", "models/de-pos-maxent.bin",
                                "models/de-lemmatizer.bin");
                        Path p = Paths.get(fileName);
                        JOptionPane.showMessageDialog(frame, "File has been saved in " + p, "Save successful!",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(frame, "There was an error saving the file.", "Saving error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class SearchButtonHandler implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {

            String tag = posList.getSelectedItem().toString();
            String toSearch = searchBox.getText();
            String url = urlField.getText();
            if (toSearch.equals(""))
            {
            	JOptionPane.showMessageDialog(frame,
                        "You did not enter a keyword!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            
            int contextWords = 0;
            if (ngramList.getSelectedItem().equals("sentence"))
            {
                contextWords = 100;
            } else
            {
                contextWords = Integer.parseInt((String) ngramList.getSelectedItem());
            }
            try
            {
            	double startTime = System.nanoTime();
                String reader;
                List<String> wikiText;
                if (urlInput.isSelected())
                {
                    POSTagging.fetchFromUrl(url);
                    reader = POSTagging.readSentencesFromFile("url.txt");
                } else if (wikiInput.isSelected()) {
                    if (english.isSelected())
                    {
                        wikiText = POSTagging.fetchFromWikipedia(url, "English");
                    } else
                    {
                        wikiText = POSTagging.fetchFromWikipedia(url, "German");
                    }
                    if(wikiText.size() != 0) {
                        String[] wow = new String[wikiText.size()];
                        for (int i = 0; i < wow.length; i++) {
                            wow[i] = wikiText.get(i);
                        }
                        String s = (String) JOptionPane.showInputDialog(frame, "Pick a search term", "Input",
                                JOptionPane.PLAIN_MESSAGE, null, wow, wow[0]);
                        //System.out.println(s);
                        if((s!=null) && (s.length() > 0)) { // if item in list is not empty
                            urlField.setText(s);
                            startTime = System.nanoTime();
                            if(english.isSelected()){
                                POSTagging.fetchFromWikipedia(s, "English");
                            }
                            else{
                                POSTagging.fetchFromWikipedia(s, "German");
                            }
                        }
                        else{
                            return;
                        }
                    }
                    reader = POSTagging.readSentencesFromFile("wiki.txt");
                } else {
                    reader = POSTagging.readSentencesFromFile(url);
                }
                // look for topic on
                // wikipedia, save text to file
                // readSentencesFromFile method has to make sure to read from
                // the file the previous method just created,
                // so the long parameter string is an attempt to predict what
                // the filename will look like

                String[] sents;
                if (english.isSelected())
                {
                    sents = POSTagging.sentenceDetector(reader, "models/en-sent.bin");
                } else
                {
                    sents = POSTagging.sentenceDetector(reader, "models/de-sent.bin");
                }
                ArrayList<String> tmp1 = finder.getSentencesWithKeyWord(sents, toSearch);
                ArrayList<String> tmp2 = finder.generateNgrams(tmp1, toSearch, contextWords);

                if (english.isSelected())
                {
                    tagList = finder.generateTagList(tmp1, toSearch, "models/en-token.bin",
                            "models/en-pos-maxent.bin");
                } else
                {
                    tagList = finder.generateTagList(tmp1, toSearch, "models/de-token.bin",
                            "models/de-pos-maxent.bin");
                }

                // If there is a POSTag we have to take that into consideration
                if (!tag.isEmpty())
                {
                    ArrayList<String> tmp3;
                    if (english.isSelected())
                    {
                        tmp3 = finder.getNgramsWithCorrectPOSTag(tmp2, tagList, toSearch, tag, "models/en-token.bin",
                                "models/en-pos-maxent.bin");
                    } else
                    {
                        tmp3 = finder.getNgramsWithCorrectPOSTag(tmp2, tagList, toSearch, tag, "models/de-token.bin",
                                "models/de-pos-maxent.bin");
                    }

                    //search time
                    searchTime = (System.nanoTime() - startTime) * Math.pow(10, -9);

                    String[] filteredSentences = new String[tmp3.size()];
                    // this array is used to figure out how wide the cells in
                    // the Jlist should be
                    int[] filteredSentencesLength = new int[tmp3.size()];

                    for (int i = 0; i < filteredSentences.length; i++)
                    {
                        filteredSentences[i] = "<html>" + tmp3.get(i) + "</html>";
                        filteredSentencesLength[i] = filteredSentences[i].length();
                    }

                    int maxWidth = 0;
                    // find the biggest value in the int array
                    for (int i = 0; i < filteredSentencesLength.length; i++)
                    {
                        if (filteredSentencesLength[i] > maxWidth)
                        {
                            maxWidth = filteredSentencesLength[i];
                        }
                    }
                    // multiply it by 6.3 and use that as cellwidth
                    maxWidth *= 6.3;

                    // set the control boolean to true
                    wordAndTagSearchDone = true;
                    wordSearchDone = false;

                    // the following block of code is just repeating what is
                    // already
                    // written above (look for "centerLeft")
                    // surely there is a way to avoid this?
                    sentenceList = new JList<>(filteredSentences);
                    sentenceList.setFont(new Font("Serif", Font.PLAIN, 18));
                    sentenceList.setFixedCellHeight(24);
                    sentenceList.setFixedCellWidth(maxWidth);
                    sentenceList.setVisibleRowCount(24);
                    sentenceList.addListSelectionListener(new SentenceListHandler());

                    scrollPane.setViewportView(sentenceList); // replace old
                    // scrollpane
                } else
                {
                    String[] filteredSentences = new String[tmp2.size()];
                    // this array is used to figure out how wide the cells in
                    // the Jlist should be
                    int[] filteredSentencesLength = new int[tmp2.size()];

                    for (int i = 0; i < filteredSentences.length; i++)
                    {
                        filteredSentences[i] = "<html>" + tmp2.get(i) + "</html>";
                        // add the length of each item into the int array
                        filteredSentencesLength[i] = filteredSentences[i].length();
                    }
                    searchTime = (System.nanoTime() - startTime) * Math.pow(10, -9);


                    int maxWidth = 0;
                    // find the biggest value in the int array
                    for (int i = 0; i < filteredSentencesLength.length; i++)
                    {
                        if (filteredSentencesLength[i] > maxWidth)
                        {
                            maxWidth = filteredSentencesLength[i];
                        }
                    }
                    // multiply it by 6.3 and use that as cellwidth
                    maxWidth *= 6.3;

                    // set the control boolean to true
                    wordSearchDone = true;
                    wordAndTagSearchDone = false;

                    // the following block of code is just repeating what is
                    // already
                    // written above (look for "centerLeft")
                    // surely there is a way to avoid this?
                    sentenceList = new JList<>(filteredSentences);
                    sentenceList.setFont(new Font("Serif", Font.PLAIN, 18));
                    sentenceList.setFixedCellHeight(24);
                    sentenceList.setFixedCellWidth(maxWidth);
                    sentenceList.setVisibleRowCount(24);
                    sentenceList.addListSelectionListener(new SentenceListHandler());
                    
                  
                    // replace old scrollpane
                    scrollPane.setViewportView(sentenceList); 
 
                }
            } catch (MalformedURLException m)
            {
                JOptionPane.showMessageDialog(frame,
                        "Text could not be fetched from URL",
                        "URL error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IOException i)
            {
                i.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Text could not be fetched from file \n Word could not be found in file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ClearButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            // confirmation the user wants to clear it
            int n = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete everything?", "Warning!",
                    JOptionPane.YES_NO_OPTION);
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
                wikiInput.setSelected(true);

                // set the control booleans for stats to false;
                wordSearchDone = false;
                wordAndTagSearchDone = false;

            } else if (n == JOptionPane.CLOSED_OPTION || n == JOptionPane.NO_OPTION)
            {
                // do nothing
            }
        }
    }

    private class HelpButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {

            JFrame helpFrame = new JFrame("Help");
            helpFrame.setVisible(true);
            helpFrame.setSize((int) width / 3, (int) height / 2);
            helpFrame.setResizable(false);
            Dimension size = helpFrame.getSize();
            helpPane = new JTabbedPane();
            JPanel panel1 = new JPanel();;
            panel1.setLayout(new GridLayout(1, 1));
            JPanel innPanel = new JPanel();
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

            helpPane.setBackground(new Color(223, 240, 255));
            panel1.setBackground(new Color(223, 240, 255));
            panel2.setBackground(new Color(223, 240, 255));
            innerPanel.setBackground(new Color(223, 240, 255));
            innPanel.setBackground(new Color(223, 240, 255));
        }
    }

    private class SentenceListHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            if (sentenceList.getModel().getSize() == 0)
            {
                // do nothing
            } else
            {

                // clear the table before putting in new content
                for (int i = 0; i < resultTable.getRowCount(); i++)
                {
                    for (int j = 0; j < 3; j++)
                        resultTable.setValueAt("", i, j);
                }

                // this block removes the html tags that have been added for
                // formatting earlier
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

    private class StatisticsButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String output = "";
            // this block alters the message in the messageDialog depending on
            // whether there has been a word without tag search, a word with tag
            // search, or neither
            if (wordSearchDone)
            {
                output = "The word \"" + searchBox.getText() + "\" has been found " + finder.getKeyWordCount()
                        + " times in " + finder.getSentencesWithKeyWordCount() + " out of " + finder.getSentenceCount()
                        + " sentences." + "\n\n" + "POS Tag Distribution: \n";

                // make a new ArrayList and add the unique items in the tagList
                // to it
                ArrayList<String> uniqueTags = new ArrayList<>();
                for (String item : tagList)
                {
                    if (!uniqueTags.contains(item))
                    {
                        uniqueTags.add(item);
                    }
                }

                // go through the list of unique tags and find the amount of
                // times the tags occurs in tagList then add this information to
                // the ouput string
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

            } else
            {
                if (wordAndTagSearchDone)
                {
                    output = "The word \"" + searchBox.getText() + "\" with the tag \""
                            + posList.getSelectedItem().toString() + "\" has been found " + finder.getKeyWordCount()
                            + " times in " + finder.getSentencesWithKeyWordCount() + " out of "
                            + finder.getSentenceCount() + " sentences." + "\n\nThe search took " + searchTime + " seconds.";
                } else
                {
                    output = "There is nothing to show statistics for!";
                }
            }

            // show a message dialog with the output string
            JOptionPane.showMessageDialog(frame, output, "Statistics", 1);

        }
    }

    private class FunButton2Handler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            // whatever this button is actually gonna do
        }
    }

    private class FunButton3Handler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            // whatever this button is actually gonna do
        }
    }

    // make a window
    public static void main(String[] args)
    {
        new GUI();
    }
}