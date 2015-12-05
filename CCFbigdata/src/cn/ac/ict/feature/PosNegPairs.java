package cn.ac.ict.feature;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import cn.ac.ict.util.FileUtil;

public class PosNegPairs {

	public static void main(String[] args) {
		String rightFile = "data/pos.txt";
		String wrongFIle = "data/neg.txt";
		String rawFile = "data/feature_train_100_filter.csv";
		FileUtil filePath = FileUtil.getInstance();
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(new FileInputStream(rawFile),
					"UTF-8");
			BufferedReader reader0 = new BufferedReader(reader);
			String filecontent = null;
			while ((filecontent = reader0.readLine()) != null) {
				if(filecontent.startsWith("1")){
					filePath.saveToFile(filecontent+"\n", rightFile , true);
				} else {
					filePath.saveToFile(filecontent+"\n", wrongFIle , true);
				}
			}
			reader.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
