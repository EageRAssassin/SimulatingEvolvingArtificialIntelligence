package client.world;

public class EmptySpace implements WorldObject
{
    public String toString()
    {
        return "-";
    }

    @Override
    public int getInfo(int dir)
    {
        return 0;
    }
}
