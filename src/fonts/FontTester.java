/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fonts;

import hopfieldnet.HopfieldBinaryTree;
import hopfieldnet.Vector;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ocr.OCRImage;
import blackwhiteimages.ImageCreator;

/**
 *
 * @author installer
 */
public class FontTester {
    private Font font;
    private String chars = "abcdefghijklmnopqrstuvwxyz";
    private int charSize;
    private ArrayList<Vector> vectors;
    private ArrayList<Vector> testVectors;
    //private HopfieldTree hopfieldTree;
    private HopfieldBinaryTree hopfieldBinaryTree;

    public FontTester(String font, int charSize){
        this.charSize = charSize;
        this.font = new Font(font,Font.PLAIN,100);
        vectors = new ArrayList<Vector>();
        testVectors = new ArrayList<Vector>();
        String fontName = this.font.getFontName();
        File dir = new File("fonts");
        dir = new File(dir,fontName);
        if(!dir.exists()){
            dir.mkdirs();            
        }
        loadImages(dir);
        testLoadedVectors(dir);        
        createHopfieldTree(vectors);
        System.out.println("fontTester created");
    }

    public String getChars() {
        return chars;
    }

    private void createHopfieldTree(List<Vector> vectors){
        hopfieldBinaryTree = new HopfieldBinaryTree(vectors);
        hopfieldBinaryTree.setChars(chars);
        //hopfieldBinaryTree.learn();
        //hopfieldTree = new HopfieldTree(vectors, 2);
        //hopfieldTree.learn(vectors, 1, 0.3);
    }

    private void testLoadedVectors(File dir){
        int length = charSize*charSize;
        boolean correct = true;
        int i = 1;
        while(i < vectors.size() && correct){
            correct = correct   && (vectors.get(i).getSize() == length)
                                && (testVectors.get(i).getSize() == length);
            i++;
        }
        if(!correct){
            vectors.clear();
            testVectors.clear();
            createImages(dir);
            loadImages(dir);
        }
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
                vectors.add(new Vector(new OCRImage(image,true)));
                fileName = "test_" + fileName;
                f = new File(dir,fileName);
                image = ImageIO.read(f);
                testVectors.add(new Vector(new OCRImage(image,true)));
            }
        }catch(Exception exc){
            vectors.clear();
            testVectors.clear();
            createImages(dir);
            loadImages(dir);
        }
    }

    private void createImages(File dir){
            System.out.println("FontTester: creating images");
            ImageCreator ic = new ImageCreator();
            ic.createImages(chars, font, dir,charSize,charSize);
            ic.createTestImages(chars, font, dir, charSize, charSize, 0.1);
    }

    public char testImage(OCRImage bi){
        Vector testVector = new Vector(bi);
        Vector resultVector = hopfieldBinaryTree.test(testVector);
        int index = vectors.indexOf(resultVector);
        if(index != -1){
            return chars.charAt(vectors.indexOf(resultVector));
        }else{
            return '?';
        }        
    }

    public Vector getVector(char c){
        return vectors.get(chars.indexOf(c));
    }

    public Vector getTestVector(char c){
        return testVectors.get(chars.indexOf(c));
    }

}
