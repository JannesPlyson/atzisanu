/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ocr.Character;
/**
 *
 * @author installer
 */
public class CharacterPanel extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Character character;
    JTextField textField;

    public CharacterPanel(){}

    public CharacterPanel(Character character){
        this.character = character;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.removeAll();
        ImagePanel imagePanel = new ImagePanel(character.getOriginalImage().image);
        this.add(imagePanel);        
        textField = new JTextField(5);
        textField.setText(character.getCharacter());
        int height = Math.max(imagePanel.getPreferredSize().height, textField.getPreferredSize().height);
        int width = imagePanel.getPreferredSize().width + textField.getPreferredSize().width;
        this.setPreferredSize(new Dimension(width,height));
        this.add(textField);                 
    }

    public void setCharacter(Character character){
        this.character = character;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.removeAll();
        ImagePanel imagePanel = new ImagePanel(character.getImage().image);
        this.add(imagePanel);
        textField = new JTextField(5);
        textField.setText(character.getCharacter());
        int height = Math.max(imagePanel.getPreferredSize().height, textField.getPreferredSize().height);
        int width = imagePanel.getPreferredSize().width + textField.getPreferredSize().width;
        this.setPreferredSize(new Dimension(width,height));
        this.add(textField);
        this.revalidate();
    }

    public String getText(){
        return textField.getText();
    }
}
