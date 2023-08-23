package com.isos.h3s.pdq.data.mergepoc;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Data {
	
	//Gson gson = new Gson();
	
	private ArrayList<JsonObject> data;

	public ArrayList<JsonObject> getData() {
		//ResponseService responseService = gson.fromJson(responseData.getData().get(0).toString(), ResponseService .class);
		return data;
	}

	public void setData(ArrayList<JsonObject> data) {
		this.data = data;
	}

}