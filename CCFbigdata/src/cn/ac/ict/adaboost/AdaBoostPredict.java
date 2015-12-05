package cn.ac.ict.adaboost;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.ac.ict.classifier.WeakClassifier;
import cn.ac.ict.entity.MessageIns;

public class AdaBoostPredict {
	private List<WeakClassifier> classifiers;

	public AdaBoostPredict(List<WeakClassifier> classifiers, String modelFile) {
		this.classifiers = classifiers;
		loadModel(modelFile);
	}
	
	private void loadModel(String modelFile){
		InputStreamReader reader = null;
		int classifierNum = 0;
		try {
			reader = new InputStreamReader(new FileInputStream(modelFile),
					"UTF-8");
			BufferedReader reader0 = new BufferedReader(reader);
			String filecontent = null;
			while ((filecontent = reader0.readLine()) != null) {
				classifiers.get(classifierNum++).setWeight(Double.parseDouble(filecontent));
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int predictLabel(MessageIns message){
		double score = 0.0;
		for (WeakClassifier weakClassifier : classifiers) {
			double predict = weakClassifier.predict(message) == 0 ? -1.0 : 1.0;
			score += weakClassifier.getWeight() * predict;
		}
		if(score > 0){
			return 1;
		} else {
			return 0;
		}
	}
	

}
