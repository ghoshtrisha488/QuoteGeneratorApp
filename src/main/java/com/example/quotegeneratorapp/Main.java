package com.example.quotegeneratorapp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main {
    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel mainContainer;
    private static Image bgImage = null;
    private static Font customFont = null;
    private static Font footerFont = null;

    // Data Storage Maps & Files
    private static final HashMap<String, String> userDatabase = new HashMap<>();
    private static final HashMap<String, ArrayList<String>> genreQuotes = new HashMap<>();
    private static final String ACCOUNTS_FILE = "user_accounts.txt";
    private static final String CUSTOM_QUOTES_FILE = "my_custom_quotes.txt";
    private static final String DEVELOPER_SIGNATURE = "Developed by Trisha Ghosh";

    // Dynamic Tracking Reference for Quote Panel
    private static JLabel quoteDisplayLabel;
    private static String selectedGenre = "Motivational";
    private static int currentQuoteIndex = 0;

    public static void main(String[] args) {
        loadLocalDatabase();
        initializeQuotesEngine();
        loadAssetsPipeline();

        // Establish System Frame
        frame = new JFrame("Premium Quote Vault App");
        frame.setSize(800, 600);
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
                customFont = baseFont.deriveFont(Font.BOLD, 15f);
                footerFont = baseFont.deriveFont(Font.PLAIN, 11f);
            } else {
                customFont = new Font("Segoe UI", Font.BOLD, 15);
                footerFont = new Font("Segoe UI", Font.ITALIC, 11);
            }
        } catch (Exception e) {
            customFont = new Font("SansSerif", Font.BOLD, 14);
            footerFont = new Font("SansSerif", Font.ITALIC, 11);
        }
    }

    // --- DATA MANAGEMENT ---
    private static void initializeQuotesEngine() {
        ArrayList<String> motivational = new ArrayList<>();
        motivational.add("The only way to do great work is to love what you do. - Steve Jobs");
        motivational.add("Stay hungry, stay foolish. - Steve Jobs");
        motivational.add("The best way to predict the future is to invent it. - Alan Kay");
        genreQuotes.put("Motivational", motivational);

        ArrayList<String> funny = new ArrayList<>();
        funny.add("Code is like humor. When you have to explain it, it’s bad. - Cory House");
        funny.add("Java is to JavaScript what car is to Carpet. - Chris Heilmann");
        funny.add("Talk is cheap. Show me the code. - Linus Torvalds");
        genreQuotes.put("Funny", funny);

        ArrayList<String> deep = new ArrayList<>();
        deep.add("Simplicity is the soul of efficiency. - Austin Freeman");
        deep.add("Before software can be reusable it first has to be usable. - Ralph Johnson");
        deep.add("Fix the cause, not the symptom. - Steve Maguire");
        genreQuotes.put("Deep", deep);

        // Load custom user quotes into their own distinct folder
        genreQuotes.put("My Custom Quotes", new ArrayList<>());
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
                genreQuotes.get("My Custom Quotes").add(line);
            }
        } catch (IOException ignored) {}
    }

    private static void saveCustomQuoteToDisk(String quoteContent) {
        genreQuotes.get("My Custom Quotes").add(quoteContent);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOM_QUOTES_FILE, true))) {
            writer.write(quoteContent);
            writer.newLine();
        } catch (IOException ignored) {}
    }

    // --- UI DESIGN ENGINE & SUB-PANELS ---
    private static JPanel createBackgroundPanel() {
        return new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(25, 20, 35));
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

    // VIEW 1: AUTHENTICATION (LOGIN & REGISTRATION)
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

        // Grid Layout Insertion Mapping
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(40, 40, 40, 40);
        container.add(inputWrapper, gbc);

        gbc.gridy = 1; gbc.anchor = GridBagConstraints.SOUTH;
        container.add(createFooterLabel(), gbc);

        // Action Listeners
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
                cardLayout.show(mainContainer, "HOME_PAGE");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials. Try again!");
            }
        });

        return container;
    }

    // VIEW 2: HOME GENERICS DISPLAY (SELECT A GENRE / WRITE NEW QUOTE)
    private static JPanel createHomePanel() {
        JPanel container = createBackgroundPanel();
        JPanel contentGrid = new JPanel(new BorderLayout(20, 20));
        contentGrid.setOpaque(false);

        JLabel title = new JLabel("SELECT QUOTE GENRE", SwingConstants.CENTER);
        title.setFont(customFont.deriveFont(20f));
        title.setForeground(new Color(0, 240, 255));
        contentGrid.add(title, BorderLayout.NORTH);

        // Folders Selection Hub
        JPanel folderPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        folderPanel.setOpaque(false);

        String[] categories = {"Motivational", "Funny", "Deep", "My Custom Quotes"};
        for (String cat : categories) {
            JButton folderBtn = new JButton("<html><center>📁<br><br>" + cat + "</center></html>");
            styleButton(folderBtn, new Color(44, 62, 80));
            folderBtn.setPreferredSize(new Dimension(140, 140));
            folderBtn.addActionListener(e -> {
                selectedGenre = cat;
                currentQuoteIndex = 0;
                updateQuoteViewText();
                cardLayout.show(mainContainer, "VIEWER_PAGE");
            });
            folderPanel.add(folderBtn);
        }
        contentGrid.add(folderPanel, BorderLayout.CENTER);

        // Lower Write Creation Frame Layout Area Components
        JPanel creatorArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        creatorArea.setOpaque(false);
        JTextField customField = new JTextField(25);
        JButton saveQuoteBtn = new JButton("WRITE YOUR OWN QUOTE");
        styleButton(saveQuoteBtn, new Color(234, 46, 121));

        creatorArea.add(customField);
        creatorArea.add(saveQuoteBtn);
        contentGrid.add(creatorArea, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 0.9;
        container.add(contentGrid, gbc);

        gbc.gridy = 1; gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.SOUTH;
        container.add(createFooterLabel(), gbc);

        saveQuoteBtn.addActionListener(e -> {
            String customTxt = customField.getText().trim();
            if(!customTxt.isEmpty()) {
                saveCustomQuoteToDisk(customTxt + " - Me");
                customField.setText("");
                JOptionPane.showMessageDialog(frame, "Quote pinned safely inside 'My Custom Quotes' directory!");
            } else {
                JOptionPane.showMessageDialog(frame, "Please type a quote content string first!");
            }
        });

        return container;
    }

    // VIEW 3: SELECTED GENRE CORE QUOTE DISPLAY PIPELINE
    private static JPanel createQuoteViewerPanel() {
        JPanel container = createBackgroundPanel();
        JPanel structuralPanel = new JPanel(new BorderLayout(20, 40));
        structuralPanel.setOpaque(false);

        quoteDisplayLabel = new JLabel("", SwingConstants.CENTER);
        quoteDisplayLabel.setFont(customFont.deriveFont(16f));
        quoteDisplayLabel.setForeground(Color.WHITE);
        quoteDisplayLabel.setPreferredSize(new Dimension(500, 200));
        structuralPanel.add(quoteDisplayLabel, BorderLayout.CENTER);

        JPanel navActionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navActionRow.setOpaque(false);

        JButton prevBtn = new JButton("BACK");
        JButton nextBtn = new JButton("NEXT QUOTE");
        JButton homeReturnBtn = new JButton("GENRES HOME");

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

        // Action Logic Listeners
        nextBtn.addActionListener(e -> {
            ArrayList<String> list = genreQuotes.get(selectedGenre);
            if (list != null && !list.isEmpty()) {
                currentQuoteIndex = (currentQuoteIndex + 1) % list.size();
                updateQuoteViewText();
            }
        });

        prevBtn.addActionListener(e -> {
            ArrayList<String> list = genreQuotes.get(selectedGenre);
            if (list != null && !list.isEmpty()) {
                currentQuoteIndex = (currentQuoteIndex - 1 + list.size()) % list.size();
                updateQuoteViewText();
            }
        });

        homeReturnBtn.addActionListener(e -> cardLayout.show(mainContainer, "HOME_PAGE"));

        return container;
    }

    private static void updateQuoteViewText() {
        ArrayList<String> list = genreQuotes.get(selectedGenre);
        if (list == null || list.isEmpty()) {
            quoteDisplayLabel.setText("<html><body style='text-align: center;'>No quotes available inside this folder directory yet.</body></html>");
        } else {
            String currentQuoteStr = list.get(currentQuoteIndex);
            quoteDisplayLabel.setText("<html><body style='text-align: center;'>" + currentQuoteStr + "</body></html>");
        }
    }

    private static void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }
}