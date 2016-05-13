// Author : Archit Jain

package edu.asu.irs13;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.store.FSDirectory;

public class Indexing {
	public static HashMap<Integer, HashMap> docMap = new HashMap<Integer, HashMap>();
//	public HashMap<String, Integer> termMap;
	static HashMap<Integer, Double> DiMap = new HashMap<Integer, Double>();
	public static HashMap<Integer,ArrayList<TermPos>> termPosMap;
    
	//creates the hashmap of hashmaps, where every document is linked with all its terms and their frequency
	//docmap has keys as document id and values as termMap, which has terms and their frequency
	public void fwdIndex() {
		docMap.clear();
		IndexReader r;
		termPosMap=new HashMap<Integer,ArrayList<TermPos>>();
		try {

			r = IndexReader.open(FSDirectory.open(new File("index")));
			TermEnum t = r.terms();
			while (t.next()) {
				if (t.term().field().equals("contents")) {
					Term termval = t.term();
					TermPositions termpositions = r.termPositions(termval);
					  
					
				/*	while (termpositions.next()) {
						   int i = 0;
						   System.out.println("freq"+termpositions.freq());
						   while (i < termpositions.freq()) {
							   System.out.println(i);
							   // Retrieve the terms list if it exists, otherwise create a new one
							   ArrayList<TermPos> terms;
							   if (termPosMap.containsKey(termpositions.doc())) 
								   terms = termPosMap.get(termpositions.doc());
							   else
								   terms = new ArrayList<TermPos>();
							   
							   // Copy values to a temporary variable
							   TermPos tp = new TermPos();
							   tp.term = termval.text();
							   tp.pos = termpositions.nextPosition();
							   
							   // Store it in documents hashmap
							   terms.add(tp);
							   termPosMap.put(termpositions.doc(), terms);
							   i++;
						   }
						//   System.out.println("**");
					   }
				*/	
					
					
					Term term = t.term();
					TermDocs td = r.termDocs(term);
					while (td.next()) {
						HashMap<String, Double> termMap;
						if (docMap.containsKey(td.doc()))
							termMap = docMap.get(td.doc());
						else
							termMap = new HashMap<String, Double>();
						termMap.put(term.text(), (double) td.freq() );
                    //    System.out.println(termMap.size());
						docMap.put(td.doc(), termMap);

					}
				}
			}
			
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OOOOOO"+ docMap.get(991).size());
		
	}

	
	
	// calculates Norm di as per the weight type
	// idf values are included if wieght type is tf-idf
	public void calcDi(String weightType) {
		long startTime=System.nanoTime();
		startTime=startTime/1000000;
	//	System.out.println("Start calculating Document norm:" + startTime);
	//	System.out.println("asdsasadas"+docMap.size());
	//	System.out.println(docMap.get(991).size());
        Iterator it1 = docMap.entrySet().iterator();
        System.out.println("lol"+docMap.get(991).size());
		
		while (it1.hasNext()) {

			Map.Entry pair1 = (Map.Entry) it1.next();

			HashMap wordMap = docMap.get(pair1.getKey());
			Iterator it = wordMap.entrySet().iterator();
			double di = 0.0;
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();

				if (weightType.equals("tf-idf")) {
					//idf of the term is multiplied in case of if-idf weight type
					double tempdi = Double.parseDouble(pair.getValue()
							.toString())
							* CosineSimilarity.idfMap.get(pair.getKey());
					di += tempdi * tempdi;
				} else {
					di += Math.pow(
							Double.parseDouble(pair.getValue().toString()), 2);

				}
				//it.remove();
			}

			di = Math.sqrt(di);
			//DiMap contains docid and its calculated normdi
			DiMap.put((int) pair1.getKey(), di);
            
		}
		//System.out.println("lol"+docMap.get(991).size());
		 long endTime=System.nanoTime();
			endTime=endTime/1000000;
	//		System.out.println("End time :" + endTime);
			long totTime=endTime-startTime;
		//	System.out.println("Norm Calculation Time : "  + totTime);
	}

	
	
}

class TermPos {
	public int pos;
	public String term;
	public double tfidf;
	
	public TermPos() {
		pos = 0;
		term = new String();
		tfidf = 0d;
	}
}
