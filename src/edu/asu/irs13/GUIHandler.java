package edu.asu.irs13;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.lucene.index.CorruptIndexException;

public class GUIHandler implements ServletContextListener  {
    
	private String query;
    private boolean flag;
	private int count;
	public static List<Integer> top10 = new ArrayList<Integer>();
	public GUIHandler(String query, Boolean flag, int count) {
		super();
		this.query = query;
		this.flag = flag;
		this.count  = count;
	}
	public GUIHandler() {
		
	}
	public int getCount() {
		return count;
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	CosineSimilarity obj = new CosineSimilarity();
	
		public List<String> processRequest(int c) throws CorruptIndexException, IOException{
			
			top10.clear();
			PageRankMain.queryMap.clear();
			PageRankMain.docDotProduct.clear();
			PageRankMain.root.clear();
			CosineSimilarity.SimilarityMap.clear();
			
					
		
	
			String[] queryArray = query.split("\\s");
			System.out.println(queryArray);
			
			for (int i = 0; i < queryArray.length; i++) {
			
				// put into map the words in query and its count within query
				if (PageRankMain.queryMap.containsKey(queryArray[i])) {
					int newValue = PageRankMain.queryMap.get(queryArray[i]) + 1;
					PageRankMain.queryMap.put(queryArray[i], newValue);
				} else {
					PageRankMain.queryMap.put(queryArray[i], 1);
				}
			}
			long startTime = System.nanoTime();
			startTime = startTime / 1000000;
			PageRankMain main = new PageRankMain();
			main.calcDotProd("tf-idf"); // comment if tf calculation is required

			obj.calCosSim();
            SnippetsGenerator snipps = new SnippetsGenerator();
            List<String> snippList = snipps.getSnippet(top10);
            
           for(int i=0;i<10;i++){
            	
           	System.out.println(top10.get(i)+" : "+ snippList.get(i));
            }
            
    	//	List<String> snipList = gen.getSnipp(root);
		//	System.out.println(snipList);
           	return snippList;
			//KMeans.allTerms.clear();
		//	Calc_PageRank.rankMap.clear();

		}
		@Override
		public void contextInitialized(ServletContextEvent sce) {
			// TODO Auto-generated method stub
			System.out.println("runnning while server is starting");
			System.out.println(Indexing.docMap.size());
			System.out.println("clearing docmap");
			Indexing.docMap.clear();
			System.out.println(Indexing.docMap.size());
			System.out.println("wait!! doing initial calculations---idf map calculations");
			
			obj.calcIdf();
			String lowestIdfTerm = "";
			Iterator it = CosineSimilarity.idfMap.entrySet().iterator();
			Indexing index = new Indexing();
			index.fwdIndex();
	
			index.calcDi("tf-idf");
			//Indexing.docMap.clear();

			
		}
		@Override
		public void contextDestroyed(ServletContextEvent sce) {
			// TODO Auto-generated method stub
			
		}
		
		
		

	
}
