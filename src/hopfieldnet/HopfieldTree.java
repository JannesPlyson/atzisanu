/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hopfieldnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author installer
 */
public class HopfieldTree {
    int vectorSize;
    int groupSize;
    HopfieldNode hopfieldNode;
    HashMap<Vector, HashMap<Vector, HopfieldNet>> faults;
    HopfieldBinaryTree binairyTree;
    String chars = "abcdefghijklmnopqrstuvwxyz";

    public HopfieldTree(List<Vector> vectors,int groupSize){
        this.groupSize = groupSize;
        vectorSize = vectors.get(0).getSize();
        faults = new HashMap<Vector, HashMap<Vector ,HopfieldNet>>();
        binairyTree = new HopfieldBinaryTree(vectors);
        if(vectors.size() > groupSize){
            hopfieldNode = new HopfieldNode(vectors);
        }else{
            hopfieldNode = new HopfieldLeaf(vectors);
        }
    }

    public void learn(List<Vector> originals, int rounds, double noisePercentage){
        //Todo volledig herwerken
        for(int i = 0; i < originals.size(); i++){
            System.out.print("leren van vector: " + i);
            Vector original = originals.get(i);
            for(int round = 0; round < rounds; round++){
                Vector v = (Vector)original.clone();
                v.addNoise(noisePercentage);
                Vector result = test(v);
                if(result != null){
                    if(!result.equals(originals.get(i))){
                        //fault detected
                        //ArrayList<HopfieldNet> array;
                        if(faults.containsKey(result)){
                            HashMap<Vector,HopfieldNet> map = faults.get(result);
                            if(map.containsKey(original)){
                                //fault already detected;
                            }else{
                                //fault for this result vector known but not for this original vector
                                HopfieldNet hn = new HopfieldNet(vectorSize);
                                hn.learn(original);
                                hn.learn(result);
                                map.put(original, hn);
                            }
                        }else{
                            //fault for this result vector not known
                            HashMap<Vector,HopfieldNet> map = new HashMap<Vector, HopfieldNet>();
                            HopfieldNet hn = new HopfieldNet(vectorSize);
                            hn.learn(original);
                            hn.learn(result);
                            map.put(original, hn);
                            faults.put(result, map);
                        }
                    }
                }
            }
        }
    }

    public Vector test(Vector v){        
        Vector result = hopfieldNode.test(v);
        result = catchFaults(result,v);
        return result;         
    }

    private Vector catchFaults(Vector vFound,Vector v){
        if(vFound != null){
            if(faults.containsKey(vFound)){
                //possible the wrong result;
                HashMap<Vector,HopfieldNet> map = faults.get(vFound);
                Set<Vector> keys = map.keySet();
                Iterator<Vector> iterator = keys.iterator();
                while(iterator.hasNext()){
                    Vector key = iterator.next();
                    HopfieldNet hn = map.get(key);
                    Vector result = hn.test(v);
                    if(result.equals(key)){
                        return key;
                    }
                }
                return vFound;
            }else{
                return vFound;
            }
        }else{
            return null;
            //return binairyTree.test(v);
        }        
    }

    private class HopfieldNode{
        protected HopfieldNet hn;
        protected HashMap<Vector,HopfieldNode> nexthn;
        protected List<Vector> nodeVectors;
        protected HopfieldNode(){}

        public HopfieldNode(List<Vector> vectors){
            nodeVectors = vectors;
            hn = new HopfieldNet(vectorSize);
            nexthn = new HashMap<Vector, HopfieldNode>();
            hn.learn(vectors);
            HashMap<Vector,ArrayList<Vector>> groups = new HashMap<Vector, ArrayList<Vector>>();
            for(int i = 0; i < vectors.size(); i++){
                Vector v = hn.test(vectors.get(i));
                if(groups.containsKey(v)){
                    ArrayList<Vector> array = groups.get(v);
                    array.add(vectors.get(i));
                }else{
                    ArrayList<Vector> array = new ArrayList<Vector>();
                    array.add(vectors.get(i));
                    groups.put(v, array);
                }
            }
            System.out.println("groupsize: " + groups.size());
            Set<Vector> set = groups.keySet();
            for (Iterator<Vector> iter = set.iterator(); iter.hasNext();) {
                Vector parentVector = iter.next();
                ArrayList<Vector> group = groups.get(parentVector);
                if(groups.size() <= 1 ){
                    nexthn.put(parentVector, new HopfieldLeaf(group));
                }else if(group.size() > groupSize){
                    nexthn.put(parentVector, new HopfieldNode(group));
                }else{
                    nexthn.put(parentVector, new HopfieldLeaf(group));
                }
            }

        }

        public Vector test(Vector v){

            Vector result = hn.test(v);            
            if (nexthn.containsKey(result))
            {
                result = nexthn.get(result).test(v);
                if(result == null){
                    String test = "";
                    for(int i = 0; i < nodeVectors.size(); i++){
                        test = test + chars.charAt(hopfieldNode.nodeVectors.indexOf(nodeVectors.get(i)));
                    }
                    System.out.println("binaryTree, vectors: " + nodeVectors.size() + test);
                    HopfieldBinaryTree hbt = new HopfieldBinaryTree(nodeVectors);
                    hbt.setChars(test);
                    result = hbt.test(v);
                }
                return result;
                //return nexthn.get(result).test(v);
            }else{
                System.out.println("Node: twijfel");
                return null;
            }
        }
    }

    private class HopfieldLeaf extends HopfieldNode{        
        private List<Vector> vectors;
        public HopfieldLeaf(List<Vector> vectors){
            hn = new HopfieldNet(vectorSize);
            hn.learn(vectors);
            this.vectors = vectors;            
        }

        @Override
        public Vector test(Vector v){
            Vector returnValue = hn.test(v);
            if(vectors.contains(returnValue)){
                return returnValue;
            }else{
                System.out.println("leaf: twijfel");
                return null;
            }            
        }
    }
}
