import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainDashboard extends JFrame {
    private JButton selectFolderBtn;
    private JButton runBaselineBtn;
    private JButton runIntegrityBtn; // NAYA BUTTON
    private JButton viewAlertsBtn;   // NAYA BUTTON
    private JTextArea logArea;
    private File selectedFolder;

    public MainDashboard() {
        setTitle("File Integrity Monitor");
        setSize(500, 450); // Frame ki height thori barha di hai taake buttons fit ayen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // --- ROW 1: Purane Buttons ---
        selectFolderBtn = new JButton("Select Folder");
        selectFolderBtn.setBounds(20, 20, 150, 30);

        runBaselineBtn = new JButton("Run Baseline Scan");
        runBaselineBtn.setBounds(180, 20, 150, 30);
        runBaselineBtn.setEnabled(false); // Jab tak folder select na ho disable rahay

        // --- ROW 2: Naye Buttons ---
        runIntegrityBtn = new JButton("Run Integrity Check");
        runIntegrityBtn.setBounds(20, 60, 150, 30); // Iski position log area ke upar set ki hai
        runIntegrityBtn.setEnabled(false); // Ye bhi folder select hone tak disable rahega

        viewAlertsBtn = new JButton("View Alert History");
        viewAlertsBtn.setBounds(180, 60, 150, 30); // Isko bhi set kar diya

        // --- LOG AREA ---
        logArea = new JTextArea();
        logArea.setBounds(20, 110, 440, 270); // Log area thora neeche shift kar diya
        logArea.setEditable(false);

        // --- SAB KUCH SCREEN PAR ADD KARNA ---
        add(selectFolderBtn);
        add(runBaselineBtn);
        add(runIntegrityBtn); // Naya button add kiya
        add(viewAlertsBtn);   // Naya button add kiya
        add(logArea);

        // 1. Folder selection ka kaam
        selectFolderBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFolder = fileChooser.getSelectedFile();
                    logArea.append("Folder Selected: " + selectedFolder.getAbsolutePath() + "\n");
                    runBaselineBtn.setEnabled(true);
                    runIntegrityBtn.setEnabled(true); // Folder select hone par Integrity check bhi enable ho jayega
                }
            }
        });

        // 2. Baseline scan ka kaam
        runBaselineBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.append("Starting Baseline Scan...\n");
                File[] files = selectedFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            String hash = HashGenerator.generateSHA256(file);
                            DatabaseManager.saveFileRecord(file.getName(), file.getAbsolutePath(), hash, file.length());
                            logArea.append("Saved: " + file.getName() + " -> " + hash + "\n");
                        }
                    }
                    logArea.append("Baseline Scan Complete!\n");
                }
            }
        });

        // 3. NAYA KAM: Integrity Check Button ka logic
        runIntegrityBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ye line ChangeDetector class ko call karegi jo tumne abhi banayi thi
                ChangeDetector.runIntegrityCheck(selectedFolder, logArea);
            }
        });

        // 4. NAYA KAM: View Alerts Button ka logic
        viewAlertsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ye line nayi window (AlertPanel) open kar degi
                AlertPanel alerts = new AlertPanel();
                alerts.setVisible(true);
            }
        });
    }
}