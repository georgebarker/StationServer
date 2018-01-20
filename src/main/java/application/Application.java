package application;

import dao.StationDao;
import database.StationDatabase;
import server.StationServer;

public class Application {
	public static void main(String[] args) {
		new StationServer(new StationDao(new StationDatabase()));
	}
}