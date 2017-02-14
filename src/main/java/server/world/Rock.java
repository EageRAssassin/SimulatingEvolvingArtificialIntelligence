package server.world;

public class Rock implements WorldObject
{
    /**
     * print out the symbol for a rock in the map
     * 
     * @return # symbol for a rock
     */
    public String toString()
    {
        return "#";
    }

    @Override
    public int getInfo(int dir)
    {
        return -1;
    }
    public Rock clone(){
    	return new Rock();
    }
}
