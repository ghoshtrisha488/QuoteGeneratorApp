package com.example.quotegeneratorapp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel mainContainer;
    private static Image bgImage = null;
    private static Font customFont = null;
    private static Font footerFont = null;

    // Data Storage Maps & Files
    private static final HashMap<String, String> userDatabase = new HashMap<>();

    // Core Hierarchy Maps
    // Major Category -> List of Sub-genres
    private static final HashMap<String, String[]> categorySubGenres = new HashMap<>();
    // Sub-genre -> List of Quotes
    private static final HashMap<String, ArrayList<String>> subGenreQuotes = new HashMap<>();

    private static final String ACCOUNTS_FILE = "user_accounts.txt";
    private static final String CUSTOM_QUOTES_FILE = "my_custom_quotes.txt";
    private static final String DEVELOPER_SIGNATURE = "Developed by Trisha Ghosh";

    // Dynamic Tracking References
    private static JLabel quoteDisplayLabel;
    private static JPanel dynamicFolderPanel;
    private static JLabel categoryTitleLabel;
    private static String selectedSubGenre = "Goal-Getter";
    private static int currentQuoteIndex = 0;

    public static void main(String[] args) {
        loadLocalDatabase();
        initializeStructuredQuotesEngine();
        loadAssetsPipeline();

        // Establish System Frame
        frame = new JFrame("Premium Quote Vault App");
        frame.setSize(950, 650); // Increased slightly to comfortably fit the expansion layout
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Inject App Panels into Navigation Framework
        mainContainer.add(createAuthPanel(), "AUTH_PAGE");
        mainContainer.add(createHomePanel(), "HOME_PAGE");
        mainContainer.add(createQuoteViewerPanel(), "VIEWER_PAGE");

        frame.add(mainContainer);
        frame.setLocationRelativeTo(null);
        cardLayout.show(mainContainer, "AUTH_PAGE");
        frame.setVisible(true);
    }

    // --- ASSETS PIPELINE ---
    private static void loadAssetsPipeline() {
        try {
            java.net.URL bgURL = Main.class.getResource("/start_bg.jpg");
            if (bgURL != null) bgImage = new ImageIcon(bgURL).getImage();

            java.net.URL logoURL = Main.class.getResource("/app_logo.png");
            if (logoURL != null) frame.setIconImage(new ImageIcon(logoURL).getImage());

            java.net.URL fontURL = Main.class.getResource("/ananda-black-font/AnandaBlack.ttf");
            if (fontURL != null) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontURL.openStream());
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(baseFont);
                customFont = baseFont.deriveFont(Font.BOLD, 14f);
                footerFont = baseFont.deriveFont(Font.PLAIN, 11f);
            } else {
                customFont = new Font("Segoe UI", Font.BOLD, 14);
                footerFont = new Font("Segoe UI", Font.ITALIC, 11);
            }
        } catch (Exception e) {
            customFont = new Font("SansSerif", Font.BOLD, 13);
            footerFont = new Font("SansSerif", Font.ITALIC, 11);
        }
    }

    // --- DATA MANAGEMENT & ARCHITECTURE DEPLOYMENT ---
    private static void initializeStructuredQuotesEngine() {
        // 1. Productivity & Motivation Mapping
        categorySubGenres.put("Productivity & Motivation", new String[]{"Goal-Getter", "Resilience & Failure", "Leadership & Success"});

        ArrayList<String> goalGetter = new ArrayList<>();
        goalGetter.add("The secret of getting ahead is getting started. - Mark Twain");
        goalGetter.add("Action is the foundational key to all success. - Pablo Picasso");
        subGenreQuotes.put("Goal-Getter", goalGetter);

        ArrayList<String> resilienceFailure = new ArrayList<>();
        resilienceFailure.add("Failure is simply the opportunity to begin again, this time more intelligently. - Henry Ford");
        resilienceFailure.add("It's not whether you get knocked down, it's whether you get up. - Vince Lombardi");
        subGenreQuotes.put("Resilience & Failure", resilienceFailure);

        ArrayList<String> leadershipSuccess = new ArrayList<>();
        leadershipSuccess.add("Leadership is not about being in charge. It is about taking care of those in your charge. - Simon Sinek");
        leadershipSuccess.add("Success is walking from failure to failure with no loss of enthusiasm. - Winston Churchill");
        subGenreQuotes.put("Leadership & Success", leadershipSuccess);

        // 2. Wellness & Mindfulness Mapping
        categorySubGenres.put("Wellness & Mindfulness", new String[]{"Daily Morning Positivity", "Stress Relief & Peace", "Self-Love & Healing"});

        ArrayList<String> morningPositivity = new ArrayList<>();
        morningPositivity.add("Write it on your heart that every day is the best day in the year. - Ralph Waldo Emerson");
        morningPositivity.add("An early-morning walk is a blessing for the whole day. - Henry David Thoreau");
        subGenreQuotes.put("Daily Morning Positivity", morningPositivity);

        ArrayList<String> stressRelief = new ArrayList<>();
        stressRelief.add("Rule number one is, don't sweat the small stuff. Rule number two is, it's all small stuff. - Robert Eliot");
        stressRelief.add("Within you, there is a stillness and a sanctuary to which you can retreat at any time. - Hermann Hesse");
        subGenreQuotes.put("Stress Relief & Peace", stressRelief);

        ArrayList<String> selfLove = new ArrayList<>();
        selfLove.add("You yourself, as much as anybody in the entire universe, deserve your love and affection. - Buddha");
        selfLove.add("To love oneself is the beginning of a lifelong romance. - Oscar Wilde");
        subGenreQuotes.put("Self-Love & Healing", selfLove);

        // 3. Entertainment & Humor Mapping
        categorySubGenres.put("Entertainment & Humor", new String[]{"Witty & Sarcastic", "Pop Culture", "Internet/Gen Z Slang"});

        ArrayList<String> wittySarcastic = new ArrayList<>();
        wittySarcastic.add("I am so clever that sometimes I don't understand a single word of what I am saying. - Oscar Wilde");
        wittySarcastic.add("Light travels faster than sound. This is why some people appear bright until you hear them speak. - Alan Dundes");
        subGenreQuotes.put("Witty & Sarcastic", wittySarcastic);

        ArrayList<String> popCulture = new ArrayList<>();
        popCulture.add("May the Force be with you. - Star Wars");
        popCulture.add(" there is no place like home. - The Wizard of Oz");
        subGenreQuotes.put("Pop Culture", popCulture);

        ArrayList<String> genZSlang = new ArrayList<>();
        genZSlang.add("This app is absolute fire, no cap. - Internet");
        genZSlang.add("Let Trisha cook with this code setup. - Gen Z");
        subGenreQuotes.put("Internet/Gen Z Slang", genZSlang);

        // 4. Philosophy & Deep Thought Mapping
        categorySubGenres.put("Philosophy & Deep Thought", new String[]{"Stoicism", "Existential & Mystical", "Creativity & Art"});

        ArrayList<String> stoicism = new ArrayList<>();
        stoicism.add("You have power over your mind - not outside events. Realize this, and you will find strength. - Marcus Aurelius");
        stoicism.add("We suffer more often in imagination than in reality. - Seneca");
        subGenreQuotes.put("Stoicism", stoicism);

        ArrayList<String> existentialMystical = new ArrayList<>();
        existentialMystical.add("He who has a why to live for can bear almost any how. - Friedrich Nietzsche");
        existentialMystical.add("The universe is full of magical things, patiently waiting for our wits to grow sharper. - Eden Phillpotts");
        subGenreQuotes.put("Existential & Mystical", existentialMystical);

        ArrayList<String> creativityArt = new ArrayList<>();
        creativityArt.add("Creativity is intelligence having fun. - Albert Einstein");
        creativityArt.add("Every artist was first an amateur. - Ralph Waldo Emerson");
        subGenreQuotes.put("Creativity & Art", creativityArt);

        // 5. Social Media & Aesthetics Mapping
        categorySubGenres.put("Social Media & Aesthetics", new String[]{"Short & Punchy", "Trendy & Seasonal", "Aesthetic Vibe"});

        ArrayList<String> shortPunchy = new ArrayList<>();
        shortPunchy.add("Be yourself; everyone else is already taken. - Oscar Wilde");
        shortPunchy.add("Own your narrative.");
        subGenreQuotes.put("Short & Punchy", shortPunchy);

        ArrayList<String> trendySeasonal = new ArrayList<>();
        trendySeasonal.add("Live in the sunshine, swim the sea, drink the wild air. - Ralph Waldo Emerson");
        trendySeasonal.add("Fresh code, fresh system beginnings.");
        subGenreQuotes.put("Trendy & Seasonal", trendySeasonal);

        ArrayList<String> aestheticVibe = new ArrayList<>();
        aestheticVibe.add("Collect moments, not things. - Unknown");
        aestheticVibe.add("Chasing neon dreams and deep code bases. - Aesthetic");
        subGenreQuotes.put("Aesthetic Vibe", aestheticVibe);

        // User Custom Input Setup Directory Integration
        subGenreQuotes.put("My Custom Quotes", new ArrayList<>());
        loadCustomQuotes();
    }

    private static void loadLocalDatabase() {
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) userDatabase.put(parts[0], parts[1]);
            }
        } catch (IOException ignored) {}
    }

    private static void saveAccountToLocalDisk(String username, String password) {
        userDatabase.put(username, password);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE, true))) {
            writer.write(username + ":" + password);
            writer.newLine();
        } catch (IOException ignored) {}
    }

    private static void loadCustomQuotes() {
        File file = new File(CUSTOM_QUOTES_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                subGenreQuotes.get("My Custom Quotes").add(line);
            }
        } catch (IOException ignored) {}
    }

    private static void saveCustomQuoteToDisk(String quoteContent) {
        subGenreQuotes.get("My Custom Quotes").add(quoteContent);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOM_QUOTES_FILE, true))) {
            writer.write(quoteContent);
            writer.newLine();
        } catch (IOException ignored) {}
    }

    // --- UI DESIGN ENGINE ---
    private static JPanel createBackgroundPanel() {
        return new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(20, 18, 30));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
    }

    private static JLabel createFooterLabel() {
        JLabel footer = new JLabel(DEVELOPER_SIGNATURE, SwingConstants.CENTER);
        footer.setFont(footerFont);
        footer.setForeground(new Color(255, 255, 255, 130));
        return footer;
    }

    // VIEW 1: AUTHENTICATION
    private static JPanel createAuthPanel() {
        JPanel container = createBackgroundPanel();
        JPanel inputWrapper = new JPanel(new GridLayout(4, 2, 10, 15));
        inputWrapper.setOpaque(false);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(customFont);
        JTextField userField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(customFont);
        JPasswordField passField = new JPasswordField(15);

        JButton loginBtn = new JButton("LOGIN");
        JButton registerBtn = new JButton("REGISTER");
        styleButton(loginBtn, new Color(46, 204, 113));
        styleButton(registerBtn, new Color(52, 152, 219));

        inputWrapper.add(userLabel); inputWrapper.add(userField);
        inputWrapper.add(passLabel); inputWrapper.add(passField);
        inputWrapper.add(loginBtn);  inputWrapper.add(registerBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(40, 40, 40, 40);
        container.add(inputWrapper, gbc);

        gbc.gridy = 1; gbc.anchor = GridBagConstraints.SOUTH;
        container.add(createFooterLabel(), gbc);

        registerBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Fields cannot be empty!");
                return;
            }
            if (userDatabase.containsKey(user)) {
                JOptionPane.showMessageDialog(frame, "Username already exists!");
            } else {
                saveAccountToLocalDisk(user, pass);
                JOptionPane.showMessageDialog(frame, "Registration Successful! You can now Log in.");
            }
        });

        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            if (userDatabase.containsKey(user) && userDatabase.get(user).equals(pass)) {
                // Instantly select first category to default display list
                renderSubGenreFolders("Productivity & Motivation");
                cardLayout.show(mainContainer, "HOME_PAGE");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials. Try again!");
            }
        });

        return container;
    }

    // VIEW 2: SPLIT MATRIX HOME GENERICS DISPLAY
    private static JPanel createHomePanel() {
        JPanel container = createBackgroundPanel();
        JPanel masterLayout = new JPanel(new BorderLayout(15, 15));
        masterLayout.setOpaque(false);
        masterLayout.setPreferredSize(new Dimension(900, 500));

        // Top Main Title
        JLabel appHeader = new JLabel("QUOTE HUB EXPLORER SYSTEM", SwingConstants.CENTER);
        appHeader.setFont(customFont.deriveFont(22f));
        appHeader.setForeground(new Color(0, 240, 255));
        masterLayout.add(appHeader, BorderLayout.NORTH);

        // LEFT COLUMN: Major Category Nav Deck Buttons
        JPanel categoryNavPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        categoryNavPanel.setOpaque(false);

        String[] topCategories = {
                "Productivity & Motivation",
                "Wellness & Mindfulness",
                "Entertainment & Humor",
                "Philosophy & Deep Thought",
                "Social Media & Aesthetics",
                "Custom Dashboard"
        };

        for (String majorCat : topCategories) {
            JButton catBtn = new JButton(majorCat);
            styleButton(catBtn, new Color(44, 62, 80));
            catBtn.setFont(customFont.deriveFont(12f));
            catBtn.addActionListener(e -> renderSubGenreFolders(majorCat));
            categoryNavPanel.add(catBtn);
        }
        masterLayout.add(categoryNavPanel, BorderLayout.WEST);

        // CENTER COLUMN: Dynamic Workspace showing current Sub-genre file folders
        JPanel rightWorkspace = new JPanel(new BorderLayout(10, 15));
        rightWorkspace.setOpaque(false);

        categoryTitleLabel = new JLabel("Productivity & Motivation Sub-Directories", SwingConstants.LEFT);
        categoryTitleLabel.setFont(customFont.deriveFont(16f));
        categoryTitleLabel.setForeground(Color.YELLOW);
        rightWorkspace.add(categoryTitleLabel, BorderLayout.NORTH);

        dynamicFolderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        dynamicFolderPanel.setOpaque(false);
        rightWorkspace.add(dynamicFolderPanel, BorderLayout.CENTER);

        // Lower Custom Quote creation Pad panel area
        JPanel creatorArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        creatorArea.setOpaque(false);
        JTextField customField = new JTextField(30);
        JButton saveQuoteBtn = new JButton("WRITE YOUR OWN QUOTE");
        styleButton(saveQuoteBtn, new Color(234, 46, 121));

        creatorArea.add(customField);
        creatorArea.add(saveQuoteBtn);
        rightWorkspace.add(creatorArea, BorderLayout.SOUTH);

        masterLayout.add(rightWorkspace, BorderLayout.CENTER);

        // Global Grid Wrapper placements
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 0.9;
        container.add(masterLayout, gbc);

        gbc.gridy = 1; gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.SOUTH;
        container.add(createFooterLabel(), gbc);

        saveQuoteBtn.addActionListener(e -> {
            String customTxt = customField.getText().trim();
            if(!customTxt.isEmpty()) {
                saveCustomQuoteToDisk(customTxt + " - User Submission");
                customField.setText("");
                JOptionPane.showMessageDialog(frame, "Quote pinned inside 'My Custom Quotes' dashboard!");
                renderSubGenreFolders("Custom Dashboard"); // Refresh views if looking inside custom map
            } else {
                JOptionPane.showMessageDialog(frame, "Please type a quote string sequence first!");
            }
        });

        return container;
    }

    // CORE RENDERING METHOD FOR LOADING TWO-TIER SUB-GENRES
    private static void renderSubGenreFolders(String majorCategory) {
        categoryTitleLabel.setText(majorCategory + " Sub-Folders");
        dynamicFolderPanel.removeAll();

        String[] subFolders;
        if (majorCategory.equals("Custom Dashboard")) {
            subFolders = new String[]{"My Custom Quotes"};
        } else {
            subFolders = categorySubGenres.get(majorCategory);
        }

        if (subFolders != null) {
            for (String subFolderName : subFolders) {
                JButton folderBtn = new JButton("<html><center><font size='6'>📁</font><br><br>" + subFolderName + "</center></html>");
                styleButton(folderBtn, new Color(30, 130, 76));
                folderBtn.setPreferredSize(new Dimension(180, 140));

                folderBtn.addActionListener(e -> {
                    selectedSubGenre = subFolderName;
                    currentQuoteIndex = 0;
                    updateQuoteViewText();
                    cardLayout.show(mainContainer, "VIEWER_PAGE");
                });

                dynamicFolderPanel.add(folderBtn);
            }
        }

        dynamicFolderPanel.revalidate();
        dynamicFolderPanel.repaint();
    }

    // VIEW 3: CORE SLIDESHOW PIPELINE
    private static JPanel createQuoteViewerPanel() {
        JPanel container = createBackgroundPanel();
        JPanel structuralPanel = new JPanel(new BorderLayout(20, 40));
        structuralPanel.setOpaque(false);

        quoteDisplayLabel = new JLabel("", SwingConstants.CENTER);
        quoteDisplayLabel.setFont(customFont.deriveFont(18f));
        quoteDisplayLabel.setForeground(Color.WHITE);
        quoteDisplayLabel.setPreferredSize(new Dimension(650, 200));
        structuralPanel.add(quoteDisplayLabel, BorderLayout.CENTER);

        JPanel navActionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navActionRow.setOpaque(false);

        JButton prevBtn = new JButton("BACK");
        JButton nextBtn = new JButton("NEXT QUOTE");
        JButton homeReturnBtn = new JButton("CATEGORIES HOME");

        styleButton(prevBtn, new Color(127, 140, 141));
        styleButton(nextBtn, new Color(234, 46, 121));
        styleButton(homeReturnBtn, new Color(41, 128, 185));

        navActionRow.add(prevBtn);
        navActionRow.add(nextBtn);
        navActionRow.add(homeReturnBtn);
        structuralPanel.add(navActionRow, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        container.add(structuralPanel, gbc);

        gbc.gridy = 1; gbc.anchor = GridBagConstraints.SOUTH;
        container.add(createFooterLabel(), gbc);

        nextBtn.addActionListener(e -> {
            ArrayList<String> list = subGenreQuotes.get(selectedSubGenre);
            if (list != null && !list.isEmpty()) {
                currentQuoteIndex = (currentQuoteIndex + 1) % list.size();
                updateQuoteViewText();
            }
        });

        prevBtn.addActionListener(e -> {
            ArrayList<String> list = subGenreQuotes.get(selectedSubGenre);
            if (list != null && !list.isEmpty()) {
                currentQuoteIndex = (currentQuoteIndex - 1 + list.size()) % list.size();
                updateQuoteViewText();
            }
        });

        homeReturnBtn.addActionListener(e -> cardLayout.show(mainContainer, "HOME_PAGE"));

        return container;
    }

    private static void updateQuoteViewText() {
        ArrayList<String> list = subGenreQuotes.get(selectedSubGenre);
        if (list == null || list.isEmpty()) {
            quoteDisplayLabel.setText("<html><body style='text-align: center; width: 450px;'>No quotes logged into this folder yet. Use the Home Dashboard to write one!</body></html>");
        } else {
            String currentQuoteStr = list.get(currentQuoteIndex);
            quoteDisplayLabel.setText("<html><body style='text-align: center; width: 450px;'>" + currentQuoteStr + "</body></html>");
        }
    }

    private static void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }
}