package server.world;

public interface WorldObject
{
    /**
     * get the info of the object in "dir" direction of the object
     * 
     * @param dir
     *            the direction
     * 
     * @return (amount + 1) * -1 for food object, 1 for rock object, 0 for empty
     *         space, invoke getAppearance() function for critters
     */
    public int getInfo(int dir);
    public WorldObject clone();
}
