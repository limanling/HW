package cn.ac.ict.classifier;

import cn.ac.ict.entity.MessageIns;
import cn.ac.ict.feature.BayesMat;
import cn.ac.ict.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by yixuanhe on 11/20/15.
 */
public class BayesClassifier extends WeakClassifier {

	public BayesClassifier(int n, String modelPath) {
		number = n;
		totalItemNum = new double[n];

		itemProb = new HashMap[n];
		itemNum = new HashMap[n];

		for (int i = 0; i < n; i++) {
			itemProb[i] = new HashMap<>();
			itemNum[i] = new HashMap<>();
		}

		System.out.println(itemProb.length);
		loadModel(modelPath);
	}

	private void loadModel(String modelPath) {
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(new FileInputStream(modelPath),
					"UTF-8");
			BufferedReader reader0 = new BufferedReader(reader);
			String filecontent = null;
			while ((filecontent = reader0.readLine()) != null) {
				String[] stra = filecontent.split("==");
				int index = Integer.parseInt(stra[0]);
				int key = Integer.parseInt(stra[1]);
				Double prob = Double.parseDouble(stra[2]);
				itemProb[index].put(key, prob);
			}
			reader.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// the class nuber
	int number;

	// the probability of each item in each
	private Map<Integer, Double>[] itemProb;

	// the total weight value of spam and ham in word vector
	private double totalItemNum[];

	// the total weight value of each item class
	private Map<Integer, Double>[] itemNum;

	public BayesClassifier(int n) {
		number = n;
		totalItemNum = new double[n];

		itemProb = new HashMap[n];
		itemNum = new HashMap[n];

		for (int i = 0; i < n; i++) {
			itemProb[i] = new HashMap<>();
			itemNum[i] = new HashMap<>();
		}
	}

	public void train(double[][] X, int[][] Y, String modelFile) {
		int leng = X.length;
		for (int i = 0; i < leng; i++) {
			int label = Y[i][0];
			double[] xs = X[i];
			Map<Integer, Double> tmpItemNum = itemNum[label];
			int lenX = xs.length;
			for (int j = 0; j < lenX; j++) {
				double x = xs[j];
				totalItemNum[label] += x;
				if (tmpItemNum.containsKey(j)) {
					double n = tmpItemNum.get(j);
					n = n + x;
					tmpItemNum.replace(j, n);
				} else {
					tmpItemNum.put(j, x);
				}
			}
		}

		for (int i = 0; i < number; i++) {
			Map<Integer, Double> tmpItemNum = itemNum[i];
			for (Map.Entry<Integer, Double> item : tmpItemNum.entrySet()) {
				itemProb[i].put(item.getKey(), item.getValue()
						/ totalItemNum[i]);
			}
		}

		saveModel(modelFile);
	}

	private void saveModel(String modelFile) {
		try {
			File file = new File(modelFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer0 = new FileWriter(file, false);
			for (int i = 0; i < itemProb.length; i++) {
				for (Entry<Integer, Double> entry : itemProb[i].entrySet()) {
					writer0.write(i + "==" + entry.getKey() + "=="
							+ entry.getValue() + "\n");
				}
			}
			writer0.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int predict(MessageIns message) {

		Map<Integer, Double> X = message.getFeature();
		int len = X.size();
		// int len = X.length;

		double prob[] = new double[number];
		for (int i = 0; i < number; i++)
			prob[i] = 1;

		for (int i = 0; i < len; i++) {
			for (int j = 0; j < number; j++)
				prob[j] = prob[j] * X.get(i) * itemProb[j].get(i);
		}

		int result = 0;

		for (int i = 1; i < number; i++) {
			if (prob[i] > prob[result])
				result = i;
		}

		return result;
	}

	public int predict(double[] X) {
		int len = X.length;

		double prob[] = new double[number];
		for (int i = 0; i < number; i++)
			prob[i] = 1;

		for (int i = 0; i < len; i++) {
			for (int j = 0; j < number; j++) {
				prob[j] = prob[j] * X[i] * itemProb[j].get(i);
			}
		}

		int result = 0;

		for (int i = 1; i < number; i++) {
			if (prob[i] > prob[result])
				result = i;
		}

		return result;
	}

	/*
	 * test train data, return number* number matrix the Mij in this matrix
	 * means that the number of item which belongs to class i but be judged to
	 * class j
	 */

	public int[][] test(double[][] X, int[][] Y) {
		int[][] result = new int[number][number];
		int len = X.length;

		for (int[] is : result) {
			for (int i : is) {
				System.out.println(i);
			}
		}
		for (int i = 0; i < len; i++) {
			int judge_Y = predict(X[i]);
			result[Y[i][0]][judge_Y] += 1;
			System.out.println(Y[i][0] + "  " + judge_Y);
		}

		return result;
	}

	public static void main(String[] args) {
		FileUtil fileUtil = FileUtil.getInstance();
		// feature_train_100_filter.csv
		List<MessageIns> messages = fileUtil
				.intDataRead("data/train_200000_100");
		BayesMat bayesMat = new BayesMat(messages);
//		BayesClassifier bayes = new BayesClassifier(2);
//		bayes.train(bayesMat.getX(), bayesMat.getY(),
//				"conf/bayes_200000_100.model");
		BayesClassifier bayes = new BayesClassifier(2,"conf/bayes_80000_100.model");

		messages = fileUtil.intDataRead("data/test_10000_100");
		bayesMat = new BayesMat(messages);
		int[][] result = bayes.test(bayesMat.getX(), bayesMat.getY());

		double spamP = (double) result[1][1]
				/ (double) (result[1][1] + result[0][1]);
		double spamR = (double) result[1][1]
				/ (double) (result[1][1] + result[1][0]);
		double normalP = (double) result[0][0]
				/ (double) (result[0][0] + result[1][0]);
		double normalR = (double) result[0][0]
				/ (double) (result[0][0] + result[0][1]);
		double spamF = 0.65 * spamP + 0.35 * spamR;
		double normalF = 0.65 * normalP + 0.35 * normalR;

		System.out.println("spam precision:" + spamP);
		System.out.println("spam recall:" + spamR);
		System.out.println("normal precision:" + normalP);
		System.out.println("normal recall:" + normalR);
		System.out.println(0.65 * spamF + 0.35 * normalF);
	}
}
