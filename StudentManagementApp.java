import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;

public class StudentManagementApp extends JFrame {
    private ArrayList<Student> students = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField nameField, rollField, gradeField, searchField;
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final String DATA_FILE = "students.dat";

    public StudentManagementApp() {
        setTitle("Student Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Custom font
        Font customFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Button.font", customFont);
        UIManager.put("Label.font", customFont);
        UIManager.put("TextField.font", customFont);

        // Top Panel - Form
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2), "Add Student"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        nameField = createStyledTextField();
        rollField = createStyledTextField();
        gradeField = createStyledTextField();
        JButton addButton = createStyledButton("Add", PRIMARY_COLOR);

        formPanel.add(createStyledLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(createStyledLabel("Roll No:"));
        formPanel.add(rollField);
        formPanel.add(createStyledLabel("Grade:"));
        formPanel.add(gradeField);
        formPanel.add(new JLabel());
        formPanel.add(addButton);

        // Center Panel - Table
        String[] columns = {"Name", "Roll No", "Grade"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2), "Student List"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        tableScroll.setBackground(BACKGROUND_COLOR);

        // Bottom Panel - Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionPanel.setBackground(BACKGROUND_COLOR);
        JButton deleteButton = createStyledButton("Delete Selected", new Color(231, 76, 60));
        JButton clearButton = createStyledButton("Clear All", new Color(230, 126, 34));
        searchField = createStyledTextField();
        JButton searchButton = createStyledButton("Search", SECONDARY_COLOR);
        
        actionPanel.add(deleteButton);
        actionPanel.add(clearButton);
        actionPanel.add(createStyledLabel("Search by Name:"));
        actionPanel.add(searchField);
        actionPanel.add(searchButton);

        // Add panels to frame
        add(formPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        clearButton.addActionListener(e -> clearAllStudents());
        searchButton.addActionListener(e -> searchStudent());

        // Add window listener for saving data when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });

        // Load saved data after all components are initialized
        loadData();
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(PRIMARY_COLOR);
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        
        // Style header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String roll = rollField.getText().trim();
        String grade = gradeField.getText().trim();
        if (name.isEmpty() || roll.isEmpty() || grade.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Student student = new Student(name, roll, grade);
        students.add(student);
        tableModel.addRow(new Object[]{name, roll, grade});
        nameField.setText("");
        rollField.setText("");
        gradeField.setText("");
        // Save data after adding
        saveData();
    }

    private void deleteSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            students.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            // Save data after deletion
            saveData();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAllStudents() {
        students.clear();
        tableModel.setRowCount(0);
        // Save data after clearing
        saveData();
    }

    private void searchStudent() {
        String search = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(search)) {
                tableModel.addRow(new Object[]{s.getName(), s.getRollNumber(), s.getGrade()});
            }
        }
    }

    private void loadData() {
        try {
            if (Files.exists(Paths.get(DATA_FILE))) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                    students = (ArrayList<Student>) ois.readObject();
                    // Update table with loaded data
                    for (Student student : students) {
                        tableModel.addRow(new Object[]{student.getName(), student.getRollNumber(), student.getGrade()});
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveData() {
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
                oos.writeObject(students);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentManagementApp().setVisible(true);
        });
    }
} 