package server;

import server.world.*;
import spark.Request;

import java.util.*;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.delete;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Server {
	private World world;
	private WorldController controller = new WorldController();
	private HashMap<Integer, String> access = new HashMap<Integer, String>();
	private HashMap<String, String> auth = new HashMap<String, String>();
	private HashMap<Integer, World> stored = new HashMap<Integer, World>();
	private HashMap<Integer, Boolean> isNotEmpty = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Integer> deadCritters = new HashMap<Integer, Integer>();
	Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	public Server(String adminPW, String writePW, String readPW) {
		auth.put("admin", adminPW);
		auth.put("write", writePW);
		auth.put("read", readPW);
	}

	public void run() {
		port(8080);
		world = controller.randomWorld();
		stored.put(world.version, world.clone());

		post("/CritterWorld/login", (request, response) -> {
			synchronized (this) {
				request.body();
				Combo combo = gson.fromJson(request.body(), Combo.class);
				String level = combo.level;
				String password = combo.password;
				if (auth.get(level) != null && auth.get(level).equals(password)) {
					return new Login(level);
				} else {
					response.status(401);
					return "Unauthorized";
				}
			}
		}, gson::toJson);

		post("/CritterWorld/world", (request, response) -> {
			synchronized (this) {
				request.body();
				int id = Integer.parseInt(request.queryParams("session_id"));
				if (access.get(id) != null && access.get(id).equals("admin")) {
					response.status(201);
					try {
						if (world == null) {
							world = controller.newWorld(request);
						} else {
							world = controller.newWorld(request, world.version);
						}
					} catch (Exception e) {
						response.status(406);
						return "Cannot create world";
					}
					for (Integer i : isNotEmpty.keySet()) {
						isNotEmpty.put(i, false);
					}
					return "Ok";
				} else {
					response.status(401);
					return "Unauthorized";
				}
			}
		});

		get("/CritterWorld/critters", (request, response) -> {
			synchronized (this) {
				request.body();
				int id = Integer.parseInt(request.queryParams("session_id"));
				ArrayList<ObjectInfo> cinfo = new ArrayList<ObjectInfo>();
				if (access.get(id).equals("admin")) {
					for (Critter c : world.critterList) {
						cinfo.add(new CCInfo(c));
					}
					return cinfo;
				} else if (access.get(id).equals("write")) {
					for (Critter c : world.critterList) {
						if (c.createdBy == id) {
							cinfo.add(new CCInfo(c));
						} else {
							cinfo.add(new PCInfo(c));
						}
					}
				} else {
					for (Critter c : world.critterList) {
						cinfo.add(new PCInfo(c));
					}
				}
				return cinfo;
			}
		}, gson::toJson);

		post("/CritterWorld/critters", (request, response) -> {
			synchronized (this) {
				request.body();
				int session_id = Integer.parseInt(request.queryParams("session_id"));
				if (access.get(session_id) != null && !access.get(session_id).equals("read")) {
					return new CreateCritter(request, session_id);
				} else {
					response.status(401);
					return "Unauthorized";
				}
			}
		}, gson::toJson);

		get("/CritterWorld/critter/:key", (request, response) -> {
			synchronized (this) {
				request.body();
				int id = Integer.parseInt(request.params(":key"));
				int session_id = Integer.parseInt(request.queryParams("session_id"));
				int created = 0;
				ObjectInfo cinfo = null;
				for (Critter c : world.critterList) {
					if (c.id == id) {
						created = c.createdBy;
						if (access.get(session_id) != null) {
							if (access.get(session_id).equals("admin") || created == session_id) {
								cinfo = new CCInfo(c);
							} else {
								cinfo = new PCInfo(c);
							}
						}
					}
				}

				if (access.get(session_id) != null) {
					return cinfo;
				} else {
					response.status(401);
					return "Unauthorized";
				}
			}
		}, gson::toJson);

		delete("/CritterWorld/critter/:key", (request, response) -> {
			synchronized (this) {
				request.body();
				int id = Integer.parseInt(request.params(":key"));
				int session_id = Integer.parseInt(request.queryParams("session_id"));
				int createdBy = 0;
				for (Critter c : world.critterList) {
					if (c.id == id) {
						createdBy = c.createdBy;
					}
				}
				if (createdBy == session_id
						|| (access.get(session_id) != null && access.get(session_id).equals("admin"))) {
					controller.remove(id);
					response.status(204);
					return 204;
				} else {
					response.status(401);
					return "Unauthorized";
				}
			}
		});

		get("/CritterWorld/world", (request, response) -> {
			synchronized (this) {
				request.body();
				int from_row = Integer.MIN_VALUE, to_row = Integer.MIN_VALUE, from_col = Integer.MIN_VALUE,
						to_col = Integer.MIN_VALUE;
				try {
					from_row = Integer.parseInt(request.queryParams("from_row"));
					to_row = Integer.parseInt(request.queryParams("to_row"));
					from_col = Integer.parseInt(request.queryParams("from_col"));
					to_col = Integer.parseInt(request.queryParams("to_col"));
				} catch (Exception e) {
					from_row = Integer.MIN_VALUE;
					to_row = Integer.MIN_VALUE;
					from_col = Integer.MIN_VALUE;
					to_col = Integer.MIN_VALUE;
				}
				int update_since = 0;
				try {
					update_since = Integer.parseInt(request.queryParams("update_since"));
				} catch (Exception e) {
				}
				if (update_since > world.version) {
					update_since = world.version;
				}
				int session_id = Integer.parseInt(request.queryParams("session_id"));

				if (access.get(session_id) != null) {
					WorldInfo info = new WorldInfo(update_since, session_id, from_row, to_row, from_col, to_col);
					stored.put(world.version, world.clone());
					return info;
				} else {
					response.status(401);
					return "Unauthorized";
				}
			}
		}, gson::toJson);

		post("/CritterWorld/world/create_entity", (request, response) -> {
			synchronized (this) {
				request.body();
				int session_id = Integer.parseInt(request.queryParams("session_id"));
				if (access.get(session_id) != null && !access.get(session_id).equals("read")) {
					Create_Entity temp = gson.fromJson(request.body(), Create_Entity.class);
					if (temp == null) {
						response.status(406);
						return "Create failed";
					}
					boolean suc;
					if (temp.type.equals("food")) {
						suc = controller.addFood(temp.col, temp.row, temp.amount);
					} else if (temp.type.equals("rock")) {
						suc = controller.addRock(temp.col, temp.row);
					} else {
						response.status(406);
						return "Cannot create" + temp.type;
					}
					if (suc) {
						return "Ok";
					} else {
						response.status(406);
						return "Cannot create" + temp.type;
					}
				} else {
					response.status(401);
					return "Unauthorized";
				}
			}
		});

		post("/CritterWorld/step", (request, response) -> {
			synchronized (this) {
				if (controller.velocityint != 0) {
					response.status(406);
					return "The world is running! Cannot step!";
				}
				request.body();
				int session_id = Integer.parseInt(request.queryParams("session_id"));
				if (access.get(session_id) != null && !access.get(session_id).equals("read")) {
					Step step = gson.fromJson(request.body(), Step.class);
					if (step != null) {
						controller.advance(step.count);
					} else {
						controller.advance();
					}
					return "Ok";
				} else {
					response.status(401);
					return "Unauthorized";
				}
			}
		});

		post("/CritterWorld/run", (request, response) -> {
			synchronized (this) {
				request.body();
				int session_id = Integer.parseInt(request.queryParams("session_id"));
				if (access.get(session_id) != null && !access.get(session_id).equals("read")) {
					Start start = gson.fromJson(request.body(), Start.class);
					int rate = start.rate;
					if (controller.velocityint == 0) {
						if (rate > 0) {
							controller.velocityint = rate;
							controller.start();
						}
					} else {
						controller.stop();
						if (rate > 0) {
							controller.velocityint = rate;
							controller.start();
						}
					}
					return "Ok";
				} else {
					response.status(401);
					return "Unauthorized";
				}
			}
		});

	}

	private class Combo {
		public String level;
		public String password;
	}

	private class Login {
		private int session_id;

		public Login(String level) {
			while (session_id < 1000000) {
				session_id = ((int) (42949671 * Math.random()) + 1927841242) % 10000000;
			}
			access.put(session_id, level);
			isNotEmpty.put(session_id, false);
		}
	}

	private class EInfo implements ObjectInfo {
		public String type;
		public int row, col;

		public EInfo(int c, int r) {
			type = "nothing";
			row = r;
			col = c;
		}
	}

	private class RInfo implements ObjectInfo {
		public int row, col;
		public String type;

		public RInfo(int c, int r) {
			type = "rock";
			row = r;
			col = c;
		}
	}

	private class FInfo implements ObjectInfo {
		public int row, col;
		public String type;
		public int value;

		public FInfo(int c, int r, int amount) {
			type = "food";
			row = r;
			col = c;
			value = amount;
		}

	}

	private class CCInfo implements ObjectInfo {
		public String type;
		public int id;
		public String species_id;
		public String program;
		public int row, col, direction;
		public int[] mem;
		public int recently_executed_rule;

		public CCInfo(int c, int r, Critter cr) {
			type = "critter";
			id = cr.id;
			species_id = cr.name;
			row = r;
			col = c;
			direction = cr.direction;
			mem = cr.mem;
			program = cr.rules.toString();
			recently_executed_rule = cr.rules.root.rules.indexOf(cr.lastRule);
		}

		public CCInfo(Critter c) {
			id = c.id;
			species_id = c.name;
			program = c.rules.toString();
			row = c.r;
			col = c.c;
			direction = c.direction;
			mem = c.mem;
			recently_executed_rule = c.rules.root.rules.indexOf(c.lastRule);
		}
	}

	private class PCInfo implements ObjectInfo {
		public String type;
		public int id;
		public String species_id;
		public int row, col, direction;
		public int[] mem;

		public PCInfo(int c, int r, Critter cr) {
			type = "critter";
			id = cr.id;
			species_id = cr.name;
			row = r;
			col = c;
			direction = cr.direction;
			mem = cr.mem;
		}

		public PCInfo(Critter c) {
			id = c.id;
			species_id = c.name;
			row = c.r;
			col = c.c;
			direction = c.direction;
			mem = c.mem;
		}
	}

	private interface ObjectInfo {
	}

	private class WorldInfo {
		public int current_timestep;
		public int current_version_number;
		public int update_since = Integer.MAX_VALUE;
		public double rate;
		public String name;
		public int population;
		public int rows, cols;
		public Integer[] dead_critters;
		public ObjectInfo[] state;

		public WorldInfo(int update_since, int session_id, int from_row, int to_row, int from_col, int to_col) {
			current_timestep = world.numSteps;
			current_version_number = world.version;
			rate = controller.velocityint;
			name = world.worldName;
			population = world.getCritterNumber();
			rows = world.rows;
			cols = world.cols;
			dead_critters = dead(update_since);
			if (stored.size() > 500){
				int min = Integer.MAX_VALUE;
				for (Integer i : stored.keySet()) {
					if (min > i) {
						min = i;
					}
				}
				stored.remove(new Integer(min));
			}
			for (Integer i : stored.keySet()) {
				if (this.update_since > i) {
					this.update_since = i;
				}
			}
			if (!isNotEmpty.get(session_id) || stored.get(update_since) == null) {
				state = cdiff(session_id);
			} else if (stored.get(update_since).worldName == world.worldName) {
				state = diff(update_since, session_id, from_row, to_row, from_col, to_col);
			} else {
				state = cdiff(session_id);
			}
		}

		public Integer[] dead(int update_since) {
			ArrayList<Integer> deadlist = new ArrayList<Integer>();
			deadCritters.putAll(world.dead);
			for (int i : deadCritters.keySet()) {
				if (deadCritters.get(i) >= update_since) {
					deadlist.add(deadCritters.get(i));
				}
			}
			Integer[] a = new Integer[deadlist.size()];
			return deadlist.toArray(a);
		}

		public ObjectInfo[] diff(int update_since, int session_id, int from_row, int to_row, int from_col, int to_col) {
			WorldObject[][] original = stored.get(update_since).worldArray;
			WorldObject[][] current = world.worldArray;
			ArrayList<ObjectInfo> d = new ArrayList<ObjectInfo>();
			int c, r, c1, r1;
			if (from_row != Integer.MIN_VALUE && to_row != Integer.MIN_VALUE && from_col != Integer.MIN_VALUE
					&& to_col != Integer.MIN_VALUE) {
				c = from_col;
				r = from_row;
				c1 = to_col + 1;
				r1 = to_row + 1;
			} else {
				c = 0;
				c1 = current.length;
				r = 0;
				r1 = current[0].length;
			}
			if (c1 > current.length) {
				c1 = current.length;
			}
			if (r1 > current[0].length) {
				r1 = current.length;
			}
			if (c < 0) {
				c = 0;
			}
			if (r < 0) {
				r = 0;
			}
			for (int start = c; start < c1; start++) {
				for (int start1 = r; start1 < r1; start1++) {
					if (original[start][start1] != current[start][start1]) {
						ObjectInfo temp = null;
						if (current[start][start1] instanceof EmptySpace) {
							temp = new EInfo(start, start1);
						}
						if (current[start][start1] instanceof Rock) {
							temp = new RInfo(start, start1);
						}
						if (current[start][start1] instanceof Food) {
							int amount = ((Food) current[start][start1]).getAmount();
							temp = new FInfo(start, start1, amount);
						}
						if (current[start][start1] instanceof Critter) {
							if (((Critter) current[start][start1]).createdBy == session_id
									|| access.get(session_id).equals("admin")) {
								temp = new CCInfo(start, start1, (Critter) current[start][start1]);
							} else {
								temp = new PCInfo(start, start1, (Critter) current[start][start1]);
							}
						}
						if (temp != null) {
							d.add(temp);
						}
					}
				}
			}
			ObjectInfo[] temp = new ObjectInfo[d.size()];
			return d.toArray(temp);
		}

		public ObjectInfo[] cdiff(int session_id) {
			WorldObject[][] current = world.worldArray;
			ArrayList<ObjectInfo> d = new ArrayList<ObjectInfo>();
			for (int c = 0; c < current.length; c++) {
				for (int r = 0; r < current[0].length; r++) {
					ObjectInfo temp = null;
					if (current[c][r] instanceof EmptySpace) {
						temp = new EInfo(c, r);
					}
					if (current[c][r] instanceof Rock) {
						temp = new RInfo(c, r);
					}
					if (current[c][r] instanceof Food) {
						int amount = ((Food) current[c][r]).getAmount();
						temp = new FInfo(c, r, amount);
					}
					if (current[c][r] instanceof Critter) {
						if (access.get(session_id).equals("admin")
								|| ((Critter) current[c][r]).createdBy == session_id) {
							temp = new CCInfo(c, r, (Critter) current[c][r]);
						} else {
							temp = new PCInfo(c, r, (Critter) current[c][r]);
						}
					}
					if (temp != null) {
						d.add(temp);
					}
				}
			}
			ObjectInfo[] temp = new ObjectInfo[d.size()];
			isNotEmpty.put(session_id, true);
			return d.toArray(temp);
		}
	}

	private class Create_Entity {
		public int row;
		public int col;
		public String type;
		public int amount;
	}

	private class Step {
		int count;
	}

	private class Start {
		int rate;
	}

	private class CreateCritter {
		public String species_id;
		public ArrayList<Integer> ids;

		public CreateCritter(Request request, int session_id) {
			Create temp = gson.fromJson(request.body(), Create.class);
			species_id = temp.species_id;
			if (temp.num != 0) {
				ids = controller.addCritters(temp.species_id, temp.program, temp.mem, temp.num, session_id);
			} else {
				ids = controller.addCritters(temp.species_id, temp.program, temp.mem, temp.positions, session_id);
			}

		}

		private class Create {
			public String species_id;
			public String program;
			public int[] mem;
			public HashMap<String, Integer>[] positions;
			public int num;
		}
	}
}
