/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import ocr.Document;

/**
 *
 * @author installer
 */
public class CharacterDetectionPanel extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Document document;
    ImageOptimizationPanel imageOptimizationPanel;

    public CharacterDetectionPanel(final ImageOptimizationPanel imageOptimizationPanel,final Document document,FontDetectionPanel fontDetectionPanel){
        this.imageOptimizationPanel = imageOptimizationPanel;
        this.setLayout(new BorderLayout());
        this.document = document;
        //TextFieldDocumentLogger documentLogger = new TextFieldDocumentLogger();
        //this.add(documentLogger,BorderLayout.SOUTH);
        //document.setDocumentLogger(documentLogger);

        AlphabetPanel alphabetPanel = new AlphabetPanel(document);
        this.add(alphabetPanel, BorderLayout.WEST);
        document.addDocumentChangedListener(alphabetPanel);

        JButton buttonCreateDocument = new JButton("Create document");
        this.add(buttonCreateDocument,BorderLayout.NORTH);
        buttonCreateDocument.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                document.setDocumentImage(imageOptimizationPanel.getOptimizedImage());
                document.createDocument();
            }
        });

        TextPanel textPanel = new TextPanel(document,fontDetectionPanel);
        this.add(textPanel, BorderLayout.CENTER);
        textPanel.setEnabled(false);
        document.addDocumentChangedListener(textPanel);

        CharacterDetectionOptionPanel characterDetectionOptionPanel = new CharacterDetectionOptionPanel(document,fontDetectionPanel);
        this.add(characterDetectionOptionPanel,BorderLayout.EAST);

    }


}
