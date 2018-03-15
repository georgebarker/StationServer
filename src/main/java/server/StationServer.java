package server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import dao.StationDao;
import handler.StationHandler;

/**
 * I am a class that initiates the server.
 */
public class StationServer {
	private static final int PORT = 8080;

	/**
	 * I create the server using a HttpServer on the port specified, at the endpoint
	 * /stations. I output if the server has started or if the server has failed to
	 * start.
	 * 
	 * @param dao
	 *            This is the data access object required to retrieve the stations
	 *            from the database. I give this to the Handler.
	 */
	public StationServer(StationDao dao) {
		HttpServer server;
		try {
			System.out.println("Starting server...");
			server = HttpServer.create(new InetSocketAddress(PORT), 0);
			server.createContext("/stations", new StationHandler(dao));
			server.setExecutor(null);
			server.start();
			System.out.println("Server started.");
		} catch (IOException e) {
			System.err.print("Server failed to start.");
			e.printStackTrace();
		}

	}

}
