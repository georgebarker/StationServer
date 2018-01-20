package handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import dao.StationDao;
import model.LatLng;
import model.Station;

public class StationHandler implements HttpHandler {

	private StationDao dao;
	
	public StationHandler(StationDao dao) {
		this.dao = dao;
	}
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		LatLng latLng = getLatLngFromExchange(exchange);
		if (latLng != null) {
			List<Station> stations = dao.getStationsByLatLng(latLng);
			
			if (stations != null) {
				JSONArray stationsAsJson = getJsonFromStations(stations);
			    sendJson(stationsAsJson.toString(), exchange, 200);
			} else {
				sendErrorJson("Failed to retrieve results from database.", exchange);
			}
		} else {
			sendErrorJson("The Lat/Lng provided is incorrect. Check key and value.", exchange);
		}
	}

	private LatLng getLatLngFromExchange(HttpExchange exchange) {
		String query = exchange.getRequestURI().getQuery();
		
		// splits by &, e.g. element 0 lat=53.472, element 1 lng=-2.24
		String[] parameters = query.split("&"); 
		
		// splits by =, e.g. element 0 is lat, element 1 is 53.472.
		String[] latParams = parameters[0].split("=");
		String[] lngParams = parameters[1].split("=");
		
		//checks that the user has entered valid parameter names
		String latParamName = latParams[0];
		String lngParamName = lngParams[0];
		if (!paramNamesAreValid(latParamName, lngParamName)) {
			return null;
		}
		
		try {
			String lat = latParams[1]; 
			String lng = lngParams[1];
			return new LatLng(lat, lng);
		} catch (NumberFormatException e) {
			System.err.println("Issue with parameters provided by user. Error will be returned to user.");
			e.printStackTrace();
			return null;
		}
	}
	
	private JSONArray getJsonFromStations(List<Station> stations) {
		JSONArray array = new JSONArray();
		for (Station station : stations) {
			JSONObject object = new JSONObject();
			object.put("StationName", station.getStationName());
			object.put("Latitude", station.getLatLng().getLatitude());
			object.put("Longitude", station.getLatLng().getLongitude());
			array.put(object);
		}
		return array;
	}
	
	private void sendJson(String jsonString, HttpExchange exchange, int statusCode) {
		exchange.getResponseHeaders().set("Content-Type", "application/json");
		byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
	    try {
			exchange.sendResponseHeaders(statusCode, bytes.length);
			OutputStream os = exchange.getResponseBody();
		    os.write(bytes);
		    os.close();
		} catch (IOException e) {
			System.err.println("Fatal error: issue sending JSON.");
			e.printStackTrace();
		}
	    
	}
	
	private void sendErrorJson(String errorMessage, HttpExchange exchange) {
		JSONArray array = new JSONArray();
		JSONObject object = new JSONObject();
		object.put("Status", 400);
		object.put("Message", errorMessage);
		array.put(object);
		sendJson(array.toString(), exchange, 400);
	}
	
	private boolean paramNamesAreValid(String latParamName, String longParamName) {
		return "lat".equals(latParamName) && "lng".equals(longParamName);
	}
}