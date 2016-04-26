package com.xxxl.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import org.bson.Document;

public class SearchHelper {
	private static int threshold = 10;
	public Hashtable<String, Document> parent;

	public SearchHelper() {
		this.parent = new Hashtable<String, Document>();
	}

	public String findPath(Map<String, Document> docsMap, Document curDoc,
			String keyword2, int depth, HashSet<String> visitedSet) {
		String result = DFS(docsMap, curDoc, keyword2, depth, visitedSet);
		// String path = (String) findDoc(result, docsMap).get("key");
		String path = docsMap.get(result).getString("key") + ":"
				+ docsMap.get(result).getString("value");
		Document parentDoc = null;
		while ((parentDoc = parent.get(result)) != null) {
			path = parentDoc.getString("key") + ":"
					+ parentDoc.getString("value") + " " + path;
			result = parentDoc.getString("id");
		}
		return path;
	}

	private String DFS(Map<String, Document> docsMap, Document curDoc,
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
			// Document d = findDoc(linkedId, docsMap);
			Document d = docsMap.get(linkedId);
			if (d == null) {
				continue;
			}
			if (d.get("value").equals(keyword2)) {
				return (String) d.get("id");
			}
			String ret = DFS(docsMap, d, keyword2, depth + 1, visitedSet);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}
}
