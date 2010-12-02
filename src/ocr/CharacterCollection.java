/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author installer
 */
public class CharacterCollection {
    private ArrayList<Character> characters;
    private int maxDistance;
    public CharacterCollection(int maxDistance){
        this.maxDistance = maxDistance;
        characters = new ArrayList<Character>();
    }

    public int getCharacterNumber(Character character){
        if(characters.contains(character) && characters.get(characters.indexOf(character)).getDistance(character) < maxDistance){
            return characters.indexOf(character);
        }else{
            int i = 0;
            boolean found = false;
            while(i < characters.size() && !found){
                if(characters.get(i).getDistance(character) < maxDistance){                    
                    found = true;
                }
                i++;
            }
            if(found){                
                return i-1;
            }else{                
                characters.add(character);
                return characters.size()-1;
            }
        }
    }

    public int getAlphabetSize(){
        return characters.size();
    }

    public Character getCharacter(int i){
        if(i >= 0 && i < characters.size()){
            return characters.get(i);
        }
        return null;
    }

    public List<Character> getAlphabet(){
        return characters;
    }
}
