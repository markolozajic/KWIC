import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static javax.swing.BoxLayout.Y_AXIS;

public class GUI extends JPanel {

    private JFrame frame; // top window
    private JTextField urlField; // to enter URL
    private JTextField searchBox; // the thing to search
    private JComboBox<String> ngramList; // how many context words
    private JComboBox<String> posList; //the different POS tags
    private JList<String> sentenceList; // sentences word has been found in
    private JTable resultTable; // table displaying word, lemma, and POS tags
    //types of input
    private JRadioButton fileInput;
    private JRadioButton urlInput;
    private JRadioButton wikiInput;
    //languages
    private JRadioButton english;
    private JRadioButton german;
    private JScrollPane scrollPane;

    String[] englishPOS = {"", "CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP",
            "NNPS", "PDT", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB", "VBD", "VBG",
            "VBN", "VBP", "VBZ", "WDT", "WP", "WP$", "WRB"};
    String[] germanPOS = {"", "ADJA", "ADJD", "ADV", "APPR", "APPRART", "APPO", "APZR", "ART", "CARD", "FM", "ITJ",
            "KOUI", "KOUS", "KON", "KOKOM", "NN", "NE", "PDS", "PDAT", "PIS", "PIAT", "PIDAT", "PPER", "PPOSS",
            "PPOSAT", "PRELS", "PRELAT", "PRF", "PWS", "PWAT", "PWAV", "PAV", "PTKZU", "PTKNEG", "PTKVZ", "PTKANT",
            "PTKA", "TRUNC", "VVFIN", "VVIMP", "VVINF", "VVIZU", "VVPP", "VAFIN", "VAIMP", "VAINF", "VAPP", "VMFIN",
            "VMINF", "VMPP", "XY", "$,", "$.", "$("};

    // constructor
    GUI() {

        // top window- set size and name of window
        frame = new JFrame("KWIC search");
        // get the size of the screen it's on, so that everything is relative to
        // screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double windowWidth = width * 0.7;
        double windowHeight = height * 0.7;
        // set window size relative to screen
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

        // if the box has text in it, keep it- if not, give example URL
        urlField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                urlField.setText("");
                urlField.setForeground(Color.black);
            }

            public void focusLost(FocusEvent e) {
                while (urlField.getText().equals("")) {
                    urlField.setForeground(Color.gray);
                    urlField.setText("https://docs.oracle.com/javase/7/docs/api/javax/swing/JTextField.html");
                }
            }
        });

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
        String[] ngrams = {"sentence", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
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
        //language buttons
        english = new JRadioButton("English");
        german = new JRadioButton("German");
        english.setSelected(true);
        english.addActionListener(e -> {
            posList.removeAllItems();
            for (String s : englishPOS)
                posList.addItem(s);
        });
        german.addActionListener(e -> {
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
        westPanel.add(Box.createRigidArea(new Dimension(0, (int) height/20)));
        //Saving
        JButton saveButton = new JButton("save");
        saveButton.setToolTipText("Save results!");
        saveButton.addActionListener(new SaveButtonHandler());
        westPanel.add(saveButton);
        westPanel.add(Box.createRigidArea(verSep));
        //Clearing
        JButton clearButton = new JButton("clear all");
        clearButton.setToolTipText("Clear all results and search terms");
        westPanel.add(clearButton);
        clearButton.addActionListener(new ClearButtonHandler());
        westPanel.add(Box.createRigidArea(verSep));

        // centerPanel- boxlayout containing main two boxes
        JPanel centerPanel = new JPanel();
        BoxLayout BL = new BoxLayout(centerPanel, BoxLayout.X_AXIS);
        centerPanel.setLayout(BL);

        // centerLeft- sentences containing word
        JPanel centerLeft = new JPanel();
        String[] defaultSentences = {"<html>Welcome to KWIC! Please click me</html>"};
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
        String[] columnNames = {"Word", "Lemma", "POS Tags"};
        Object[][] data = new Object[100][3];
        resultTable = new JTable(data, columnNames);
        TableColumn column = null;
        // for each column, set preferred width
        for (int i = 0; i < 3; i++) {
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
        // later
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
        but1.setBackground(Color.white);
        but2.setBackground(Color.white);
        but3.setBackground(Color.white);
    }

    // so that when you close a window the window actually closes
    private class WindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    // actionlisteners
    private class SaveButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String fileName = JOptionPane.showInputDialog(frame, "Please enter a filename:");

            ArrayList<String> listContents = new ArrayList<>();

            ListModel model = sentenceList.getModel(); // have to make listModel to access all elements in JList

            // add all the elements in JList to model as strings (Object by default)

            for(int i = 0; i<model.getSize(); i++){
                listContents.add(model.getElementAt(i).toString());
            }

            try {
                Saving.saveToFile(listContents, fileName);
                Path p = Paths.get(fileName);
                JOptionPane.showMessageDialog(frame, "File has been saved in " + p,
                        "Save successful!", JOptionPane.PLAIN_MESSAGE);
            } catch (IOException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(frame, "There was an error saving the file.",
                        "Saving error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SearchButtonHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String tag = posList.getSelectedItem().toString();
            String toSearch = searchBox.getText();
            String url = urlField.getText();
            int contextWords = 0;
            if (ngramList.getSelectedItem().equals("sentence")) {
                contextWords = 100;
            } else {
                contextWords = Integer.parseInt((String) ngramList.getSelectedItem());
            }
            try {
                /*
                 * I'm using the url field here, but it's very simple to
				 * reassign this to another one if need be. What it's doing at
				 * the moment is expecting a simple string as input (e.g.
				 * "helen keller"), and attempts to find an English wikipedia
				 * article with such a title. If not found, an exception is
				 * thrown. Note: for now we only have a general IOException,
				 * maybe we could consider making more specific ones for
				 * different failures (FileNotFound, URL not found) so we could
				 * inform the user more precisely what went wrong?
				 */
                POSTagging.fetchFromWikipedia(url); // look for topic on
                // wikipedia, save text to
                // file
                // readSentencesFromFile method has to make sure to read from
                // the file the previous method just created,
                // so the long parameter string is an attempt to predict what
                // the filename will look like
                String reader = POSTagging.readSentencesFromFile(url.replaceAll(" ", "_") + ".txt");
                String[] sents = POSTagging.sentenceDetector(reader);
                ArrayList<String> tmp1 = keyWordFinder.getSentencesWithKeyWord(sents, toSearch);
                ArrayList<String> tmp2 = keyWordFinder.generateNgrams(tmp1, toSearch, contextWords);


                // If there is a POSTag we have to take that into consideration
                if (!tag.isEmpty()) {
                    ArrayList<String> tmp3 = keyWordFinder.getNgramsWithCorrectPOSTag(tmp2, toSearch, tag);

                    String[] filteredSentences = new String[tmp3.size()];
                    // this array is used to figure out how wide the cells in
                    // the Jlist should be
                    int[] filteredSentencesLength = new int[tmp3.size()];

                    for (int i = 0; i < filteredSentences.length; i++) {
                        filteredSentences[i] = "<html>" + tmp3.get(i) + "</html>";
                        filteredSentencesLength[i] = filteredSentences[i].length();
                    }

                    int maxWidth = 0;
                    // find the biggest value in the int array
                    for(int i = 0; i < filteredSentencesLength.length; i++) {
                        if (filteredSentencesLength[i] > maxWidth) {
                            maxWidth = filteredSentencesLength[i];
                        }
                    }
                    // multiply it by 6.5 and use that as cellwidth
                    maxWidth *= 6.3;

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
                } else {

                    String[] filteredSentences = new String[tmp2.size()];
                    // this array is used to figure out how wide the cells in
                    // the Jlist should be
                    int[] filteredSentencesLength = new int[tmp2.size()];

                    for (int i = 0; i < filteredSentences.length; i++) {
                        filteredSentences[i] = "<html>" + tmp2.get(i) + "</html>";
                        // add the length of each item into the int array
                        filteredSentencesLength[i] = filteredSentences[i].length();
                    }

                    int maxWidth = 0;
                    // find the biggest value in the int array
                    for (int i = 0; i < filteredSentencesLength.length; i++) {
                        if (filteredSentencesLength[i] > maxWidth) {
                            maxWidth = filteredSentencesLength[i];
                        }
                    }
                    // multiply it by 6.5 and use that as cellwidth
                    maxWidth *= 6.3;

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
                }
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
            //confirmation the user wants to clear it
            int n = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete everything?",
                    "Warning!", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                //clears sentence list
                DefaultListModel model = new DefaultListModel();
                sentenceList.setModel(model);
                model.removeAllElements();
                //clears table
                for (int i = 0; i < resultTable.getRowCount(); i++) {
                    for (int j = 0; j < 3; j++)
                        resultTable.setValueAt("", i, j);
                }

                //clear text boxes
                urlField.setText("");
                searchBox.setText("");
                posList.setSelectedIndex(0);
                ngramList.setSelectedIndex(0);

            } else if (n == JOptionPane.CLOSED_OPTION || n == JOptionPane.NO_OPTION){
                //do nothing
            }
        }
    }

    private class SentenceListHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (sentenceList.getModel().getSize() == 0) {
                //do nothing
            } else {

                //clear the table before putting in new content
                for (int i = 0; i < resultTable.getRowCount(); i++) {
                    for (int j = 0; j < 3; j++)
                        resultTable.setValueAt("", i, j);
                }

                //this block removes the html tags that have been added for formatting earlier
                String theSentence = "";
                String listContent = sentenceList.getSelectedValue().substring(6, sentenceList.getSelectedValue().length() - 7);
                String[] tmp1 = listContent.split("\\s+");
                String[] tmp2 = new String[tmp1.length];

                //find the marked keyword and remove the <b></b>
                for (int i = 0; i < tmp1.length; i++) {
                    if (tmp1[i].startsWith("<b>")) {
                        tmp2[i] = tmp1[i].substring(3, tmp1[i].length() - 4);
                    } else {
                        tmp2[i] = tmp1[i];
                    }

                }

                for (String item : tmp2) {
                    theSentence += item + " ";
                }
                theSentence = theSentence.substring(0, theSentence.length() - 1);


                try {
                    String[] tokenized = POSTagging.tokenizer(theSentence);
                    String[] tagged = POSTagging.postagger(tokenized);
                    String[] lemmas = POSTagging.lemmatizer(tokenized, tagged);

                    for (int i = 0; i < tokenized.length; i++) {
                        resultTable.setValueAt(tokenized[i], i, 0);
                    }

                    for (int i = 0; i < tokenized.length; i++) {
                        resultTable.setValueAt(lemmas[i], i, 1);
                    }

                    for (int i = 0; i < tokenized.length; i++) {
                        resultTable.setValueAt(tagged[i], i, 2);
                    }

                } catch (IOException e1) {
                    System.out.println("Cannot tokenize for some reason");
                }

                // maybe format somehow, table or at least separate columns could
                // look neater?
                // word, pos tags and lemma in a table (if that's a thing)

            }
        }
    }
//
//    private void languageChanger() {
//        if (english.isSelected())
//            posList = new JComboBox<>(englishPOS);
//        if (german.isSelected())
//            posList = new JComboBox<>(germanPOS);
//    }

    private class FunButton1Handler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // whatever this button is actually gonna do
        }
    }

    private class FunButton2Handler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // whatever this button is actually gonna do
        }
    }

    private class FunButton3Handler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // whatever this button is actually gonna do
        }
    }

    // make a window
    public static void main(String[] args) {
        new GUI();
    }
}