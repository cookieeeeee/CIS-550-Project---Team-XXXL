package com.xxxl.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import org.bson.Document;

public class SearchHelper {
	private static int threshold = 10;
	public Hashtable<String, Document> parent;

	public SearchHelper() {
		this.parent = new Hashtable<String, Document>();
	}

	private static Document findDoc(String id, LinkedList<Document> allDocs) {
		for (Document d : allDocs) {
			// try {
			if (d.get("id").equals(id)) {
				return d;
				// }
				// } catch (Exception e) {
				// continue;
			}
		}
		return null;
	}

	public String findPath(LinkedList<Document> allDocs, Document curDoc,
			String keyword2, int depth, HashSet<String> visitedSet) {
		String result = DFS(allDocs, curDoc, keyword2, depth, visitedSet);
		String path = (String) findDoc(result, allDocs).get("key");
		Document parentDoc = null;
		while ((parentDoc = parent.get(result)) != null) {
			path = parentDoc.getString("key") + ":"
					+ parentDoc.getString("value") + " " + path;
			result = parentDoc.getString("id");
		}
		return path;
	}

	private String DFS(LinkedList<Document> allDocs, Document curDoc,
			String keyword2, int depth, HashSet<String> visitedSet) {

		if (depth == threshold) {
			return null;
		}
		// System.out.println(curDoc.get("linkedId"));
		for (String linkedId : (ArrayList<String>) curDoc.get("linkedId")) {
			if (visitedSet.contains(linkedId)) {
				continue;
			}
			visitedSet.add(linkedId);
			parent.put(linkedId, curDoc);
			Document d = findDoc(linkedId, allDocs);
			if (d.get("value").equals(keyword2)) {
				return (String) d.get("id");
			}
			String ret = DFS(allDocs, d, keyword2, depth + 1, visitedSet);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}
}
