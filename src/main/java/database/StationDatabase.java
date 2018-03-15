package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.LatLng;

/**
 * I am a class that establishes a connection with the SQLite database, and I am
 * used to perform queries to the database.
 */
public class StationDatabase {

	Connection connection = null;
	private static final String DATABASE_NAME = "jdbc:sqlite:trainstations.db";
	private static final String QUERY = "select StationName, Longitude, Latitude, (((? - Latitude) * (? - Latitude)) + (0.59 * ((? - Longitude) * (? - Longitude)))) AS DistanceMetric FROM stations ORDER BY DistanceMetric LIMIT 5;";

	/**
	 * I instantiate the database connection.
	 */
	public StationDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(DATABASE_NAME);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * I query the database with the provided LatLng. I use safe parameters to
	 * prevent SQL injection.
	 * 
	 * @param latLng
	 *            I am the latitude and longitude to query the database with
	 * @return a ResultSet that contains the result of the query - or null if there
	 *         are issues with the database.
	 */
	public ResultSet query(LatLng latLng) {
		if (connection != null) {
			try {
				PreparedStatement statement = connection.prepareStatement(QUERY);
				statement.setString(1, latLng.getLatitude());
				statement.setString(2, latLng.getLatitude());
				statement.setString(3, latLng.getLongitude());
				statement.setString(4, latLng.getLongitude());
				return statement.executeQuery();
			} catch (SQLException e) {
				System.err.println("Issue retreiving results from the database." + e);
				return null;
			}
		} else {
			System.err.println("Connection is null, database has not been instantiated.");
			return null;
		}
	}

}
