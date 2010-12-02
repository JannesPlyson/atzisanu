/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import javax.swing.JTextField;
import ocr.DocumentLogger;

/**
 *
 * @author installer
 */
public class TextFieldDocumentLogger extends JTextField implements DocumentLogger {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void log(String string) {
        setText(string);
        revalidate();
    }

}
