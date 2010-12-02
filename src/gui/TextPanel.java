/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import ocr.Character;
import ocr.Document;
import ocr.DocumentChangedListener;

/**
 *
 * @author installer
 */
public class TextPanel extends JPanel implements DocumentChangedListener,KeyListener,MouseListener{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Document document;
    JTextArea textArea;
    CharacterPanel characterPanel;   
    FontDetectionPanel fontDetectionPanel;
    int lastMark;
    
    public TextPanel(Document document, FontDetectionPanel fontDetectionPanel){
        this.document = document;
        this.fontDetectionPanel = fontDetectionPanel;
        this.setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.addKeyListener(this);
        textArea.addMouseListener(this);
        textArea.setEditable(false);
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
    	if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP){    		
    		if(textArea.getCaretPosition()> 0){
    			textArea.setCaretPosition(textArea.getCaretPosition()-1);
    		}
    	}
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
        	textArea.setCaretPosition(lastMark-1);            
        }
        setSelection();
    }

    private Character getCharacter(int caretPosition){
        String s = textArea.getText().substring(0,caretPosition);
        s = s.replace(" ", "");
        Character character = document.getCharacter(s.length());
        return character;
    }
    
    private int getCharacterIndex(int caretPosition){
    	String s = textArea.getText().substring(0,caretPosition);
        s = s.replace(" ", "");
        return document.getCharacterIndex(s.length());        
    }

    private void setSelection(){
        int caretPosition = textArea.getCaretPosition();
        Character character = getCharacter(caretPosition);
        if(character != null){
            characterPanel.setCharacter(character);
            int index = getCharacterIndex(caretPosition);            
            int left = caretPosition;
            int right = caretPosition;
            while(left > 0 && index == getCharacterIndex(left-1)){            	
            	left--;
            }            
            while(right < textArea.getText().length()-1 && index == getCharacterIndex(right+1)){            	
            	right++;
            }
            if(right > textArea.getText().length()-1){
            	right = textArea.getText().length()-1;
            }
            lastMark = left;
            textArea.setCaretPosition(left);
            textArea.moveCaretPosition(right+1);            
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub		
		setSelection();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub		
	}
}
