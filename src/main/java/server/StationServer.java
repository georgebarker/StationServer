package server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import dao.StationDao;
import handler.StationHandler;

public class StationServer {
	private static final int PORT = 8080;
	
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
