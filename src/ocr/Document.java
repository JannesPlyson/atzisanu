/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 *
 * @author installer
 */
public class Document {
    private ArrayList<Line> lines;
    private CharacterCollection characterCollection;
    private OCRImage documentImage;
    private int charPixelSize, maxDistance;
    private DocumentLogger documentLogger;
    private boolean askedForRecreation;
//    private Thread creatingThread;
    private ArrayList<DocumentChangedListener> listeners;
    private String newline;

    public Document(OCRImage documentImage,int maxDistance,DocumentLogger documentLogger){
        this.documentLogger = documentLogger;
        this.documentImage = documentImage;
        charPixelSize = 20;
        this.maxDistance = maxDistance;
        listeners = new ArrayList<DocumentChangedListener>();
        newline = System.getProperty("line.separator");        
    }

    public Document(OCRImage documentImage,int maxDistance){
        this(documentImage,maxDistance,new EmptyDocumentLogger());
    }

    public Document(int maxDistance){
        this(null,maxDistance,new EmptyDocumentLogger());
    }

    public void setDocumentImage(OCRImage documentImage){
        this.documentImage = documentImage;
    }

    public BufferedImage getFirstLineImage(){
        if(documentImage != null){
            List<OCRImage> lineImages = documentImage.getLines();
            return lineImages.get(0).image;
        }else{
            return null;
        }
    }

    public String getFirstLineText(){
        if(documentImage != null){
            return lines.get(0).getString(characterCollection);
        }else{
            return null;
        }
    }

    public void createDocument(){
        if(documentImage != null){
            askedForRecreation = true;
            new Thread(new Runnable() {
                public void run() {
                    synchronizedCreateDocument();
                }
            }).start();
        }
    }

    private synchronized void synchronizedCreateDocument(){
        try{
            askedForRecreation = false;
            lines = new ArrayList<Line>();
            characterCollection = new CharacterCollection(maxDistance);
            List<OCRImage> lineImages = documentImage.getLines();
            for(int i = 0; i < lineImages.size(); i++){            
                if(!askedForRecreation){
                    warnListerners();
                    OCRImage lineImage = lineImages.get(i);
                    documentLogger.log("evaluating line number " + i);
                    Line line = new Line(lineImage, characterCollection, charPixelSize);
                    lines.add(line);
                }else{                    
                    break;
                }
            }
            documentLogger.log("created an alphabet of size " + getAlphabetSize());
            warnListerners();
        }catch(Exception exc){
            documentLogger.log("Error when trying to create the document: " + exc);
            exc.printStackTrace();
        }
    }

    public int getCharPixelSize() {
        return charPixelSize;
        }

    public void setCharPixelSize(int charPixelSize) {        
        this.charPixelSize = charPixelSize;
        createDocument();
    }

    public int getAlphabetSize(){
        return characterCollection.getAlphabetSize();
    }

    public DocumentLogger getDocumentLogger() {
        return documentLogger;
    }

    public void setDocumentLogger(DocumentLogger documentLogger) {
        this.documentLogger = documentLogger;
    }

    public List<Character> getAlphabet(){
        return characterCollection.getAlphabet();
    }

    public void addDocumentChangedListener(DocumentChangedListener listener){
        listeners.add(listener);
    }

    public void warnListerners(){
        for(int i = 0; i < listeners.size();i++){
            listeners.get(i).changed();
        }
    }

    public void saveAlphabet(){
        File dir = new File("Alphabet");
        dir.mkdirs();
        List<Character> characters = characterCollection.getAlphabet();
        for(int i = 0; i < characters.size(); i++){
            File f = new File(dir,"char_" + i +  ".png");
            try {
                ImageIO.write(characters.get(i).getImage().image, "png", f);
            } catch (IOException ex) {}
        }
    }

    public void changeCharacterValues(List<String> newValues){
        if(newValues.size() == characterCollection.getAlphabetSize()){
            for(int i=0; i < newValues.size(); i++){
                characterCollection.getCharacter(i).setCharacter(newValues.get(i));
            }
            warnListerners();
            documentLogger.log("Character values updated");
        }else{
            documentLogger.log("can't change characters value: list is not the right size");
        }
    }

    public String getText(){
        String s = "";        
        for(int i=0; i<lines.size(); i++){
            s += (lines.get(i).getString(characterCollection) + newline);
        }
        return s;
    }

    public String getText(String fontName){
        String s = "";
        for(int i=0; i<lines.size(); i++){
            s += (lines.get(i).getStringWithSpaces(characterCollection,fontName) + newline);
        }
        return s;
    }

    public Character getCharacter(int dot){
        int location = 0;
        int lineNumber = 0;
        Line line;
        int lineCharacterNumber = 0;
        Character lastCharacter = null;
        String passedString = "";
        if(!lines.isEmpty()){
            line = lines.get(lineNumber);
            while(location <= dot && lineNumber < lines.size()){
                if(lineCharacterNumber < line.getCharacters().size()){
                    lastCharacter = characterCollection.getCharacter(line.getCharacters().get(lineCharacterNumber));
                    passedString += lastCharacter.getCharacter();
                    location += lastCharacter.getCharacter().length();
                    lineCharacterNumber++;
                }else{
                    lineNumber++;
                    if(lineNumber < lines.size()){
                        line = lines.get(lineNumber);
                        lineCharacterNumber = 0;
                    }
                    location += newline.length(); //linefeed
                }
            }
        }
        if(lastCharacter == null){
            documentLogger.log("Could not find the character from the dot value " + dot);            
        }else{
            documentLogger.log("Found character at dot position " + dot + ": " + lastCharacter.getCharacter());            
            //System.out.println(passedString);
        }
        return lastCharacter;
    }

    public void detectCharacters(String fontFamily){
        if(documentImage != null){
            final String fontName = fontFamily;
            final Document thisDocument = this;
            new Thread(new Runnable() {
                public void run() {
                    CharacterDetector detector = new CharacterDetectorUsingDistanceVector(fontName, charPixelSize, thisDocument);
                    List<Character> characters = characterCollection.getAlphabet();
                    int alphabetSize = characters.size();
                    for(int i = 0; i < alphabetSize; i++){
                        documentLogger.log("testing character " + (i+1) + "/" + alphabetSize +  " with font " + fontName);
                        Character character = characters.get(i);
                        character.setCharacter(detector.getString(character));
                        //warnListerners();
                    }
                    warnListerners();
                }
            }).start();
        }
        
    }

    public void detectCharactersNoThread(String fontFamily){
        if(documentImage != null){
            CharacterDetector detector = new CharacterDetectorUsingDistanceVector(fontFamily, charPixelSize, this);
            List<Character> characters = characterCollection.getAlphabet();
            int alphabetSize = characters.size();
            for(int i = 0; i < alphabetSize; i++){
                documentLogger.log("testing character " + (i+1) + "/" + alphabetSize + " with font " + fontFamily);
                Character character = characters.get(i);
                character.setCharacter(detector.getString(character));
                warnListerners();
            }
        }

    }
}
