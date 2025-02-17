package org.brtracer.bug;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.brtracer.property.Property;
import org.brtracer.utils.Splitter;
import org.brtracer.utils.Stem;
import org.brtracer.utils.Stopword;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BugCorpusCreator {

	private String workDir;
	private String pathSeperator;
	private String lineSeperator;
	
	public BugCorpusCreator() {
		workDir = Property.getInstance().WorkDir;
		pathSeperator = Property.getInstance().Separator;
		lineSeperator = Property.getInstance().LineSeparator;
	}
	
	public void create(ArrayList<Bug> list) throws IOException {
		String dirPath = workDir + this.pathSeperator + "BugCorpus" + this.pathSeperator;
		File file = new File(dirPath);
		Property.getInstance().BugReportCount = list.size();
		if (!file.exists())
			file.mkdirs();

		for (Bug bug : list) {
			writeCorpus(bug, dirPath);
		}
		FileWriter writer = new FileWriter(workDir + this.pathSeperator + "SortedId.txt");
		FileWriter writerFix = new FileWriter(workDir + this.pathSeperator + "FixLink.txt");
		FileWriter writerClassName = new FileWriter(workDir + this.pathSeperator + "DescriptionClassName.txt");

		for (Bug bug : list) {
			writer.write(bug.getBugId() + "\t" + bug.getFixDate() + this.lineSeperator);

			writer.flush();
			for (String fixName : bug.getSet()) {
				writerFix.write(bug.getBugId() + "\t" + fixName + this.lineSeperator);
				writerFix.flush();
			}
			String classnames = extractClassName(bug.getBugDescription());
			writerClassName.write(bug.getBugId() + "\t" + classnames + this.lineSeperator);
		}
		writerClassName.close();
		writer.close();
		writerFix.close();
	}

	/**
	 * 
	 * @param bug
	 * @param storeDir
	 * @throws IOException
	 */
	private void writeCorpus(Bug bug, String storeDir) throws IOException {

		String content = bug.getBugSummary() + " " + bug.getBugDescription();
		String[] splitWords = Splitter.splitNatureLanguage(content);
		StringBuffer corpus = new StringBuffer();
		for (String word : splitWords) {
			word = Stem.stem(word.toLowerCase());
			if (!Stopword.isEnglishStopword(word)) {
				corpus.append(word + " ");
			}
		}
		FileWriter writer = new FileWriter(storeDir + bug.getBugId() + ".txt");
		writer.write(corpus.toString().trim());
		writer.flush();
		writer.close();
	}

	public String extractClassName(String content) {

		String pattern = "[a-zA-Z_][a-zA-Z0-9_\\-]*\\.java";
		StringBuffer res = new StringBuffer();

		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);

		// Create matcher object.
		Matcher m = r.matcher(content);
		while (m.find()) {
			res.append(m.group(0) + " ");
		}
		return res.toString();
	}

}
