package ocr;

import java.awt.Font;
import java.util.ArrayList;

import fonts.ImageCreator;

public class CharacterDetectorAdvancedUsingDistanceVector implements CharacterDetector {
	private Font font;
    private String charstring = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,.?!%();/\\0123456789";
    private ArrayList<Character> characters;
    private Document document;
    private double ratioImportance;

    public CharacterDetectorAdvancedUsingDistanceVector(String fontName, int charSize,Document document){
        this.document = document;        
        font = new Font(fontName,Font.PLAIN,100);
        DocumentLogger documentLogger = document.getDocumentLogger();
        documentLogger.log("creating font characters for " + fontName);
        characters = ImageCreator.getCharacters(charstring, font,charSize);
        documentLogger.log("detector for font created");
        ratioImportance = 5.0;
    }

    public String getString(Character character) {
        int distance = Integer.MAX_VALUE;             
        String str = "?";
        for(int i = 0; i < characters.size();i++){
            int currentDistance;
            try {
                currentDistance = characters.get(i).getDistance(character, Integer.MAX_VALUE);
                //making the ratio a part of the distance;
                double ratio1 = 1.0*characters.get(i).getOriginalImage().image.getWidth()/characters.get(i).getOriginalImage().image.getHeight();
                double ratio2 = 1.0*character.getOriginalImage().image.getWidth()/character.getOriginalImage().image.getHeight();
                currentDistance += (int)(currentDistance*(ratioImportance*Math.abs(ratio1-ratio2)));
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
                double ratio1 = 1.0*characters.get(i).getOriginalImage().image.getWidth()/characters.get(i).getOriginalImage().image.getHeight();
                double ratio2 = 1.0*character.getOriginalImage().image.getWidth()/character.getOriginalImage().image.getHeight();
                int oldDistance = distance;
                distance += (int)(distance*(ratioImportance*Math.abs(ratio1-ratio2)));
                if(distance < 200){
                	String str = characters.get(i).getCharacter() + "-->" + distance + "(" + oldDistance +")";
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
