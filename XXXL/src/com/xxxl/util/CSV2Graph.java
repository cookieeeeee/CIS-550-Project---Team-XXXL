package com.xxxl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSV2Graph {
	public List<JsonNode> parseCSV(File file, String userName, String fileName) {
		List<JsonNode> ret = new LinkedList<JsonNode>();
		CSVParser csvParser = null;
		List<CSVRecord> csvRecords = null;
		int idCount = 0;
		String prefix = userName + ":" + fileName.replace(".", "_") + ":";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			csvParser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
			csvRecords = csvParser.getRecords();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Integer> headerMap = csvParser.getHeaderMap();
		Map<Integer, String> invertedHeaderMap = inverHeaderMap(headerMap);
		JsonNode root = new JsonNode(null, fileName, "__content__", prefix);
		JsonNode ancestor = new JsonNode(root, "__content__", "rs", prefix
				+ idCount);
		ret.add(root);
		ret.add(ancestor);
		idCount++;
		for (int row = 0; row < csvRecords.size(); row++) {
			JsonNode rx = new JsonNode(ancestor, "rs->r" + row, "r" + row, prefix
					+ idCount);
			CSVRecord thisRow = csvRecords.get(row);
			idCount++;
			ret.add(rx);
			for(int column = 0;column < thisRow.size();column++){
				JsonNode rxcx = new JsonNode(rx, "r"+row+"->"+invertedHeaderMap.get(column), thisRow.get(column), prefix+idCount);
				idCount++;
				ret.add(rxcx);
			}
		}
		getAllChildren(ret);
		setLinkedIds(ret);
		return ret;
		// for (CSVRecord csvRecord : csvRecords) {
		// System.out.println(invertedHeaderMap.get(0)+":"+csvRecord.get(0) +
		// " " + invertedHeaderMap.get(5)+":"+csvRecord.get(5));
		// }
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

	private Map<Integer, String> inverHeaderMap(Map<String, Integer> headerMap) {
		Map<Integer, String> ret = new HashMap<Integer, String>();
		for (String key : headerMap.keySet()) {
			ret.put(headerMap.get(key), key);
		}
		return ret;
	}
}
