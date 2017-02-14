package client.SimulationGUI;

public class GetWorldSubsection {
	    public int current_timestep;
	    public int current_version_number;// represents the version number of the world. This gets updates every time that something changes in the world - e.g. when a critter steps, gets added, gets deleted, etc. It does not necessarily correspond with the current_timestep value.
	    public int update_since;// only needed in the response if the update_since query string was used in the request
	    public int rate;
	    public String name;
	    public int population;
	    public int rows;
	    public int cols; // the size of the world
	    public int[] dead_critters; // the critters that have died in the world since update_since
	    /*"state": [{
	        "row": 3, "col": 5,
	        "type": "rock",
	    }, {
	        "row": 6, "col": 7,
	        "type": "food",
	        "value": 10
	    }, {
	        "type":"critter",
	        "id": 1,
	        "species_id": "basic-critter",
	        "row":5,"col":7,"direction":1,
	        "mem":[1,2,3,4...]
	    }, {
	        "type":"critter",
	        "id": 2,
	        "species_id": "acidic-critter",
	        "program":"[the critter's entire program]",
	        "row":3,"col":3,"direction":2,
	        "mem":[1,2,3,4...],
	        "recently_executed_rule":4
	    }, {  // if some grid changed from having something to having nothing, then it has the type "nothing"
	        "type": "nothing",
	        "row": 6, "col": 3
	    }, ...]
	}*/
}
