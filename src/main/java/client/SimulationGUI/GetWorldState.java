package client.SimulationGUI;

import client.SimulationGUI.ObjectInfo;

public class GetWorldState {
	    public int current_timestep;
	    // represents the version number of the world. This gets
	    // updates every time that something changes in the world
	    // - e.g. when a critter steps, gets added, gets deleted,
	    // etc. It does not necessarily correspond with the
	    // current_timestep value.
	    public int current_version_number;
	    // The minimum version of the world the client must have
	    // such that when combined with the updates, they will have
	    // a fully updated copy of the world. In most cases this will
	    // be the same as the update_since field provided in the
	    // request.  The returned update_since value will always be
	    // less than or equal to the requested update_since value.
	    public int update_since;
	    public int rate;
	    public String name;
	    public int population; // this is the number of critters
	    public int rows;
	    public int cols; // the size of the world
	    public int[] dead_critters; // the critters that have died in the world since update_since
		public ObjectInfo[] state;
}
