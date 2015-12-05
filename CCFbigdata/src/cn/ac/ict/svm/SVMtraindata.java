package cn.ac.ict.svm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.ac.ict.entity.MessageIns;
import cn.ac.ict.util.FileUtil;

public class SVMtraindata {

	public void generateSVMtraindata(List<MessageIns> messages, String svmTrainDataPath){
		try {
			File file = new File(svmTrainDataPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer0 = new FileWriter(file, false);
			for (MessageIns messageIns : messages) {
				writer0.write(messageIns.getLabel()+" ");
				for (Entry<Integer, Double> entry : messageIns.getFeature().entrySet()) {
					writer0.write("  "+entry.getKey()+":"+entry.getValue());
				}
				writer0.write("\n");
			}
			writer0.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		FileUtil fileUtil = FileUtil.getInstance();
		List<MessageIns> messages = fileUtil.intDataRead("data/train_200000_100");
//		List<MessageIns> messages = fileUtil.intDataRead("data/test_10000_100");
		SVMtraindata svMtraindata = new SVMtraindata();
//		svMtraindata.generateSVMtraindata(messages, "data/svm_test_10000_100");
		svMtraindata.generateSVMtraindata(messages, "data/svm_train_200000_100");
	}

}
