/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fonts;

import ocr.OCRImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import ocr.Character;

/**
 *
 * @author installer
 */
public class ImageCreator{

    private static Graphics2D graphics = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB).createGraphics();

    public static ArrayList<Character> getCharacters(String s, Font f, int heightWidth){
        Font font = f.deriveFont(Font.ITALIC);
        ArrayList<Character> characters = new ArrayList<Character>();
        for(int i = 0; i < s.length(); i++){
            String str = "" + s.charAt(i);
            OCRImage bwiPlain = getImage(str, f);            
            if(bwiPlain.image != null){
            	OCRImage originalImage = new OCRImage(bwiPlain.image);
                bwiPlain.resize(heightWidth, heightWidth);
                Character character = new Character(bwiPlain);
                character.setCharacter(str);
                character.setOriginalImage(originalImage);
                characters.add(character);
            }
            OCRImage bwiItalic = getImage(str, font);
            if(bwiItalic.image != null){
                bwiItalic.resize(heightWidth, heightWidth);
                Character character = new Character(bwiItalic);
                character.setCharacter(str);
                characters.add(character);
            }
        }
        //TODO: add connected characters;
        return characters;
    }

    public static OCRImage getImage(String s, Font f){
        int height = graphics.getFontMetrics(f).getHeight();
        int width = graphics.getFontMetrics(f).stringWidth(s)+10;
        int baseline = graphics.getFontMetrics(f).getAscent();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setFont(f);
        g2d.setColor(Color.BLACK);
        g2d.drawString(s, 0, baseline);
        OCRImage ocrImg = new OCRImage(image,true);
        ocrImg.trimImage();
        return ocrImg;
    }

    public static int getMinSpaceGap(String s, int stringHeight, String fontName){
        Font f = new Font(fontName,Font.PLAIN,100);
        OCRImage ocrImage = getImage(s,f);
        int height = ocrImage.image.getHeight();
        double ratio = 1.0*stringHeight/height;
        int spaceWidth = graphics.getFontMetrics(f).stringWidth(" ");
        spaceWidth = (int)(ratio * spaceWidth);
        return spaceWidth;
    }

}
