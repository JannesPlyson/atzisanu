/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hopfieldnet;

import java.util.List;
import java.util.Stack;

/**
 *
 * @author jannes
 */
public class HopfieldBinaryTree {
    private int vectorSize;    
    //private int bladen = 0;
    private Stack<Vector> vectors;
    //private List<Vector> vectorList;
    //private String chars = "abcdefghijklmnopqrstuvwxyz";
   
    public void setChars(String chars){
        //this.chars = chars;
    }

    public HopfieldBinaryTree(List<Vector> vectors){
        //vectorList = vectors;
        vectorSize = vectors.get(0).getSize();
        this.vectors = new Stack<Vector>();
        this.vectors.addAll(vectors);        
    }

    /*
    public void learn(){
        //List<Vector> vectorTest = new ArrayList<Vector>();
        for(int i = 0; i < vectorList.size(); i++){
            Vector originalVector = vectorList.get(i);
            Vector testVector = (Vector)originalVector.clone();
            testVector.addNoise(0.45);
            Vector result = test(testVector);
            if(result != null){
                if(!result.equals(originalVector)){
                    System.out.println("should learn: " + chars.charAt(vectorList.indexOf(originalVector)) + ",and " + chars.charAt(vectorList.indexOf(result)));
                }
            }else{
                System.out.println("failed to learn: " + chars.charAt(i));
            }
        }
    }
    */
    public Vector test(Vector v){
        @SuppressWarnings("unchecked")
		Stack<Vector> stack = (Stack<Vector>)vectors.clone();
        HopfieldNet hn = new HopfieldNet(vectorSize);
        while(stack.size() > 1){
            hn.resetConnections();
            Vector vectorLeft = stack.pop();
            Vector vectorRight = stack.pop();
            hn.learn(vectorLeft);
            hn.learn(vectorRight);
            Vector result = hn.test(v);            
            if(result.equals(vectorLeft)){                
                stack.push(vectorLeft);
            }else if(result.equals(vectorRight)){                
                stack.push(vectorRight);
            }else{                
            }            
        }
        if(!stack.isEmpty()){            
            return stack.pop();
        }else{
            return null;
        }
    }
}
