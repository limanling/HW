package cn.ac.ict.test;

import java.util.ArrayList;
import java.util.List;

import cn.ac.ict.adaboost.AdaBoostPredict;
import cn.ac.ict.adaboost.AdaBoostTrain;
import cn.ac.ict.classifier.BayesClassifier;
import cn.ac.ict.classifier.NNClassifer;
import cn.ac.ict.classifier.SVMClassifier;
import cn.ac.ict.classifier.WeakClassifier;
import cn.ac.ict.entity.MessageIns;
import cn.ac.ict.svm.SVMTrain;
import cn.ac.ict.util.FileUtil;

public class AdaBoostPredictTest {

	public void test(){
		List<MessageIns> data = new ArrayList<MessageIns>();
		double[] ins1 = {0};  
        double[] ins2 = {1};  
        double[] ins3 = {2};  
        double[] ins4 = {3};  
        double[] ins5 = {4};  
        double[] ins6 = {5};  
        double[] ins7 = {6};  
        double[] ins8 = {7};  
        double[] ins9 = {8};  
        double[] ins10 = {9};  
        MessageIns instance1 = new MessageIns(ins1, 1);  
        MessageIns instance2 = new MessageIns(ins2, 1);  
        MessageIns instance3 = new MessageIns(ins3, 1);  
        MessageIns instance4 = new MessageIns(ins4, -1);  
        MessageIns instance5 = new MessageIns(ins5, -1);  
        MessageIns instance6 = new MessageIns(ins6, -1);  
        MessageIns instance7 = new MessageIns(ins7, 1);  
        MessageIns instance8 = new MessageIns(ins8, 1);  
        MessageIns instance9 = new MessageIns(ins9, 1);  
        MessageIns instance10 = new MessageIns(ins10, -1);   
        
		List<WeakClassifier> classifiers = new ArrayList<WeakClassifier>();
//		classifiers.add(new BayesClassifier());
//		classifiers.add(new NNClassifer());
		
		String modelPath = "data/model";
		
		AdaBoostPredict adaboost = new AdaBoostPredict(classifiers, modelPath);
		System.out.println(adaboost.predictLabel(instance1));
		System.out.println(adaboost.predictLabel(instance2));
		System.out.println(adaboost.predictLabel(instance3));
		System.out.println(adaboost.predictLabel(instance4));
		System.out.println(adaboost.predictLabel(instance5));
		System.out.println(adaboost.predictLabel(instance6));
		System.out.println(adaboost.predictLabel(instance7));
		System.out.println(adaboost.predictLabel(instance8));
		System.out.println(adaboost.predictLabel(instance9));
		System.out.println(adaboost.predictLabel(instance10));
	}
	
	public static void main(String[] args) {
		FileUtil fileUtil = FileUtil.getInstance();
		List<MessageIns> messages = fileUtil.intDataRead("data/data_80000_100");
		
		List<WeakClassifier> classifiers = new ArrayList<WeakClassifier>();
		classifiers.add(new SVMClassifier("conf/svm_200000_100.model"));
		classifiers.add(new BayesClassifier(2,"conf/bayes_200000_100.model"));
		classifiers.add(new NNClassifer(100, "conf/bp_200000_100.model"));
		
		String modelPath = "conf/adaboost_200000_100.model";
		AdaBoostPredict adaboost = new AdaBoostPredict(classifiers,modelPath);
		
		int result[][] = new int[2][2];
		for(int i =0; i<2; i++){
			for(int j = 0; j<2; j++){
				result[i][j]=0;
			}
		}
		for (MessageIns message : messages) {
			int predict = adaboost.predictLabel(message);
			result[message.getLabel()][predict]++;
		}
		
		for (int[] is : result) {
			for (int i : is) {
				System.out.println(i);
			}
		}
		
		double spamP = (double)result[1][1]/(double)(result[1][1]+result[0][1]);
		double spamR = (double)result[1][1]/(double)(result[1][1]+result[1][0]);
		double normalP = (double)result[0][0]/(double)(result[0][0]+result[1][0]);
		double normalR = (double)result[0][0]/(double)(result[0][0]+result[0][1]);
		double spamF = 0.65 * spamP + 0.35 * spamR;
		double normalF = 0.65 * normalP + 0.35 * normalR;
		
		System.out.println("spam precision:"+spamP);
		System.out.println("spam recall:"+spamR);
		System.out.println("normal precision:"+normalP);
		System.out.println("normal recall:"+normalR);
		System.out.println(0.65 * spamF + 0.35 * normalF);
	}
	
	

}
