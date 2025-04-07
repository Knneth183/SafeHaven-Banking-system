import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class BankingSystemGUI extends JFrame implements ActionListener {
    private JButton depositButton, withdrawButton, balanceButton, newAccountButton, toggleHistoryButton, printReceiptButton;
    private JTextArea historyArea;
    private JLabel userInfoLabel;
    private JTextField depositField, withdrawField;
    private double balance = 0;
    private StringBuilder transactionHistory = new StringBuilder();
    private String userName;
    private int userId;
    private ImageIcon backgroundImage;
    private boolean isHistoryVisible = true;
    private List<String> transactions = new ArrayList<>();

    public BankingSystemGUI() {
        backgroundImage = new ImageIcon("bank.jpg"); 

        // Prompt for user details
        userName = JOptionPane.showInputDialog("Enter your name:");
        if (userName == null || userName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Error.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        userId = new Random().nextInt(900000) + 100000;
        JOptionPane.showMessageDialog(null, "Welcome, " + userName + "! \nYour User ID is: " + userId,
                "Welcome", JOptionPane.INFORMATION_MESSAGE);

        setTitle("SafeHaven Banking");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Background Panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("SAFEHAVEN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        backgroundPanel.add(titleLabel, gbc);

        // User Info Label
        userInfoLabel = new JLabel("User: " + userName + " | ID: " + userId);
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        userInfoLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        backgroundPanel.add(userInfoLabel, gbc);

        // Deposit Section
        depositField = new JTextField(10);
        depositButton = new JButton("Deposit");
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        backgroundPanel.add(depositField, gbc);
        gbc.gridx = 1;
        backgroundPanel.add(depositButton, gbc);

        // Withdraw Section
        withdrawField = new JTextField(10);
        withdrawButton = new JButton("Withdraw");
        gbc.gridx = 0;
        gbc.gridy = 3;
        backgroundPanel.add(withdrawField, gbc);
        gbc.gridx = 1;
        backgroundPanel.add(withdrawButton, gbc);

        // Buttons
        balanceButton = new JButton("Check Balance");
        newAccountButton = new JButton("Create Another Account");
        toggleHistoryButton = new JButton("Hide Transaction History");
        printReceiptButton = new JButton("Print Receipt");

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        backgroundPanel.add(balanceButton, gbc);

        gbc.gridy = 5;
        backgroundPanel.add(newAccountButton, gbc);

        gbc.gridy = 6;
        backgroundPanel.add(toggleHistoryButton, gbc);

        gbc.gridy = 7;
        backgroundPanel.add(printReceiptButton, gbc);

        // Transaction History
        historyArea = new JTextArea(10, 30);
        historyArea.setEditable(false);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 6;
        gbc.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(new JScrollPane(historyArea), gbc);

        // Button Listeners
        depositButton.addActionListener(this);
        withdrawButton.addActionListener(this);
        balanceButton.addActionListener(this);
        newAccountButton.addActionListener(this);
        toggleHistoryButton.addActionListener(this);
        printReceiptButton.addActionListener(this);

        add(backgroundPanel);
        setVisible(true);

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                printReceipt(); 
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == depositButton) {
            handleDeposit(depositField.getText());
            depositField.setText("");
        } else if (e.getSource() == withdrawButton) {
            handleWithdraw(withdrawField.getText());
            withdrawField.setText("");
        } else if (e.getSource() == balanceButton) {
            JOptionPane.showMessageDialog(this, "Your current balance is: \u20B1" + String.format("%.2f", balance),
                    "Account Balance", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == newAccountButton) {
            dispose();
            new BankingSystemGUI();
        } else if (e.getSource() == toggleHistoryButton) {
            toggleTransactionHistory();
        } else if (e.getSource() == printReceiptButton) {
            printReceipt();
        }
    }

    private void toggleTransactionHistory() {
        isHistoryVisible = !isHistoryVisible;
        historyArea.setVisible(isHistoryVisible);
        toggleHistoryButton.setText(isHistoryVisible ? "Hide Transaction History" : "Show Transaction History");
    }

    private void handleDeposit(String amountText) {
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Deposit amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            balance += amount;
            transactionHistory.append("Deposited: \u20B1").append(String.format("%.2f", amount)).append("\n");
            transactions.add("Deposited: \u20B1" + String.format("%.2f", amount));
            historyArea.setText(transactionHistory.toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid deposit amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleWithdraw(String amountText) {
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Withdrawal amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (amount > balance) {
                JOptionPane.showMessageDialog(this, "Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            balance -= amount;
            transactionHistory.append("Withdrew: \u20B1").append(String.format("%.2f", amount)).append("\n");
            transactions.add("Withdrew: \u20B1" + String.format("%.2f", amount));
            historyArea.setText(transactionHistory.toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid withdrawal amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReceipt() {
        ReceiptPrinter receiptPrinter = new ReceiptPrinter(transactions, balance, userName, userId);
        receiptPrinter.printReceipt();
    }

    public static void main(String[] Args) {
        JFrame startFrame = new JFrame("SafeHaven Banking");
        startFrame.setSize(300, 150);
        startFrame.setLayout(new GridBagLayout());
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel welcomeLabel = new JLabel("Welcome to SafeHaven Banking!");
        startFrame.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        JButton startButton = new JButton("Start");
        startFrame.add(startButton, gbc);

        startButton.addActionListener(e -> {
            startFrame.dispose();
            SwingUtilities.invokeLater(BankingSystemGUI::new);
        });

        startFrame.setVisible(true);
    }

    public class ReceiptPrinter {
        private List<String> transactions;
        private double finalBalance;
        private String userName;
        private int userId;

        public ReceiptPrinter(List<String> transactions, double finalBalance, String userName, int userId) {
            this.transactions = transactions;
            this.finalBalance = finalBalance;
            this.userName = userName;
            this.userId = userId;
        }

        public void printReceipt() {
            String receipt = generateReceipt();
            saveReceiptToFile(receipt);
        }

        private String generateReceipt() {
            StringBuilder receipt = new StringBuilder();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTime = dtf.format(LocalDateTime.now());

            receipt.append("\n=========== SafeHaven Bank ===========\n");
            receipt.append("User: ").append(userName).append(" | ID: ").append(userId).append("\n");
            receipt.append("Date: ").append(dateTime).append("\n");
            receipt.append("-------------------------------------\n");

            for (String transaction : transactions) {
                receipt.append(transaction).append("\n");
            }

            receipt.append("-------------------------------------\n");
            receipt.append("Final Balance: \u20B1" + String.format("%.2f", finalBalance)).append("\n");
            receipt.append("Thank you for banking with SafeHaven!\n");
            receipt.append("=====================================\n");

            return receipt.toString();
        }

        private void saveReceiptToFile(String receipt) {
            String fileName = "SafeHaven_Receipt_" + userId + "_" + System.currentTimeMillis() + ".txt";
            try (FileWriter fileWriter = new FileWriter(fileName)) {
                fileWriter.write(receipt);
                System.out.println("Receipt saved as " + fileName);
            } catch (IOException e) {
                System.out.println("Error saving receipt: " + e.getMessage());
            }
        }
    }
}
