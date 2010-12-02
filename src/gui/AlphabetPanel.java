/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ocr.Character;
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

    public AlphabetPanel(Document document){
        this.document = document;
        this.setLayout(new BorderLayout());
        panels = new ArrayList<CharacterPanel>();
    }

    public void changed() {
        List<Character> characters = document.getAlphabet();
        panels.clear();
        JPanel panel =new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        int height = 0;
        int width = 0;
        for(int i = 0; i < characters.size(); i++){
            CharacterPanel characterPanel = new CharacterPanel(characters.get(i));
            panels.add(characterPanel);
            height += characterPanel.getPreferredSize().height;
            if(width < characterPanel.getPreferredSize().width){
                width = characterPanel.getPreferredSize().width;
            }
            panel.add(characterPanel);
        }
        height *= 1.4;
        panel.setPreferredSize(new Dimension(width,height));
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.removeAll();                
        this.add(scrollPane,BorderLayout.CENTER);
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

}
