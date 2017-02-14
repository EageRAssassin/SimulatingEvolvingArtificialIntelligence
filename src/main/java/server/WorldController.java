package server;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import com.google.gson.Gson;

import server.ast.ProgramImpl;
import server.exceptions.SyntaxError;
import server.parse.ParserImpl;
import server.parse.Tokenizer;
import server.world.Critter;
import server.world.EmptySpace;
import server.world.World;
import spark.Request;

public class WorldController {

	public World world;

	public World randomWorld() {
		world = new World();
		return world;
	}

	public void advance() {
		world.advanceTime(1);
	}

	public void advance(int i) {
		world.advanceTime(i);
	}

	public World newWorld(Request request) {
		Gson gson = new Gson();
		WorldInfo info = gson.fromJson(request.body(), WorldInfo.class);
		Scanner scan = new Scanner(info.description);
		scan.findInLine("name ");
		String worldName = scan.nextLine();
		scan.findInLine("size ");
		int c = scan.nextInt();
		int r = scan.nextInt();
		world = new World(c, r);
		world.worldName = worldName;
		while (scan.hasNextLine()) {
			scan.nextLine();
			if (scan.findInLine("//") == null)
				if (scan.findInLine("rock") != null) {
					c = scan.nextInt();
					r = scan.nextInt();
					world.addRock(c, r);
				} else if (scan.findInLine("food") != null) {
					c = scan.nextInt();
					r = scan.nextInt();
					int amount = scan.nextInt();
					world.addFood(c, r, amount);
				}
		}
		scan.close();
		return world;
	}

	public World newWorld(Request request, int previous_version) {
		Gson gson = new Gson();
		WorldInfo info = gson.fromJson(request.body(), WorldInfo.class);
		Scanner scan = new Scanner(info.description);
		scan.findInLine("name ");
		String worldName = scan.nextLine();
		scan.findInLine("size ");
		int c = scan.nextInt();
		int r = scan.nextInt();
		world = new World(c, r);
		world.worldName = worldName;
		while (scan.hasNextLine()) {
			scan.nextLine();
			if (scan.findInLine("//") == null)
				if (scan.findInLine("rock") != null) {
					c = scan.nextInt();
					r = scan.nextInt();
					world.addRock(c, r);
				} else if (scan.findInLine("food") != null) {
					c = scan.nextInt();
					r = scan.nextInt();
					int amount = scan.nextInt();
					world.addFood(c, r, amount);
				}
		}
		scan.close();
		world.version = previous_version + 1;
		return world;
	}

	private class WorldInfo {
		String description;
	}

	public int velocityint;
	boolean staticState;

	public void setRate(int r) {
		velocityint = r;
	}

	public void start() {
		Thread advancing = new Thread(new StartSimulation());
		advancing.start();
	}

	public void stop() {
		velocityint = 0;
	}

	private class StartSimulation implements Runnable {
		@Override
		public void run() {
			int timePerStep = 1000 / velocityint;
			while (velocityint > 0) {
				advance();
				try {
					Thread.sleep(timePerStep);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public boolean addRock(int c, int r) {
		return world.addRock(c, r);
	}

	public boolean addFood(int c, int r, int amount) {
		return world.addFood(c, r, amount);
	}

	public boolean remove(int id) {
		return world.remove(id);
	}

	public ArrayList<Integer> addCritters(String species_id, String program, int[] mem, int num, int session_id) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		Reader reader = new StringReader(program);
		Tokenizer t = new Tokenizer(reader);
		ProgramImpl ast = new ProgramImpl();
		try {
			ast = ParserImpl.parseProgram(t);
		} catch (SyntaxError e) {
			System.out.println("Syntax Error!");
			return ids;
		}
		for (int i = 0; i < num; i++) {
			int direction;
			direction = (int) (Math.random() * 6);
			int c = -1;
			int r = -1;
			Random random = new Random();
			boolean isEmptySpace = false;
			while (!isEmptySpace) {
				c = random.nextInt(world.worldArray.length);
				r = random.nextInt(world.worldArray[0].length);
				if (world.isValidHex(c, r) == true)
					if (world.hexAt(c, r) instanceof EmptySpace)
						isEmptySpace = true;
			}
			try {
				Critter cr = new Critter(species_id, mem[0], mem[1], mem[2], mem[3], mem[4], mem[7], world, direction,
						c, r, ast);
				cr.createdBy = session_id;
				world.addCritter(cr);
				ids.add(cr.id);
			} catch (Exception e) {

			}
		}
		world.version++;
		return ids;
	}

	public ArrayList<Integer> addCritters(String species_id, String program, int[] mem,
			HashMap<String, Integer>[] positions, int session_id) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		Reader reader = new StringReader(program);
		Tokenizer t = new Tokenizer(reader);
		ProgramImpl ast = new ProgramImpl();
		try {
			ast = ParserImpl.parseProgram(t);
		} catch (SyntaxError e) {
			System.out.println("Syntax Error!");
			return ids;
		}
		for (HashMap<String, Integer> i : positions) {
			int c = i.get("col");
			int r = i.get("row");
			int direction;
			direction = (int) (Math.random() * 6);
			try {
				Critter cr = new Critter(species_id, mem[0], mem[1], mem[2], mem[3], mem[4], mem[7], world, direction,
						c, r, ast);
				world.addCritter(cr);
				cr.createdBy = session_id;
				ids.add(cr.id);
			} catch (Exception e) {

			}
		}
		world.version++;
		return ids;
	}
}