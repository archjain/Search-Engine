package edu.asu.irs13;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;

//@Author: Archit Jain
public class SnippetsGenerator {

	
	// Please change this base file path to the local path of ProjectClass folder
	
	String baseFilePath = "C:/Users/archjain/Desktop/Projectclass/result3/";
	
    public static List<String> titleList = new ArrayList<String>();   
	public List<String> getSnippet(List rootSet) throws CorruptIndexException, IOException {
		long startTime3 = System.nanoTime();
		startTime3 = startTime3 / 1000000;
		
		
		Iterator it = PageRankMain.queryMap.entrySet().iterator();
		List<String> snippList = new ArrayList<String>();
		double maxIdf = -1.0;
		String maxIdfTerm="";
		
		//identifying the most important term in query terms
		long startTime = System.nanoTime();
		startTime = startTime / 1000000;
		
		while (it.hasNext()) {
			
			Map.Entry pair = (Entry) it.next();
			String s = pair.getKey().toString();
			double idf = CosineSimilarity.idfMap.get(s);
			if(idf>maxIdf){
				maxIdf = idf;
				maxIdfTerm = s; 
			}
		
		}
		long endTime = System.nanoTime();
		endTime = endTime / 1000000;
		long maxIdfTermTime =startTime - endTime;
		
	//	System.out.println("maxIdfTermTime : " +maxIdfTermTime );
		
		
		long startTime2 = System.nanoTime();
		startTime2 = startTime2 / 1000000;
		IndexReader r = IndexReader.open(FSDirectory.open(new File("index")));
		
		for (int i=0; i<10; i++) {
			// Look for a match
			long startTime4 = System.nanoTime();
			startTime4 = startTime4 / 1000000;
			int docId = Integer.parseInt(rootSet.get(i).toString());
			 
			Document d = r.document(docId);
			String url = d.getFieldable("path").stringValue(); // the 'path' field of the Document object holds the UR
			url = baseFilePath+url; 
			
			org.jsoup.nodes.Document htmlFile = null; 
			try { 
								htmlFile = Jsoup.parse(new File(url), "ISO-8859-1"); 
				String content = htmlFile.body().text();
				String title = htmlFile.title();
				//adding titles of page
				titleList.add(title);
				
				// to remove html tags
			
				content = content.replaceAll("\\<.*?>","");
				int midIndex = content.indexOf(maxIdfTerm);
				String snip ="";
				int startIndex=0;
				if(midIndex-40>0){
					startIndex = midIndex-40;
				}
				
				if(midIndex>40)
				{
					snip = content.substring(startIndex, midIndex+40);
				}else
				    snip = content.substring(0, 80);
				
				snippList.add(snip);
				//System.out.println(snip);*/
			} catch (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); } // right
			}
			long endTime4 = System.nanoTime();
			endTime4 = endTime4 / 1000000;
			long itTime = startTime4 - endTime4;
		//	System.out.println("Iteration Time "+itTime);
		}
		long endTime2 = System.nanoTime();
		endTime2 = endTime2 / 1000000;
		long SearchTime =startTime2 - endTime2;
	//	System.out.println("SearchTime : "+SearchTime );
		long totTime = startTime3 - endTime2;
	//	System.out.println("Total time for algo " + totTime);
		return snippList;
		
	}
}
