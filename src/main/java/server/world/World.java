package server.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class World {
	public String worldName;
	public WorldObject[][] worldArray;
	public int numSteps = 0;
	public ArrayList<Critter> critterList = new ArrayList<Critter>();
	public int version;
	public int rows;
	public int cols;
	public HashMap<Integer, Integer> dead = new HashMap<Integer, Integer>();

	/**
	 * Starts new simulation with world with col 50 row 68
	 */
	public World() {
		worldName = "Initial World";
		worldArray = new WorldObject[WorldConstants.COLUMNS][WorldConstants.ROWS];
		int actualRow = worldArray[0].length - (int) Math.floor(worldArray.length / 2);
		for (int i = 0; i < worldArray.length; i++) {
			for (int j = (int) Math.floor((i + 1) / 2); j < (int) Math.floor((i + 1) / 2) + actualRow; j++)
				worldArray[i][j] = new EmptySpace();
		}
		// generate rocks
		Random random = new Random();
		int rockNum = (int) ((random.nextFloat() * 0.2 + 0.05) * (WorldConstants.COLUMNS * WorldConstants.ROWS));
		for (int i = 0; i < rockNum; i++) {
			int col = random.nextInt(WorldConstants.COLUMNS);
			int row = random.nextInt(WorldConstants.ROWS);
			if (isValidHex(col, row))
				worldArray[col][row] = new Rock();
		}
		version = 0;
		rows = WorldConstants.ROWS;
		cols = WorldConstants.COLUMNS;
	}

	/**
	 * Starts new simulation with world col and row number.
	 *
	 * @param c
	 *            colomn number
	 * @param r
	 *            row number
	 */
	public World(int c, int r) {
		worldArray = new WorldObject[c][r];
		int actualRow = worldArray[0].length - (int) Math.floor(worldArray.length / 2);
		for (int i = 0; i < c; i++) {
			for (int j = (int) Math.floor((i + 1) / 2); j < (int) Math.floor((i + 1) / 2) + actualRow; j++)
				worldArray[i][j] = new EmptySpace();
		}
		version = 0;
		rows = r;
		cols = c;
	}

	/**
	 * return the current alive critter number
	 *
	 * @return the current alive critter number
	 */
	public int getCritterNumber() {
		return critterList.size();
	}

	/**
	 * return the number of steps passed
	 *
	 * @return the number of steps passed
	 */
	public int getnumSteps() {
		return numSteps;
	}

	/**
	 * Add a valid critter, the col and row parameter can be get inside the
	 * critter object
	 * 
	 * @throws AddCritterException
	 *
	 */

	public boolean addCritter(Critter c){
		if (!isValidHex(c.c, c.r)) {
			return false;
		} else {
			if (worldArray[c.c][c.r] instanceof EmptySpace) {
				worldArray[c.c][c.r] = c;
				critterList.add(c);
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a rock object into the world with the col and row number received
	 *
	 * @param c
	 *            colomn number
	 * @param r
	 *            row number
	 */
	public boolean addRock(int c, int r) {
		if (!isValidHex(c, r)) {
			return false;
		} else {
			if (worldArray[c][r] instanceof EmptySpace) {
				worldArray[c][r] = new Rock();
				version++;
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a food object into the world with the col, row and food number
	 * received
	 *
	 * @param c
	 *            colomn number
	 * @param r
	 *            row number
	 * @param amount
	 *            the food amount
	 */
	public boolean addFood(int c, int r, int amount) {
		if (!isValidHex(c, r)) {
			return false;
		} else {
			if (worldArray[c][r] instanceof EmptySpace) {
				worldArray[c][r] = new Food(amount);
				version++;
				return true;
			}
		}
		return false;
	}

	/**
	 * return the object in the position c, r in the world return null if out of
	 * bound
	 *
	 * @return the object in the position c, r in the world
	 */
	public Object hexAt(int c, int r) {
		return worldArray[c][r];
	}

	/**
	 * print the world map to the system console
	 * 
	 */
	public void printWorldMap() {
		int actualRow = worldArray[0].length - (int) Math.floor(worldArray.length / 2);
		int actualCol = worldArray.length;
		int printTime = 0;
		boolean printOddRow = true;
		if (actualCol % 2 == 0) // print row starts from 1
		{
			printTime = actualRow * 2;
		} else { // print row starts from 0
			printTime = actualRow * 2 - 1;
			printOddRow = false;
		}
		int startRowForOdd = (actualCol % 2 == 1) ? actualRow - 1 : actualRow;
		int startRowForEven = actualRow - 1;
		while (printTime > 0) {
			if (printOddRow) {// print Odd Row
				System.out.print("  ");
				for (int i = 1; i < actualCol; i = i + 2) {
					System.out.print(worldArray[i][startRowForOdd + (i + 1) / 2 - 1] + "   ");
				}
				System.out.println();
				startRowForOdd--;
			} else { // print Even Row
				for (int i = 0; i < actualCol; i = i + 2) {
					System.out.print(worldArray[i][startRowForEven + i / 2] + "   ");
				}
				System.out.println();
				startRowForEven--;
			}
			printOddRow = !printOddRow;
			printTime--;
		}
	}

	/**
	 * advance the world by advancing n steps
	 * 
	 * @param the
	 *            steps the world needs to advance
	 * 
	 */
	public void advanceTime(int steps) {
		for (int i = 0; i < steps; i++) {
			for (int j = 0; j < critterList.size(); j++) {
				critterList.get(j).advanceTime();
			}

			for (int j = 0; j < critterList.size(); j++) {
				if (critterList.get(j).getMem(4) <= 0) {
					worldArray[critterList.get(j).c][critterList.get(j).r] = new Food(
							critterList.get(j).getMem(3) * WorldConstants.FOOD_PER_SIZE);
					dead.put(version, critterList.get(j).id);
					critterList.remove(j);
					j--;
				}
			}
			version++;
		}
		numSteps += steps;
	}

	/**
	 * decide if the position of (c,r) passed to the function is a valid hex(not
	 * out of bound) in the world
	 * 
	 * @param c
	 *            colomn number
	 * 
	 * @param r
	 *            row number
	 * 
	 * @return whether the col and row parameter is a valid hex in the world
	 */
	public boolean isValidHex(int c, int r) {
		if (c < 0 || r < 0 || c >= worldArray.length || r >= worldArray[0].length) {
			return false;
		}
		int actualRow = worldArray[0].length - (int) Math.floor(worldArray.length / 2);
		if (worldArray.length % 2 == 0) { // c is even
			if ((r >= (int) Math.floor((c + 1) / 2)) && (r < (int) Math.floor((c + 1) / 2) + actualRow))
				return true;
		} else { // c is odd
			if (c % 2 == 0) {
				if ((r >= (int) Math.floor((c + 1) / 2)) && (r < (int) Math.floor((c + 1) / 2) + actualRow))
					return true;
			} else {
				if ((r >= (int) Math.floor((c + 1) / 2)) && (r < (int) Math.floor((c + 1) / 2) + actualRow - 1))
					return true;
			}
		}
		return false;
	}

	/**
	 * decide if the world has empty space
	 * 
	 * @return whether the world has no empty space
	 */
	public boolean isFull() {
		for (int c = 0; c < worldArray.length; c++)
			for (int r = 0; r < worldArray[0].length; r++)
				if (isValidHex(c, r) == true)
					if (hexAt(c, r) instanceof EmptySpace)
						return false;
		return true;
	}

	public World(String name, WorldObject[][] worldArray, ArrayList<Critter> clist) {
		worldName = name;
		this.worldArray = worldArray;
		critterList = clist;
	}

	public World clone() {
		WorldObject[][] temp = new WorldObject[worldArray.length][worldArray[0].length];
		for (int i = 0; i < worldArray.length; i++) {
			for (int j = 0; j < worldArray[0].length; j++) {
				if (worldArray[i][j] != null) {
					if (worldArray[i][j] instanceof Critter) {
						temp[i][j] = worldArray[i][j].clone();
					} else {
						temp[i][j] = worldArray[i][j];
					}
				}
			}
		}
		return new World(worldName, temp, (ArrayList<Critter>) critterList.clone());
	}

	public boolean remove(int id) {
		for (int i = 0; i < critterList.size(); i++) {
			if (critterList.get(i).id == id) {
				Critter temp = critterList.get(i);
				worldArray[temp.c][temp.r] = new EmptySpace();
				critterList.remove(i);
				dead.put(version, critterList.get(i).id);
				version++;
				return true;
			}
		}
		return false;
	}
}
