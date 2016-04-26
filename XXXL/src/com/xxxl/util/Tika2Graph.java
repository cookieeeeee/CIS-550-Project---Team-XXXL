package com.xxxl.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class Tika2Graph {
	public List<JsonNode> parthFile(File file, String userName, String fileName) {
		List<JsonNode> ret = new LinkedList<JsonNode>();
		String prefix = userName + ":" + fileName.replace(".", "_") + ":";
		int idCount = 0;

		JsonNode root = new JsonNode(null, fileName, "__content__", prefix);
		JsonNode ancestor = new JsonNode(root, "__content__", "words", prefix
				+ idCount);
		idCount++;
		ret.add(root);
		ret.add(ancestor);
		Tika tika = new Tika();
		String text = null;
		try {
			text = tika.parseToString(file);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
		String[] sentences = text.split("\n");
		for (String sentence : sentences) {
			if (sentence.equals("")) {
				continue;
			}
			List<String> tokens = textToTokens(sentence);
			for (String token : tokens) {
				JsonNode thisNode = new JsonNode(ancestor, "word"
						+ (idCount - 1), token, prefix + idCount);
				idCount++;
				ret.add(thisNode);
			}
		}
		getAllChildren(ret);
		setLinkedIds(ret);
		return ret;
	}

	private List<String> textToTokens(String text) {
		List<String> ret = new LinkedList<String>();
		Analyzer sa = new StandardAnalyzer(Version.LUCENE_CURRENT);
		TokenStream ts = null;
		try {
			ts = sa.tokenStream("content", new StringReader(text));
			CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				ret.add(ch.toString());
			}
			ts.end();
			ts.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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
