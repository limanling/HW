package cn.ac.ict.feature;

import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class StanfordWordSegmentUtil {
	private String segClassifierConfigFilePath;
	private String segSerializedClassifier;
	private Properties segConfigProps;
	private CRFClassifier<CoreLabel> segmenter;

	public StanfordWordSegmentUtil() {
		segConfigProps = new Properties();
		String fileSeparator = System.getProperty("file.separator");
		segClassifierConfigFilePath = System.getProperty("user.dir")
				+ fileSeparator + "conf" + fileSeparator + "data";

		segConfigProps.setProperty("sighanCorporaDict",
				segClassifierConfigFilePath);
		// segConfigProps.setProperty("useChPos", "true");
		// segConfigProps.setProperty("NormalizationTable",
		// "data/norm.simp.utf8");
		// segConfigProps.setProperty("normTableEncoding", "UTF-8");
		// below is needed because CTBSegDocumentIteratorFactory accesses it
		// load user dict
//		segConfigProps.setProperty("serDictionary", segClassifierConfigFilePath
//				+ fileSeparator + "dict-chris6.ser.gz,"
//				+ segClassifierConfigFilePath + fileSeparator
//				+ "user_dict");
		segConfigProps.setProperty("serDictionary", segClassifierConfigFilePath
				+ fileSeparator + "dict-chris6.ser.gz");
		segConfigProps.setProperty("inputEncoding", "UTF-8");
		segConfigProps.setProperty("sighanPostProcessing", "true");
		segmenter = new CRFClassifier<CoreLabel>(segConfigProps);
		segSerializedClassifier = segClassifierConfigFilePath + fileSeparator
				+ "ctb.gz";
		segmenter.loadClassifierNoExceptions(segSerializedClassifier,
				segConfigProps);
	}

	public String getWordsSegments(String sInput) {
		String results = "";
		if (sInput != null && !sInput.isEmpty()) {
			results = segmenter.classifyToString(sInput);
		}

		return results;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// String sentence =
		// "The Stanford's dependencies provide a representation of grammatical relations between words in a sentence. They have been designed to be easily understood and effectively used by people who want to extract textual relations. ";
		long startTime = System.currentTimeMillis();
		String sentence = "面对新世纪，中国人民的共同愿望是：继续发展人类以往创造的一切文明成果，克服20世纪困扰着人类的战争和贫困问题，推进和平与发展的崇高事业，创造一个美好的世界。";
		
		StanfordWordSegmentUtil segmenter = new StanfordWordSegmentUtil();
		long endTime = System.currentTimeMillis();
		System.out.println( (endTime - startTime) /1000);
		System.out.println(segmenter.getWordsSegments(sentence));
		endTime = System.currentTimeMillis();
		System.out.println( (endTime - startTime) /1000);
	}

}
