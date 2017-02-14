package client.SimulationGUI;

public class PostCreateObject {
	public int row = 0; // The row at which to create the entity
	public int col = 0; // The col at which to create the entity
	public String type = ""; // The type of entity to create. May be one of "food" or "rock"
	public int amount = 0;// If the entity is "food", the amount of food to place.
}
