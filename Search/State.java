
public class State implements Comparable<State>{
	
private State parent;
private String value;
private int move;
//holds the value of f which could be: g, h, or g + h
private int f;
private int depth;
private int cost;
private int totalCost;
//for FIFO tie break
private int tieBreaker;

	/*See assignments*/
	public State(String s, State p, int m, int function, int d, int c, int n)
	{
		parent = p;
		value = s;
		move = m;
		f = function;
		depth = d;
		cost = c;
		//if the parent is not the root, then the total cost = this state's cost + its parents total cost
		if(p!=null)
			totalCost = c + p.totalCost;
		else
			totalCost = 0;
		tieBreaker = n;
	}
	/*All getters*/
	public String getValue()
	{
		return value;
	}
	public State getParent()
	{
		return parent;
	}
	public int getDepth()
	{
		return depth;
	}
	public int getTotalCost()
	{
		return totalCost;
	}
	public int getCost()
	{
		return cost;
	}
	public boolean hasParent()
	{
		if (parent != null)
			return true;
		return false;
	}
	public int getMove()
	{
		return move;
	}
	/* Compare the values of f, and break ties differently depending on the cost flag.
	 * The tiebreaker method used when cost is set is FIFO, tieBreaker keeping track of the order added to the queue. 
	 * */
	public int compareTo(State s)
	{
		//if -cost
		if(cost != 0)
			if(f-s.f == 0)
				return tieBreaker-s.tieBreaker;
			else
				return f-s.f;
		//no cost flag
		if(f-s.f ==0)
			if(depth-s.depth==0)
				return move-s.move;
			else 
				return depth-s.depth;
		
		return f - s.f;
	}
	
}
