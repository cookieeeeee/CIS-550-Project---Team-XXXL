package com.xxxl.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.bson.Document;

public class SearchHelper {
	private static int threshold = 20;
	public HashMap<String, Document> parent;

	public SearchHelper() {
		this.parent = new HashMap<String, Document>();
	}

	public List<Document> findPath(Map<String, Document> docsMap,
			Document curDoc, String keyword2, int depth,
			HashSet<String> visitedSet) {
		List<Document> ret = new LinkedList<Document>();
		visitedSet.add(curDoc.getString("id"));
		Document resultDoc = BFS(curDoc, keyword2, docsMap);
		ret.add(resultDoc);
		String result = resultDoc.getString("id");
		Document parentDoc = null;
		while ((parentDoc = parent.get(result)) != null) {
			ret.add(parentDoc);
			// path = parentDoc.getString("id").split(":")[1].replace("_", ".")
			// + "::" + parentDoc.getString("key") + ":"
			// + parentDoc.getString("value") + "||" + path;
			result = parentDoc.getString("id");
		}
		return ret;
	}

	class BFSNode {
		public Document d;
		public int depth;

		public BFSNode(Document d, int depth) {
			this.d = d;
			this.depth = depth;
		}
	}

	private Document BFS(Document root, String keyword2,
			Map<String, Document> docsMap) {
		Set<String> visitedSet = new HashSet<String>();
		Queue<BFSNode> q = new LinkedList<BFSNode>();
		String rootId = root.getString("id");
		visitedSet.add(rootId);
		q.add(new BFSNode(root, 0));
		while (q.size() > 0) {
			BFSNode node = q.poll();
			if (node.depth >= threshold)
				return null;
			List<String> linkedIds = (List<String>) node.d.get("linkedId");
			for (String linkedId : linkedIds) {
				Document child = docsMap.get(linkedId);
				if (child == null)
					continue;
				if (visitedSet.contains(linkedId))
					continue;
				visitedSet.add(child.getString("id"));
				q.add(new BFSNode(child, node.depth + 1));
				parent.put(linkedId, node.d);
				if (child.getString("value").equals(keyword2)) {
					// resultList.add(child);
					// continue;
					return child;
				}
			}
		}
		return null;
	}
	// private void DFS(Map<String, Document> docsMap, Document curDoc,
	// String keyword2, int depth, HashSet<String> visitedSet) {
	//
	// if (depth == threshold) {
	// // return null;
	// return;
	// }
	// for (String linkedId : (List<String>) curDoc.get("linkedId")) {
	// if (visitedSet.contains(linkedId)) {
	// continue;
	// }
	// visitedSet.add(linkedId);
	// parent.put(linkedId, curDoc);
	// Document d = docsMap.get(linkedId);
	// if (d == null) {
	// continue;
	// }
	// if (d.get("value").equals(keyword2)) {
	// // return (String) d.get("id");
	// resultList.add(d);
	// continue;
	// }
	// // String ret = DFS(docsMap, d, keyword2, depth + 1, visitedSet);
	// DFS(docsMap, d, keyword2, depth + 1, visitedSet);
	// // if (ret != null) {
	// // return ret;
	// // }
	// }
	// // return null;
	// }
}
