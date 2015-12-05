package cn.ac.ict.svm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.ac.ict.entity.MessageIns;
import libsvm.*;

/**JAVA to build svm model.
 * @author Yaru Yang
 * @throws IOException 
 */

public class SVMTrain {
	
	public static String trainFile = "./conf/train_data"; 
	public static String modelFile = "./conf/train_data.model";
	
	public SVMTrain(String modelFile) {
		this.modelFile = modelFile;
	}
	
	/**
	 * 	Build svm model
	 * @param 
	 * @return 
	 * @throws IOException
	 */
	public void getModel() {
		String[] trainArgs = {trainFile,modelFile};//directory of training file
		try {
			svm_train.main(trainArgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	//Predict to get label
	/**
	 * 
	 * @param feature  pair of feature label and the value
	 * @return predict label
	 * @throws IOException
	 */	
	public int predict(MessageIns message) {
		Map<Integer,Double> feature = message.getFeature();
		double preLabel;
		try {
			preLabel = svm_predict.getPredictLabel(feature,modelFile);
			return (int)preLabel;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	
	public static void main(String[] args) throws IOException  {
//		BuildModel.weightMap = BuildModel.readWeightFromFile();
		String train_file = "./data/svm_train_200000_100";
		String model_file = "./data/svm_200000_100.model";
		SVMTrain svm = new SVMTrain(model_file);
//		BuildModel.buildVSMList("./fileFolder/train_data4.txt", train_file);
		//*****used by lml****
//		svm.getModel();
		
		
		String[] trainArgs = {train_file,model_file};//directory of training file
		String modelFile = svm_train.main(trainArgs);
		
		long startTime = System.currentTimeMillis();
		String test_file = "./data/svm_test_10000_100";
//		BuildModel.buildVSMList("./fileFolder/test_data4.txt", test_file);
		String[] testArgs = {test_file, model_file, "data/svm_10000_result_new"};//directory of test file, model file, result file
		Double accuracy = svm_predict.main(testArgs);
		System.out.println("SVM Classification is done! The accuracy is " + accuracy);
		long endTime = System.currentTimeMillis();
		System.out.println( (endTime - startTime) );
		
		//Test for cross validation
		//String[] crossValidationTrainArgs = {"-v", "10", "UCI-breast-cancer-tra"};// 10 fold cross validation
		//modelFile = svm_train.main(crossValidationTrainArgs);
		//System.out.print("Cross validation is done! The modelFile is " + modelFile);
		
//		Map<Integer,Double> feature = new HashMap<Integer,Double>();
//		feature.put(1, 1.5);
//		feature.put(3, -2.0);
//		MessageIns message = new MessageIns(feature, -1);
//		int label = svm.predict(message);
//		System.out.println(label);
	}

}

