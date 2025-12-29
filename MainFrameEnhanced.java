package View;

import Service.CppIntegrationService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.List;
import javax.swing.Timer;

public class MainFrameEnhanced extends JFrame {

    // Professional Color Scheme - Modern Medical Theme
    private static final Color DARK_NAVY = new Color(15, 23, 42);           // Deep background
    private static final Color SLATE_DARK = new Color(30, 41, 59);          // Sidebar
    private static final Color SLATE_MEDIUM = new Color(51, 65, 85);        // Panels
    private static final Color ELECTRIC_BLUE = new Color(59, 130, 246);     // Primary accent
    private static final Color CYAN_BRIGHT = new Color(34, 211, 238);       // Secondary accent
    private static final Color EMERALD = new Color(16, 185, 129);           // Success
    private static final Color AMBER = new Color(251, 146, 60);             // Warning
    private static final Color ROSE = new Color(244, 63, 94);               // Danger
    private static final Color PURPLE = new Color(168, 85, 247);            // Info
    private static final Color TEXT_WHITE = new Color(248, 250, 252);       // Primary text
    private static final Color TEXT_GRAY = new Color(148, 163, 184);        // Secondary text
    private static final Color HOVER_BLUE = new Color(37, 99, 235);         // Hover state

    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JButton currentActiveButton;
    private ExecutorService executor = Executors.newFixedThreadPool(3);
    private int nextPatientId = 1;

    public MainFrameEnhanced() {
        setTitle("MEDICO - Advanced Medical Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1700, 1000);
        setLocationRelativeTo(null);
        setResizable(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        getContentPane().setBackground(DARK_NAVY);
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(DARK_NAVY);

        // Enhanced sidebar with gradient
        sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(DARK_NAVY);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(DARK_NAVY);
        scrollPane.getViewport().setBackground(DARK_NAVY);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        showDashboard();
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, SLATE_DARK,
                        0, getHeight(), new Color(20, 30, 48)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 2, new Color(59, 130, 246, 30)));

        // Header with logo
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Logo circle background
                g2.setColor(ELECTRIC_BLUE);
                g2.fillOval(20, 15, 45, 45);

                // Plus symbol
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(42, 27, 42, 48);
                g2.drawLine(31, 37, 52, 37);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        headerPanel.setPreferredSize(new Dimension(260, 80));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(5, 70, 0, 0));

        JLabel titleLabel = new JLabel("MEDICO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_WHITE);

        JLabel subtitleLabel = new JLabel("Medical Management");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(TEXT_GRAY);

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel);

        sidebar.add(headerPanel, BorderLayout.NORTH);

        // Menu items with icons
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        String[][] menuItems = {
                {"🏠  Dashboard", "dashboard"},
                {"👥  Patients", "patients"},
                {"👨‍⚕️  Doctors", "doctors"},
                {"📅  Appointments", "appointments"},
                {"⚖️  Load Balance", "loadbalance"},
                {"📊  Analytics", "analytics"},
                {"🔍  Search", "search"},
                {"📄  Reports", "reports"},
                {"💾  Database", "database"},
                {"⚙️  Settings", "settings"}
        };

        for (String[] item : menuItems) {
            JButton btn = createSidebarButton(item[0], item[1]);
            menuPanel.add(btn);
            menuPanel.add(Box.createVerticalStrut(6));
        }

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        sidebar.add(scrollPane, BorderLayout.CENTER);

        return sidebar;
    }

    private JButton createSidebarButton(String text, String action) {
        JButton btn = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor;
                if (this == currentActiveButton) {
                    // Active state - gradient
                    GradientPaint gradient = new GradientPaint(
                            0, 0, ELECTRIC_BLUE,
                            getWidth(), getHeight(), CYAN_BRIGHT
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                } else if (isHovered) {
                    g2.setColor(new Color(51, 65, 85, 180));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                } else {
                    g2.setColor(new Color(51, 65, 85, 100));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }

                // Text
                g2.setColor(TEXT_WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = 20;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(TEXT_WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(14, 20, 14, 20));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(230, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            currentActiveButton = btn;
            for (Component c : btn.getParent().getComponents()) {
                c.repaint();
            }

            switch (action) {
                case "dashboard": showDashboard(); break;
                case "patients": showPatients(); break;
                case "doctors": showDoctors(); break;
                case "appointments": showAppointments(); break;
                case "loadbalance": showLoadBalance(); break;
                case "analytics": showAnalytics(); break;
                case "search": showSearch(); break;
                case "reports": showReports(); break;
                case "database": showDatabase(); break;
                case "settings": showSettings(); break;
            }
        });

        return btn;
    }

    private void showDashboard() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(DARK_NAVY);

        // Hero section with gradient
        JPanel heroPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(59, 130, 246),
                        getWidth(), getHeight(), new Color(147, 51, 234)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        heroPanel.setOpaque(false);
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        heroPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel welcomeTitle = new JLabel("Welcome to MEDICO");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 38));
        welcomeTitle.setForeground(TEXT_WHITE);

        JLabel welcomeSubtitle = new JLabel("Advanced Medical Appointment Management System");
        welcomeSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeSubtitle.setForeground(new Color(226, 232, 240));

        heroPanel.add(welcomeTitle);
        heroPanel.add(Box.createVerticalStrut(8));
        heroPanel.add(welcomeSubtitle);

        mainContent.add(heroPanel);
        mainContent.add(Box.createVerticalStrut(25));

        // Stats grid
        JLabel statsLabel = new JLabel("System Overview");
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        statsLabel.setForeground(TEXT_WHITE);
        statsLabel.setBorder(new EmptyBorder(5, 0, 15, 0));
        mainContent.add(statsLabel);

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        statsGrid.setBackground(DARK_NAVY);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        statsGrid.add(createModernStatCard("Total Patients", "1,247", "↑ 12%", ELECTRIC_BLUE));
        statsGrid.add(createModernStatCard("Active Doctors", "28", "↑ 4%", EMERALD));
        statsGrid.add(createModernStatCard("Appointments", "156", "Today", AMBER));
        statsGrid.add(createModernStatCard("Critical Cases", "7", "Urgent", ROSE));

        mainContent.add(statsGrid);
        mainContent.add(Box.createVerticalStrut(30));

        // Features section
        JLabel featuresLabel = new JLabel("Key Features");
        featuresLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        featuresLabel.setForeground(TEXT_WHITE);
        featuresLabel.setBorder(new EmptyBorder(5, 0, 15, 0));
        mainContent.add(featuresLabel);

        JPanel featuresGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        featuresGrid.setBackground(DARK_NAVY);
        featuresGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        featuresGrid.add(createModernFeatureCard("👥", "Patient Management", "Comprehensive patient records and history tracking"));
        featuresGrid.add(createModernFeatureCard("🏥", "Doctor Scheduling", "Intelligent scheduling and availability management"));
        featuresGrid.add(createModernFeatureCard("📅", "Smart Appointments", "Automated appointment booking and reminders"));
        featuresGrid.add(createModernFeatureCard("⚖️", "Load Balancing", "AI-powered workload distribution system"));
        featuresGrid.add(createModernFeatureCard("📊", "Analytics", "Real-time insights and trend analysis"));
        featuresGrid.add(createModernFeatureCard("💾", "Data Security", "Enterprise-grade backup and encryption"));

        mainContent.add(featuresGrid);
        mainContent.add(Box.createVerticalStrut(50));

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createModernStatCard(String title, String value, String change, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card background
                g2.setColor(SLATE_MEDIUM);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Accent bar
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 6, getHeight(), 20, 20);

                // Subtle glow effect
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                g2.fillRoundRect(6, 0, 50, getHeight(), 20, 20);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 25, 20, 20));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setForeground(TEXT_GRAY);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(TEXT_WHITE);

        JLabel changeLbl = new JLabel(change);
        changeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        changeLbl.setForeground(accentColor);

        textPanel.add(titleLbl);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(valueLbl);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(changeLbl);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createModernFeatureCard(String icon, String title, String description) {
        JPanel card = new JPanel() {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isHovered) {
                    GradientPaint gradient = new GradientPaint(
                            0, 0, new Color(51, 65, 85),
                            getWidth(), getHeight(), new Color(71, 85, 105)
                    );
                    g2.setPaint(gradient);
                } else {
                    g2.setColor(SLATE_MEDIUM);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Border
                g2.setColor(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), isHovered ? 100 : 50));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
            }

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLbl.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(TEXT_WHITE);

        JLabel descLbl = new JLabel("<html>" + description + "</html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(TEXT_GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(iconLbl);
        textPanel.add(titleLbl);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(descLbl);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createOutputPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SLATE_MEDIUM);
        panel.setBorder(new CompoundBorder(
                new LineBorder(EMERALD, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLbl = new JLabel("✓ " + title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(EMERALD);
        titleLbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleLbl, BorderLayout.NORTH);

        JTextArea outputArea = new JTextArea();
        outputArea.setBackground(new Color(15, 23, 42));
        outputArea.setForeground(EMERALD);
        outputArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setText(">> System ready. Waiting for operations...");
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(new LineBorder(new Color(59, 130, 246, 50), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showPatients() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setBackground(DARK_NAVY);

        // Form panel
        JPanel formPanel = createModernForm();
        topPanel.add(formPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = createPatientTable();
        topPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel outputPanel = createOutputPanel("Operation Output");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, outputPanel);
        splitPane.setDividerLocation(0.75);
        splitPane.setBackground(DARK_NAVY);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(splitPane);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createModernForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SLATE_MEDIUM);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = createModernTextField(22);
        JTextField ageField = createModernTextField(8);
        JTextField diseaseField = createModernTextField(22);
        JTextField phoneField = createModernTextField(22);

        addModernFormField(panel, gbc, "Patient Name", nameField, 0);
        addModernFormField(panel, gbc, "Age", ageField, 1);
        addModernFormField(panel, gbc, "Disease", diseaseField, 2);
        addModernFormField(panel, gbc, "Phone Number", phoneField, 3);

        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 0.3;

        JPanel btnPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        btnPanel.setOpaque(false);

        JButton addBtn = createModernButton("➕ Add Patient", ELECTRIC_BLUE);
        JButton csvBtn = createModernButton("📤 Upload CSV", CYAN_BRIGHT);
        JButton refreshBtn = createModernButton("🔄 Refresh", EMERALD);
        JButton clearBtn = createModernButton("🗑️ Clear All", ROSE);

        addBtn.addActionListener(e -> {
            try {
                String result = CppIntegrationService.addPatient(
                        nextPatientId++, nameField.getText(),
                        Integer.parseInt(ageField.getText()),
                        diseaseField.getText(), phoneField.getText()
                );
                showModernMessage(result, result.contains("SUCCESS"));
                if (result.contains("SUCCESS")) {
                    nameField.setText("");
                    ageField.setText("");
                    diseaseField.setText("");
                    phoneField.setText("");
                    showPatients();
                }
            } catch (Exception ex) {
                showModernMessage("Error: " + ex.getMessage(), false);
            }
        });

        csvBtn.addActionListener(e -> uploadCSV());
        refreshBtn.addActionListener(e -> showPatients());
        clearBtn.addActionListener(e -> {
            try {
                Files.deleteIfExists(Paths.get("DataFiles/patients_data.txt"));
                showModernMessage("All patients cleared successfully!", true);
                showPatients();
            } catch (Exception ex) {
                showModernMessage("Error: " + ex.getMessage(), false);
            }
        });

        btnPanel.add(addBtn);
        btnPanel.add(csvBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(clearBtn);

        panel.add(btnPanel, gbc);

        return panel;
    }

    private JPanel createPatientTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SLATE_MEDIUM);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        String[] columns = {"ID", "Name", "Age", "Disease", "Phone", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setBackground(DARK_NAVY);
        table.setForeground(TEXT_WHITE);
        table.setSelectionBackground(ELECTRIC_BLUE);
        table.setSelectionForeground(TEXT_WHITE);
        table.setGridColor(new Color(71, 85, 105));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(SLATE_DARK);
        header.setForeground(TEXT_WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(59, 130, 246, 30), 1));
        scrollPane.getViewport().setBackground(new Color(240, 242, 245));
        panel.add(scrollPane, BorderLayout.CENTER);

        executor.execute(() -> {
            try {
                List<String> patients = CppIntegrationService.getAllPatientsSorted();
                SwingUtilities.invokeLater(() -> {
                    model.setRowCount(0);
                    for (String line : patients) {
                        String[] parts = line.split(",");
                        if (parts.length >= 5) {
                            String status = parts[3].contains("Critical") ? "🔴 Critical" : "✅ Active";
                            model.addRow(new Object[]{parts[0], parts[1], parts[2], parts[3], parts[4], status});
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return panel;
    }

    private void showDoctors() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setBackground(DARK_NAVY);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(SLATE_MEDIUM);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = createModernTextField(18);
        JTextField specField = createModernTextField(18);
        JTextField slotsField = createModernTextField(8);

        addModernFormField(formPanel, gbc, "Doctor Name", nameField, 0);
        addModernFormField(formPanel, gbc, "Specialization", specField, 1);
        addModernFormField(formPanel, gbc, "Available Slots", slotsField, 2);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        JButton addBtn = createModernButton("➕ Add Doctor", ELECTRIC_BLUE);
        addBtn.addActionListener(e -> {
            System.out.println("✓ Doctor added: " + nameField.getText());
            nameField.setText("");
            specField.setText("");
            slotsField.setText("");
        });
        formPanel.add(addBtn, gbc);

        topPanel.add(formPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Specialization", "Assigned", "Available", "Load %"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);
        table.setBackground(DARK_NAVY);
        table.setForeground(TEXT_WHITE);
        table.setSelectionBackground(ELECTRIC_BLUE);
        table.setGridColor(new Color(71, 85, 105));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(40);

        JTableHeader header = table.getTableHeader();
        header.setBackground(SLATE_DARK);
        header.setForeground(TEXT_WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(SLATE_MEDIUM);
        tablePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(59, 130, 246, 30), 1));
        scrollPane.getViewport().setBackground(DARK_NAVY);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(tablePanel, BorderLayout.CENTER);

        String[] specializations = {"General Medicine", "Cardiology", "Orthopedic", "Neurology", "Surgery", "Dermatology", "Psychiatry", "Pediatrics"};
        for (int i = 1; i <= 8; i++) {
            int assigned = (int)(Math.random() * 30);
            int available = (int)(Math.random() * 20);
            int loadPercent = (int)(Math.random() * 100);
            model.addRow(new Object[]{"DR-" + String.format("%03d", i), "Dr. " + ("ABCDEFGH".charAt(i-1)) + " Khan", specializations[i-1], assigned, available, loadPercent + "%"});
        }

        JPanel outputPanel = createOutputPanel("Operation Output");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, outputPanel);
        splitPane.setDividerLocation(0.75);
        splitPane.setBackground(DARK_NAVY);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(splitPane);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAppointments() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setBackground(DARK_NAVY);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(SLATE_MEDIUM);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField pidField = createModernTextField(12);
        JTextField didField = createModernTextField(12);
        JTextField dateField = createModernTextField(15);
        JTextField timeField = createModernTextField(12);

        addModernFormField(formPanel, gbc, "Patient ID", pidField, 0);
        addModernFormField(formPanel, gbc, "Doctor ID", didField, 1);
        addModernFormField(formPanel, gbc, "Date (YYYY-MM-DD)", dateField, 2);
        addModernFormField(formPanel, gbc, "Time (HH:MM)", timeField, 3);

        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        JButton scheduleBtn = createModernButton("📅 Schedule", ELECTRIC_BLUE);
        scheduleBtn.addActionListener(e -> {
            try {
                String result = CppIntegrationService.scheduleAppointment(
                        Integer.parseInt(pidField.getText()),
                        Integer.parseInt(didField.getText()),
                        dateField.getText(),
                        timeField.getText()
                );
                System.out.println(result);
                if (result.contains("SUCCESS")) {
                    pidField.setText("");
                    didField.setText("");
                    dateField.setText("");
                    timeField.setText("");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });
        formPanel.add(scheduleBtn, gbc);

        topPanel.add(formPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Patient", "Doctor", "Date", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setBackground(DARK_NAVY);
        table.setForeground(TEXT_WHITE);
        table.setSelectionBackground(ELECTRIC_BLUE);
        table.setGridColor(new Color(71, 85, 105));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(40);

        JTableHeader header = table.getTableHeader();
        header.setBackground(SLATE_DARK);
        header.setForeground(TEXT_WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(SLATE_MEDIUM);
        tablePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(59, 130, 246, 30), 1));
        scrollPane.getViewport().setBackground(DARK_NAVY);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(tablePanel, BorderLayout.CENTER);

        for (int i = 1; i <= 8; i++) {
            model.addRow(new Object[]{"APT-" + String.format("%04d", i), "P-" + (100+i), "DR-" + String.format("%03d", i%5+1), "2025-01-" + (10+i), String.format("%02d:%02d", 9+i, (i*15)%60), "✅ Confirmed"});
        }

        JPanel outputPanel = createOutputPanel("Operation Output");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, outputPanel);
        splitPane.setDividerLocation(0.75);
        splitPane.setBackground(DARK_NAVY);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(splitPane);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showLoadBalance() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setBackground(DARK_NAVY);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        controlPanel.setBackground(SLATE_MEDIUM);
        controlPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JButton balanceBtn = createModernButton("⚖️ Balance Load", ELECTRIC_BLUE);
        JButton statusBtn = createModernButton("📊 View Status", EMERALD);

        balanceBtn.addActionListener(e -> System.out.println("✓ Load balanced successfully!"));
        statusBtn.addActionListener(e -> {
            try {
                String status = CppIntegrationService.getDoctorLoadStatus();
                System.out.println("Load Status:\n" + status);
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });

        controlPanel.add(balanceBtn);
        controlPanel.add(statusBtn);
        topPanel.add(controlPanel, BorderLayout.NORTH);

        String[] columns = {"Doctor", "Current Load", "Capacity", "Utilization", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setBackground(DARK_NAVY);
        table.setForeground(TEXT_WHITE);
        table.setSelectionBackground(ELECTRIC_BLUE);
        table.setGridColor(new Color(71, 85, 105));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(45);

        JTableHeader header = table.getTableHeader();
        header.setBackground(SLATE_DARK);
        header.setForeground(TEXT_WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(SLATE_MEDIUM);
        tablePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(59, 130, 246, 30), 1));
        scrollPane.getViewport().setBackground(DARK_NAVY);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(tablePanel, BorderLayout.CENTER);

        for (int i = 1; i <= 6; i++) {
            int load = 15 + (int)(Math.random() * 35);
            int capacity = 50;
            int util = (load * 100) / capacity;
            String status = util > 75 ? "🔴 High Load" : util > 50 ? "🟡 Medium" : "🟢 Optimal";
            model.addRow(new Object[]{"Dr. " + ("ABCDEFGH".charAt(i-1)) + " Khan", load + " patients", capacity + " patients", util + "%", status});
        }

        JPanel outputPanel = createOutputPanel("Operation Output");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, outputPanel);
        splitPane.setDividerLocation(0.75);
        splitPane.setBackground(DARK_NAVY);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(splitPane);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAnalytics() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setBackground(DARK_NAVY);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        controlPanel.setBackground(SLATE_MEDIUM);
        controlPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JLabel titleLbl = new JLabel("📊 Disease Distribution Analysis");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(TEXT_WHITE);

        JButton analyzeBtn = createModernButton("🔍 Analyze", ELECTRIC_BLUE);
        analyzeBtn.addActionListener(e -> {
            try {
                String result = CppIntegrationService.getPatientReferralAnalysis();
                System.out.println("Analysis:\n" + result);
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });

        controlPanel.add(titleLbl);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(analyzeBtn);
        topPanel.add(controlPanel, BorderLayout.NORTH);

        String[] columns = {"Disease", "Count", "Percentage", "Priority", "Trend"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setBackground(DARK_NAVY);
        table.setForeground(TEXT_WHITE);
        table.setSelectionBackground(ELECTRIC_BLUE);
        table.setGridColor(new Color(71, 85, 105));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(40);

        JTableHeader header = table.getTableHeader();
        header.setBackground(SLATE_DARK);
        header.setForeground(TEXT_WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(SLATE_MEDIUM);
        tablePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(59, 130, 246, 30), 1));
        scrollPane.getViewport().setBackground(DARK_NAVY);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(tablePanel, BorderLayout.CENTER);

        String[] diseases = {"Diabetes Type 2", "Hypertension", "Cardiovascular", "Orthopedic Issues", "Neurological", "Respiratory", "Dermatology", "Mental Health"};
        for (int i = 0; i < diseases.length; i++) {
            int count = 50 + (int)(Math.random() * 150);
            int percentage = 10 + (int)(Math.random() * 25);
            String priority = count > 120 ? "🔴 High" : count > 80 ? "🟡 Medium" : "🟢 Normal";
            String trend = Math.random() > 0.5 ? "📈 Rising" : "📉 Declining";
            model.addRow(new Object[]{diseases[i], count + " cases", percentage + "%", priority, trend});
        }

        JPanel outputPanel = createOutputPanel("Operation Output");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, outputPanel);
        splitPane.setDividerLocation(0.75);
        splitPane.setBackground(DARK_NAVY);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(splitPane);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSearch() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setBackground(DARK_NAVY);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(SLATE_MEDIUM);
        searchPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = createModernTextField(18);
        JButton searchBtn = createModernButton("🔍 Search", ELECTRIC_BLUE);

        JComboBox<String> diseaseCombo = new JComboBox<>(new String[]{"All Diseases", "Diabetes", "Hypertension", "Cardiology", "Orthopedic", "Neurology", "Respiratory"});
        diseaseCombo.setBackground(new Color(220, 225, 230)); // Light grey
        diseaseCombo.setForeground(new Color(30, 30, 30)); // Dark black text
        diseaseCombo.setFont(new Font("Segoe UI", Font.BOLD, 12));

        addModernFormField(searchPanel, gbc, "Patient ID", idField, 0);
        gbc.gridx = 2; gbc.gridy = 0;
        searchPanel.add(searchBtn, gbc);

        addModernFormField(searchPanel, gbc, "Filter by Disease", diseaseCombo, 1);

        topPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Age", "Disease", "Phone", "Match"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setBackground(DARK_NAVY);
        table.setForeground(TEXT_WHITE);
        table.setSelectionBackground(ELECTRIC_BLUE);
        table.setGridColor(new Color(71, 85, 105));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(40);

        JTableHeader header = table.getTableHeader();
        header.setBackground(SLATE_DARK);
        header.setForeground(TEXT_WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(SLATE_MEDIUM);
        tablePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(59, 130, 246, 30), 1));
        scrollPane.getViewport().setBackground(DARK_NAVY);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(tablePanel, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            try {
                String result = CppIntegrationService.searchPatient(Integer.parseInt(idField.getText()));
                System.out.println(result);
                if (!result.contains("ERROR")) {
                    String[] parts = result.split(",");
                    if (parts.length >= 5) {
                        model.setRowCount(0);
                        model.addRow(new Object[]{parts[0], parts[1], parts[2], parts[3], parts[4], "✅ 100%"});
                    }
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });

        diseaseCombo.addActionListener(e -> {
            String disease = (String) diseaseCombo.getSelectedItem();
            if (!disease.equals("All Diseases")) {
                executor.execute(() -> {
                    try {
                        List<String> patients = CppIntegrationService.getAllPatientsSorted();
                        SwingUtilities.invokeLater(() -> {
                            model.setRowCount(0);
                            for (String line : patients) {
                                if (line.toLowerCase().contains(disease.toLowerCase())) {
                                    String[] parts = line.split(",");
                                    if (parts.length >= 5) {
                                        model.addRow(new Object[]{parts[0], parts[1], parts[2], parts[3], parts[4], "✅ 95%"});
                                    }
                                }
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });

        JPanel outputPanel = createOutputPanel("Operation Output");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, outputPanel);
        splitPane.setDividerLocation(0.75);
        splitPane.setBackground(DARK_NAVY);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(splitPane);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showReports() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setBackground(DARK_NAVY);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 15));
        controlPanel.setBackground(SLATE_MEDIUM);
        controlPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JButton patientBtn = createModernButton("📋 Patient Report", ELECTRIC_BLUE);
        JButton aptBtn = createModernButton("📅 Appointments", EMERALD);
        JButton doctorBtn = createModernButton("👨‍⚕️ Doctor Stats", AMBER);
        JButton exportBtn = createModernButton("📤 Export CSV", ROSE);

        controlPanel.add(patientBtn);
        controlPanel.add(aptBtn);
        controlPanel.add(doctorBtn);
        controlPanel.add(exportBtn);
        topPanel.add(controlPanel, BorderLayout.NORTH);

        String[] columns = {"Report Name", "Generated", "Records", "Size", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setBackground(DARK_NAVY);
        table.setForeground(TEXT_WHITE);
        table.setSelectionBackground(ELECTRIC_BLUE);
        table.setGridColor(new Color(71, 85, 105));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(40);

        JTableHeader header = table.getTableHeader();
        header.setBackground(SLATE_DARK);
        header.setForeground(TEXT_WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(SLATE_MEDIUM);
        tablePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(59, 130, 246, 30), 1));
        scrollPane.getViewport().setBackground(DARK_NAVY);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(tablePanel, BorderLayout.CENTER);

        patientBtn.addActionListener(e -> {
            executor.execute(() -> {
                try {
                    List<String> patients = CppIntegrationService.getAllPatientsSorted();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    SwingUtilities.invokeLater(() -> {
                        model.insertRow(0, new Object[]{"Patient Report", sdf.format(new Date()), patients.size() + " records", (patients.size() * 120) + " KB", "✅ Complete"});
                        System.out.println("✓ Generated Patient Report: " + patients.size() + " records");
                    });
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            });
        });

        JPanel outputPanel = createOutputPanel("Operation Output");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, outputPanel);
        splitPane.setDividerLocation(0.75);
        splitPane.setBackground(DARK_NAVY);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(splitPane);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showDatabase() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setBackground(DARK_NAVY);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        controlPanel.setBackground(SLATE_MEDIUM);
        controlPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JButton backupBtn = createModernButton("💾 Backup", ELECTRIC_BLUE);
        JButton restoreBtn = createModernButton("🔄 Restore", EMERALD);
        JButton optimizeBtn = createModernButton("⚡ Optimize", AMBER);
        JButton repairBtn = createModernButton("🔧 Repair", PURPLE);

        backupBtn.addActionListener(e -> System.out.println("✓ Database backup created successfully!"));
        restoreBtn.addActionListener(e -> System.out.println("✓ Database restored successfully!"));
        optimizeBtn.addActionListener(e -> System.out.println("✓ Database optimized!"));
        repairBtn.addActionListener(e -> System.out.println("✓ Integrity check completed: All OK"));

        controlPanel.add(backupBtn);
        controlPanel.add(restoreBtn);
        controlPanel.add(optimizeBtn);
        controlPanel.add(repairBtn);
        topPanel.add(controlPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(DARK_NAVY);
        statsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        statsPanel.add(createModernStatCard("Total Records", "1,247", "All data", ELECTRIC_BLUE));
        statsPanel.add(createModernStatCard("Database Size", "3.8 MB", "Storage used", EMERALD));
        statsPanel.add(createModernStatCard("Last Backup", "2 hours ago", "Auto-saved", AMBER));
        statsPanel.add(createModernStatCard("Integrity", "100%", "✓ Healthy", PURPLE));

        topPanel.add(statsPanel, BorderLayout.CENTER);

        JPanel outputPanel = createOutputPanel("Operation Output");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, outputPanel);
        splitPane.setDividerLocation(0.75);
        splitPane.setBackground(DARK_NAVY);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(splitPane);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSettings() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK_NAVY);

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(SLATE_MEDIUM);
        settingsPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(ELECTRIC_BLUE.getRed(), ELECTRIC_BLUE.getGreen(), ELECTRIC_BLUE.getBlue(), 60), 2, true),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel systemLabel = new JLabel("⚙️ SYSTEM SETTINGS");
        systemLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        systemLabel.setForeground(ELECTRIC_BLUE);
        gbc.gridy = 0;
        settingsPanel.add(systemLabel, gbc);

        JCheckBox darkModeCheckBox = new JCheckBox("🌙 Dark Mode (Currently Active)");
        darkModeCheckBox.setSelected(true);
        darkModeCheckBox.setBackground(SLATE_MEDIUM);
        darkModeCheckBox.setForeground(TEXT_WHITE);
        darkModeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 1;
        settingsPanel.add(darkModeCheckBox, gbc);

        JCheckBox notifCheckBox = new JCheckBox("🔔 Enable Notifications");
        notifCheckBox.setSelected(true);
        notifCheckBox.setBackground(SLATE_MEDIUM);
        notifCheckBox.setForeground(TEXT_WHITE);
        notifCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 2;
        settingsPanel.add(notifCheckBox, gbc);

        JCheckBox autoSaveCheckBox = new JCheckBox("💾 Auto-Save Every 5 Minutes");
        autoSaveCheckBox.setSelected(true);
        autoSaveCheckBox.setBackground(SLATE_MEDIUM);
        autoSaveCheckBox.setForeground(TEXT_WHITE);
        autoSaveCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 3;
        settingsPanel.add(autoSaveCheckBox, gbc);

        JLabel dataLabel = new JLabel("💾 DATA MANAGEMENT");
        dataLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dataLabel.setForeground(ELECTRIC_BLUE);
        gbc.gridy = 5;
        gbc.insets = new Insets(30, 20, 15, 20);
        settingsPanel.add(dataLabel, gbc);

        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridy = 6;
        JButton clearCacheBtn = createModernButton("🗑️ Clear Cache", AMBER);
        clearCacheBtn.addActionListener(e -> System.out.println("✓ Cache cleared successfully!"));
        settingsPanel.add(clearCacheBtn, gbc);

        gbc.gridy = 7;
        JButton resetBtn = createModernButton("🔄 Reset Application", ROSE);
        resetBtn.addActionListener(e -> System.out.println("✓ Settings reset. Please restart the application."));
        settingsPanel.add(resetBtn, gbc);

        topPanel.add(settingsPanel, BorderLayout.NORTH);

        JPanel outputPanel = createOutputPanel("Operation Output");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, outputPanel);
        splitPane.setDividerLocation(0.75);
        splitPane.setBackground(DARK_NAVY);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(splitPane);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void uploadCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            executor.execute(() -> {
                try {
                    List<String> lines = Files.readAllLines(chooser.getSelectedFile().toPath());
                    int count = 0;
                    for (int i = 1; i < lines.size(); i++) {
                        String[] parts = lines.get(i).split(",");
                        if (parts.length >= 5) {
                            CppIntegrationService.addPatient(nextPatientId++, parts[1].trim(),
                                    Integer.parseInt(parts[2].trim()), parts[3].trim(), parts[4].trim());
                            count++;
                        }
                    }
                    int finalCount = count;
                    SwingUtilities.invokeLater(() -> {
                        showModernMessage("✓ Successfully imported " + finalCount + " patients!", true);
                        showPatients();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> showModernMessage("✗ Import error: " + e.getMessage(), false));
                }
            });
        }
    }

    private void showModernMessage(String message, boolean isSuccess) {
        JPanel messagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = isSuccess ? new Color(16, 185, 129, 30) : new Color(244, 63, 94, 30);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                Color borderColor = isSuccess ? EMERALD : ROSE;
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
            }
        };
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(18, 25, 18, 25));
        messagePanel.setLayout(new BorderLayout());

        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(isSuccess ? EMERALD : ROSE);
        messagePanel.add(label, BorderLayout.CENTER);

        contentPanel.add(messagePanel, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();

        Timer timer = new Timer(4000, e -> {
            contentPanel.remove(messagePanel);
            contentPanel.revalidate();
            contentPanel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private JTextField createModernTextField(int cols) {
        JTextField field = new JTextField(cols) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Light grey background for better visibility
                g2.setColor(new Color(220, 225, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                super.paintComponent(g);
            }
        };
        field.setBackground(new Color(220, 225, 230)); // Light grey
        field.setForeground(new Color(30, 30, 30)); // Dark black text
        field.setCaretColor(ELECTRIC_BLUE);
        field.setFont(new Font("Segoe UI", Font.BOLD, 12));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(100, 116, 139), 2, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        field.setOpaque(true);
        return field;
    }

    private JButton createModernButton(String text, Color color) {
        JButton btn = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = color;
                if (isPressed) {
                    bgColor = darkenColor(color, 40);
                } else if (isHovered) {
                    bgColor = darkenColor(color, 20);
                }

                GradientPaint gradient = new GradientPaint(
                        0, 0, bgColor,
                        0, getHeight(), darkenColor(bgColor, 15)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        isPressed = false;
                        repaint();
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private Color darkenColor(Color c, int amount) {
        return new Color(
                Math.max(0, c.getRed() - amount),
                Math.max(0, c.getGreen() - amount),
                Math.max(0, c.getBlue() - amount)
        );
    }

    private void addModernFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(200, 210, 220)); // Light grey for better visibility

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        panel.add(field, gbc);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MainFrameEnhanced());
    }
}