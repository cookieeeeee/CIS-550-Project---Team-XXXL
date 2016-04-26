package com.xxxl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.io.SAXReader;
import org.junit.Test;

public class TestUtil {
//	@Test
//	public void testJson() {
//		File file = new File("C:/Users/Xu/Desktop/1.json");
//		String json = "";
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					new FileInputStream(file)));
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				json += line;
//			}
//			// JSONObject jsonObject = new JSONObject(json);
//			LinkedList<JsonNode> nodes = Json2Graph.json2Graph(json, "zhixu", "123.json");
//			for(JsonNode j:nodes){
//				System.out.println(j.linkedId+"  "+j.value);
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
//	@Test
//	public void testCSV(){
//		File file = new File("C:/Users/Xu/Desktop/2.csv");
//		CSV2Graph csv = new CSV2Graph();
//		csv.parseCSV(file,"zhixu", "1.csv");
//	}
	
//	@Test
//	public void testXML(){
//		File file =  new File("C:/Users/Xu/Desktop/1.xml"); 
//		List<JsonNode> parseXML = new XML2Graph().parseXML(file, "zhixu", "1.xml");
//		for(JsonNode node:parseXML){
//			System.out.println(node);
//		}
//	}
	
	@Test
	public void testTika(){
		File file = new File("C:/Users/Xu/Desktop/1.docx");
		List<JsonNode> parthFile = new Tika2Graph().parthFile(file, "zhixu", "1.docx");
		for(JsonNode node:parthFile){
			System.out.println(node);
		}
	}
}
