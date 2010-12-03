/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hopfieldnet;

import ocr.OCRImage;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author jannes
 */
public class Vector {

    public boolean[] values;
    public Vector(int size){
        values = new boolean[size];
    }

    public Vector(boolean[] values){
        this.values = values;
    }

    public Vector(OCRImage bwi){
        BufferedImage image = bwi.image;
        values = new boolean[image.getWidth()*image.getHeight()];
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                values[x*image.getWidth()+y] = image.getRGB(x, y) == Color.BLACK.getRGB();
            }
        }
    }

    @Override
    public String toString(){
        String s = "";
        for(int i = values.length -1 ; i >= 0 ; i--){
            if(values[i]){
                s += "1";
            }else{
                s += "0";
            }
        }
        return s;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Vector){
            boolean[] v = ((Vector)obj).values;
            boolean returnValue = true;
            int i = 0;
            while(i < values.length && returnValue){
                returnValue = returnValue && (v[i] == values[i]);
                i++;
            }
            return returnValue;
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Arrays.hashCode(this.values);
        return hash;
    }

    public int getSize(){
        return values.length;
    }

    @Override
    public Object clone(){
        Vector v = new Vector(values.length);
        v.values = values.clone();
        return v;
    }

    public void next(){
        int firstZero = 0;
        while(firstZero < values.length && values[firstZero]){
            firstZero++;
        }
        if(firstZero == values.length){
            for(int i = 0; i < values.length; i++){
                values[i] = false;
            }
        }else{
            values[firstZero] = true;
            for(int i = firstZero-1; i >=0; i-- ){
                values[i] = false;
            }
        }
    }

/*
    @Override
    public int hashCode(){
        return md5.getHash(this.toString());
    }
*/
    public void addNoise(double percentage){
        int changes = (int)(percentage*values.length);
        Random random = new Random();
        for(int i = 0; i < changes; i++){
            int x = random.nextInt(values.length);
            values[x] = !values[x];
        }
    }
}
