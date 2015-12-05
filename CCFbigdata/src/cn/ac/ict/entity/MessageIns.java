package cn.ac.ict.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MessageIns {

//	private List<FeatureIns> feature;
	private Map<Integer, Double> feature;
	private int label;
	private double weight;
	private int svmPredict;
	
	public int getSvmPredict() {
		return svmPredict;
	}
	public void setSvmPredict(int svmPredict) {
		this.svmPredict = svmPredict;
	}
	public MessageIns(double[] featureArray, int label) {
		int index = 0;
//		feature = new ArrayList<FeatureIns>();
		feature = new HashMap<Integer, Double>();
		for (double d : featureArray) {
//			FeatureIns ins = new FeatureIns(index++, d);
//			feature.add(ins);
			feature.put(index++, d);
		}
		this.label = label;
	}
	public MessageIns(Map<Integer,Double> feature, int label) {
		this.feature = feature;
		this.label = label;
	}
	
	public double[] getFeatureArray() {
		double[] featureArray = new double[feature.size()];
		for (int i = 0; i< feature.size(); i++) {
			featureArray[i] = feature.get(i).doubleValue();
		}
		return featureArray;
	}
	
	public Map<Integer, Double> getFeature() {
		return feature;
	}

	public void setFeature(Map<Integer, Double> feature) {
		this.feature = feature;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
//		for (FeatureIns featureIns : feature) {
//			sb.append(featureIns.getIndex());
//			sb.append("\t");
//			sb.append(featureIns.getWeight());
//			sb.append("\t");
//		}
		for (Entry<Integer, Double> entry : feature.entrySet()) {
			sb.append(entry.getKey());
			sb.append("\t");
			sb.append(entry.getValue());
			sb.append("\t");
		}
		sb.append(label);
		return sb.toString();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
