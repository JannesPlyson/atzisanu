/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hopfieldnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jannes
 */
public class HopfieldNet{
    private Vector vector;
    private ArrayList<int[]> connections;
    //private int[][] connections;
    public HopfieldNet(int size){
        vector = new Vector(size);
        connections = new ArrayList<int[]>();
        for(int i = 1; i <= size; i++){
            connections.add(new int[i]);
        }
    }    

    public void learn(Vector v){
        if (v.getSize() == vector.getSize()){
            System.arraycopy(v.values, 0, vector.values, 0, vector.getSize());
            learn();
        }else{
            System.out.println("learn: vector " + v.toString() + " is not the right size");
        }
    }

    public void learn(List<Vector> vectors){
        for(int i = 0; i < vectors.size(); i++){
            Vector v = vectors.get(i);
            learn(v);
        }
    }

    public void resetConnections(){
        for(int i = 0; i < vector.getSize(); i++){
            for(int j = 0; j < i; j++){
                connections.get(i)[j] = 0;
            }
        }
    }

    private void learn(){
        for(int i = 0; i < vector.getSize(); i++){
            for(int j = 0; j < i; j++){
                int value;
                if(vector.values[i]==vector.values[j]){
                    value = 1;
                }else{
                    value = -1;
                }
                connections.get(i)[j] = connections.get(i)[j] + value;
                //connections.get(j)[i] += value;
            }
        }
    }

    public Vector test(Vector v){
        if(v.getSize() == vector.getSize()){
            vector = (Vector)v.clone();
            ArrayList<Integer> list = new ArrayList<Integer>();
            for(int i = 0; i < vector.getSize();i++){
                list.add(i);
            }
            Collections.shuffle(list);
            boolean changed = test(list); //hier list gebruiken
            while(changed){
                changed = test(list); //hier list gebruiken
            }
            return vector;
        }else{
            System.out.println("test: vector is not the right size");
            return null;
        }
    }
    private boolean test(){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < vector.getSize();i++){
            list.add(i);
        }
        return test(list);
        
    }
    private boolean test(List<Integer> list){
        //fout ik bekijk niet alle connection
        //voorlopig bekijk ik slechts diegene op mijn lijn (moet diagonaal kijken)
        Vector oldVector = (Vector)vector.clone();        
        for(int i = 0; i < vector.getSize(); i++){
            int value = 0;
            for(int j=0; j < list.get(i); j++){
                if(vector.values[j]){
                    value += connections.get(list.get(i))[j];
                }else{
                    value -= connections.get(list.get(i))[j];
                }
            }
            for(int j = list.get(i); j < vector.getSize(); j++){
                if(vector.values[j]){
                    value += connections.get(j)[list.get(i)];
                }else{
                    value -= connections.get(j)[list.get(i)];
                }
            } 
            if(value <= 0){
                vector.values[list.get(i)] = false;
            }else{
                vector.values[list.get(i)] = true;
            }
        }

        //return true if changed
        return !oldVector.equals(vector);
    }

    public void printConnections(){
        for(int i = 0; i < vector.getSize(); i++){
            for(int j=0; j < vector.getSize(); j++){
                System.out.print(connections.get(i)[j] + ",");
            }
            System.out.println("");
        }
    }

    public List<Vector> getStableVectors(){
        List<Vector> list = new ArrayList<Vector>();
        Vector v = new Vector(vector.getSize());
        Vector beginVector = (Vector)v.clone();
        vector = (Vector)v.clone();
        if(!test()){
            list.add((Vector)v.clone());
        }
        v.next();
        int teller = 0;
        while(!beginVector.equals(v)){
            System.out.println(teller);
            vector = (Vector)v.clone();
            if(!test()){
                list.add((Vector)v.clone());
            }
            v.next();
            teller++;
        }
        return list;
    }
}
