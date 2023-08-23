package com.isos.h3s.pdq.data.mergepoc;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {
	@Autowired 
	private ProfileRepository profileRepository;
	@Autowired 
	private ProfilePageRepository profilePageRepository;
	@Autowired 
	private ProfilerefRepository profilerefRepository;
	private RestClient restClient;
    
	public void run(String... args) throws Exception {
    	restClient = getRestClient();
		//getProfilesList(restClient);
		searchDups(restClient);
		System.exit(0);

    }
    
	public RestClient getRestClient () throws IOException {
		final CredentialsProvider credentialsProvider =
			    new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY,
			    new UsernamePasswordCredentials("ISOSAdmin", "Py7j*sxf"));

			RestClientBuilder builder = RestClient.builder(
			    new HttpHost("vpc-di-es-q3ouh3naokzgj2udj3npxwni5i.us-east-1.es.amazonaws.com", 443, "https"))
				.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
		            public RequestConfig.Builder customizeRequestConfig(
		                    RequestConfig.Builder requestConfigBuilder) {
		                return requestConfigBuilder
		                    .setConnectTimeout(5000)
		                    .setSocketTimeout(100000);
		            }

		        })
			    .setHttpClientConfigCallback(new HttpClientConfigCallback() {
			        //@Override
			        public HttpAsyncClientBuilder customizeHttpClient(
			                HttpAsyncClientBuilder httpClientBuilder) {
			            return httpClientBuilder
			                .setDefaultCredentialsProvider(credentialsProvider);
			        }
			        
			    });

			
			
			
			RestClient restClient = builder.build();
			//RestClient.setMaxRetryTimeoutMillis(100000);
			return restClient;
	}
	public int saveProfile(ArrayList profileList) {
		ArrayList<Profile> profiles = new ArrayList<Profile>(); 
		Gson gson = new Gson();
		Profile p1 = new Profile();
		for (int i = 0; i < profileList.size(); i++) {
			Profile p = new Profile();
			LinkedTreeMap map = (LinkedTreeMap)profileList.get(i);
			JsonObject jo = gson.toJsonTree(map).getAsJsonObject();
			//System.out.println(jo);
			try {
			p.setPhone(gson.fromJson(jo.get("_source").getAsJsonObject().get("Phone"), String.class));
			p.setEmail(gson.fromJson(jo.get("_source").getAsJsonObject().get("Email"), String.class));
		    p.setEmployeeId(gson.fromJson(jo.get("_source").getAsJsonObject().get("employeeId"), String.class));
		    p.setTtProfileId(gson.fromJson(jo.get("_source").getAsJsonObject().get("ttProfileId"), Double.class).intValue());
		    p.setCompanyId(gson.fromJson(jo.get("_source").getAsJsonObject().get("companyId"), Double.class).intValue());
		    p.setFirstName(gson.fromJson(jo.get("_source").getAsJsonObject().get("firstName"), String.class));
		    p.setLastName(gson.fromJson(jo.get("_source").getAsJsonObject().get("lastName"), String.class));
		    profiles.add(p);
			} catch (Exception e) {
				System.out.println("Error in "+p);
			}
		    //profileRepository.save(p);
			//System.out.println(gson.fromJson(jo.get("_source").getAsJsonObject().get("Phone"), String.class));
		    //System.out.println("i "+i);
			//System.out.println(p.toString());
		    p1=p;
		}
		profileRepository.saveAll(profiles);
		return p1.getTtProfileId();
	}
	
	public void getProfileList(RestClient restClient) throws Exception {
		int searchAfter = 106466897 ;//103456332 
		int count = 1;
		while (count <= 36000000) {
			Request request = new Request(
				    "GET",  
				    "/tt-profile/_search");
			request.setJsonEntity("{\"size\":10000," + 
					" \"sort\": [{\"_id\": \"asc\"}]," + 
					"  \"search_after\": ["+ searchAfter +"]}");
			Gson gsonresponse = new Gson();
			Response response = restClient.performRequest(request);
			JsonObject jobject = gsonresponse.fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
			int hits = gsonresponse.fromJson(jobject.get("hits").getAsJsonObject().get("hits").getAsJsonArray(), ArrayList.class).size();
			//System.out.println(hits);
			if (hits == 0) {
				System.out.println("Hits 0");
				break;
			}
			ArrayList profileList = gsonresponse.fromJson(jobject.get("hits").getAsJsonObject().get("hits").getAsJsonArray(), ArrayList.class);
			searchAfter = saveProfile(profileList);
			System.out.println("SearchAfter "+searchAfter);
			count = count + 10000;
			System.out.println("Count "+count);
		}
	}
	
	public void searchDups(RestClient restClient) throws IOException {
		Gson gsonresponse = new Gson();
		Gson gson = new Gson();
		ArrayList<Profileref> refList = new ArrayList<Profileref>();
		//Iterable<Profile> i = profileRepository.findAll();
		for ( int pg = 0; pg < 35000; pg++) {
			Iterable<Profile> i = profilePageRepository.findAll(PageRequest.of(pg, 1000, Sort.by("id").ascending()));
			//Iterable<Profile> i = profilePageRepository.findByCompanyId(304359, PageRequest.of(pg, 1000, Sort.by("id").ascending()));
			System.out.println("Try Page "+ pg);
			Iterator<Profile> it = i.iterator();
			while (it.hasNext()) {
				Profile p = it.next();
				
				//if ((p.getEmail() != null || p.getEmail().length() != 0) && (p.getPhone() != null || p.getPhone().length() != 0) && (p.getEmployeeId() != null || p.getEmployeeId().length() != 0)) {
					//System.out.println("p1 " + p.toString());
					//Optional<Profile> o = profileRepository.findById(3);
					//Profile p = o.get();
					//System.out.println(p.toString());
					
					Request request = new Request(
						    "GET",  
						    "/tt-profile/_search");
						String requestString = "{"+
							"\"size\": 20,"+
							"\"query\": {"+
						    "\"bool\": {"+
						      "\"must\": ["+
						        "{"+
						          "\"match\": {"+
						            "\"companyId\": \""+ p.getCompanyId() + "\"" +
						          "}"+
						        "},"+
						        "{"+
						          "\"bool\": {"+
						            "\"should\": ["+
						              "{"+
						                "\"match\": {"+
						                  "\"firstName\": {"+
						                    "\"query\": \""+ p.getFirstName() + "\"" +","+
						                    "\"analyzer\": \"synonym\""+
						                  "}"+
						                "}"+
						              "},"+
						              "{"+
						                "\"match\": {"+
						                  "\"firstName\": {"+
						                    "\"query\": \""+ p.getFirstName() + "\"" +
						                  "}"+
						                "}"+
						              "}"+
						            "],"+
						            "\"minimum_should_match\": 1"+
						          "}"+
						        "},"+
						        "{"+
						          "\"bool\": {"+
						            "\"should\": ["+
						              "{"+
						                "\"match\": {"+
						                  "\"lastName\": {"+
						                    "\"query\": \""+ p.getLastName() + "\"" +","+
						                    "\"analyzer\": \"synonym\""+
						                  "}"+
						                "}"+
						              "},"+
						              "{"+
						                "\"match\": {"+
						                  "\"lastName\": {"+
						                    "\"query\": \""+ p.getLastName() + "\"" +
						                  "}"+
						               "}"+
						              "}"+
						            "],"+
						            "\"minimum_should_match\": 1"+
						          "}"+
						        "}"+
						      "],"+
						      "\"should\": ["+
						        "{"+
						          "\"match\": {"+
						            "\"Phone\": \""+ p.getPhone() + "\"" +
						          "}"+
						        "},"+
						        "{"+
						          "\"multi_match\": {"+
						          "\"query\": \""+p.getEmail() + "\"" +","+
						         "\"fields\": [ \"*Email*\" ]"+
						          "}"+
						        "},"+
						        "{"+
						          "\"match\": {"+
						            "\"employeeId\": \""+ p.getEmployeeId() + "\"" +
						          "}"+
						        "}"+
						      "],"+
						      "\"minimum_should_match\": 1"+
						    "}"+
						  "}"+
						"}";
						//System.out.println(requestString);
						//System.exit(0);
						request.setJsonEntity(requestString);
						/*if (p.getId() == 147) {
							System.out.println(requestString);
							System.exit(0);
						}*/
						//try {
						Response response = restClient.performRequest(request);
						JsonObject jobject = gsonresponse.fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
						ArrayList profileList = gsonresponse.fromJson(jobject.get("hits").getAsJsonObject().get("hits").getAsJsonArray(), ArrayList.class);
						
						
						
						for (int j = 0; j < profileList.size(); j++) {
							Profileref pr = new Profileref();
							LinkedTreeMap map = (LinkedTreeMap)profileList.get(j);
							JsonObject jo = gson.toJsonTree(map).getAsJsonObject();
							//System.out.println(jo);
							pr.setTtProfileId(p.getTtProfileId());
						    pr.setTtProfileIdRef(gson.fromJson(jo.get("_source").getAsJsonObject().get("ttProfileId"), Double.class).intValue());
						    pr.setCompanyId(gson.fromJson(jo.get("_source").getAsJsonObject().get("companyId"), Double.class).intValue());
						    pr.setProfileId(p.getId());
						    refList.add(pr);
						    if (refList.size() >= 1000) {
						    	profilerefRepository.saveAll(refList);
						    	refList.clear();
						    	System.out.println("Single id Batch saved");
						    }
						}
						if (refList.size() >= 1000) {
					    	profilerefRepository.saveAll(refList);
					    	refList.clear();
					    	System.out.println("Multiple id Batch saved");
					    }
						
						
						//parseReference(p.getId(), p.getTtProfileId(),profileList);
						/*} catch (Exception e) {
							System.out.println(requestString);
						}*/
					//}
			}
			if (refList.size() > 0) {
				profilerefRepository.saveAll(refList);
				System.out.println("Rest of batch saved");
			}
		}
	}

	public void parseReference(int profileId, int ttProfileId, ArrayList profileList) {
		ArrayList<Profileref> refList = new ArrayList<Profileref>();
		Gson gson = new Gson();
		for (int i = 0; i < profileList.size(); i++) {
			Profileref pr = new Profileref();
			LinkedTreeMap map = (LinkedTreeMap)profileList.get(i);
			JsonObject jo = gson.toJsonTree(map).getAsJsonObject();
			//System.out.println(jo);
			pr.setTtProfileId(ttProfileId);
		    pr.setTtProfileIdRef(gson.fromJson(jo.get("_source").getAsJsonObject().get("ttProfileId"), Double.class).intValue());
		    pr.setCompanyId(gson.fromJson(jo.get("_source").getAsJsonObject().get("customerId"), Double.class).intValue());
		    pr.setProfileId(profileId);
		    refList.add(pr);
		    if (refList.size() >= 1000) {
		    	profilerefRepository.saveAll(refList);
		    	refList.clear();
		    	System.out.println("Batch saved");
		    }
		    //System.out.println("Profile "+ ttProfileId);
		    /*int count= Integer.parseInt(gsonresponse.fromJson(jobject.get("hits").getAsJsonObject().get("total").getAsJsonObject().get("value"), String.class));
			for (int icount = 1; icount <= count; icount++) {
				Profileref pr = new Profileref();
				pr.setTtProfileId(gsonresponse.fromJson(jo.get("_source").getAsJsonObject().get("employeeId"), String.class));
			    pr.setTtProfileIdRef(gsonresponse.fromJson(jo.get("_source").getAsJsonObject().get("ttProfileId"), Double.class).intValue());
				profileRepository.save(pr);
			}*/
		}
		if (refList.size() > 0) {
			profilerefRepository.saveAll(refList);
			System.out.println("Rest of batch saved");
		}
	}
	
	public void getProfilesList(RestClient restClient) throws Exception {
		int count = 1;//28520001;//17600001;//12820001;
		String scroll;// = "FGluY2x1ZGVfY29udGV4dF91dWlkDXF1ZXJ5QW5kRmV0Y2gBFDIxVzh6WDBCc3JwcDNSTTB6SEZ4AAAAAAAAAQcWRUt6blJuOGRUMy1Xd0htVXg1cEk2dw==";
		Gson gsonresponse = new Gson();
		Request requests = new Request(
			    "POST",  
			    "/tt-profile/_search?scroll=1m");
		requests.setJsonEntity("{\"size\":20000}");
		/*Request requests = new Request(
			    "POST",  
			    "/_search/scroll");
		requests.setJsonEntity("{\"scroll\" : \"300m\", \"scroll_id\" : \""+ scroll +"\"}");*/
		Gson gsonresponses = new Gson();
		Response responses = restClient.performRequest(requests);
		JsonObject jobjects= gsonresponses.fromJson(EntityUtils.toString(responses.getEntity()), JsonObject.class);
		

		int hits = gsonresponse.fromJson(jobjects.get("hits").getAsJsonObject().get("hits").getAsJsonArray(), ArrayList.class).size();
		//System.out.println(hits);
		if (hits == 0) {
			System.out.println("Hits 0");
			System.exit(0);
		}
		scroll = gsonresponse.fromJson(jobjects.get("_scroll_id"), String.class);
		//String scroll = "FGluY2x1ZGVfY29udGV4dF91dWlkDXF1ZXJ5QW5kRmV0Y2gBFDIxVzh6WDBCc3JwcDNSTTB6SEZ4AAAAAAAAAQcWRUt6blJuOGRUMy1Xd0htVXg1cEk2dw==";
		System.out.println(scroll);
		ArrayList profileList = gsonresponse.fromJson(jobjects.get("hits").getAsJsonObject().get("hits").getAsJsonArray(), ArrayList.class);
		saveProfiles(profileList);
		
		 
		while (count <= 36000000) {
			Request request = new Request(
				    "POST",  
				    "/_search/scroll");
			request.setJsonEntity("{\"scroll\" : \"1m\", \"scroll_id\" : \""+ scroll +"\"}");
			System.out.println("Nextscroll "+scroll);
			Response response = restClient.performRequest(request);
			JsonObject jobject = gsonresponse.fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
			int hits1 = gsonresponse.fromJson(jobject.get("hits").getAsJsonObject().get("hits").getAsJsonArray(), ArrayList.class).size();
			//System.out.println(hits);
			if (hits1 == 0) {
				System.out.println("Hits 0");
				break;
			}
			ArrayList profileList1 = gsonresponse.fromJson(jobject.get("hits").getAsJsonObject().get("hits").getAsJsonArray(), ArrayList.class);
			saveProfile(profileList1);
			scroll = gsonresponse.fromJson(jobjects.get("_scroll_id"), String.class);
			count = count + 20000;
			System.out.println("Count "+count);
		}
	}
	public void saveProfiles(ArrayList profileList) {
		ArrayList<Profile> profiles = new ArrayList<Profile>(); 
		Gson gson = new Gson();
		for (int i = 0; i < profileList.size(); i++) {
			System.out.println("Count"+i);
			Profile p = new Profile();
			LinkedTreeMap map = (LinkedTreeMap)profileList.get(i);
			JsonObject jo = gson.toJsonTree(map).getAsJsonObject();
			//System.out.println(jo);
			try {
			p.setPhone(gson.fromJson(jo.get("_source").getAsJsonObject().get("Phone"), String.class));
			p.setEmail(gson.fromJson(jo.get("_source").getAsJsonObject().get("Email"), String.class));
		    p.setEmployeeId(gson.fromJson(jo.get("_source").getAsJsonObject().get("employeeId"), String.class));
		    p.setTtProfileId(gson.fromJson(jo.get("_source").getAsJsonObject().get("ttProfileId"), Double.class).intValue());
		    p.setCompanyId(gson.fromJson(jo.get("_source").getAsJsonObject().get("companyId"), Double.class).intValue());
		    p.setFirstName(gson.fromJson(jo.get("_source").getAsJsonObject().get("firstName"), String.class));
		    p.setLastName(gson.fromJson(jo.get("_source").getAsJsonObject().get("lastName"), String.class));
		    profiles.add(p);
			} catch (Exception e) {
				System.out.println("Error in "+p);
			}
		    //profilesRepository.save(p);
			//System.out.println(gson.fromJson(jo.get("_source").getAsJsonObject().get("Phone"), String.class));
		    //System.out.println("i "+i);
			//System.out.println(p.toString());
		    //p1=p;
		}
		try {
			System.out.println("Try to save");
			profileRepository.saveAll(profiles);
			System.out.println("Saved");
		} catch (Exception e)
		{System.out.println("Exception in batch save");}
	}
}
