/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ocr.Document;

/**
 *
 * @author installer
 */
public class FontDetectionOptionsPanel extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private Document document;
    //private JTextField textField;
    private String lastFont;

    public FontDetectionOptionsPanel(final Document document, final JTextField textField){
        //this.document = document;
        //this.textField = textField;
        lastFont = null;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        final JList fonts = new JList(fontList);
        //JComboBox fonts = new JComboBox(fontList);
        JScrollPane jscrollPane = new JScrollPane(fonts);
        this.add(jscrollPane);
        final JTextField fontPreview = new JTextField("abcdefghijklmnopqrstuvwxyz");
        fontPreview.setEditable(false);
        this.add(fontPreview);
        final DefaultListModel dlm = new DefaultListModel();
        JButton btnSelectAll = new JButton("Select all");
        this.add(btnSelectAll);
        btnSelectAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ListModel listModel = fonts.getModel();
                for(int i = 0; i < listModel.getSize(); i++){
                    dlm.addElement(listModel.getElementAt(i));
                }
            }
        });
        
        final JList possibleFonts = new JList(dlm);
        JScrollPane jscrollPane2 = new JScrollPane(possibleFonts);
        this.add(jscrollPane2);
        
        fonts.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                String selected = (String)fonts.getSelectedValue();
                Font f = new Font(selected, Font.PLAIN, 12);
                fontPreview.setFont(f);
            }
        });
        fonts.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getClickCount() == 2){
                    dlm.addElement(fonts.getSelectedValue());
                }
            }            
        });
        possibleFonts.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getClickCount() == 2){
                    dlm.remove(possibleFonts.getSelectedIndex());
                }
            }            
        });
        JButton btnDetect = new JButton("Detect");
        this.add(btnDetect);
        btnDetect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        String text = textField.getText();
                        int max = 0;
                        String font = "";
                        for(int i=0; i < dlm.getSize(); i++){                            
                            document.detectCharactersNoThread((String)dlm.get(i));
                            String foundText = document.getFirstLineText();
                            int similar = compareStrings(text, foundText);
                            if(similar > max){
                                max = similar;
                                font = (String)dlm.get(i);
                            }
                        }
                        dlm.clear();
                        dlm.addElement(font);
                        lastFont = font;                        
                    }
                }).start();
            }
        });
    }

    private int compareStrings(String s1,String s2){
        int shortest = Math.min(s1.length(), s2.length());
        int similarFromFront = 0;
        int similarFromEnd = 0;
        for(int i = 0; i < shortest; i++){
            if(s1.charAt(i) == s2.charAt(i)){
                similarFromFront++;
            }
            if(s1.charAt(s1.length()-1-i) == s2.charAt(s2.length()-1-i)){
                similarFromEnd++;
            }
        }
        return Math.max(similarFromEnd, similarFromFront);
    }

    public String getLastFont(){
        return lastFont;
    }
}
