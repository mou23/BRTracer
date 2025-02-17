package org.brtracer.bug;

import java.util.TreeSet;

public class Bug {

	String bugId;
	long openDate;
	long fixDate;
	String bugSummary;
	String bugDescription;
	String commitHash;
	TreeSet<String> set = new TreeSet<String>();

	public String getBugId() {
		return bugId;
	}

	public void setBugId(String bugId) {
		this.bugId = bugId;
	}

	public long getOpenDate() {
		return openDate;
	}

	public void setOpenDate(long openDate) {
		this.openDate = openDate;
	}

	public long getFixDate() {
		return fixDate;
	}

	public void setFixDate(long fixDate) {
		this.fixDate = fixDate;
	}

	public String getBugSummary() {
		return bugSummary;
	}

	public void setBugSummary(String bugSummary) {
		this.bugSummary = bugSummary;
	}

	public String getBugDescription() {
		return bugDescription;
	}

	public void setBugDescription(String bugDescription) {
		this.bugDescription = bugDescription;
	}
	
	public String getBugCommit() {
		return commitHash;
	}

	public void setBugCommit(String commit) {
		this.commitHash = commit;
	}
	
	public TreeSet<String> getSet() {
		return set;
	}

	public void addFixedFile(String fileName) {
		this.set.add(fileName);
	}

}
