package cn.ac.ict.svm;

import libsvm.*;

import java.io.*;
import java.util.*;

public class svm_predict {
	private static Double accuracy;
	private static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}

	private static void predict(BufferedReader input, DataOutputStream output, svm_model model, int predict_probability) throws IOException
	{
		int correct = 0;
		int total = 0;
		int countDust = 0;
		int countRightDust = 0;
		int countWrongDust = 0;
		int countNormal = 0;
		int countRightNormal = 0;
		int countWrongNormal = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;

		if(predict_probability == 1)
		{
			if(svm_type == svm_parameter.EPSILON_SVR ||
			   svm_type == svm_parameter.NU_SVR)
			{
				System.out.print("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="+svm.svm_get_svr_probability(model)+"\n");
			}
			else
			{
				int[] labels=new int[nr_class];
				svm.svm_get_labels(model,labels);
				prob_estimates = new double[nr_class];
				output.writeBytes("labels");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(" "+labels[j]);
				output.writeBytes("\n");
			}
		}
		while(true)
		{
			String line = input.readLine();
			if(line == null) break;

			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

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
			if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC))
			{
				v = svm.svm_predict_probability(model,x,prob_estimates);
				output.writeBytes(v+" ");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(prob_estimates[j]+" ");
				output.writeBytes("\n");
			}
			else
			{
				v = svm.svm_predict(model,x);
				output.writeBytes(v+"\n");
			}
			
			
			if(target > 0)
				++countDust;
			else
				++countNormal;
			if(v == target){
				++correct;
				if(target > 0){
					++countRightDust;
				}
				else{
					++countRightNormal;
				}
			}
			else{
				if(v > 0){
					++countWrongNormal;
				}
				else{
					++countWrongDust;
				}
			}
				
			error += (v-target)*(v-target);
			sumv += v;
			sumy += target;
			sumvv += v*v;
			sumyy += target*target;
			sumvy += v*target;
			++total;
		}
		if(svm_type == svm_parameter.EPSILON_SVR ||
		   svm_type == svm_parameter.NU_SVR)
		{
			System.out.print("Mean squared error = "+error/total+" (regression)\n");
			System.out.print("Squared correlation coefficient = "+
				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
				 " (regression)\n");
		}
		else
		{
			System.out.print("Accuracy = "+(double)correct/total*100+
				 "% ("+correct+"/"+total+") (classification)\n");
			accuracy = (double)correct/total;
		}
		double DustRCall = (double)(countRightDust)/countDust;
		double NormalRCall = (double)(countRightNormal)/countNormal;
		double DustPrec = (double)(countRightDust)/(double)(countRightDust+countWrongDust);
		double NormalPrec = (double)(countRightNormal)/(double)(countRightNormal+countWrongNormal);
		System.out.println("countRightDust:"+countRightDust);
		System.out.println("countDust:"+countDust);
		System.out.println("countNormal:"+countNormal);
		System.out.println("countRightNormal:"+countRightNormal);
		System.out.println("countWrongDust:"+countWrongDust);
		System.out.println("countWrongNormal:"+countWrongNormal);
		System.out.println("Dust-R-call:" + DustRCall);
		System.out.println("Normal-R-call:" + NormalRCall);
		System.out.println("Dust-precision:" + DustPrec);
		System.out.println("Normal-precision:" + NormalPrec);
		System.out.println("Dust-F-Score:"+ 2*DustPrec*DustRCall / (DustPrec+DustRCall));
		System.out.println("Normal-F-Score:"+ 2*NormalPrec*NormalRCall / (NormalPrec+NormalRCall));
		
	}

	private static void exit_with_help()
	{
		System.err.print("usage: svm_predict [options] test_file model_file output_file\n"
		+"options:\n"
		+"-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n");
		System.exit(1);
	}

	public static Double main(String argv[]) throws IOException
	{
		int i, predict_probability=0;

		// parse options
		for(i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			++i;
			switch(argv[i-1].charAt(1))
			{
				case 'b':
					predict_probability = atoi(argv[i]);
					break;
				default:
					System.err.print("Unknown option: " + argv[i-1] + "\n");
					exit_with_help();
			}
		}
		if(i>=argv.length-2)
			exit_with_help();
		try 
		{
			BufferedReader input = new BufferedReader(new FileReader(argv[i]));
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(argv[i+2])));
			svm_model model = svm.svm_load_model(argv[i+1]);
			if(predict_probability == 1)
			{
				if(svm.svm_check_probability_model(model)==0)
				{
					System.err.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			}
			else
			{
				if(svm.svm_check_probability_model(model)!=0)
				{
					System.out.print("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
			predict(input,output,model,predict_probability);
			input.close();
			output.close();
		} 
		catch(FileNotFoundException e) 
		{
			exit_with_help();
		}
		catch(ArrayIndexOutOfBoundsException e) 
		{
			exit_with_help();
		}
	return accuracy;
	}

	public static Double getPredictLabel(Map<Integer,Double> feature, String modelFile) throws IOException{
		svm_model model = svm.svm_load_model(modelFile);
		if(svm.svm_check_probability_model(model)!=0)
		{
			System.out.print("Model supports probability estimates, but disabled in prediction.\n");
		}
		Double reLabel = predictByOneDoc(feature,model);
		return reLabel;
	}
	
	public static Double predictByOneDoc(Map<Integer,Double> feature,svm_model model){
		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;
		Iterator iter = feature.entrySet().iterator();
		int featureNum = feature.size();
		svm_node[] x = new svm_node[featureNum];
		int nodeIndex = 0;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			int key = (int)entry.getKey();
		    double val = (double)entry.getValue();
		    x[nodeIndex] = new svm_node();
			x[nodeIndex].index = key;
			x[nodeIndex].value = val;
			nodeIndex ++;
		}
		double label = svm.svm_predict(model,x);
		return label;
		
	}
	
}
