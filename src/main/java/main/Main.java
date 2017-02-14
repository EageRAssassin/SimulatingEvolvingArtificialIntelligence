package main;

import server.Server;
import client.SimulationGUI.WorldGUI;
public class Main {

	public static void main(String[] args) {
		if (args[0].equals("-server")){
			String adminPW = null;
			String writePW = null;
			String readPW = null;
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-admin")) {
					adminPW = args[i + 1];
				}
				if (args[i].equals("-write")) {
					writePW = args[i + 1];
				}
				if (args[i].equals("-read")) {
					readPW = args[i + 1];
				}
			}
			Server server = new Server(adminPW, writePW, readPW);
			server.run();
		}else if (args[0].equals("-client")){
			WorldGUI.main(args);
		}else{
			System.out.println("Please see instructions.");
		}
	}

}
