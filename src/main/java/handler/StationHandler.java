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

/**
 * I am a custom implementation of a HttpHandler that handles Stations and
 * outputs them into JSON.
 */
public class StationHandler implements HttpHandler {

	private StationDao dao;

	/**
	 * I instantiate the StationHandler.
	 * 
	 * @param dao
	 *            This is used to retrieve the stations from the database.
	 */
	public StationHandler(StationDao dao) {
		this.dao = dao;
	}

	/**
	 * I am an implementation of HttpHandler method. I convert the HttpExchange
	 * object into an understandable LatLng, and use this to query the database. I
	 * then send the user the result; either the stations or an error in JSON
	 * format.
	 */
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

	/**
	 * I pull the latitude and longitude provided by the user from the HttpExchange.
	 * 
	 * @param exchange
	 *            the HttpExchange that contains the parameters the user entered.
	 * @return I return the LatLng the user has provided in an understandable
	 *         format, or I return null if what the user provided was not valid.
	 */
	private LatLng getLatLngFromExchange(HttpExchange exchange) {
		String query = exchange.getRequestURI().getQuery();

		// splits by &, e.g. element 0 lat=53.472, element 1 lng=-2.24
		String[] parameters = query.split("&");

		// splits by =, e.g. element 0 is lat, element 1 is 53.472.
		String[] latParams = parameters[0].split("=");
		String[] lngParams = parameters[1].split("=");

		// checks that the user has entered valid parameter names
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
			System.err.println(
					"Issue with parameters provided by user. Error will be returned to user.");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * I convert the domain model Stations into a generic JSONArray in order to be
	 * output to the user.
	 * 
	 * @param stations
	 *            the list of stations retrieved from the database.
	 * @return a generic JSONArray that has been converted from the provided
	 *         stations.
	 */
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

	/**
	 * I am a generic method that sends a successful JSON response.
	 * 
	 * @param jsonString
	 *            The json to send to the user, as a string.
	 * @param exchange
	 *            The HttpExchange the user will receive.
	 * @param statusCode
	 *            The HTTP status code to send to the user (e.g. 200, 404 etc)
	 */
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

	/**
	 * I am a generic method that sends JSON error messages using the 400 status
	 * code.
	 * 
	 * @param errorMessage
	 *            I am the error message to send to the user.
	 * @param exchange
	 *            I am the HttpExchange the user will receive.
	 */
	private void sendErrorJson(String errorMessage, HttpExchange exchange) {
		JSONArray array = new JSONArray();
		JSONObject object = new JSONObject();
		object.put("Status", 400);
		object.put("Message", errorMessage);
		array.put(object);
		sendJson(array.toString(), exchange, 400);
	}

	/**
	 * I perform a check that the names of the parameters that the user has passed
	 * in, is valid. Therefore, I reject things like '?dog=54.2', because even
	 * though the passed parameter is valid - the name is not.
	 * 
	 * @param latParamName What the user gave in the place of the latitude parameter key
	 * @param longParamName What the user gave in the place of the longitude parameter key
	 * @return
	 */
	private boolean paramNamesAreValid(String latParamName, String longParamName) {
		return "lat".equals(latParamName) && "lng".equals(longParamName);
	}
}