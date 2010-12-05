/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ocr.Character;
import ocr.CharacterDetector;
import ocr.CharacterDetectorAdvancedUsingDistanceVector;
import ocr.Document;
import ocr.DocumentChangedListener;

/**
 *
 * @author installer
 */

public class AlphabetPanel extends JPanel implements DocumentChangedListener{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Document document;
    ArrayList<CharacterPanel> panels;    
    DefaultListModel dlm;
    FontDetectionPanel fontDetectionPanel;
    public AlphabetPanel(Document document,FontDetectionPanel fdp){
        this.document = document;
        this.setLayout(new BorderLayout());
        fontDetectionPanel = fdp;
        panels = new ArrayList<CharacterPanel>();
        dlm = new DefaultListModel();
    }

    public void changed() {
        List<Character> characters = document.getAlphabet();
        panels.clear();        
        JPanel alphabet = new JPanel();
        alphabet.setLayout(new BoxLayout(alphabet, BoxLayout.Y_AXIS));                
        int height = 0;
        int width = 0;
        for(int i = 0; i < characters.size(); i++){
            final CharacterPanel characterPanel = new CharacterPanel(characters.get(i));
            panels.add(characterPanel);
            height += characterPanel.getPreferredSize().height;
            if(width < characterPanel.getPreferredSize().width){
                width = characterPanel.getPreferredSize().width;
            }
            alphabet.add(characterPanel);
            characterPanel.textField.addFocusListener(new FocusAdapter() {
            	@Override
            	public void focusGained(FocusEvent e) {            	
            		super.focusGained(e);
            		updateList(characterPanel.character);
            	}
			});
        }
        height *= 1.4;
        alphabet.setPreferredSize(new Dimension(width,height));
        JScrollPane scrollPane = new JScrollPane(alphabet);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);                        
        this.removeAll();
        this.add(scrollPane,BorderLayout.WEST);
        JList list = new JList(dlm);
        JScrollPane listScrollpane = new JScrollPane(list);
        this.add(listScrollpane,BorderLayout.CENTER);
        JButton buttonRefresh = new JButton("Refresh");
        buttonRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> chars = new ArrayList<String>();
                for(int i = 0; i < panels.size(); i++){
                    chars.add(panels.get(i).getText());
                }
                document.changeCharacterValues(chars);
            }
        });
        this.add(buttonRefresh,BorderLayout.NORTH);
        this.revalidate();        
    }
    
    private void updateList(Character character){
    	dlm.clear();    	
    	CharacterDetector detector = new CharacterDetectorAdvancedUsingDistanceVector(fontDetectionPanel.getLastFont(), document.getCharPixelSize(), document);
    	ArrayList<String> distances = detector.getDistances(character);
    	for(int i = 0; i < distances.size(); i++){
    		dlm.addElement(distances.get(i));
    	}
    }
}
