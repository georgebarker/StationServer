package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.StationDatabase;
import model.LatLng;
import model.Station;

public class StationDao {
	private static final String STATION_NAME_COLUMN = "StationName";
	private static final String LATITUDE_COLUMN = "Latitude";
	private static final String LONGITUDE_COLUMN = "Longitude";

	private StationDatabase database;

	public StationDao(StationDatabase database) {
		this.database = database;
	}

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
