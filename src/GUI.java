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
    private JScrollPane scrollPane;
    private JTabbedPane helpPane;

    //POS tags for english and german
    String[] englishPOS = {"", "CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP",
            "NNPS", "PDT", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB", "VBD", "VBG",
            "VBN", "VBP", "VBZ", "WDT", "WP", "WP$", "WRB"};
    String[] germanPOS = {"", "ADJA", "ADJD", "ADV", "APPR", "APPRART", "APPO", "APZR", "ART", "CARD", "FM", "ITJ",
            "KOUI", "KOUS", "KON", "KOKOM", "NN", "NE", "PDS", "PDAT", "PIS", "PIAT", "PIDAT", "PPER", "PPOSS",
            "PPOSAT", "PRELS", "PRELAT", "PRF", "PWS", "PWAT", "PWAV", "PAV", "PTKZU", "PTKNEG", "PTKVZ", "PTKANT",
            "PTKA", "TRUNC", "VVFIN", "VVIMP", "VVINF", "VVIZU", "VVPP", "VAFIN", "VAIMP", "VAINF", "VAPP", "VMFIN",
            "VMINF", "VMPP", "XY", "$,", "$.", "$("};

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double width = screenSize.getWidth();
    double height = screenSize.getHeight();

    // constructor
    GUI() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        // top window- set size and name of window
        frame = new JFrame("KWIC search");
        // get the size of the screen it's on, so that everything is relative to screen size
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
        JRadioButton german = new JRadioButton("German");
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
        //Help button
        JButton helpButton = new JButton("help");
        clearButton.setToolTipText("Confused?");
        westPanel.add(helpButton);
        clearButton.addActionListener(new HelpButtonHandler());

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
                if(english.isSelected()){
                    Saving.saveToFile(listContents, fileName,"models/en-token.bin",
                            "models/en-pos-maxent.bin", "models/en-lemmatizer.bin");
                    Path p = Paths.get(fileName);
                    JOptionPane.showMessageDialog(frame, "File has been saved in " + p,
                            "Save successful!", JOptionPane.PLAIN_MESSAGE);
                }
                else{
                    Saving.saveToFile(listContents, fileName,"models/de-token.bin",
                            "models/de-pos-maxent.bin", "models/de-lemmatizer.bin");
                    Path p = Paths.get(fileName);
                    JOptionPane.showMessageDialog(frame, "File has been saved in " + p,
                            "Save successful!", JOptionPane.PLAIN_MESSAGE);
                }
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
				 * "helen keller"), and attempts to find an English/German wikipedia
				 * article with such a title. If not found, an exception is
				 * thrown. Note: for now we only have a general IOException,
				 * maybe we could consider making more specific ones for
				 * different failures (FileNotFound, URL not found) so we could
				 * inform the user more precisely what went wrong?
				 */
                if(english.isSelected()) {
                    POSTagging.fetchFromWikipedia(url,"English");
                }
                else{
                    POSTagging.fetchFromWikipedia(url,"German");
                }
                // look for topic on
                // wikipedia, save text to
                // file
                // readSentencesFromFile method has to make sure to read from
                // the file the previous method just created,
                // so the long parameter string is an attempt to predict what
                // the filename will look like
                String reader = POSTagging.readSentencesFromFile(url.replaceAll(" ", "_") + ".txt");
                String[] sents;
                if(english.isSelected()) {
                    sents = POSTagging.sentenceDetector(reader, "models/en-sent.bin");
                }
                else{
                    sents = POSTagging.sentenceDetector(reader, "models/de-sent.bin");
                }
                ArrayList<String> tmp1 = keyWordFinder.getSentencesWithKeyWord(sents, toSearch);
                ArrayList<String> tmp2 = keyWordFinder.generateNgrams(tmp1, toSearch, contextWords);


                // If there is a POSTag we have to take that into consideration
                if (!tag.isEmpty()) {
                    ArrayList<String> tmp3;
                    if(english.isSelected()) {
                        tmp3 = keyWordFinder.getNgramsWithCorrectPOSTag(tmp2, toSearch, tag,
                                "models/en-token.bin", "models/en-pos-maxent.bin");
                    }
                    else {
                        tmp3 = keyWordFinder.getNgramsWithCorrectPOSTag(tmp2, toSearch, tag,
                                "models/de-token.bin", "models/de-pos-maxent.bin");
                    }

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

    private class HelpButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            //doesn't work
            //panel doesn't show up
            //but it's a start

            helpPane = new JTabbedPane();
            helpPane.setSize((int) width/5, (int) height/5);
            helpPane.setVisible(true);
            JPanel panel1 = new JPanel();
            //construct jpanel
            helpPane.addTab("How to use the program", panel1);
            JPanel panel2 = new JPanel();
            JTextField postagList = new JTextField("English POS tags:\nCC Coordinating conjunction\nCD Cardinal number\n" +
                    "DT Determiner\nEX Existential there\nFW Foreign word\nIN Preposition or subordinating conjunction\n" +
                    "JJ Adjective\nJJR Adjective, comparative\nJJS Adjective, superlative\nLS List item marker\n" +
                    "MD Modal\nNN Noun, singular or mass\nNNS Noun, plural\nNNP Proper noun, singular\n" +
                    "NNPS Proper noun, plural\nPDT Predeterminer\nPOS Possessive ending\nPRP Personal pronoun\n" +
                    "PRP$ Possessive pronoun\nRB Adverb\nRBR Adverb, comparative\nRBS Adverb, superlative\n" +
                    "RP Particle\nSYM Symbol\nTO to\nUH Interjection\nVB Verb, base form\nVBD Verb, past tense\n" +
                    "VBG Verb, gerund or present participle\nVBN Verb, past participle\n" +
                    "VBP Verb, non\u00AD3rd person singular present\nVBZ Verb, 3rd person singular present\n" +
                    "WDT Wh\u00ADdeterminer\nWP Wh\u00ADpronoun\nWP$ Possessive wh\u00ADpronoun\n" +
                    "WRB Wh\u00ADadverb\n" +
                    "German POS tags:\nADJA\tattributives Adjektiv.\t[das] große [Haus]\n" +
                    "ADJD\tadverbiales oder prädikatives Adjektiv\t[er fährt] schnell, [er ist] schnell\n" +
                    " \t \t \n" +
                    "ADV\tAdverb\tschon, bald, doch\n" +
                    " \t \t \n" +
                    "APPR\tPräposition; Zirkumposition links\tin [der Stadt], ohne [mich]\n" +
                    "APPRART\tPräposition mit Artikel\tim [Haus], zur [Sache]\n" +
                    "APPO\tPostposition\t[ihm] zufolge, [der Sache] wegen\n" +
                    "APZR\tZirkumposition rechts\t[von jetzt] an\n" +
                    " \t \t \n" +
                    "ART\tbestimmter oder unbestimmter Artikel\tder, die, das, ein, eine\n" +
                    " \t \t \n" +
                    "CARD\tKardinalzahl\tzwei [Männer], [im Jahre] 1994\n" +
                    " \t \t \n" +
                    "FM\tFremdsprachliches Material\t[Er hat das mit ``] A big fish ['' übersetzt]\n" +
                    " \t \t \n" +
                    "ITJ\tInterjektion\tmhm, ach, tja\n" +
                    " \t \t \n" +
                    "KOUI\tunterordnende Konjunktion mit ``zu'' und Infinitiv\tum [zu leben], anstatt [zu fragen]\n" +
                    "KOUS\tunterordnende Konjunktion mit Satz\tweil, dass, damit, wenn, ob\n" +
                    "KON\tnebenordnende Konjunktion\tund, oder, aber\n" +
                    "KOKOM\tVergleichskonjunktion\tals, wie\n" +
                    " \t \t \n" +
                    "NN\tnormales Nomen\tTisch, Herr, [das] Reisen\n" +
                    "NE\tEigennamen\tHans, Hamburg, HSV\n" +
                    " \t \t \n" +
                    "PDS\tsubstituierendes Demonstrativpronomen\tdieser, jener\n" +
                    "PDAT\tattribuierendes Demonstrativpronomen\tjener [Mensch]\n" +
                    " \t \t \n" +
                    "PIS\tsubstituierendes Indefinitpronomen\tkeiner, viele, man, niemand\n" +
                    "PIAT\tattribuierendes Indefinitpronomen ohne Determiner\tkein [Mensch], irgendein [Glas]\n" +
                    "PIDAT\tattribuierendes Indefinitpronomen mit Determiner\t[ein] wenig [Wasser], [die] beiden [Brüder]\n" +
                    " \t \t \n" +
                    "PPER\tirreflexives Personalpronomen\tich, er, ihm, mich, dir\n" +
                    " \t \t \n" +
                    "PPOSS\tsubstituierendes Possessivpronomen\tmeins, deiner\n" +
                    "PPOSAT\tattribuierendes Possessivpronomen\tmein [Buch], deine [Mutter]\n" +
                    " \t \t \n" +
                    "PRELS\tsubstituierendes Relativpronomen\t[der Hund ,] der\n" +
                    "PRELAT\tattribuierendes Relativpronomen\t[der Mann ,] dessen [Hund]\n" +
                    " \t \t \n" +
                    "PRF\treflexives Personalpronomen\tsich, einander, dich, mir\n" +
                    " \t \t \n" +
                    "PWS\tsubstituierendes Interrogativpronomen\twer, was\n" +
                    "PWAT\tattribuierendes Interrogativpronomen\twelche[Farbe], wessen [Hut]\n" +
                    "PWAV\tadverbiales Interrogativ- oder Relativpronomen\twarum, wo, wann, worüber, wobei\n" +
                    " \t \t \n" +
                    "PAV\tPronominaladverb\tdafür, dabei, deswegen, trotzdem\n" +
                    " \t \t \n" +
                    "PTKZU\t``zu'' vor Infinitiv\tzu [gehen]\n" +
                    "PTKNEG\tNegationspartikel\tnicht\n" +
                    "PTKVZ\tabgetrennter Verbzusatz\t[er kommt] an, [er fährt] rad\n" +
                    "PTKANT\tAntwortpartikel\tja, nein, danke, bitte\n" +
                    "PTKA\tPartikel bei Adjektiv oder Adverb\tam [schönsten], zu [schnell]\n" +
                    " \t \t \n" +
                    "TRUNC\tKompositions-Erstglied\tAn- [und Abreise]\n" +
                    " \t \t \n" +
                    "VVFIN\tfinites Verb, voll\t[du] gehst, [wir] kommen [an]\n" +
                    "VVIMP\tImperativ, voll\tkomm [!]\n" +
                    "VVINF\tInfinitiv, voll\tgehen, ankommen\n" +
                    "VVIZU\tInfinitiv mit ``zu'', voll\tanzukommen, loszulassen\n" +
                    "VVPP\tPartizip Perfekt, voll\tgegangen, angekommen\n" +
                    "VAFIN\tfinites Verb, aux\t[du] bist, [wir] werden\n" +
                    "VAIMP\tImperativ, aux\tsei [ruhig !]\n" +
                    "VAINF\tInfinitiv, aux\twerden, sein\n" +
                    "VAPP\tPartizip Perfekt, aux\tgewesen\n" +
                    "VMFIN\tfinites Verb, modal\tdürfen\n" +
                    "VMINF\tInfinitiv, modal\twollen\n" +
                    "VMPP\tPartizip Perfekt, modal\tgekonnt, [er hat gehen] können\n" +
                    " \t \t \n" +
                    "XY\tNichtwort, Sonderzeichen enthaltend\t3:7, H2O, D2XW3\n" +
                    " \t \t \n" +
                    "$,\tKomma\t,\n" +
                    "$.\tSatzbeendende Interpunktion\t. ? ! ; :\n" +
                    "$(\tsonstige Satzzeichen; satzintern\t- [,]()" );
            panel2.add(postagList);
            helpPane.addTab("POS tag meanings", panel2);

            frame.add(helpPane);

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
                    String[] tokens, tags, lemmas;
                    if(english.isSelected()){
                        tokens = POSTagging.tokenizer(theSentence,"models/en-token.bin");
                        tags = POSTagging.postagger(tokens,"models/en-pos-maxent.bin");
                        lemmas = POSTagging.lemmatizer(tokens, tags,"models/en-lemmatizer.bin");
                    }
                    else{
                        tokens = POSTagging.tokenizer(theSentence,"models/de-token.bin");
                        tags = POSTagging.postagger(tokens,"models/de-pos-maxent.bin");
                        lemmas = POSTagging.lemmatizer(tokens, tags,"models/de-lemmatizer.bin");
                    }


                    for (int i = 0; i < tokens.length; i++) {
                        resultTable.setValueAt(tokens[i], i, 0);
                    }

                    for (int i = 0; i < tokens.length; i++) {
                        resultTable.setValueAt(lemmas[i], i, 1);
                    }

                    for (int i = 0; i < tokens.length; i++) {
                        resultTable.setValueAt(tags[i], i, 2);
                    }

                } catch (IOException e1) {
                    System.out.println("Cannot tokenize for some reason");
                }
            }
        }
    }


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