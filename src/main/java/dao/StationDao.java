package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.StationDatabase;
import model.LatLng;
import model.Station;

/**
 * I am a Data Access Object - I retrieve results from the database and
 * translate them into a Station object that makes sense.
 */
public class StationDao {
	private static final String STATION_NAME_COLUMN = "StationName";
	private static final String LATITUDE_COLUMN = "Latitude";
	private static final String LONGITUDE_COLUMN = "Longitude";

	private StationDatabase database;

	/**
	 * I instantiate a Dao object
	 * 
	 * @param database
	 *            the database required to perform queries.
	 */
	public StationDao(StationDatabase database) {
		this.database = database;
	}

	/**
	 * I get the stations from the database using the provided LatLng
	 * 
	 * @param latLng
	 *            I am the LatLng that has been provided by the user
	 * @return a list of Station objects, or null if there are issues mapping the
	 *         results or retrieving from the DB
	 */
	public List<Station> getStationsByLatLng(LatLng latLng) {
		try {
			ResultSet results = database.query(latLng);
			if (results != null) {
				return mapResultSetToStations(results);
			} else {
				return null;
			}
		} catch (SQLException e) {
			System.err.println("Could not map results " + e);
			return null;
		}
	}

	/**
	 * I map a generic SQL ResultSet to a list of Stations.
	 * 
	 * @param results
	 *            I am the Stations in the format of a generic ResultSet
	 * @return the list of stations that arrived as a ResultSet, as a List of
	 *         Stations
	 * @throws SQLException
	 *             I throw this when there is an issue retrieving data from the
	 *             ResultSet
	 */
	public List<Station> mapResultSetToStations(ResultSet results) throws SQLException {
		List<Station> stations = new ArrayList<>();
		while (results.next()) {
			Station station = new Station();
			station.setStationName(results.getString(STATION_NAME_COLUMN));
			String lat = results.getString(LATITUDE_COLUMN);
			String lng = results.getString(LONGITUDE_COLUMN);
			station.setLatLng(new LatLng(lat, lng));
			stations.add(station);
		}
		return stations;
	}
}
