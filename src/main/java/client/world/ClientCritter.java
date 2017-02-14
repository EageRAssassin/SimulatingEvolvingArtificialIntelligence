package client.world;

public class ClientCritter implements WorldObject {
	public int c, r;
	public int direction;
	public int id;
	public int size;
	public String program;
	public int lastRule;

	public ClientCritter(int c, int r, int direction, int id, int size, String program, int lastRule) {
		this.direction = direction;
		this.c = c;
		this.r = r;
		this.id = id;
		this.size = size;
		this.program = program;
		this.lastRule = lastRule;
	}

	@Override
	public int getInfo(int dir) {
		return 0;
	}
}