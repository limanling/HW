package cn.ac.ict.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.ict.entity.MessageIns;

public class FileUtil {

	private FileUtil() {}
	private static FileUtil fileUtil = null;
	public static FileUtil getInstance(){
		if(fileUtil == null){
			fileUtil = new FileUtil();
		} 
		return fileUtil;
	}

	/**
	 * 读取某文件夹及其子文件夹下的全部文件
	 * 
	 * @param filePath
	 */
	public List<File> readFiles(String filePath) {
		List<File> list = new ArrayList<File>();
		
		File f = new File(filePath);
		if (f.isDirectory()) {
			list = ReadAllFile(f, list);
		} else {
			list.add(f);
		}
		return list;
	}

	private List<File> ReadAllFile(File f, List<File> list) {
		File[] files = f.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				list = ReadAllFile(file, list);
			} else {
				list.add(file);
			}
		}
		return list;
	}

	public void saveToFile(String content, String toFile, boolean append) {
		try {
			File file = new File(toFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer0 = new FileWriter(file, append);
			writer0.write(content);
			writer0.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String readFileContent(String filePath) {
		InputStreamReader reader;
		String content = "";
		try {
			reader = new InputStreamReader(new FileInputStream(filePath),
					"UTF-8");
			BufferedReader reader0 = new BufferedReader(reader);
			String filecontent = null;
			while ((filecontent = reader0.readLine()) != null) {
				content += filecontent + "\n";
			}
			reader.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public List<String> readStringLists(String filePath) {
		InputStreamReader reader;
		List<String> fileList = new ArrayList<String>();
		try {
			reader = new InputStreamReader(new FileInputStream(filePath),
					"UTF-8");
			BufferedReader reader0 = new BufferedReader(reader);
			String filecontent = null;
			while ((filecontent = reader0.readLine()) != null) {
				fileList.add(filecontent);
			}
			reader.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("fileList");
		return fileList;
	}

	public void concatFile(String inPath, String outFilePath, int n) {
		InputStreamReader reader;
		String filecontent = null;
		int count = 0;
		try {
			reader = new InputStreamReader(new FileInputStream(inPath), "UTF-8");
			BufferedReader reader0 = new BufferedReader(reader);
			while ((filecontent = reader0.readLine()) != null) {
				count++;
				if (count <= n) {
					saveToFile(filecontent + "\n", outFilePath, true);
				} else {
					break;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<MessageIns> intDataRead(String filePath) {
		InputStreamReader reader;
		Map<Integer, Double> feature = null;
		List<MessageIns> messages = new ArrayList<MessageIns>();
		try {
			reader = new InputStreamReader(new FileInputStream(filePath),
					"UTF-8");
			BufferedReader reader0 = new BufferedReader(reader);
			String filecontent = null;
			while ((filecontent = reader0.readLine()) != null) {
				feature = new HashMap<Integer, Double>();
				String[] strs = filecontent.split(",");
				for (int i = 0; i<strs.length-1; i++) {
					feature.put(i, Double.parseDouble(strs[i]));
				}
				MessageIns messageIns = new MessageIns(feature, 
						Integer.parseInt(strs[strs.length-1]));
				messages.add(messageIns);
			}
			reader.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	public static void main(String[] args) {
		FileUtil fileUtil = FileUtil.getInstance();
		int n = 90000;
		fileUtil.concatFile("data/feature_train_100_filter.csv", "data/train_"+n+"_100", n);
	}
}
