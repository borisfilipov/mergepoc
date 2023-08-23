package com.isos.h3s.pdq.data.mergepoc;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.google.gson.Gson;

@SpringBootApplication

public class DedupApplication {

	public static void main(String[] args) {
		SpringApplication.run(DedupApplication.class, args);
		/*try {
			//testRequest();
			testConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);*/

	}
	
	public static void testRequest() throws IOException {
		Gson gson = new Gson();
		RestClient restClient = RestClient.builder(
			    //new HttpHost("localhost", 9200, "http"),
			    //new HttpHost("localhost", 9201, "http")).build();
				new HttpHost("vpc-di-es-q3ouh3naokzgj2udj3npxwni5i.us-east-1.es.amazonaws.com", 443, "https")).build();
		Request request = new Request(
			    "GET",  
			    "/phonetic_sample");   
			Response response = restClient.performRequest(request);
			System.out.println("************************************************************************"+EntityUtils.toString(response.getEntity()));
			
			//Data responseData = gson.fromJson(line, Data .class);
		
			//if (gson.fromJson(line, Data .class).getData().get(0).has("service")) {
			//responseService = gson.fromJson(responseData.getData().get(0).get("service").toString(), String.class);
	}


}