package org.brtracer.sourcecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.TreeSet;

import org.brtracer.property.Property;
import org.brtracer.sourcecode.ast.Corpus;
import org.brtracer.sourcecode.ast.FileDetector;
import org.brtracer.sourcecode.ast.FileParser;
import org.brtracer.utils.Stem;
import org.brtracer.utils.Stopword;

public class CodeCorpusCreator {

	private String workDir;
	private String srcDir;
	private String pathSeparator;
	private String lineSeparator;
	private String project;
	
	public static int spiltclass = 800;
	
	/**
	 * Constructor
	 * @throws IOException
	 * @throws ParseException
	 */
	public CodeCorpusCreator() throws IOException, ParseException {
		workDir = Property.getInstance().WorkDir;
		srcDir = Property.getInstance().SourceCodeDir;
		pathSeparator = Property.getInstance().Separator;
		lineSeparator = Property.getInstance().LineSeparator;
		project = Property.getInstance().Project;
	}

	/**
	 * Entry method
	 * @throws Exception
	 */
	public void create() throws Exception {

		// java file listing
		FileDetector detector = new FileDetector("java");
		File[] files = detector.detect(srcDir);

		// prepare file writer
		FileWriter writer = new FileWriter(workDir + pathSeparator + "ClassName.txt");
		FileWriter writeCorpus = new FileWriter(workDir + pathSeparator + "CodeCorpus_OriginClass.txt");
		FileWriter writeImport = new FileWriter(workDir + pathSeparator + "Import.txt");
		//FileWriter writeNames = new FileWriter(workDir + pathSeparator + "ClassAndMethodCorpus.txt");	//TODO:나중에 문제없으면 삭제 
		
		FileWriter writeSegCorpus = new FileWriter(workDir + pathSeparator + "CodeCorpus.txt");	//for segment
		FileWriter writerSegName = new FileWriter(workDir + pathSeparator + "MethodName.txt");	//for segment
		

		// create corpus each file.
		int segIndex = 0;
		int fileIndex = 0;
		TreeSet<String> nameSet = new TreeSet<String>();
		
		for (File file : files) {
			Corpus corpus = this.create(file, writeImport);
			if (corpus == null)
				continue;

			// get full filename
			String fullFileName = corpus.getJavaFileFullClassName();
			if (!fullFileName.endsWith(".java"))
				fullFileName += ".java";
			
			// Classs name
			if (project.startsWith("ASPECTJ")){
				fullFileName = file.getPath().substring(srcDir.length()); //경로명을 통한 인식.
				fullFileName = fullFileName.replace("\\", "/");
				if (fullFileName.startsWith("/")) 
					fullFileName = fullFileName.substring(1); //경로명을 통한 인식.
			}
			if (nameSet.contains(fullFileName)) continue;
			nameSet.add(fullFileName);			

			//store segment code corpus_____________________________________ 
			String srccontent = corpus.getContent();
			String[] src = srccontent.split(" ");
			Integer methodCount = 0;
			while (methodCount == 0 || methodCount * spiltclass < src.length) {
				StringBuffer content = new StringBuffer();
				Integer i = methodCount * spiltclass;
				while (true) {
					if (i >= src.length || i >= (methodCount + 1) * spiltclass) {
						break;
					}
					content.append(src[i] + " ");
					i++;
				}
				content.append(corpus.getNameContent());

				int tmp = segIndex + methodCount;
				writerSegName.write(tmp + "\t" + fullFileName + "@" + methodCount + ".java" + lineSeparator);
				writeSegCorpus.write(fullFileName + "@" + methodCount + ".java" + "\t"	+ content.toString() + lineSeparator);

				methodCount++;
			}
			writerSegName.flush();
			writeSegCorpus.flush();
			segIndex += methodCount;
			
			
			// store in files.
			writer.write(fileIndex + "\t" + fullFileName + lineSeparator);
			writer.flush();
			//writeNames.write(fullFileName + "\t" + corpus.getNameContent() + lineSeparator);
			//writeNames.flush();
			writeCorpus.write(fullFileName + "\t" + corpus.getContent() + lineSeparator);
			writeCorpus.flush();

			fileIndex++;
		}
		Property.getInstance().OriginFileCount = fileIndex;
		Property.getInstance().FileCount = segIndex;
		writerSegName.close();
		writeSegCorpus.close();		
		writeCorpus.close();
		writeImport.close();
		//writeNames.close();
		writer.close();
	}

	/**
	 * 
	 * @param file
	 * @param writeImport
	 * @return
	 * @throws IOException
	 */
	public Corpus create(File file, FileWriter writeImport) throws IOException {

		FileParser parser = new FileParser(file);
		String fileName = file.getPath();
		fileName = fileName.substring(Property.getInstance().Offset).replace("\\", "/");
		// make file full name.
//		String fileName = parser.getPackageName();
//		if (fileName.trim().equals("")) {
//			fileName = file.getName();
//		} else {
//			fileName += "." + file.getName();
//		}

//		/* modification for AspectJ */
//		if (Property.getInstance().Project.startsWith("ASPECTJ")) {
//			fileName = file.getPath();
//			fileName = fileName.substring(Property.getInstance().Offset);
//		}
//		/* ************************** */


		writeImport.write(fileName + "\t");
		parser.getImport(writeImport);
		writeImport.write(lineSeparator);

//		fileName = fileName.substring(0, fileName.lastIndexOf("."));

		String[] content = parser.getContent();
		StringBuffer contentBuf = new StringBuffer();
		for (String word : content) {
			String stemWord = Stem.stem(word.toLowerCase());
			if (!(Stopword.isKeyword(word) || Stopword.isEnglishStopword(word))) {

				contentBuf.append(stemWord);
				contentBuf.append(" ");
			}
		}
		String sourceCodeContent = contentBuf.toString();

		String[] classNameAndMethodName = parser.getClassNameAndMethodName();
		StringBuffer nameBuf = new StringBuffer();
		for (String word : classNameAndMethodName) {
			String stemWord = Stem.stem(word.toLowerCase());
			nameBuf.append(stemWord);
			nameBuf.append(" ");
		}
		String names = nameBuf.toString();

		Corpus corpus = new Corpus();
		corpus.setJavaFilePath(file.getAbsolutePath());
		corpus.setJavaFileFullClassName(fileName);
		corpus.setContent(sourceCodeContent + " " + names);
		corpus.setNameContent(names);
		return corpus;
	}
}
