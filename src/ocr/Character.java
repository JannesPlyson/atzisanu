/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr;

/**
 *
 * @author installer
 */
public class Character {    
    private String character;
    private OCRImage image, originalImage;

    public Character(OCRImage image){
        this.image = image;
        character = "?";
        originalImage = image;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public OCRImage getImage() {
        return image;
    }

    public int getDistance(Character c){
        int widthDifference = Math.abs(originalImage.image.getWidth()-c.originalImage.image.getWidth());
        int heightDifference = Math.abs(originalImage.image.getHeight()-c.originalImage.image.getHeight());
        if(widthDifference <= image.image.getWidth()*0.1 && heightDifference <= image.image.getHeight()*0.1 ){
            return image.getDistance(c.image);
        }else{
            return Integer.MAX_VALUE;
        }        
    }

    public int getDistance(Character c, int maxDistance) throws Exception{
        return image.getDistance(c.image,maxDistance);
    }

    public OCRImage getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(OCRImage originalImage) {
        double scale = 1.0*image.image.getHeight()/originalImage.image.getHeight();
        originalImage.resize((int)(originalImage.image.getWidth() * scale), image.image.getHeight());
        this.originalImage = originalImage;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Character){
            return ((Character)obj).image.equals(image);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.image != null ? this.image.hashCode() : 0);
        return hash;
    }

}
