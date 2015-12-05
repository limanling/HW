package cn.ac.ict.feature;

import java.util.List;
import java.util.Map.Entry;

import cn.ac.ict.entity.MessageIns;

public class BayesMat {
	private double X[][];
	private int Y[][];
	
	public BayesMat(List<MessageIns> messages) {
		loadMessages(messages);
	}
	
	private void loadMessages(List<MessageIns> messages){
		int featureSize = messages.get(0).getFeature().size();
		X = new double[messages.size()][featureSize];
		Y = new int[messages.size()][2];
		int index = 0;
		for (MessageIns messageIns : messages) {
			for (Entry<Integer, Double> entry : messageIns.getFeature().entrySet()) {
				X[index][entry.getKey()] = entry.getValue();
			}
			Y[index][0]=messageIns.getLabel();
			index++;
		}
	}
	
	public double[][] getX() {
		return X;
	}

	public void setX(double[][] x) {
		X = x;
	}

	public int[][] getY() {
		return Y;
	}

	public void setY(int[][] y) {
		Y = y;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
