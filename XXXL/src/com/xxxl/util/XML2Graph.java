package com.xxxl.util;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * file root, xml rootnode, xml node
 */
public class XML2Graph {
	public static final String FILE_ROOT = "file root";
	public final static String XML_ROOTNODE = "xml rootnode";
	public final static String XML_NODE = "xml rootnode";

	public List<JsonNode> parseXML(File file, String userName, String fileName) {
		List<JsonNode> ret = new LinkedList<JsonNode>();
		LinkedList<Element> nodeList = new LinkedList<Element>();
		Map<Element, JsonNode> findParent = new HashMap<Element, JsonNode>();
		SAXReader reader = new SAXReader();
		String prefix = userName + ":" + fileName.replace(".", "_") + ":";
		int idCount = 0;
		Document xmlDoc = null;
		try {
			xmlDoc = reader.read(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = xmlDoc.getRootElement();
		JsonNode jRoot = new JsonNode(null, fileName, "__content__", prefix,
				FILE_ROOT);
		ret.add(jRoot);
		JsonNode rootNode = new JsonNode(jRoot, root.getPath(),
				root.getTextTrim(), prefix + idCount, XML_ROOTNODE);
		ret.add(rootNode);
		idCount++;
		for (Iterator<Element> ite = root.elementIterator(); ite.hasNext();) {
			Element node = ite.next();
			nodeList.add(node);
			findParent.put(node, rootNode);
		}
		while (nodeList.size() > 0) {
			Element node = nodeList.pop();
			JsonNode nodeNode = new JsonNode(findParent.get(node),
					node.getPath(), node.getTextTrim(), prefix + idCount,
					XML_NODE);
			idCount++;
			ret.add(nodeNode);
			for (Iterator<Element> ite = node.elementIterator(); ite.hasNext();) {
				Element childNode = ite.next();
				nodeList.add(childNode);
				findParent.put(childNode, nodeNode);
			}
		}
		getAllChildren(ret);
		setLinkedIds(ret);
		return ret;
	}

	private final static void getAllChildren(List<JsonNode> resultList) {
		for (JsonNode j : resultList) {
			JsonNode parent = j.parent;
			if (parent != null) {
				parent.children.add(j);
			}
		}
	}

	private final static void setLinkedIds(List<JsonNode> resultList) {
		for (JsonNode j : resultList) {
			JsonNode parent = j.parent;
			if (parent != null) {
				j.linkedId.add(parent.id);
			}
			for (JsonNode child : j.children) {
				j.linkedId.add(child.id);
			}
		}
	}

}
