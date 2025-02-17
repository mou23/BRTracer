package org.brtracer;

import java.util.ArrayList;

import org.brtracer.bug.Bug;
import org.brtracer.bug.BugCorpusCreator;
import org.brtracer.bug.BugReportProcessor;
import org.brtracer.bug.BugSimilarity;
import org.brtracer.bug.BugVector;
import org.brtracer.bug.SimilarityDistribution;
import org.brtracer.evaluation.Evaluation;
import org.brtracer.sourcecode.*;
import org.brtracer.utils.CommitChecker;

import org.brtracer.property.Property;

public class Core {

	public void process() {
		
		ArrayList<ArrayList<Bug>> arrayListOfBugs = new BugReportProcessor().getBugReports();
		
		for (int i = 0; i < arrayListOfBugs.size(); i++) {
			ArrayList<Bug> bugs = arrayListOfBugs.get(i);
			Property.getInstance().BugReportCount = bugs.size();
			if(i==0)
				Property.getInstance().WorkDir = Property.getInstance().WorkDir + String.valueOf(i);
			else
				Property.getInstance().WorkDir = Property.getInstance().WorkDir.replaceAll("\\d+",String.valueOf(i));
			System.out.println(Property.getInstance().WorkDir);
			try
			{
				Property.getInstance().CommitHash = bugs.get(bugs.size()-1).getBugCommit();
				System.out.println("checkout to one commit before fix...");
				new CommitChecker().create();
			}
			catch (Exception z) {
				z.printStackTrace();
				return;
			}
			
	        // ------------bug-------------------------
			try {
				System.out.println("create bug corpus...");
				new BugCorpusCreator().create(bugs);
	
			} catch (Exception ex) {
	
			}
	
			try {
				System.out.println("create bug vector...");
				new BugVector().create();
			} catch (Exception ex) {
	
			}
			try {
				System.out.println("compute bug similarity...");
				new BugSimilarity().computeSimilarity();
			} catch (Exception ex) {
	
			}
	
			try {
				System.out.println("create code corpus and segmented code corpus...");
				new CodeCorpusCreator().create();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	
			try {
				System.out.println("compute SimiScore...");
				new SimilarityDistribution().distribute();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			
			// --------------------sourcecode---------------
			try {
				System.out.println("create index...");
				new Indexer().index();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	
	        try {
	            System.out.println("create index origin...");
	            new Indexer_OriginClass().index();
	        } catch (Exception ex) {
	
	        }
	
	
			try {
				System.out.println("create vector...");
				new CodeVectorCreator().create();
			} catch (Exception ex) {
	
			}
			try {
				System.out.println("compute VSMScore...");
				new Similarity().compute();
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
			try {
				System.out.println("compute LengthScore...");
				new LenScore_OriginClass().computeLenScore();
			} catch (Exception ex) {
	
			}
			//----------------evaluation--------
	
			try {
				System.out.println("count LoC...");
				new LineofCode().beginCount();
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
	
			try {
				System.out.println("evaluate...");
				new Evaluation().evaluate();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
//			break;
		}
		System.out.println("finished");
	}
}
