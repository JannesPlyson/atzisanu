/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr;

import java.util.ArrayList;

/**
 *
 * @author installer
 */
public interface CharacterDetector {
    
    String getString(Character character);
    ArrayList<String> getDistances(Character character);
}
