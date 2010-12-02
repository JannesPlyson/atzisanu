/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import ocr.Document;

/**
 *
 * @author installer
 */
public class CharacterDetectionOptionPanel extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Document document;
    FontDetectionPanel fontDetectionPanel;

    public CharacterDetectionOptionPanel(final Document document,final FontDetectionPanel fontDetectionPanel){
        this.document = document;
        this.fontDetectionPanel = fontDetectionPanel;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JButton btnDetect = new JButton("Detect");
        this.add(btnDetect);
        btnDetect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String font = fontDetectionPanel.getLastFont();
                if(font != null){
                    document.detectCharacters(font);
                }else{
                    document.getDocumentLogger().log("no font detected");
                }
            }
        });
    }
}
