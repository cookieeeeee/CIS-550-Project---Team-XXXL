package com.xxxl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;

public class TestUtil {
	@Test
	public void testJson() {
		File file = new File("C:/Users/Xu/Desktop/1.json");
		String json = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				json += line;
			}
			// JSONObject jsonObject = new JSONObject(json);
			LinkedList<JsonNode> nodes = Json2Graph.json2Graph(json, "zhixu", "123.json");
			for(JsonNode j:nodes){
				System.out.println(j.linkedId+"  "+j.value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
