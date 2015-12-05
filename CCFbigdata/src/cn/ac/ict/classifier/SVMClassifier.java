package cn.ac.ict.classifier;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import libsvm.svm;
import libsvm.svm_model;
import cn.ac.ict.entity.MessageIns;
import cn.ac.ict.svm.SVMPredict;
import cn.ac.ict.svm.svm_predict;
import cn.ac.ict.util.FileUtil;

public class SVMClassifier extends WeakClassifier {
	private svm_model model;
	
	public SVMClassifier(String modelFile) {
		try {
			model = svm.svm_load_model(modelFile);
			if(svm.svm_check_probability_model(model)!=0)
			{
				System.out.print("Model supports probability estimates, but disabled in prediction.\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getFeatureString(Map<Integer, Double> featureMap){
		StringBuffer sb = new StringBuffer();
		for (Entry<Integer, Double> entry : featureMap.entrySet()) {
			sb.append("  "+entry.getKey()+":"+entry.getValue());
		}
		return sb.toString();
	}

	@Override
	public int predict(MessageIns message) {
		double reLabel = svm_predict.predictByOneDoc(message.getFeature(),model);
		return (int)reLabel;
	}
	
	@Override
	public int repredict(MessageIns message) {
		return message.getSvmPredict();
	}
	
	public static void main(String[] args) {
		SVMClassifier svmClassifier = new SVMClassifier("conf/svm_200000_100.model");
		FileUtil fileUtil = FileUtil.getInstance();
		List<MessageIns> messages = fileUtil.intDataRead("data/test_10000_100");
		
		int result[][] = new int[2][2];
		for(int i =0; i<2; i++){
			for(int j = 0; j<2; j++){
				result[i][j]=0;
			}
		}
		for (MessageIns message : messages) {
			int predict = svmClassifier.predict(message);
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
