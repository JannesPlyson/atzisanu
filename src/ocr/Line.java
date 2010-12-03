/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr;

import java.util.ArrayList;
import java.util.List;

import fonts.ImageCreator;

/**
 *
 * @author installer
 */
public class Line {
    private ArrayList<Integer> characters;
    private ArrayList<Integer> gaps;

    public Line(OCRImage lineImage,CharacterCollection characterCollection, int charPixelSize){
        characters = new ArrayList<Integer>();
        gaps = new ArrayList<Integer>();
        List<OCRImage> originalImages = new ArrayList<OCRImage>();
        List<OCRImage> charImages = lineImage.getCharacters(charPixelSize,originalImages,gaps);
        for(int i = 0; i < charImages.size(); i++){
            Character character = new Character(charImages.get(i));
            character.setOriginalImage(originalImages.get(i));
            characters.add(characterCollection.getCharacterNumber(character));
        }
    }

    public ArrayList<Integer> getCharacters() {
        return characters;
    }

    public String getString(CharacterCollection characterCollection) {
        String s = "";
        for(int i=0; i < characters.size(); i++){
            s += (characterCollection.getCharacter(characters.get(i)).getCharacter());
        }
        return s;
    }

    public String getStringWithSpaces(CharacterCollection characterCollection,String fontName){
        String s = "";
        for(int i=0; i < characters.size()-1; i++){
            Character c = characterCollection.getCharacter(characters.get(i));
            s += (c.getCharacter());            
            if(gaps.get(i) >= ImageCreator.getMinSpaceGap(c.getCharacter(), c.getOriginalImage().image.getHeight(), fontName)){
                s+= " ";
            }
        }
        if(!characters.isEmpty()){
            s += (characterCollection.getCharacter(characters.get(characters.size()-1)).getCharacter());
        }
        return s;
    }
}
