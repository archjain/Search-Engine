package edu.asu.irs13;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

// @Author: Archit Jain

public class KMeans {

	static List<String> allTerms = new ArrayList<String>();

	public HashMap<Integer, ArrayList<Integer>> makeClusters(int k, int N,
			List rootSet) throws IOException {
       // System.out.println(rootSet); 
        long startTime = System.nanoTime();
		startTime = startTime / 1000000;

		HashMap<Integer, ArrayList<Integer>> newClusters = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, ArrayList<Integer>> oldClusters = new HashMap<Integer, ArrayList<Integer>>();

		// storing all the terms from documents in top N documents of query
		for (int i = 0; i < rootSet.size(); i++) {
			HashMap<String, Double> tempWordMap = Indexing.docMap.get(rootSet
					.get(i));
			Iterator it = tempWordMap.entrySet().iterator();

			// adding all string of top N documents in list
			while (it.hasNext()) {
				Map.Entry pair = (Entry) it.next();
				allTerms.add(pair.getKey().toString());

			}

		}
		// Creating random Centroids
		Random randomGen = new Random();
		List<Integer> randomNumbers = new ArrayList<Integer>();
		int p = 0;
		while (p < k) {
			int randTemp = randomGen.nextInt(N);
			if (!randomNumbers.contains(randTemp)) {
				randomNumbers.add(randTemp);
				p++;
			}
		}

		// to store the frequency of terms
		List<HashMap<String, Double>> centroidList = new ArrayList();

		// getting the termMap to generate initial centroids
		for (int j = 0; j < k; j++) {

			HashMap<String, Double> temp = Indexing.docMap.get(rootSet
					.get(randomNumbers.get(j)));
			centroidList.add(temp);
		}
         
		int count = 0 ;
		// iterations start
		while (true) {
            count++;
        //    System.out.println("centroid list" + centroidList);
			//to store similarity between doc and centroid
			HashMap<Integer, ArrayList<Double>> docCentroidsSimialarity = new HashMap<Integer, ArrayList<Double>>();
			for (int j = 0; j < rootSet.size(); j++) {
				
				// to store similarity between a term and centroid
				ArrayList<Double> eachTermSim = new ArrayList<Double>();
			
				//computes similarity between each term and centroids using "computeSimilarity" method
				for (int t = 0; t < k; t++) {
						eachTermSim.add(computeSimilarity(
							Integer.parseInt(rootSet.get(j).toString()),
							centroidList.get(t)));
				}
				//put each the list of term similarities for each doc in hashmap
				docCentroidsSimialarity.put(
						Integer.parseInt(rootSet.get(j).toString()),
						eachTermSim);
			}

			// assign new clusters to old clusters
			oldClusters = newClusters;

			// calculate new clusters
			newClusters = new HashMap<Integer, ArrayList<Integer>>();
			for (int i = 0; i < rootSet.size(); i++) {
				ArrayList<Integer> tempCluster;
                double max = -1;
				int clusterNum = 0;
				ArrayList<Double> list = docCentroidsSimialarity.get(Integer
						.parseInt(rootSet.get(i).toString()));
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j) > max) {
						max = list.get(j);
						clusterNum = j;
					}
				}
			    
				//add documents to corresponding clusters
				if (newClusters.containsKey(clusterNum))
					tempCluster = newClusters.get(clusterNum);
				else
					tempCluster = new ArrayList<Integer>();
				tempCluster.add(Integer.parseInt(rootSet.get(i).toString()));
				newClusters.put(clusterNum, tempCluster);
			}

			// check for convergence
			Iterator<Integer> itr = newClusters.keySet().iterator();
			boolean converged = true;
			itr = newClusters.keySet().iterator();
			
			while (itr.hasNext()) {
				
				int clusterNum = itr.next();
				//System.out.println(clusterNum);
				if (!oldClusters.containsKey(clusterNum)) {
					converged = false;
					break;
				}

				ArrayList docsInCluster = newClusters.get(clusterNum);
				ArrayList docsInOldCluster = oldClusters.get(clusterNum);
				if (docsInCluster.size() != docsInOldCluster.size()) {
					converged = false;
					break;
				}

				for (int i = 0; i < docsInCluster.size(); i++) {
					if (!docsInOldCluster.contains(docsInCluster.get(i))) {
						converged = false;
						break;
					}
				}
			}

			if (converged)
				break;

			// Computing new centroids by call "getMeans" method
			while (itr.hasNext()) {
				int clusterNum = itr.next();
				ArrayList<Integer> docsInCluster = newClusters.get(clusterNum);
				centroidList.set(clusterNum, getMean(docsInCluster));
			}

		}
      //  System.out.println("Iterations "+count);
		for(int i=0;i<newClusters.size();i++){
			int n = i+1;
			System.out.println("Cluster "+n);
			System.out.println(newClusters.get(i));
		    System.out.println("******");
		}
		//System.out.println("----" + newClusters);
		long endTime = System.nanoTime();
		endTime = endTime / 1000000;
		long totTime = endTime-startTime;
		//System.out.println("Total time taken: "+ totTime);
		return newClusters;
	}

	//method to calculate new centroids
	private HashMap<String, Double> getMean(ArrayList<Integer> cluster)
			throws IOException {
		HashMap<String, Double> termAverage = new HashMap<String, Double>();

		for (int i = 0; i < allTerms.size(); i++) {
			double centroid = 0.0;
			String term = allTerms.get(i);
			//double termIdf = CosineSimilarity.idfMap.get(term);
			for (int j = 0; j < cluster.size(); j++) {
				double docFreq = 0.0;
				int docId = cluster.get(j);
				if (Indexing.docMap.get(docId).get(term) != null) {
					docFreq = (double) Indexing.docMap.get(docId).get(
							term);
					
				}
				centroid = centroid + docFreq;
			}
			centroid = centroid / allTerms.size();

			termAverage.put(allTerms.get(i), centroid);
		}

		return termAverage;
	}

	//computing similarity between docs and centroid
	public double computeSimilarity(int docId, HashMap<String, Double> centroid) {
		try {
			double numerator = 0.0;
			double denominator = 0.0;
			double docFreqTwoNorm = 0.0;
			double centroidTwoNorm = 0.0;
			for (int i = 0; i < allTerms.size(); i++) {
				double docFreqIdf = 0.0;
				double termIdf = CosineSimilarity.idfMap.get(allTerms.get(i));

				if (Indexing.docMap.get(docId).get(allTerms.get(i)) != null) {
					double docFreq = (double) Indexing.docMap.get(docId).get(
							allTerms.get(i));
					docFreqIdf = docFreq * termIdf;
				}
				//calculating centroid and document distance
				double centroidFreqIdf = 0.0;
				if (centroid.get(allTerms.get(i)) != null) {
					double centroidFreq = centroid.get(allTerms.get(i));
					centroidFreqIdf = centroidFreq * termIdf;
				}
				numerator = numerator + docFreqIdf * centroidFreqIdf;
				docFreqTwoNorm += docFreqIdf * docFreqIdf;
				centroidTwoNorm += centroidFreqIdf * centroidFreqIdf;
			}
			docFreqTwoNorm = Math.sqrt(docFreqTwoNorm);
			centroidTwoNorm = Math.sqrt(centroidTwoNorm);
			denominator = docFreqTwoNorm * centroidTwoNorm;
          //  System.out.println(docId  +" : "+numerator/denominator);
			return numerator / denominator;
		} catch (Exception e) {
			System.out.println(e);
		}
		return 0;

	}
    static List<String> stopWords = new ArrayList<String>();
    static{
    	stopWords.add("html");
    	stopWords.add("title");
    	stopWords.add("font");
    	stopWords.add("body");
    	stopWords.add("color");
    	stopWords.add("head");
    	stopWords.add("table");
    	stopWords.add("href");
    	stopWords.add("td");
    	stopWords.add("tr");
    	stopWords.add("p");
    	stopWords.add("width");
    	stopWords.add("alt");
    	stopWords.add("b");
    	stopWords.add("menu");
    	stopWords.add("class");
    	stopWords.add("http");
    	stopWords.add("style");
    	stopWords.add("link");
    	stopWords.add("align");
    	stopWords.add("height");
    	stopWords.add("css");
    }
	// function to make summaries of clusters
	public void makeSummaries(HashMap<Integer, ArrayList<Integer>> clusters) {
		
		Iterator it = clusters.entrySet().iterator();
		ArrayList<HashMap<String, Double>> clusterWords = new ArrayList();

		while (it.hasNext()) {
			Map.Entry pair = (Entry) it.next();
			ArrayList list = (ArrayList) pair.getValue();
			HashMap<String, Double> wordMap = new HashMap<String, Double>();
			for (int j = 0; j < allTerms.size(); j++) {
              if(!stopWords.contains(allTerms.get(j))){
				double productIdf = 1.0;
				for (int i = 0; i < list.size(); i++) {
					double termIdf = 0.0;
					if (Indexing.docMap.get(list.get(i)).get(allTerms.get(j)) != null) {
						double freq = (double) Indexing.docMap.get(list.get(i))
								.get(allTerms.get(j));
						termIdf = freq
								* CosineSimilarity.idfMap.get(allTerms.get(j));
					}
					productIdf *= termIdf;
				}
				wordMap.put(allTerms.get(j), productIdf);
			}
			}
			List list2 = new LinkedList(wordMap.entrySet());
			Collections.sort(list2, new Comparator() {
				public int compare(Object o1, Object o2) {
					return -((Comparable) ((Map.Entry) (o1)).getValue())
							.compareTo(((Map.Entry) (o2)).getValue());
				}
			});
			HashMap sortedHashMap = new LinkedHashMap();
			for (Iterator it2 = list2.iterator(); it2.hasNext();) {
				Map.Entry entry = (Map.Entry) it2.next();
				sortedHashMap.put(entry.getKey(), entry.getValue());
			}
			long endTime = System.nanoTime();
			endTime = endTime / 1000000;

			clusterWords.add(sortedHashMap);
		}
		
		
		//Print Summaries
		/*
		for (int i = 0; i < clusterWords.size(); i++) {
			System.out.println("***");
			System.out.println(clusterWords.get(i));
		}
		*/
	}

}
