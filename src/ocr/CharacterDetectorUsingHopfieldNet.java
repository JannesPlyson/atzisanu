/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr;

import blackwhiteimages.ImageCreator;
import hopfieldnet.HopfieldBinaryTree;
import java.awt.Font;
import java.util.ArrayList;
import hopfieldnet.Vector;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author installer
 */
public class CharacterDetectorUsingHopfieldNet implements CharacterDetector {
    private Font font;
    private String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ()";
    private int charSize;
    private ArrayList<Vector> vectors;
    private ArrayList<OCRImage> images;
    private Document document;
    HopfieldBinaryTree hopfieldBinaryTree;
    

    public CharacterDetectorUsingHopfieldNet(String font, int charSize,Document document){
        this.document = document;
        this.charSize = charSize;
        this.font = new Font(font,Font.PLAIN,100);
        vectors = new ArrayList<Vector>();
        images = new ArrayList<OCRImage>();
        String fontName = this.font.getFontName();
        File dir = new File("fonts");
        dir = new File(dir,fontName);
        if(!dir.exists()){
            dir.mkdirs();
        }
        DocumentLogger documentLogger = document.getDocumentLogger();
        documentLogger.log("trying to load font images");
        loadImages(dir);
        documentLogger.log("testing loaded font images");
        testLoadedVectors(dir);
        documentLogger.log("creating detector for font");
        createHopfieldDetector();
        documentLogger.log("detector for font created");
    }

    private void loadImages(File dir){
        try{
            for(int i = 0; i < chars.length(); i++){
                String caption = "" + chars.charAt(i);
                if(caption.toLowerCase().equals(caption)){
                    caption = "lower_";
                }else{
                    caption = "upper_";
                }
                String fileName = caption + chars.charAt(i) + ".png";
                File f = new File(dir,fileName);
                BufferedImage image = ImageIO.read(f);
                OCRImage bwi = new OCRImage(image,true);
                vectors.add(new Vector(bwi));
                images.add(bwi);
            }
        }catch(Exception exc){
            vectors.clear();
            images.clear();
            createImages(dir);
            loadImages(dir);
        }
    }

    private void createImages(File dir){
            document.getDocumentLogger().log("creating new font images");
            ImageCreator ic = new ImageCreator();
            ic.createImages(chars, font, dir,charSize,charSize);
            document.getDocumentLogger().log("new font images created");
    }

     private void testLoadedVectors(File dir){
        int length = charSize*charSize;
        boolean correct = true;
        int i = 1;
        while(i < vectors.size() && correct){
            correct = correct   && (vectors.get(i).getSize() == length);
            i++;
        }
        if(!correct){
            vectors.clear();
            images.clear();
            createImages(dir);
            loadImages(dir);
        }
    }



    private void createHopfieldDetector(){
        //hopfieldBinaryTree = new HopfieldBinaryTree(vectors);
        //hopfieldBinaryTree.setChars(chars);
    }

    public String getString(Character character){
        int distance = Integer.MAX_VALUE;
        int charNumber = 0;
        for(int i = 0; i < chars.length();i++){
            int currentDistance = images.get(i).getDistance(character.getImage());
            if(currentDistance < distance){
                distance = currentDistance;
                charNumber = i;
            }
        }
        return "" + chars.charAt(charNumber);
        /*
        Vector vector = new Vector(character.getImage());
        Vector resultVector =  null;
        resultVector = hopfieldBinaryTree.test(vector);
        int index = vectors.indexOf(resultVector);
        if(index != -1){
            return chars.charAt(vectors.indexOf(resultVector));
        }else{
            return '?';
        }*/

    }
}
