/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr;

import fonts.ImageCreator;
import java.awt.Font;
import java.util.ArrayList;

/**
 *
 * @author installer
 */
public class CharacterDetectorUsingDistanceVector implements CharacterDetector{
    private Font font;
    private String charstring = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,.?!%();/\\0123456789";
//    private int charSize;
    private ArrayList<Character> characters;
    private Document document;

    public CharacterDetectorUsingDistanceVector(String fontName, int charSize,Document document){
        this.document = document;
//        this.charSize = charSize;
        font = new Font(fontName,Font.PLAIN,100);
        DocumentLogger documentLogger = document.getDocumentLogger();
        documentLogger.log("creating font characters for " + fontName);
        characters = ImageCreator.getCharacters(charstring, font,charSize);
        documentLogger.log("detector for font created");
    }

    public String getString(Character character) {
        int distance = Integer.MAX_VALUE;
        String str = "";
        for(int i = 0; i < characters.size();i++){
            int currentDistance;
            try {
                currentDistance = characters.get(i).getDistance(character, distance);
                if(currentDistance < distance){
                    distance = currentDistance;
                    str = characters.get(i).getCharacter();
                }
            } catch (Exception ex) {
                document.getDocumentLogger().log(ex.getMessage());
            }            
        }
        return str;
    }

	@Override
	public ArrayList<String> getDistances(Character character) {
		ArrayList<String> distances = new ArrayList<String>();
		for(int i = 0; i < characters.size();i++){
            try {
                int distance = characters.get(i).getDistance(character,Integer.MAX_VALUE);
                if(distance < 100){
                	String str = characters.get(i).getCharacter() + "-->" + distance;
                	str = str + ";  " + (1.0*character.getOriginalImage().image.getWidth()/character.getOriginalImage().image.getHeight());
                	str = str + "-->" + (1.0*characters.get(i).getOriginalImage().image.getWidth()/characters.get(i).getOriginalImage().image.getHeight()); 
                	distances.add(str);
                }
            } catch (Exception ex) {
                document.getDocumentLogger().log(ex.getMessage());
            }            
        }
		return distances;
	}
}
