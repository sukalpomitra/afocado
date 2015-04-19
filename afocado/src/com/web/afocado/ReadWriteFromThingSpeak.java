/*
 * Copyright (c) 2014 Afocado.
 * Author Sukalpo Mitra
 * 
 */

package com.web.afocado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReadWriteFromThingSpeak extends HttpServlet {
	private static final long serialVersionUID = 1L;


	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		try
		{
			handleRequest(request, response);
		}

		catch (Exception e) {
			throw new ServletException(e);
		}

	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		try
		{
			handleRequest(request, response);
		}

		catch (Exception e) {
			throw new ServletException(e);
		}

	}

	public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException, InterruptedException, ExecutionException, ParseException
	{

		try {
			String feedView = getServletContext().getInitParameter(Utils.VIEWFEED);
			String apiKey = getServletContext().getInitParameter(Utils.APIKEY);
			URL url = new URL(feedView + "?key=" + apiKey);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			JSONObject json = parseJson(br);
			conn.disconnect();
			JSONArray feeds = (JSONArray) json.get("feeds");

			List<String> compass = new ArrayList<String>();
			List<String> accelerometer = new ArrayList<String>();
			List<String> gyro = new ArrayList<String>();

			for(int i = 0 ; i < feeds.size() ; i++){
				JSONObject feed = (JSONObject)feeds.get(i);
				compass.add(feed.get("field1").toString().replace("c:", ""));
				accelerometer.add(feed.get("field2").toString().replace("a:", ""));
				gyro.add(feed.get("field3").toString().replace("g:", ""));
			}

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}


	public JSONObject parseJson(final BufferedReader reader) throws IOException, ParseException
	{
		//BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder builder = new StringBuilder();
		for (String line = null; (line = reader.readLine()) != null;) {
			builder.append(line).append("\n");
		}

		String postData = java.net.URLDecoder.decode(builder.toString(), "UTF-8").replace("jsonData=", "");

		JSONObject json = (JSONObject)new JSONParser().parse(postData);

		return json;
	}

}
