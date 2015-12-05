package cn.ac.ict.classifier;

import java.util.List;

import cn.ac.ict.bp.BPNN;
import cn.ac.ict.bp.Sigmoid;
import cn.ac.ict.entity.MessageIns;
import cn.ac.ict.util.FileUtil;

public class NNClassifer extends WeakClassifier {

	private BPNN bpnn;

    static int HIDDEN_NUMBER = 150;
    
    public NNClassifer(int featureLen, String modelPath){
        bpnn = new BPNN(featureLen, HIDDEN_NUMBER, 1, new Sigmoid(), 1, 0.5, 0.05);
        bpnn.loadModel(modelPath);
    }
    
	@Override
	public int predict(MessageIns message) {
		double[] v = bpnn.getOutput(message.getFeatureArray());
        return  v[0] > 0.5 ? 1 : 0;
	}
	
	public static void main(String[] args) {
		NNClassifer nnClassifer = new NNClassifer(100, "conf/bp_200000_100.model");
		FileUtil fileUtil = FileUtil.getInstance();
		List<MessageIns> messages = fileUtil.intDataRead("data/test_10000_100");
		
		int result[][] = new int[2][2];
		for(int i =0; i<2; i++){
			for(int j = 0; j<2; j++){
				result[i][j]=0;
			}
		}
		for (MessageIns message : messages) {
			int predict = nnClassifer.predict(message);
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
