/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hopfieldnet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author jannes
 */
public class MD5Wrapper {
    private MessageDigest md5;
    public MD5Wrapper(){
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {}
    }

    public int getHash(String s){
        try{            
            return md5.digest(s.getBytes(), 0, s.getBytes().length);
        }catch(Exception exc){
            System.out.println("hashing failed");
            return 0;
        }
    }
}
