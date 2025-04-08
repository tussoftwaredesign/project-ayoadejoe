package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataTable extends JScrollPane {
	
    private JTable table;
    private DefaultTableModel tableModel;

    public void createTable(String filePath) {
        // Table model (data will be loaded dynamically)
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Prevent auto-resizing to keep columns aligned
        
        table.setFillsViewportHeight(true); // Ensures table fills the space
        
        // Apply table styling
        styleTable();

        // Set scrollable viewport
        setViewportView(table);
        setPreferredSize(new Dimension(500, 400)); // Set preferred size

        // Load CSV data
        loadCsvData(filePath);
        this.setViewportView(table);
    }

    private void loadCsvData(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                String[] rowData = line.split(","); // Split by comma
                if (isHeader) {
                    tableModel.setColumnIdentifiers(rowData); // Set headers
                    isHeader = false;
                } else {
                    tableModel.addRow(rowData); // Add row
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading CSV file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleTable() {
        // Set row height
        table.setRowHeight(35);

        // Set header style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(130, 144, 100)); // DodgerBlue
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Apply striped effect for better readability
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    cell.setBackground(row % 2 == 0 ? new Color(230, 244, 200) : Color.WHITE); // Light blue and white alternating rows
                }
                return cell;
            }
        });

        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}
