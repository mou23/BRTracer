package org.brtracer;

import java.util.TreeSet;


/**
 * Public property�
 * @author Zeck
 *
 */
public class Bug
{
	public String bugId;
	public String openDate;
	public String fixDate;
	public String bugSummary;
	public String bugDescription;
	public TreeSet<String> set = new TreeSet<String>();
}
