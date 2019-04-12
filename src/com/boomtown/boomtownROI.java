package com.boomtown;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class boomtownROI {
	
	//Method to get the API Information from URL provided
	public static String[] getAPIInformation(String urlString) throws IOException {
		String lineFromAPI = "";
		String [] apiInfo;
		
		//Set url and read lines from API
		URL url = new URL (urlString);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		lineFromAPI = reader.readLine();
		
		//Clean up api lines
		apiInfo = lineFromAPI.split(",|\\{|\\}");
		
		reader.close();
		
		return apiInfo;
		
		
	}
	//Method to get the HTTP status code 
	public static int getHTTPCode(String urlString) throws IOException {
		int httpCode = 0;
		
		//Set url and make connection
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		
		//Get response code
		httpCode = connection.getResponseCode();
		
		return httpCode;
	}
	//Method to parse the IDs from the API information
	public static ArrayList<String> parseApiId (String[] apiURLInfoList) {
		String[] tempList = null;
		ArrayList<String> idValuesParsed = new ArrayList<String>();
		String cleanIdString = "";	//Clean ID String
		String cleanName = "";		//Clean Object Name for ID
		
		//Loop to find IDs and remove excess characters
		for(int i =0; i < apiURLInfoList.length; i++) {
			if(apiURLInfoList[i].indexOf("\"id\":") != -1){
				tempList = apiURLInfoList[i].split("\"id\"|\\:|\\,");
				cleanIdString = tempList[2].replaceAll("\"|\\s", "");
				
				//find name of object for id
				//will exclude first line of api to prevent exception
				if(i != 0 ) {
					cleanName = apiURLInfoList[i-1].replaceAll("[\\{ \":,\\[]", "");
				}
				//Add clean name and clean id string
				if(!cleanName.equals("")) {
					idValuesParsed.add(cleanName);
				}
				idValuesParsed.add(cleanIdString);
				
			}
		}
		return idValuesParsed;
	}
	//Method to output the api information found
	public static void outputAPIInformation(String[] apiInfoList) throws IOException {
		ArrayList<String> boomtownURLList = new ArrayList<String>();	
		ArrayList<String> idValuesFromURL = new ArrayList<String>();
		int httpCode;
		String[] apiInfoFromURL;
		
		//Loop to find all urls with api.github.com/orgs/BoomTownROI
		for(int i = 0; i < apiInfoList.length; i++) {
			if(apiInfoList[i].indexOf("api.github.com/orgs/BoomTownROI") != -1) {
				String[] urlLine = apiInfoList[i].split("\"");
				boomtownURLList.add(urlLine[3]);
			}
		}
		//Loop to check http code and output correct ids
		for(int i = 0; i < boomtownURLList.size(); i++) {
			httpCode = getHTTPCode(boomtownURLList.get(i));
			
			//If http code is 200
			if(httpCode ==200) {
				apiInfoFromURL = getAPIInformation(boomtownURLList.get(i));
				System.out.println("\nStatus Code: " + httpCode + " URL: " + boomtownURLList.get(i) );
				idValuesFromURL = parseApiId(apiInfoFromURL);
				
				//Loop to print IDs and Objects associated
				for(int j = 0; j < idValuesFromURL.size(); j++) {
					//Check if all digits
					if(idValuesFromURL.get(j).matches("[0-9]+")) {
						System.out.println(" ID: " + idValuesFromURL.get(j));
					}
					else {
						System.out.print("	Object: " + idValuesFromURL.get(j) + "; ");
					}
				}
			}
			//if http code anything else
			else {
				System.out.println("\nFailed Request" + " Status Code: " + httpCode + " URL: "+ boomtownURLList.get(i) );
			}
			
		}	
		
	}
	//Method to verify date information
	public static void verifyDateInformation(String[] apiInformationList) {
		Boolean datesCorrect = false;
		String createdTime = "";
		String updatedTime = "";
		
		//Loop to find created and updated times
		for(int i = 0; i < apiInformationList.length; i++) {
			if(apiInformationList[i].indexOf("\"created_at\":") != -1) {
				createdTime = apiInformationList[i].replaceAll("\\s|created_at|\":|\\,|\"", "");
			}
			if(apiInformationList[i].indexOf("\"updated_at\":") != -1) {
				updatedTime = apiInformationList[i].replaceAll("\\s|updated_at|\":|\\,|\"", "");
			}
		}
		//Compare the timestamps
		if(createdTime.compareTo(updatedTime) < 0) {
			datesCorrect = true;
		}
		
		//Output
		System.out.println("\nDate Verified: " +datesCorrect + "\nCreated Time: " + createdTime + " Updated Time: " + updatedTime);

	}

	//Main method
	public static void main(String[] args) throws IOException {
		
		String url = "https://api.github.com/orgs/boomtownroi";
		
		//String [] apiInformation = getAPIInformation(url);
		//outputAPIInformation(apiInformation);
		//verifyDateInformation(apiInformation);
		
		ArrayList<String> apiInformation = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader("boomtownroi.txt"));
		String line;
		while((line = reader.readLine()) !=null) {
			apiInformation.add(line);
			//System.out.println(line);
		}
		reader.close();
		String[] tempList = new String[apiInformation.size()];
		for(int i =0; i < tempList.length; i++) {
			tempList[i] = apiInformation.get(i);
		}
		
		verifyDateInformation(tempList);
		
		
		//System.out.println(apiIDs);
		
		
		
		
	}

}
