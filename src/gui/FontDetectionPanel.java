/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import ocr.OCRImage;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import ocr.Document;
import ocr.DocumentLogger;

/**
 *
 * @author installer
 */
public class FontDetectionPanel extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageOptimizationPanel imageOptimizationPanel;
    Document document;
    FontDetectionOptionsPanel fontDetectionOptionsPanel;

    public FontDetectionPanel(final ImageOptimizationPanel imageOptimizationPanel,final DocumentLogger documentLogger) {
        this.imageOptimizationPanel = imageOptimizationPanel;
        document = new Document(20);
        document.setDocumentLogger(documentLogger);
        this.setLayout(new BorderLayout());
        final ImagePanel imagePanel = new ImagePanel();
        JScrollPane scrollPane = new JScrollPane(imagePanel);
        this.add(scrollPane);
        JButton btnSelectLine = new JButton("select line");
        this.add(btnSelectLine,BorderLayout.NORTH);
        btnSelectLine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String returnVal = JOptionPane.showInputDialog("give a line number");
                try{
                    int lineNumber = Integer.parseInt(returnVal);
                    List<OCRImage> lines = imageOptimizationPanel.getOptimizedImage().getLines();
                    imagePanel.setImage(lines.get(lineNumber-1).image);
                    document.setDocumentImage(lines.get(lineNumber-1));
                    document.createDocument();
                    document.getDocumentLogger().log("Document created");
                }catch(Exception exc){
                    documentLogger.log(exc.getMessage());
                }
            }
        });
        JTextField textField = new JTextField("enter text of line here");
        this.add(textField,BorderLayout.SOUTH);
        fontDetectionOptionsPanel = new FontDetectionOptionsPanel(document,textField);
        this.add(fontDetectionOptionsPanel,BorderLayout.EAST);
    }

    public String getLastFont(){
        return fontDetectionOptionsPanel.getLastFont();
    }
}
