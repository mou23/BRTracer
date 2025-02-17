package org.brtracer.bug;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.brtracer.property.Property;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BugReportProcessor {
	
	public ArrayList<ArrayList<Bug>> getBugReports() {
		ArrayList<Bug> bugs = parseXML();
		int totalSize = bugs.size();
		int splitIndex = (int) Math.ceil(totalSize * 0.6);

		List<Bug> firstPart = bugs.subList(0, splitIndex);
		List<Bug> secondPart = bugs.subList(splitIndex, totalSize);

		ArrayList<Bug> training_bugs = new ArrayList<>(firstPart);
		ArrayList<Bug> test_bugs = new ArrayList<>(secondPart);
		System.out.println("size of test bugs: " + test_bugs.size());

		ArrayList<ArrayList<Bug>> arrayListOfBugs = new ArrayList<>();

		for (Bug bug : test_bugs) {
			ArrayList<Bug> newList = new ArrayList<>(training_bugs); 
			newList.add(bug);
			System.out.println(bug.bugId + "\n" + bug.set);
			arrayListOfBugs.add(newList);
		}
		

		return arrayListOfBugs;
	}

	
	private ArrayList<Bug> parseXML() {
		ArrayList<Bug> list = new ArrayList<Bug>();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			//			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			//			InputStream is = new FileInputStream(Property.getInstance().getBugFilePath());
			//			Document doc = domBuilder.parse(is);
			//			Element root = doc.getDocumentElement();

			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			InputStream is = new FileInputStream(Property.getInstance().BugFilePath);

			Document doc = domBuilder.parse(is);
			Element root = doc.getDocumentElement();

			NodeList bugRepository = root.getElementsByTagName("table");
			if (bugRepository != null) {
				for (int i = 0; i < bugRepository.getLength(); i++) {
					Node bugNode = bugRepository.item(i);
					if (bugNode.getNodeType() == Node.ELEMENT_NODE) {
						NodeList columns = bugNode.getChildNodes(); // Parsing columns inside table
						Bug bug = new Bug();

						// Loop over the column elements
						for (int j = 0; j < columns.getLength(); j++) {
							Node column = columns.item(j);
							if (column.getNodeType() == Node.ELEMENT_NODE) {
								String columnName = column.getAttributes().getNamedItem("name").getNodeValue();
								String columnValue = column.getTextContent();

								// Map XML columns to Bug fields
								switch (columnName) {
								case "bug_id":
									bug.setBugId(columnValue);
									break;
								case "summary":
									bug.setBugSummary(columnValue);
									break;
								case "description":
									bug.setBugDescription(columnValue);
									break;
								case "report_timestamp":
									bug.setOpenDate(Long.parseLong(columnValue));
									break;
								case "commit_timestamp":
									bug.setFixDate(Long.parseLong(columnValue));
									break;
								case "files":
									String[] files = columnValue.split("\\.java");
									
									for (String file : files) {
										System.out.println(file);
										if(file.length()>0)
											bug.addFixedFile(file.trim()+".java");
										System.out.println(bug.bugId + " " + bug.set);
									}
									break;
								case "commit":
									bug.setBugCommit(columnValue);
									Property.getInstance().CommitHash = columnValue;
									break;
								}
							}
						}
						list.add(bug);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		list.sort((bug1, bug2) -> Long.compare(bug1.getFixDate(), bug2.getFixDate()));
		
		return list;
	}
}
