package cn.ac.ict.bp;

import cn.ac.ict.entity.MessageIns;
import cn.ac.ict.feature.BayesMat;
import cn.ac.ict.feature.NNMat;
import cn.ac.ict.util.FileUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by yixuanhe on 11/14/15.
 */
public class SpamFilter {
    public BPNN bpnn;

    static int HIDDEN_NUMBER = 150;
    
    public SpamFilter(int featureLen){
        bpnn = new BPNN(featureLen, HIDDEN_NUMBER, 1, new Sigmoid(), 1, 0.5, 0.05);
    }

    public void train(double[][] wordBag, double[][] tag, String modelPath){
        bpnn.train(wordBag, tag, modelPath);
    }
    
    public void loadModel(String modelPath){
    	bpnn.loadModel(modelPath);
    }

    public boolean predict(double[] text){
        double[] v = bpnn.getOutput(text);
        return  (v[0] > 0.5);
    }

    public static void main(String[] args) throws IOException {
        int featureNum = 100;
//        Data data = DataReader.dataRead("data/sample_train_100_file.csv", 80136);
        FileUtil fileUtil = FileUtil.getInstance();
        List<MessageIns> messages = fileUtil.intDataRead("data/train_200000_100");
		NNMat bayesMat = new NNMat(messages);
        double[][] X = bayesMat.getX();
        double[][] Y = bayesMat.getY();

        SpamFilter sf = new SpamFilter(featureNum);
        sf.train(X, Y, "conf/bpnewnew.model");
//        sf.loadModel("conf/bp.model");

        int length = X.length;

        int TT = 0;
        int TF = 0;
        int FT = 0;
        int FF = 0;

        int right = 0;
        for (int i = 0; i < length; i++){
            if (Y[i][0] == 0)
                right++;
        }
        System.out.println(right);

        for (int i = 0; i < length; i++){
            if (sf.predict(X[i]) && Y[i][0] == 1){
                FF += 1;
            }
            else  if (sf.predict(X[i]) && Y[i][0] == 0){
                FT += 1;
            }
            else if (!sf.predict(X[i]) && Y[i][0] == 1){
                TF += 1;
            }
            else
                TT += 1;
        }

        System.out.println("TT : " + TT);
        System.out.println("TF : " + TF);
        System.out.println("FT : " + FT);
        System.out.println("FF : " + FF);
    }
}
