package cn.ac.ict.test;

import java.util.ArrayList;
import java.util.List;

import cn.ac.ict.adaboost.AdaBoostTrain;
import cn.ac.ict.classifier.BayesClassifier;
import cn.ac.ict.classifier.NNClassifer;
import cn.ac.ict.classifier.SVMClassifier;
import cn.ac.ict.classifier.WeakClassifier;
import cn.ac.ict.entity.MessageIns;
import cn.ac.ict.svm.SVMTrain;
import cn.ac.ict.util.FileUtil;

public class AdaBoostTrainTest {

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
		data.add(new MessageIns(ins1, 1));  
		data.add(new MessageIns(ins2, 1));  
		data.add(new MessageIns(ins3, 1));  
		data.add(new MessageIns(ins4, -1));  
		data.add(new MessageIns(ins5, -1));  
		data.add(new MessageIns(ins6, -1));  
		data.add(new MessageIns(ins7, 1));  
		data.add(new MessageIns(ins8, 1));  
		data.add(new MessageIns(ins9, 1));  
		data.add(new MessageIns(ins10, -1)); 
		
		List<WeakClassifier> classifiers = new ArrayList<WeakClassifier>();
//		classifiers.add(new BayesClassifier());
//		classifiers.add(new NNClassifer());
		
		String modelPath = "data/model";
		
		AdaBoostTrain adaboost = new AdaBoostTrain(data,classifiers,modelPath);
		adaboost.train();
	}
	
	public static void main(String[] args) {
		FileUtil fileUtil = FileUtil.getInstance();
		//feature_train_100_filter.csv
		List<MessageIns> messages = fileUtil.intDataRead("data/train_200000_100");
		
		List<WeakClassifier> classifiers = new ArrayList<WeakClassifier>();
		classifiers.add(new SVMClassifier("conf/svm_200000_100.model"));
		classifiers.add(new BayesClassifier(2,"conf/bayes_200000_100.model"));
		classifiers.add(new NNClassifer(100, "conf/bp_200000_100.model"));
		
		String modelPath = "conf/adaboost_200000_100.model";
//		long startTime = System.currentTimeMillis();
		AdaBoostTrain adaboost = new AdaBoostTrain(messages,classifiers,modelPath);
		adaboost.train();
//		long endTime = System.currentTimeMillis();
//		System.out.println( "all time : "+(endTime - startTime) );
	}

}
