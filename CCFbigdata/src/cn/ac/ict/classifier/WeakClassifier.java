package cn.ac.ict.classifier;

import cn.ac.ict.entity.MessageIns;

public abstract class WeakClassifier {
	private double weight = 0.0;
	private String name = this.getClass().getName();
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract int predict(MessageIns message);
	
	public int repredict(MessageIns message){
		return predict(message);
	}
}
