package cn.ac.ict.svm;

import libsvm.*;
import java.io.*;
import java.util.*;

public class SVMPredict {
	private svm_model model = null;
	private int predict_probability = 0;
	private svm_print_interface svm_print_string = null;
	
	public SVMPredict(String svm_model_file) {
		try {
			model = svm.svm_load_model(svm_model_file);
			predict_probability = 0;
			svm_print_string = new svm_print_interface() {
				public void print(String s) {
					System.out.print(s);
				}
			};
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SVMPredict(String svm_model_file, int predict_probability, boolean is_out_log) {
		try {
			model = svm.svm_load_model(svm_model_file);
			this.predict_probability = predict_probability;
			if(is_out_log) {
				svm_print_string = new svm_print_interface() {
					public void print(String s)
					{
						System.out.print(s);
					}
				};
			} else {
				svm_print_string = new svm_print_interface() {
					public void print(String s) {}
				};
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void info(String s) 
	{
		svm_print_string.print(s);
	}

	private double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

	private int atoi(String s) {
		return Integer.parseInt(s);
	}

	public double getPredictionLabel(String feturesStr) throws IOException
	{
		double target_label = -5;
		if(feturesStr == null || feturesStr.isEmpty()) {
			return target_label;
		}
		
		if(predict_probability == 1) {
			if(svm.svm_check_probability_model(model) == 0) {
				System.err.print("Model does not support probabiliy estimates\n");
				return	target_label;
			}
		} else {
			if(svm.svm_check_probability_model(model)!=0) {
				this.info("Model supports probability estimates, but disabled in prediction.\n");
			}
		}
		
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;

		if(predict_probability == 1) {
			if(svm_type == svm_parameter.EPSILON_SVR ||
			   svm_type == svm_parameter.NU_SVR) {
				this.info("Prob. model for test data: target value = predicted value + z," +
						"\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
						+svm.svm_get_svr_probability(model)+"\n");
			} else {
				int[] labels=new int[nr_class];
				svm.svm_get_labels(model, labels);
				prob_estimates = new double[nr_class];
			}
		}
		

		StringTokenizer st = new StringTokenizer(feturesStr," \t\n\r\f:");
		double target = atof(st.nextToken());
		int m = st.countTokens()/2;
		svm_node[] x = new svm_node[m];
		for(int j=0;j<m;j++)
		{
			x[j] = new svm_node();
			x[j].index = atoi(st.nextToken());
			x[j].value = atof(st.nextToken());
		}

		double v;
		if (predict_probability == 1 
			&& (svm_type == svm_parameter.C_SVC 
			|| svm_type==svm_parameter.NU_SVC)) {
			v = svm.svm_predict_probability(model, x, prob_estimates);
		} else {
			v = svm.svm_predict(model,x);
		}

		target_label = v;
//		if(v == target) {
//			++correct;
//		}
//		error += (v-target)*(v-target);
//		sumv += v;
//		sumy += target;
//		sumvv += v*v;
//		sumyy += target*target;
//		sumvy += v*target;
//		++total;
//		
//		
//		if(svm_type == svm_parameter.EPSILON_SVR ||
//		   svm_type == svm_parameter.NU_SVR) {
//			this.info("Mean squared error = " + error/total + " (regression)\n");
//			this.info("Squared correlation coefficient = "+
//				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
//				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
//				 " (regression)\n");
//		} else {
//			info("Accuracy = "+(double)correct/total*100+
//				 "% ("+correct+"/"+total+") (classification)\n");
//		}
		return target_label;
	}

	public void exit_with_help() {
		System.err.print("usage: svm_predict [options] test_file model_file output_file\n"
		+"options:\n"
		+"-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n"
		+"-q : quiet mode (no outputs)\n");
		System.exit(1);
	}

	public static void main(String[] args) {
		
	}
}
