package client.SimulationGUI;
import server.world.Critter;

public class PCInfo implements OInfo{
	public String type;
	public int id;
	public String species_id;
	public int row, col, direction;
	public int[] mem;
}
