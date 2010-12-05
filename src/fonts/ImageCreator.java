/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fonts;

import gui.ImagePanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import ocr.Character;
import ocr.OCRImage;

/**
 *
 * @author installer
 */
public class ImageCreator{

    private static Graphics2D graphics = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB).createGraphics();

    public static ArrayList<Character> getCharacters(String s, Font f, int heightWidth){
    	int maxDistance = (int)(0.01*(heightWidth*heightWidth));
        Font font = f.deriveFont(Font.ITALIC);
        ArrayList<Character> characters = new ArrayList<Character>();
        for(int i = 0; i < s.length(); i++){
            String str = "" + s.charAt(i);
            OCRImage bwiPlain = getImage(str, f);            
            if(bwiPlain.image != null){
                characters.add(createCharacter(str, heightWidth, bwiPlain));
            }
            OCRImage bwiItalic = getImage(str, font);
            if(bwiItalic.image != null){
            	characters.add(createCharacter(str, heightWidth, bwiItalic));            
            }
        }
        //testing connected character only for plain;
        
        for(int i = 0; i < s.length(); i++){
        	for(int j = 0; j < s.length(); j++){        		
        		OCRImage bwiFirst = getImage("" + s.charAt(i),f);
        		bwiFirst.resize(heightWidth,heightWidth);        		
        		OCRImage bwiSecond = getImage("" + s.charAt(j), f);
        		bwiSecond.resize(heightWidth,heightWidth);
        		String str = "" + s.charAt(i) + s.charAt(j);
        		OCRImage bwiTotal = getImage(str, f);        		
        		List<OCRImage> chars = bwiTotal.getCharacters(heightWidth);
        		System.out.println("size of chars: " + chars.size());        		
        		if(chars.size() == 2){ //2 chars found but maybe parts are connected (ex: f and point of i);
        			int distance = bwiFirst.getDistance(chars.get(0));
        			System.out.println("distance1: " + distance);
        			distance = Math.max(distance,bwiSecond.getDistance(chars.get(1)));
        			System.out.println("distance2: " + distance);
        			if(distance > maxDistance){ //character has a part of the next character;
        				characters.add(createCharacter("" + s.charAt(i), heightWidth, chars.get(0)));
        				characters.add(createCharacter("" + s.charAt(j), heightWidth, chars.get(1)));
        				System.out.println("added half connected: " + s.charAt(i) + " and " + s.charAt(j));
        			}
        		}else if(chars.size() == 1){//only one char found so characters are connected-> add to array;
        			JDialog dialog = new JDialog();
        			dialog.add(new ImagePanel(chars.get(0).image));
        			dialog.add(new ImagePanel(bwiTotal.image));
        			dialog.setVisible(true);
        			dialog.dispose();
        			characters.add(createCharacter(str, heightWidth, chars.get(0)));
        			System.out.println("added full connected: " + str);
        		}
        		//only for testing:
        		else{
        			System.out.println("imageCreator: 2 characters create 3 pictures");
        		}
        	}
        }
        //TODO: add connected characters;
        return characters;
    }

    private static Character createCharacter(String str, int heightWidth, OCRImage ocrImage){
    	OCRImage originalImage = new OCRImage(ocrImage.image);
        ocrImage.resize(heightWidth, heightWidth);
        Character character = new Character(ocrImage);
        character.setCharacter(str);
        character.setOriginalImage(originalImage);
        return character;
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
