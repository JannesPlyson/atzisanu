/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr;

/**
 *
 * @author installer
 */
public class EmptyDocumentLogger implements DocumentLogger{

    public void log(String string) {
        System.out.println(string);
    }

}
