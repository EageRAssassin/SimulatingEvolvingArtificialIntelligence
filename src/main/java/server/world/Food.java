package server.world;

public class Food implements WorldObject
{
    int amount;

    public Food(int amount)
    {
        this.amount = amount;
    }

    public int getAmount()
    {
        return amount;
    }

    public void addAmount(int amount)
    {
        this.amount += amount;
    }

    public String toString()
    {
        return "F";
    }

    @Override
    public int getInfo(int dir)
    {
        return (amount + 1) * -1;
    }
    
    public Food clone(){
    	return new Food(amount);
    }
}
