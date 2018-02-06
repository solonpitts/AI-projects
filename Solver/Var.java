import java.util.ArrayList;

public class Var implements Comparable<Var>{
private ArrayList<Integer> domain;
private ArrayList<Integer> currentDomain;
private char name;
private int constraining;

	public Var(char c)
	{
		name = c;
		constraining = 0;
		domain = new ArrayList<Integer>();
		currentDomain = new ArrayList<Integer>();
	}
	
	public void addToDomain(int d)
	{
		domain.add(d);
		currentDomain.add(d);
	}
	
	public char getName()
	{
		return name;
	}
	
	public ArrayList<Integer> getDomain()
	{
		return domain;
	}
	
	public ArrayList<Integer> getCurrentDomain()
	{
		return currentDomain;
	}
	
	public void setDomain(ArrayList<Integer> newDomain)
	{
		currentDomain = newDomain;
	}
	
	public void setNumConstraining(int num)
	{
		constraining = num;
	}
	
	public void resetDomain()
	{
		currentDomain = domain;
	}
	
	public int getNumConstraining()
	{
		return constraining;
	}
	
	public int compareTo(Var v)
	{
		if(currentDomain.size()-v.currentDomain.size() ==0)
			if(v.constraining - constraining==0)
				return name-v.name;
			else 
				return v.constraining - constraining;
		
		return currentDomain.size()-v.currentDomain.size();
	}
	
	public boolean equals(Var v)
	{
		return name == v.name;
	}

}
