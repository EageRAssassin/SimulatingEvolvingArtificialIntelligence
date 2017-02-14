package server.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

import server.ast.Mutation;
import server.ast.MutationFactory;
import server.ast.ProgramImpl;
import server.ast.Rules;
import server.ast.Rule;
import server.interpret.Interpreter;
import server.interpret.InterpreterImpl;
import server.interpret.Outcome;
import server.world.Critter.Node;

public class Critter implements WorldObject {
	public String name;
	public int[] mem;
	World world;
	public int direction;
	public ProgramImpl rules;
	public int c, r;
	boolean wannaMate = false;
	Interpreter i;
	public Rule lastRule;
	public int createdBy;
	public int id;

	public Critter(String name, int memSize, int defense, int offense, int size, int energy, int posture, World world,
			int direction, int c, int r, ProgramImpl rules) {
		mem = new int[memSize];
		this.name = name;
		mem[0] = memSize;
		mem[1] = defense;
		mem[2] = offense;
		mem[3] = size;
		mem[4] = energy > getMaxEnergy() ? getMaxEnergy() : energy;
		mem[7] = posture;
		this.world = world;
		this.direction = direction % 6;
		this.c = c;
		this.r = r;
		this.rules = rules;
		i = new InterpreterImpl(this);
		id = 1;
		for (Critter critter : world.critterList) {
			if (critter.id >= id) {
				id = critter.id + 1;
			}
		}
		;
	}

	public boolean update(int mem, int value) {
		synchronized (this) {
			if (mem < WorldConstants.MIN_MEMORY - 1 || mem >= this.mem[0]) {
				return false;
			} else if (mem == WorldConstants.MIN_MEMORY - 1 && (value < 0 || value > 99)) {
				return false;
			} else {
				this.mem[mem] = value;
				return true;
			}
		}
	}

	public int nearby(int dir) {
		synchronized (this) {
			int direction = (dir + this.direction) % 6;
			int[] change = getcoor(direction);
			if (!world.isValidHex(c + change[0], r + change[1])) {
				return 0;
			}
			return world.worldArray[c + change[0]][r + change[1]].getInfo(this.direction);
		}
	}

	public int ahead(int dist) {
		synchronized (this) {
			if (dist <= 0) {
				return getInfo(direction);
			}
			int[] change = getcoor(direction);
			if (!world.isValidHex(c + dist * change[0], r + dist * change[1])) {
				return 0;
			}
			return world.worldArray[c + dist * change[0]][r + dist * change[1]].getInfo(direction);
		}
	}

	/*
	 * perform the smell function and return the smell value
	 */
	public int smell() {
		synchronized (this) {
			Node[][] AllHexNode = new Node[world.worldArray.length][world.worldArray[0].length];
			int HexAmount = 0;
			for (int i = 0; i < world.worldArray.length; i++)
				for (int j = 0; j < world.worldArray[0].length; j++) {
					if (world.isValidHex(i, j)) {
						AllHexNode[i][j] = new Node(i, j, Integer.MAX_VALUE, -1, -1);
						HexAmount++;
					}
				}
			PriorityQueue<Node> frontier = new PriorityQueue<Node>(HexAmount, new Node(c, r, 0, 0, 0));
			// Place the six hex around it first
			if (world.isValidHex(c, r + 1))
				if (world.worldArray[c][r + 1] instanceof EmptySpace || world.worldArray[c][r + 1] instanceof Food) {
					if (direction == 0) {
						AllHexNode[c][r + 1].setddd(0, 0, 0);
						frontier.add(AllHexNode[c][r + 1]);
					}
					if (direction == 1) {
						AllHexNode[c][r + 1].setddd(1, 0, 5);
						frontier.add(AllHexNode[c][r + 1]);
					}
					if (direction == 2) {
						AllHexNode[c][r + 1].setddd(2, 0, 4);
						frontier.add(AllHexNode[c][r + 1]);
					}
					if (direction == 3) {
						AllHexNode[c][r + 1].setddd(3, 0, 3);
						frontier.add(AllHexNode[c][r + 1]);
					}
					if (direction == 4) {
						AllHexNode[c][r + 1].setddd(2, 0, 2);
						frontier.add(AllHexNode[c][r + 1]);
					}
					if (direction == 5) {
						AllHexNode[c][r + 1].setddd(1, 0, 1);
						frontier.add(AllHexNode[c][r + 1]);
					}
				}
			if (world.isValidHex(c + 1, r + 1))
				if (world.worldArray[c + 1][r + 1] instanceof EmptySpace
						|| world.worldArray[c + 1][r + 1] instanceof Food) {
					if (direction == 0) {
						AllHexNode[c + 1][r + 1].setddd(1, 1, 1);
						frontier.add(AllHexNode[c + 1][r + 1]);
					}
					if (direction == 1) {
						AllHexNode[c + 1][r + 1].setddd(0, 1, 0);
						frontier.add(AllHexNode[c + 1][r + 1]);
					}
					if (direction == 2) {
						AllHexNode[c + 1][r + 1].setddd(1, 1, 5);
						frontier.add(AllHexNode[c + 1][r + 1]);
					}
					if (direction == 3) {
						AllHexNode[c + 1][r + 1].setddd(2, 1, 4);
						frontier.add(AllHexNode[c + 1][r + 1]);
					}
					if (direction == 4) {
						AllHexNode[c + 1][r + 1].setddd(3, 1, 3);
						frontier.add(AllHexNode[c + 1][r + 1]);
					}
					if (direction == 5) {
						AllHexNode[c + 1][r + 1].setddd(2, 1, 2);
						frontier.add(AllHexNode[c + 1][r + 1]);
					}
				}
			if (world.isValidHex(c + 1, r))
				if (world.worldArray[c + 1][r] instanceof EmptySpace || world.worldArray[c + 1][r] instanceof Food) {
					if (direction == 0) {
						AllHexNode[c + 1][r].setddd(2, 2, 2);
						frontier.add(AllHexNode[c + 1][r]);
					}
					if (direction == 1) {
						AllHexNode[c + 1][r].setddd(1, 2, 1);
						frontier.add(AllHexNode[c + 1][r]);
					}
					if (direction == 2) {
						AllHexNode[c + 1][r].setddd(0, 2, 0);
						frontier.add(AllHexNode[c + 1][r]);
					}
					if (direction == 3) {
						AllHexNode[c + 1][r].setddd(1, 2, 5);
						frontier.add(AllHexNode[c + 1][r]);
					}
					if (direction == 4) {
						AllHexNode[c + 1][r].setddd(2, 2, 4);
						frontier.add(AllHexNode[c + 1][r]);
					}
					if (direction == 5) {
						AllHexNode[c + 1][r].setddd(3, 2, 3);
						frontier.add(AllHexNode[c + 1][r]);
					}
				}
			if (world.isValidHex(c, r - 1))
				if (world.worldArray[c][r - 1] instanceof EmptySpace || world.worldArray[c][r - 1] instanceof Food) {
					if (direction == 0) {
						AllHexNode[c][r - 1].setddd(3, 3, 3);
						frontier.add(AllHexNode[c][r - 1]);
					}
					if (direction == 1) {
						AllHexNode[c][r - 1].setddd(2, 3, 2);
						frontier.add(AllHexNode[c][r - 1]);
					}
					if (direction == 2) {
						AllHexNode[c][r - 1].setddd(1, 3, 1);
						frontier.add(AllHexNode[c][r - 1]);
					}
					if (direction == 3) {
						AllHexNode[c][r - 1].setddd(0, 3, 0);
						frontier.add(AllHexNode[c][r - 1]);
					}
					if (direction == 4) {
						AllHexNode[c][r - 1].setddd(1, 3, 5);
						frontier.add(AllHexNode[c][r - 1]);
					}
					if (direction == 5) {
						AllHexNode[c][r - 1].setddd(2, 3, 4);
						frontier.add(AllHexNode[c][r - 1]);
					}
				}
			if (world.isValidHex(c - 1, r - 1))
				if (world.worldArray[c - 1][r - 1] instanceof EmptySpace
						|| world.worldArray[c - 1][r - 1] instanceof Food) {
					if (direction == 0) {
						AllHexNode[c - 1][r - 1].setddd(2, 4, 4);
						frontier.add(AllHexNode[c - 1][r - 1]);
					}
					if (direction == 1) {
						AllHexNode[c - 1][r - 1].setddd(3, 4, 3);
						frontier.add(AllHexNode[c - 1][r - 1]);
					}
					if (direction == 2) {
						AllHexNode[c - 1][r - 1].setddd(2, 4, 2);
						frontier.add(AllHexNode[c - 1][r - 1]);
					}
					if (direction == 3) {
						AllHexNode[c - 1][r - 1].setddd(1, 4, 1);
						frontier.add(AllHexNode[c - 1][r - 1]);
					}
					if (direction == 4) {
						AllHexNode[c - 1][r - 1].setddd(0, 4, 0);
						frontier.add(AllHexNode[c - 1][r - 1]);
					}
					if (direction == 5) {
						AllHexNode[c - 1][r - 1].setddd(1, 4, 5);
						frontier.add(AllHexNode[c - 1][r - 1]);
					}
				}
			if (world.isValidHex(c - 1, r))
				if (world.worldArray[c - 1][r] instanceof EmptySpace || world.worldArray[c - 1][r] instanceof Food) {
					if (direction == 0) {
						AllHexNode[c - 1][r].setddd(1, 5, 5);
						frontier.add(AllHexNode[c - 1][r]);
					}
					if (direction == 1) {
						AllHexNode[c - 1][r].setddd(2, 5, 4);
						frontier.add(AllHexNode[c - 1][r]);
					}
					if (direction == 2) {
						AllHexNode[c - 1][r].setddd(3, 5, 3);
						frontier.add(AllHexNode[c - 1][r]);
					}
					if (direction == 3) {
						AllHexNode[c - 1][r].setddd(2, 5, 2);
						frontier.add(AllHexNode[c - 1][r]);
					}
					if (direction == 4) {
						AllHexNode[c - 1][r].setddd(1, 5, 1);
						frontier.add(AllHexNode[c - 1][r]);
					}
					if (direction == 5) {
						AllHexNode[c - 1][r].setddd(0, 5, 0);
						frontier.add(AllHexNode[c - 1][r]);
					}
				}

			while (!frontier.isEmpty()) {
				Node v = frontier.poll(); // extracts element with smallest
											// v.dist on the queue
				if (world.worldArray[v.col][v.row] instanceof Food) {
					return v.distance * 1000 + v.directionResult;
				}
				if (world.isValidHex(v.col, v.row + 1))
					if (world.worldArray[v.col][v.row + 1] instanceof EmptySpace
							|| world.worldArray[v.col][v.row + 1] instanceof Food) {
						if (v.direction == 0)
							if (AllHexNode[v.col][v.row + 1].distance > v.distance + 1 && v.distance + 1 <= 10) {
								AllHexNode[v.col][v.row + 1].setddd(v.distance + 1, 0, v.directionResult);
								frontier.add(AllHexNode[v.col][v.row + 1]);
							}
						if (v.direction == 1)
							if (AllHexNode[v.col][v.row + 1].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col][v.row + 1].setddd(v.distance + 2, 0, v.directionResult);
								frontier.add(AllHexNode[v.col][v.row + 1]);
							}
						if (v.direction == 5)
							if (AllHexNode[v.col][v.row + 1].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col][v.row + 1].setddd(v.distance + 2, 0, v.directionResult);
								frontier.add(AllHexNode[v.col][v.row + 1]);
							}
					}
				if (world.isValidHex(v.col + 1, v.row + 1))
					if (world.worldArray[v.col + 1][v.row + 1] instanceof EmptySpace
							|| world.worldArray[v.col + 1][v.row + 1] instanceof Food) {
						if (v.direction == 0)
							if (AllHexNode[v.col + 1][v.row + 1].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col + 1][v.row + 1].setddd(v.distance + 2, 1, v.directionResult);
								frontier.add(AllHexNode[v.col + 1][v.row + 1]);
							}
						if (v.direction == 1)
							if (AllHexNode[v.col + 1][v.row + 1].distance > v.distance + 1 && v.distance + 1 <= 10) {
								AllHexNode[v.col + 1][v.row + 1].setddd(v.distance + 1, 1, v.directionResult);
								frontier.add(AllHexNode[v.col + 1][v.row + 1]);
							}
						if (v.direction == 2)
							if (AllHexNode[v.col + 1][v.row + 1].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col + 1][v.row + 1].setddd(v.distance + 2, 1, v.directionResult);
								frontier.add(AllHexNode[v.col + 1][v.row + 1]);
							}
					}
				if (world.isValidHex(v.col + 1, v.row))
					if (world.worldArray[v.col + 1][v.row] instanceof EmptySpace
							|| world.worldArray[v.col + 1][v.row] instanceof Food) {
						if (v.direction == 1)
							if (AllHexNode[v.col + 1][v.row].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col + 1][v.row].setddd(v.distance + 2, 2, v.directionResult);
								frontier.add(AllHexNode[v.col + 1][v.row]);
							}
						if (v.direction == 2)
							if (AllHexNode[v.col + 1][v.row].distance > v.distance + 1 && v.distance + 1 <= 10) {
								AllHexNode[v.col + 1][v.row].setddd(v.distance + 1, 2, v.directionResult);
								frontier.add(AllHexNode[v.col + 1][v.row]);
							}
						if (v.direction == 3)
							if (AllHexNode[v.col + 1][v.row].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col + 1][v.row].setddd(v.distance + 2, 2, v.directionResult);
								frontier.add(AllHexNode[v.col + 1][v.row]);
							}
					}
				if (world.isValidHex(v.col, v.row - 1))
					if (world.worldArray[v.col][v.row - 1] instanceof EmptySpace
							|| world.worldArray[v.col][v.row - 1] instanceof Food) {
						if (v.direction == 2)
							if (AllHexNode[v.col][v.row - 1].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col][v.row - 1].setddd(v.distance + 2, 3, v.directionResult);
								frontier.add(AllHexNode[v.col][v.row - 1]);
							}
						if (v.direction == 3)
							if (AllHexNode[v.col][v.row - 1].distance > v.distance + 1 && v.distance + 1 <= 10) {
								AllHexNode[v.col][v.row - 1].setddd(v.distance + 1, 3, v.directionResult);
								frontier.add(AllHexNode[v.col][v.row - 1]);
							}
						if (v.direction == 4)
							if (AllHexNode[v.col][v.row - 1].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col][v.row - 1].setddd(v.distance + 2, 3, v.directionResult);
								frontier.add(AllHexNode[v.col][v.row - 1]);
							}
					}
				if (world.isValidHex(v.col - 1, v.row - 1))
					if (world.worldArray[v.col - 1][v.row - 1] instanceof EmptySpace
							|| world.worldArray[v.col - 1][v.row - 1] instanceof Food) {
						if (v.direction == 3)
							if (AllHexNode[v.col - 1][v.row - 1].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col - 1][v.row - 1].setddd(v.distance + 2, 4, v.directionResult);
								frontier.add(AllHexNode[v.col - 1][v.row - 1]);
							}
						if (v.direction == 4)
							if (AllHexNode[v.col - 1][v.row - 1].distance > v.distance + 1 && v.distance + 1 <= 10) {
								AllHexNode[v.col - 1][v.row - 1].setddd(v.distance + 1, 4, v.directionResult);
								frontier.add(AllHexNode[v.col - 1][v.row - 1]);
							}
						if (v.direction == 5)
							if (AllHexNode[v.col - 1][v.row - 1].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col - 1][v.row - 1].setddd(v.distance + 2, 4, v.directionResult);
								frontier.add(AllHexNode[v.col - 1][v.row - 1]);
							}
					}
				if (world.isValidHex(v.col - 1, v.row))
					if (world.worldArray[v.col - 1][v.row] instanceof EmptySpace
							|| world.worldArray[v.col - 1][v.row] instanceof Food) {
						if (v.direction == 0)
							if (AllHexNode[v.col - 1][v.row].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col - 1][v.row].setddd(v.distance + 2, 5, v.directionResult);
								frontier.add(AllHexNode[v.col - 1][v.row]);
							}
						if (v.direction == 4)
							if (AllHexNode[v.col - 1][v.row].distance > v.distance + 2 && v.distance + 2 <= 10) {
								AllHexNode[v.col - 1][v.row].setddd(v.distance + 2, 5, v.directionResult);
								frontier.add(AllHexNode[v.col - 1][v.row]);
							}
						if (v.direction == 5)
							if (AllHexNode[v.col - 1][v.row].distance > v.distance + 1 && v.distance + 1 <= 10) {
								AllHexNode[v.col - 1][v.row].setddd(v.distance + 1, 5, v.directionResult);
								frontier.add(AllHexNode[v.col - 1][v.row]);
							}
					}
			}
			return 1000000;
		}
	}

	class Node implements Comparator<Node> {
		public int col;
		public int row;
		public int distance;
		public int direction;
		public int directionResult;

		public Node(int col, int row, int distance, int direction, int directionResult) {
			this.col = col;
			this.row = row;
			this.distance = distance;
			this.direction = direction;
			this.directionResult = directionResult;
		}

		public void setddd(int distance, int direction, int directionResult) {
			this.distance = distance;
			this.direction = direction;
			this.directionResult = directionResult;
		}

		@Override
		public int compare(Node node1, Node node2) {
			if (node1.distance < node2.distance)
				return -1;
			if (node1.distance > node2.distance)
				return 1;
			return 0;
		}
	}

	public int random(int bound) {
		synchronized (this) {
			if (bound < 2) {
				return 0;
			}
			Random rand = new Random();
			return rand.nextInt(bound);
		}
	}

	/**
	 * 
	 * @param front
	 *            Move forward if front is true, backward if not
	 * @return True if move was successful
	 */
	public boolean move(boolean front) {
		synchronized (this) {
			mem[4] -= WorldConstants.MOVE_COST * mem[3];
			int[] change = getcoor(direction);
			if (mem[4] <= 0 || ahead(1) != 0 || !world.isValidHex(c + change[0], r + change[1])) {
				return false;
			}
			if (front) {
				world.worldArray[c + change[0]][r + change[1]] = this;
				world.worldArray[c][r] = new EmptySpace();
				c += change[0];
				r += change[1];
				return true;
			} else {
				world.worldArray[c - change[0]][r - change[1]] = this;
				world.worldArray[c][r] = new EmptySpace();
				c -= change[0];
				r -= change[1];
				return true;
			}

		}
	}

	public void waitAction() {
		synchronized (this) {
			mem[4] += mem[3] * WorldConstants.SOLAR_FLUX;
			if (mem[4] > getMaxEnergy()){
				mem[4] = getMaxEnergy();
			}
		}
	}

	/**
	 * 
	 * @param left
	 *            Turn left if left is true, right otherwise
	 */
	public boolean turn(boolean left) {
		synchronized (this) {
			if (mem[4] < mem[3]) {
				mem[4] -= mem[3];
				return false;
			}
			if (left) {
				direction--;
			} else {
				direction++;
			}
			direction = (direction + 6) % 6;
			mem[4] -= mem[3];
			return true;
		}
	}

	public boolean eat() {
		synchronized (this) {
			int[] change = getcoor(direction);
			if (ahead(1) >= -1) {
				mem[4] -= mem[3];
				return false;
			}
			Food food = (Food) world.worldArray[c + change[0]][r + change[1]];
			if (food.getAmount() <= getMaxEnergy() - mem[4]) {
				mem[4] += food.getAmount() - mem[3];
				world.worldArray[c + change[0]][r + change[1]] = new EmptySpace();
			} else {
				food = new Food(food.getAmount() + (mem[4] - getMaxEnergy()));
				world.worldArray[c + change[0]][r + change[1]] = food;
				mem[4] = getMaxEnergy() - mem[3];
			}
			return true;
		}
	}

	public boolean serve(int amount) {
		synchronized (this) {
			if (amount <= 0) {
				return false;
			}
			if (amount >= mem[4] - mem[3]) {
				amount = mem[4] - mem[3];
			}
			Food food = new Food(amount);
			int[] change = getcoor(direction);
			if (!world.isValidHex(c + change[0], r + change[1]) || ahead(1) >= 1) {
				mem[4] -= mem[3] + amount;
				return false;
			}
			if (ahead(1) == 0) {
				world.worldArray[c + change[0]][r + change[1]] = food;
				mem[4] -= mem[3] + amount;
				return true;
			}
			if (ahead(1) < -1) {
				food = new Food(food.amount + amount);
				world.worldArray[c + change[0]][r + change[1]] = food;
				mem[4] -= mem[3] + amount;
				return true;
			}
			return true;
		}
	}

	public boolean attack() {
		synchronized (this) {
			if (mem[4] <= mem[3] * WorldConstants.ATTACK_COST) {
				mem[4] -= mem[3] * WorldConstants.ATTACK_COST;
				return false;
			}
			int[] change = getcoor(direction);
			try {
				if (ahead(1) > 1) {
					int s1 = mem[3];
					int s2 = ((Critter) world.worldArray[c + change[0]][r + change[1]]).getMem(3);
					int o1 = mem[2];
					int d2 = ((Critter) world.worldArray[c + change[0]][r + change[1]]).getMem(1);
					int damage = getDamage(s1, s2, o1, d2);
					((Critter) world.worldArray[c + change[0]][r + change[1]]).attacked(damage);
					mem[4] -= mem[3] * WorldConstants.ATTACK_COST;
					return true;
				}
			} catch (Exception e) {
			}
			mem[4] -= mem[3] * WorldConstants.ATTACK_COST;
			return false;
		}
	}

	private int getDamage(int s1, int s2, int o1, int d2) {
		double x = WorldConstants.DAMAGE_INC * (s1 * o1 - s2 * d2);
		double p = 1 / Math.pow(Math.E, x * -1);
		return (int) (WorldConstants.BASE_DAMAGE * s1 * p);
	}

	private void attacked(int loss) {
		mem[4] -= loss;
	}

	public boolean tag(int amount) {
		synchronized (this) {
			mem[4] -= mem[3];
			if (amount < 0 || amount > 99) {
				return false;
			}
			if (ahead(1) > 0) {
				int[] change = getcoor(direction);
				((Critter) world.worldArray[c + change[0]][r + change[1]]).tagged(amount);
				return true;
			}
			return false;
		}
	}

	private void tagged(int amount) {
		mem[6] = amount;
	}

	public void grow() {
		synchronized (this) {
			mem[3]++;
			mem[4] -= mem[3] * getComplexity() * WorldConstants.GROW_COST;
		}
	}

	private int getComplexity() {
		return ((Rules) rules.root).getRules().size() * WorldConstants.RULE_COST
				+ (mem[1] + mem[2]) * WorldConstants.ABILITY_COST;
	}

	private int getMaxEnergy() {
		return WorldConstants.ENERGY_PER_SIZE * mem[3];
	}

	public boolean bud() {
		synchronized (this) {
			if (mem[4] < WorldConstants.BUD_COST * getComplexity()) {
				mem[4] -= WorldConstants.BUD_COST * getComplexity();
				return false;
			}
			int[] change = getcoor(direction);
			Critter bud;
			if (!world.isValidHex(c - change[0], c - change[1])) {
				mem[4] -= WorldConstants.BUD_COST * getComplexity();
				return false;
			}
			if (world.worldArray[c - change[0]][r - change[1]].getInfo(direction) == 0) {
				bud = new Critter(name, mem[0], mem[1], mem[2], 1, WorldConstants.INITIAL_ENERGY, 0, world, direction,
						c - change[0], r - change[1], rules.clone());
				world.worldArray[c - change[0]][r - change[0]] = bud;
				bud.mutate();
				mem[4] -= WorldConstants.BUD_COST * getComplexity();
				world.critterList.add(bud);
				return true;
			}
		}
		return false;
	}

	public boolean mate() {
		synchronized (this) {
			wannaMate = true;
			Critter child, parent;
			int zero, one, two;
			String name;
			Random rand = new Random();
			if (ahead(1) < 2) {
				mem[4] -= mem[3];
				return false;
			}
			int[] change = getcoor(direction);
			if (!world.isValidHex(c + change[0], r + change[1])) {
				mem[4] -= mem[3];
				return false;
			}
			parent = (Critter) world.worldArray[c + change[0]][r + change[1]];
			if (parent.getMateStatus() && mem[4] >= 5 * getComplexity() && parent.mem[4] >= 5 * parent.getComplexity()
					&& parent.getAppearance(direction) % 10 == 3) {
				ProgramImpl ast = mixRules(rules.clone(), parent.rules.clone());
				name = rand.nextBoolean() ? this.name : parent.name;
				zero = rand.nextBoolean() ? mem[0] : parent.mem[0];
				one = rand.nextBoolean() ? mem[1] : parent.mem[1];
				two = rand.nextBoolean() ? mem[2] : parent.mem[2];
				if (rand.nextBoolean()) {
					if (!world.isValidHex(c + 2 * change[0], r + 2 * change[1])) {
						mem[4] -= mem[3];
						return false;
					}
					if (world.worldArray[c + 2 * change[0]][r + 2 * change[0]].getInfo(0) == 0) {
						child = new Critter(name, zero, one, two, 1, WorldConstants.INITIAL_ENERGY, 0, world,
								rand.nextInt(6), c + 2 * change[0], r + 2 * change[1], ast);
						world.worldArray[c + 2 * change[0]][r + 2 * change[1]] = child;
						mem[4] -= 5 * getComplexity();
						parent.mem[4] -= 5 * parent.getComplexity();
						world.critterList.add(child);
						return true;
					} else {
						mem[4] -= mem[3];
						parent.mem[4] -= parent.mem[3];
						return false;
					}
				} else {
					if (!world.isValidHex(c - change[0], r - change[1])) {
						mem[4] -= mem[3];
						return false;
					}
					if (world.worldArray[c - change[0]][r - change[0]].getInfo(0) == 0) {
						child = new Critter(name, zero, one, two, 1, WorldConstants.INITIAL_ENERGY, 0, world,
								rand.nextInt(6), c - change[0], r - change[1], ast);
						world.worldArray[c - change[0]][r - change[1]] = child;
						mem[4] -= 5 * getComplexity();
						parent.mem[4] -= 5 * parent.getComplexity();
						world.critterList.add(child);
						return true;
					} else {
						mem[4] -= mem[3];
						parent.mem[4] -= parent.mem[3];
						return false;
					}
				}
			} else {
				mem[4] -= mem[3];
				if (parent.getMateStatus())
					parent.mem[4] -= parent.mem[3];
				return false;
			}
		}
	}

	private ProgramImpl mixRules(ProgramImpl clone, ProgramImpl clone2) {
		Random rand = new Random();
		ProgramImpl childRules = new ProgramImpl();
		int times;
		if (rand.nextBoolean()) {
			times = clone.root.rules.size();
		} else {
			times = clone2.root.rules.size();
		}
		for (int i = 0; i < times; i++) {
			while (true) {
				if (rand.nextBoolean()) {
					if (i < clone.root.rules.size()) {
						childRules.root.addRule(clone.root.rules.get(i));
						break;
					}
				} else {
					if (i < clone2.root.rules.size()) {
						childRules.root.addRule(clone2.root.rules.get(i));
						break;
					}
				}
			}
		}
		return childRules;
	}

	public boolean getMateStatus() {
		return wannaMate;
	}

	/**
	 * 
	 * @param dir
	 *            The direction that the observing critter is facing
	 * @return the critter's "size * 100000 + tag * 1000 + posture * 10 +
	 *         direction" the relative direction of this critter to the observer
	 *         critter
	 */
	public int getAppearance(int dir) {
		int size = mem[3];
		int tag = mem[6];
		int posture = mem[7];
		int direction = (6 - this.direction - dir + 12) % 6;
		return size * 100000 + tag * 1000 + posture * 10 + direction;
	}

	public String toString() {
		return String.valueOf(direction);
	}

	@Override
	public int getInfo(int dir) {
		return getAppearance(dir);
	}

	public int getMem(int index) {
		if (index >= mem[0] || index < 0) {
			return 0;
		}
		return mem[index];
	}

	/**
	 * A 25% chance to mutate once by calling the helper method, 6.25% to mutate
	 * twice, etc.
	 */
	public void mutate() {
		synchronized (this) {
			Random rand = new Random();
			int mutate = rand.nextInt(4);
			while (mutate == 0) {
				mutateHelper();
				mutate = rand.nextInt(4);
			}
		}
	}

	// copied from main.java from A4
	private void mutateHelper() {
		synchronized (this) {
			Random rand = new Random();
			int mutateType = rand.nextInt(5);
			if (rules.size() >= 14) {
				mutateType = rand.nextInt(6);
			}
			int index = rand.nextInt(rules.size());
			Mutation m = null;
			switch (mutateType) {
			case 0:
				m = MutationFactory.getDuplicate();
				m.addRules(rules.root);
				m.mutate(index);
				break;
			case 1:
				m = MutationFactory.getInsert();
				m.addRules(rules.root);
				m.mutate(index);
				break;
			case 5:
				m = MutationFactory.getRemove();
				m.addRules(rules.root);
				m.mutate(index);
				break;
			case 3:
				m = MutationFactory.getReplace();
				m.addRules(rules.root);
				m.mutate(index);
				break;
			case 4:
				m = MutationFactory.getSwap();
				m.addRules(rules.root);
				m.mutate(index);
				break;
			case 2:
				m = MutationFactory.getTransform();
				m.addRules(rules.root);
				m.mutate(index);
				break;
			}
		}
	}

	public void advanceTime() {
		synchronized (this) {
			wannaMate = false;
			Outcome o = i.interpret(this.rules);
			if (o.executed() == false) {
				waitAction();
			}
			if (o.lastRule() == null || o.lastRule().equals("")) {
				waitAction();
				this.lastRule = null;
			} else
				this.lastRule = o.lastRule();
		}
	}

	private int[] getcoor(int direction) {
		int[] temp = new int[2];
		direction = (direction % 6 + 6) % 6;
		switch (direction) {
		case 0:
			temp[0] = 0;
			temp[1] = 1;
			break;
		case 1:
			temp[0] = 1;
			temp[1] = 1;
			break;
		case 2:
			temp[0] = 1;
			temp[1] = 0;
			break;
		case 3:
			temp[0] = 0;
			temp[1] = -1;
			break;
		case 4:
			temp[0] = -1;
			temp[1] = -1;
			break;
		case 5:
			temp[0] = -1;
			temp[1] = 0;
			break;
		}
		return temp;
	}

	public Critter clone() {
		return new Critter(name, mem[0], mem[1], mem[2], mem[3], mem[4], mem[7], world, direction, c, r, rules.clone());
	}
}