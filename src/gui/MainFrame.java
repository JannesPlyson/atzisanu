/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainFrame.java
 *
 * Created on 6-nov-2010, 15:03:42
 */

package gui;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import ocr.Document;
import ocr.DocumentLogger;

/**
 *
 * @author installer
 */
public class MainFrame extends javax.swing.JFrame implements DocumentLogger{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Document document;
    JLabel logLabel;
    /** Creates new form MainFrame */
    
    public MainFrame(){
        initComponents();
        JPanel mainPanel = new JPanel(new BorderLayout());
        this.setContentPane(mainPanel);
        logLabel = new JLabel();
        mainPanel.add(logLabel,BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane,BorderLayout.CENTER);
        
        ImageOptimizationPanel imageOptimizationPanel = new ImageOptimizationPanel();
        tabbedPane.addTab("Image optimization", imageOptimizationPanel);        

        FontDetectionPanel fontDetectionPanel = new FontDetectionPanel(imageOptimizationPanel,this);
        tabbedPane.addTab("Font detection", fontDetectionPanel);

        document = new Document(20);
        document.setDocumentLogger(this);
        CharacterDetectionPanel characterDetectionPanel = new CharacterDetectionPanel(imageOptimizationPanel,document,fontDetectionPanel);
        tabbedPane.addTab("Character Detection", characterDetectionPanel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    public void log(String string) {
        logLabel.setText(string);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}