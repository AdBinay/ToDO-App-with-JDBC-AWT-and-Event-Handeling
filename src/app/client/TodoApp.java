package app.client;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TodoApp extends Frame implements ActionListener {
    
    // GUI components
    Label taskLabel;
    TextField taskField;
    Button addButton, deleteButton, viewButton;
    List taskList;

    // JDBC connection variables
    Connection conn;
    PreparedStatement insertStmt, deleteStmt, fetchStmt;

    public TodoApp() {
        // Initialize the Frame
        setTitle("TODO App");
        setSize(400, 400);
        setLayout(new FlowLayout());
        
        // GUI Components
        taskLabel = new Label("Enter Task:");
        taskField = new TextField(20);
        addButton = new Button("Add Task");
        deleteButton = new Button("Delete Task");
        viewButton = new Button("View Tasks");
        taskList = new List(10);

        // Add components to Frame
        add(taskLabel);
        add(taskField);
        add(addButton);
        add(deleteButton);
        add(viewButton);
        add(taskList);

        // Event listeners
        addButton.addActionListener(this);
        deleteButton.addActionListener(this);
        viewButton.addActionListener(this);

        // Close the app on window close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        // Initialize database connection
        connectToDatabase();
        loadTasks();
    }

    // JDBC Connection
    public void connectToDatabase() {
        try {
            // Register JDBC Driver and connect to database
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/todo_app", "root", "0935");

            // Prepare SQL statements
            insertStmt = conn.prepareStatement("INSERT INTO tasks (task) VALUES (?)");
            deleteStmt = conn.prepareStatement("DELETE FROM tasks WHERE task = ?");
            fetchStmt = conn.prepareStatement("SELECT task FROM tasks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load tasks from database
    public void loadTasks() {
        taskList.removeAll();
        try {
            ResultSet rs = fetchStmt.executeQuery();
            while (rs.next()) {
                taskList.add(rs.getString("task"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Handle button actions
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addTask();
        } else if (e.getSource() == deleteButton) {
            deleteTask();
        } else if (e.getSource() == viewButton) {
            loadTasks();
        }
    }

    // Add task to database
    public void addTask() {
        String task = taskField.getText().trim();
        if (!task.isEmpty()) {
            try {
                insertStmt.setString(1, task);
                insertStmt.executeUpdate();
                taskField.setText("");
                loadTasks();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            showMessage("Task cannot be empty!");
        }
    }

    // Delete selected task
    public void deleteTask() {
        String selectedTask = taskList.getSelectedItem();
        if (selectedTask != null) {
            try {
                deleteStmt.setString(1, selectedTask);
                deleteStmt.executeUpdate();
                loadTasks();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            showMessage("Please select a task to delete!");
        }
    }

    // Display error or info messages
    public void showMessage(String message) {
        Dialog d = new Dialog(this, "Message", true);
        d.setLayout(new FlowLayout());
        Label msg = new Label(message);
        Button ok = new Button("OK");
        ok.addActionListener(_ -> d.setVisible(false));
        d.add(msg);
        d.add(ok);
        d.setSize(250, 100);
        d.setVisible(true);
    }

    public static void main(String[] args) {
        new TodoApp().setVisible(true);
    }
}
