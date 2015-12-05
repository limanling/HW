package cn.ac.ict.adaboost;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import cn.ac.ict.classifier.WeakClassifier;
import cn.ac.ict.entity.MessageIns;

public class AdaBoostTrain {
	private final int LOOP = 30;
	private List<MessageIns> data;
	private List<WeakClassifier> classifiers;
	private String modelPath;
	
	// load data
	public AdaBoostTrain(List<MessageIns> data, List<WeakClassifier> classifiers,
			String modelPath) {
		this.data = data;
		this.classifiers = classifiers;
		this.modelPath = modelPath;
	}
	
	public void train(){
		boolean isAllRight = true;
		double error = 0.0;
		double minError = 1.0;
		WeakClassifier minErrorClassifier = null;
		int weakResult = 0;
		
		long endTime = 0;
		long startTime = System.currentTimeMillis();
		
		initDataWeight();
		
		for (int i = 0; i<LOOP; i++){
			// calculate error
			System.out.println(i);
			minError = 1.0;
			for (WeakClassifier weakClassifier : classifiers) {
				error = 0.0;
				for (MessageIns message : data) {
					weakResult = weakClassifier.predict(message);
					if(weakClassifier.getName().startsWith("SVM")){
						message.setSvmPredict(weakResult);
					}
					if(weakResult != message.getLabel()){
						error += message.getWeight();
					}
				}
//				maxScore = maxScore > score ? maxScore : score;
				if(minError > error){
					minError = error;
					minErrorClassifier = weakClassifier;
				}
//				endTime = System.currentTimeMillis();
				System.out.println( weakClassifier.getName() + "calculate error : "+error);
//						+(endTime - startTime) );
			}
			
			System.out.println("minError:"+minErrorClassifier.getName());
			if(Math.abs(minError - 0.5) < 0.1){
				break;
			}
			
			// calculate classifierWeight
			double classifierWeight = (Math.log((1-minError)/minError))/2;
			minErrorClassifier.setWeight(classifierWeight);
			endTime = System.currentTimeMillis();
			System.out.println( "calculate classifierWeight : "+(endTime - startTime) );
			
			// update dataWeight
			double weightBefore = 0.0;
			double weightAfter = 0.0;
			double regularItem = 0.0;
			for (MessageIns message : data) {
				weakResult = minErrorClassifier.predict(message);
//				weakResult = minErrorClassifier.repredict(message);
				weightBefore = message.getWeight();
				weightAfter = weightBefore*
						Math.exp(-classifierWeight*(weakResult==0?-1:1)
								*(message.getLabel()==0?-1:1));
				message.setWeight(weightAfter);
				regularItem += weightAfter;
			}
			endTime = System.currentTimeMillis();
			System.out.println( "update dataWeight : "+(endTime - startTime) );
			
			// regular dataWeight
			for (MessageIns message : data) {
				message.setWeight(message.getWeight() / regularItem);
			}
			
			// if all right
			isAllRight = true;
			for (MessageIns message : data) {
				if(predictLabel(message) != message.getLabel()){
//					System.out.println(predictLabel(message));
//					System.out.println(message.getLabel());
//					System.out.println(message.toString()+predictLabel(message));
					isAllRight = false;
					break;
				}
			}
			if(isAllRight == true){
				break;
			}
			endTime = System.currentTimeMillis();
			System.out.println( "isAllRight : "+(endTime - startTime) );
//			endTime = System.currentTimeMillis();
//			System.out.println( "reverse time : "+(endTime - startTime) );
			saveModel();
		}
		
		
		System.out.println("finish training adaboost....");
	}
	
	private void saveModel(){
		for (WeakClassifier weakClassifier : classifiers) {
			System.out.println(weakClassifier.getWeight()+"\t");
		}
		try {
			File file = new File(modelPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer0 = new FileWriter(file, true);
			writer0.write("==================================\n");
			for (WeakClassifier weakClassifier : classifiers) {
				writer0.write(weakClassifier.getWeight()+"\n");
			}
			writer0.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initDataWeight(){
		double weight = 1.0/data.size();
		for (MessageIns message : data) {
			message.setWeight(weight);
		}
	}
	
	public int predictLabel(MessageIns message){
		double score = 0.0;
		for (WeakClassifier weakClassifier : classifiers) {
			score += weakClassifier.getWeight() * (double)weakClassifier.predict(message);
		}
		if(score > 0){
			return 1;
		} else {
			return 0;
		}
	}

}
