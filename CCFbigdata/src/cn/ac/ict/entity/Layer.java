package cn.ac.ict.entity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import cn.ac.ict.bp.ActiveFunction;

/**
 * Created by yixuanhe on 11/13/15.
 */
public class Layer {
    // the cell number
    int number;

    // the cell array
    Cell[] cells;

    // the output
    double[] output;

    // the active situation cell
    boolean[] isActive;

    /*
     * number : cell number
     * length : input feature number
     * active : the active function
     * rate : the learning rate
     */
    public Layer(int number, int length, ActiveFunction active, double rate, double threshold){
        this.number = number;
        this.cells = new Cell[number];
        this.output = new double[number];
        this.isActive = new boolean[number];

        for (int i = 0; i < number; i++){
            this.cells[i] = new Cell(active, length, rate, threshold);
        }
    }

    public double[] calOutput(double[] value){
        for (int i = 0; i < number; i++){
            output[i] = cells[i].calOutput(value);
        }

        return output;
    }

    public double[] getOutput(){
        return output;
    }

    public void update(double[][] gradients){
        for (int i = 0; i < number; i++){
            cells[i].update(gradients[i]);
        }
    }
    
    public void saveModel(String modelPath){
    	for (int i = 0; i < number; i++){
            cells[i].saveModel(modelPath);
        }
    }

    public void loadModel(String modelPath, int startLine){
    	InputStreamReader reader;
		try {
			reader = new InputStreamReader(new FileInputStream(modelPath),
					"UTF-8");
			BufferedReader reader0 = new BufferedReader(reader);
			String filecontent = null;
			int j = 0;
			int currentline = 0;
			while ((filecontent = reader0.readLine()) != null) {
				if(currentline < startLine){
					currentline++;
					continue;
				} else if (j >= cells.length){
					break;
				}
				String[] strs = filecontent.split("\t");
				double[] cellsTmp = new double[strs.length];
//				System.out.println(strs.length);
//				System.out.println(cells[j].weight.length);
				for (int i =0; i<strs.length; i++) {
					cellsTmp[i] = Double.parseDouble(strs[i]);
				}
				cells[j].weight = cellsTmp.clone();
				j++;
			}
			reader.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean[] calActive(){
        for (int i = 0; i < number; i++){
            isActive[i] = cells[i].isActive();
        }

        return isActive;
    }

    public boolean[] getActive(){
        return isActive;
    }

    // get the weight of ith cell
    public double[] getWeight(int i){
        return cells[i].getWeight();
    }

    // add epsilon to the jth weight of ith cells in this layer
    public void addWeight(int i, int j, double epsilon){
        cells[i].addEpsilon(j, epsilon);
    }

    // minus epsilon to the jth weight of ith cells in this layer
    public void minusWeight(int i, int j, double epsilon){
        cells[i].minusEpsilon(j, epsilon);
    }
}
