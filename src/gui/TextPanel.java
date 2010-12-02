/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import ocr.Character;
import ocr.Document;
import ocr.DocumentChangedListener;

/**
 *
 * @author installer
 */
public class TextPanel extends JPanel implements DocumentChangedListener,KeyListener{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Document document;
    JTextArea textArea;
    CharacterPanel characterPanel;   
    FontDetectionPanel fontDetectionPanel;
    
    public TextPanel(Document document, FontDetectionPanel fontDetectionPanel){
        this.document = document;
        this.fontDetectionPanel = fontDetectionPanel;
        this.setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.addKeyListener(this);
        //textArea.setEditable(false);
        this.add(textArea,BorderLayout.CENTER);
        characterPanel = new CharacterPanel();
        this.add(characterPanel,BorderLayout.SOUTH);
    }

    public void changed() {
        if(fontDetectionPanel.getLastFont() != null){
            textArea.setText(document.getText(fontDetectionPanel.getLastFont()));
        }else{
            textArea.setText(document.getText());
        }
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {
        //TODO still problems when there are 2 of the same characters after each other.
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            if(textArea.getText().charAt(textArea.getCaretPosition()) == ' '){
                textArea.setCaretPosition(textArea.getCaretPosition()-1);
            }
        }
        setSelection();
    }

    private Character getCharacter(int caretPosition){
        String s = textArea.getText().substring(0,caretPosition);
        s = s.replace(" ", "");
        Character character = document.getCharacter(s.length());
        return character;
    }

    private void setSelection(){
        int caretPosition = textArea.getCaretPosition();
        Character character = getCharacter(caretPosition);
        if(character != null){
            characterPanel.setCharacter(character);
            int last = caretPosition;
            boolean space = false;
            while(character == getCharacter(last)){
                space = space || (textArea.getText().charAt(last) == ' ');
                last++;
            }
            if(last-caretPosition == character.getCharacter().length()){
                textArea.setCaretPosition(caretPosition+character.getCharacter().length());
                textArea.moveCaretPosition(caretPosition);
            }else{
                if(space){
                    last--;
                }
                textArea.setCaretPosition(last);
                setSelection();
            }
        }
    }
}
