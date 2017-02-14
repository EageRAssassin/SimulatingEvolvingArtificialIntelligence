package client.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class WorldConstants {
	// A class storing the constants
	public static final int BASE_DAMAGE; // The multiplier for all damage done
											// by attacking
	public static final double DAMAGE_INC; // Controls how quickly increased
											// offensive or defensive ability
											// affects damage
	public static final int ENERGY_PER_SIZE; // How much energy a critter can
												// have per point of size
	public static final int FOOD_PER_SIZE; // How much food is created per point
											// of size when a critter dies
	public static final int MAX_SMELL_DISTANCE; // Maximum distance at which
												// food can be sensed
	public static final int ROCK_VALUE; // The value reported when a rock is
										// sensed
	public static final int COLUMNS; // Default number of columns in the world
										// map
	public static final int ROWS; // Default number of rows in the world map
	public static final int MAX_RULES_PER_TURN; // The maximum number of rules
												// that can be run per critter
												// turn
	public static final int SOLAR_FLUX; // Energy gained from sun by doing
										// nothing
	public static final int MOVE_COST; // Energy cost of moving (per unit size)
	public static final int ATTACK_COST; // Energy cost of attacking (per unit
											// size)
	public static final int GROW_COST; // Energy cost of growing (per size and
										// complexity)}
	public static final int BUD_COST; // Energy cost of budding (per unit
										// complexity)
	public static final int MATE_COST; // Energy cost of successful mating (per
										// unit complexity)
	public static final int RULE_COST; // Complexity cost of having a rule
	public static final int ABILITY_COST; // Complexity cost of having an
											// ability point}
	public static final int INITIAL_ENERGY; // Energy of a newly birthed critter
	public static final int MIN_MEMORY; // Minimum number of memory entries in a
										// critter

	static Scanner scanner;

	static {
		try {
			scanner = new Scanner(new File("constants.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Constants not found!");
		}
		scanner.findInLine("BASE_DAMAGE");
		BASE_DAMAGE = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("DAMAGE_INC");
		DAMAGE_INC = scanner.nextDouble();
		scanner.nextLine();
		scanner.findInLine("ENERGY_PER_SIZE");
		ENERGY_PER_SIZE = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("FOOD_PER_SIZE");
		FOOD_PER_SIZE = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("MAX_SMELL_DISTANCE");
		MAX_SMELL_DISTANCE = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("ROCK_VALUE");
		ROCK_VALUE = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("COLUMNS");
		COLUMNS = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("ROWS");
		ROWS = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("MAX_RULES_PER_TURN");
		MAX_RULES_PER_TURN = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("SOLAR_FLUX");
		SOLAR_FLUX = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("MOVE_COST");
		MOVE_COST = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("ATTACK_COST");
		ATTACK_COST = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("GROW_COST");
		GROW_COST = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("BUD_COST");
		BUD_COST = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("MATE_COST");
		MATE_COST = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("RULE_COST");
		RULE_COST = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("ABILITY_COST");
		ABILITY_COST = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("INITIAL_ENERGY");
		INITIAL_ENERGY = scanner.nextInt();
		scanner.nextLine();
		scanner.findInLine("MIN_MEMORY");
		MIN_MEMORY = scanner.nextInt();
		scanner.nextLine();
	}

}
