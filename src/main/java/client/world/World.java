package client.world;

public class World {
	public WorldObject[][] worldArray;
	public int rows;
	public int cols;

	public World(int cols, int rows) {
		worldArray = new WorldObject[cols][rows];
		this.cols = cols;
		this.rows = rows;
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
				if (isValidHex(i, j))
					if (worldArray[i][j] == null)
						worldArray[i][j] = new EmptySpace();
	}

	/**
	 * Add a valid critter, the col and row parameter can be get inside the
	 * critter object
	 *
	 */
	public void addCritter(ClientCritter c) {
		worldArray[c.c][c.r] = c;
	}

	/**
	 * Add a rock object into the world with the col and row number received
	 *
	 * @param c
	 *            colomn number
	 * @param r
	 *            row number
	 */
	public void addRock(int c, int r) {
		worldArray[c][r] = new Rock();

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
	public void addFood(int c, int r, int amount) {
		worldArray[c][r] = new Food(amount);
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
}
