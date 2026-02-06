/* GROUP 9
--- MEMBERS ---
BULAN, Zaldy Marcus
CO, Emerson
ENRIQUE, Lanz Aaron
PARREÑO, Jonh Biel
MALLAN, Randyl
YUKAWA, Rei Emerson

Submitted to: Prof Patrick Sta. Maria
*/
import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RREFSolverGUI extends JFrame {

    // ui components
    private JTextField rowsField, colsField;
    private JPanel matrixPanel; // holds the grid
    private JTextField[][] matrixCells; // input refs
    private JButton solveButton, fillZeroButton; 
    private int m = 0; // rows
    private int n = 0; // cols

    public RREFSolverGUI() {
        setTitle("Automated Linear System Solver (RREF)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 750);
        setLocationRelativeTo(null); // center on screen
        setLayout(new BorderLayout(10, 10));

        // top section: config
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        configPanel.setBorder(new EmptyBorder(10, 10, 0, 10));

        configPanel.add(new JLabel("Rows (m ≤ 10):"));
        rowsField = new JTextField("", 3);
        configPanel.add(rowsField);

        configPanel.add(new JLabel("Cols (n ≤ 10):"));
        colsField = new JTextField("", 3);
        configPanel.add(colsField);

        JButton generateButton = new JButton("Generate Matrix");
        generateButton.setFocusable(false);
        configPanel.add(generateButton);

        add(configPanel, BorderLayout.NORTH);

        // center section: the matrix grid
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        matrixPanel = new JPanel();
        centerWrapper.add(matrixPanel);
        
        // scrollpane for larger matrices
        add(new JScrollPane(centerWrapper), BorderLayout.CENTER);

        // bottom section: buttons
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        fillZeroButton = new JButton("Fill empty entries with 0");
        fillZeroButton.setEnabled(false);
        fillZeroButton.setFocusable(false);

        solveButton = new JButton("Convert to RREF");
        solveButton.setEnabled(false); 
        solveButton.setFont(new Font("Arial", Font.BOLD, 14));
        solveButton.setBackground(new Color(70, 130, 180)); 
        solveButton.setForeground(Color.WHITE);
        solveButton.setFocusable(false);
        
        JButton resetButton = new JButton("Reset");
        resetButton.setFocusable(false);

        actionPanel.add(fillZeroButton);
        actionPanel.add(solveButton);
        actionPanel.add(resetButton);
        add(actionPanel, BorderLayout.SOUTH);

        // listeners
        generateButton.addActionListener(e -> generateMatrixGrid());
        solveButton.addActionListener(e -> solveMatrix());
        
        fillZeroButton.addActionListener(e -> {
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (matrixCells[i][j].getText().trim().isEmpty()) {
                        matrixCells[i][j].setText("0");
                    }
                }
            }
        });

        resetButton.addActionListener(e -> {
            matrixPanel.removeAll();
            matrixPanel.revalidate();
            matrixPanel.repaint();
            solveButton.setEnabled(false);
            fillZeroButton.setEnabled(false);
            rowsField.setText("");
            colsField.setText("");
        });
    }

    // creates the textfield grid based on row/col input
    private void generateMatrixGrid() {
        try {
            String rText = rowsField.getText().trim();
            String cText = colsField.getText().trim();
            
            if (rText.isEmpty() || cText.isEmpty()) return;

            m = Integer.parseInt(rText);
            n = Integer.parseInt(cText);

            if (m < 1 || m > 10 || n < 1 || n > 10) {
                JOptionPane.showMessageDialog(this, "Size from 1-10 Only!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // clear old grid and setup new one
            matrixPanel.removeAll();
            matrixPanel.setLayout(new GridLayout(m, n, 10, 10)); 

            matrixCells = new JTextField[m][n];

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    JTextField cell = new JTextField();
                    cell.setHorizontalAlignment(JTextField.CENTER);
                    cell.setPreferredSize(new Dimension(80, 65)); 
                    matrixCells[i][j] = cell;
                    matrixPanel.add(cell);
                }
            }

            solveButton.setEnabled(true);
            fillZeroButton.setEnabled(true);
            matrixPanel.revalidate();
            matrixPanel.repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Integers only for dimension size!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
  //   Handles empty inputs, "5/", "/5", and "1/0" safely.
     
    private double parseInput(String text) throws NumberFormatException {
        text = text.trim();
        if (text.isEmpty()) return 0.0;
        
        if (text.contains("/")) {
            String[] parts = text.split("/");
            
            // Check if split resulted in valid parts
            if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
                 throw new NumberFormatException("Invalid fraction format");
            }
            
            double num = Double.parseDouble(parts[0]);
            double den = Double.parseDouble(parts[1]);
            
            if (den == 0) throw new NumberFormatException("Division by zero");
            
            return num / den;
        }
        return Double.parseDouble(text);
    }

    // reads grid and solves
    private void solveMatrix() {
        double[][] matrix = new double[m][n];

        // get the data
        try {
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    String text = matrixCells[i][j].getText().trim();
                    matrix[i][j] = parseInput(text);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //  crunch numbers
        convertToRREF(matrix);

        // show results in grid
        matrixPanel.removeAll();
        matrixPanel.setLayout(new GridLayout(m, n, 10, 10));
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                double val = matrix[i][j];
    
                if (Math.abs(val) < 1e-9) val = 0.0;
                
                FractionCell cell = new FractionCell(val);
                cell.setBackground(new Color(230, 255, 230)); 
                matrixPanel.add(cell);
            }
        }
        
        matrixPanel.revalidate();
        matrixPanel.repaint();
        JOptionPane.showMessageDialog(this, "Matrix transformed to RREF successfully!");
    }

    // standard rref / using gauss-jordan method
    public static void convertToRREF(double[][] matrix) {
        int rowCount = matrix.length;
        int colCount = matrix[0].length;
        int lead = 0;

        for (int r = 0; r < rowCount; r++) {
            if (colCount <= lead) return;

            int i = r;
            while (Math.abs(matrix[i][lead]) < 1e-9) {
                i++;
                if (rowCount == i) {
                    i = r;
                    lead++;
                    if (colCount == lead) return;
                }
            }

            // swap rows
            double[] temp = matrix[i];
            matrix[i] = matrix[r];
            matrix[r] = temp;

            // divide row by pivot
            double val = matrix[r][lead];
            if (Math.abs(val) > 1e-9) {
                for (int j = 0; j < colCount; j++) {
                    matrix[r][j] /= val;
                }
            }

            // eliminate other rows
            for (int k = 0; k < rowCount; k++) {
                if (k != r) {
                    double factor = matrix[k][lead];
                    for (int j = 0; j < colCount; j++) {
                        matrix[k][j] -= factor * matrix[r][j];
                    }
                }
            }
            lead++;
        }
    }

    class FractionCell extends JPanel {
        private long num, den;
        private boolean isInteger;
        private String fallbackDecimal = null; // Used if fraction is too wide

        public FractionCell(double value) {
            setPreferredSize(new Dimension(80, 65));
            setBorder(BorderFactory.createLineBorder(new Color(180, 200, 180)));
            if (Math.abs(value) < 1e-8) value = 0.0;

            if (Math.abs(value % 1) < 1e-6 || value == 0.0) {
                this.num = Math.round(value);
                this.isInteger = true;
            } else {
                long[] parts = toFraction(value);
                this.num = parts[0];
                this.den = parts[1];
                this.isInteger = (this.num == 0); // Handle 0/1 as integer 0
            }
            
            
            if (!isInteger) {
                 DecimalFormat df = new DecimalFormat("#.###");
                 fallbackDecimal = df.format(value);
            }
        }

      
        private long[] toFraction(double value) {
            double x = Math.abs(value);
            double h1 = 1, h2 = 0, k1 = 0, k2 = 1;
            double b = x;
            
        
            for (int i = 0; i < 20; i++) {
                double a = Math.floor(b);
                double aux = h1; h1 = a * h1 + h2; h2 = aux;
                aux = k1; k1 = a * k1 + k2; k2 = aux;
                
              
                if (h1 > Long.MAX_VALUE / 2 || k1 > Long.MAX_VALUE / 2) break;
                
                if (Math.abs(b - a) < 1e-11) break;
                b = 1 / (b - a);
            }
            long finalNum = (long) h1;
            if (value < 0) finalNum *= -1;
            return new long[]{finalNum, (long) k1};
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            FontMetrics fm = g2.getFontMetrics();

            if (isInteger) {
                String s = String.valueOf(num);
                g2.drawString(s, (getWidth() - fm.stringWidth(s)) / 2, (getHeight() + fm.getAscent()) / 2 - 2);
            } else {
                String nStr = String.valueOf(Math.abs(num));
                String dStr = String.valueOf(den);
                boolean isNeg = num < 0;
                
                int wN = fm.stringWidth(nStr);
                int wD = fm.stringWidth(dStr);
                int maxW = Math.max(wN, wD);
                int signW = isNeg ? fm.stringWidth("-") + 4 : 0;
                int totalW = maxW + signW;

            
        if (totalW > getWidth() - 4) {
                    g2.drawString(fallbackDecimal, (getWidth() - fm.stringWidth(fallbackDecimal)) / 2, (getHeight() + fm.getAscent()) / 2 - 2);
                    return;
                }

                int startX = (getWidth() - totalW) / 2;
                int centerY = getHeight() / 2;

                if (isNeg) g2.drawString("-", startX, centerY + (fm.getAscent()/3));
                
                g2.drawString(nStr, startX + signW + (maxW - wN) / 2, centerY - 8);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawLine(startX + signW - 2, centerY, startX + signW + maxW + 2, centerY);
                g2.drawString(dStr, startX + signW + (maxW - wD) / 2, centerY + fm.getAscent() + 2);
            }
        }
    }

    public static void main(String[] args) {
        // run on edt for safety
        SwingUtilities.invokeLater(() -> new RREFSolverGUI().setVisible(true));
    }
}