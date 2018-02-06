import java.util.*;
public class Clause implements Comparable<Clause>{
	private ArrayList<String> variables;
	private ArrayList<Boolean> negation;
	private int lastResolution;
	//the index of the first parent
	private int parentA;
	//the index of the second parent
	private int parentB;
	//the index of this clause
	private int index;
	//only true when you've reached the end of the proof
	public boolean isfalse;
	
	/* Constructor
	 * @param i the index of this clause
	 * @param a the index of the first parent of this clause
	 * @param b the index of the second parent of this clause
	 * @param clause the String version of the entire clause(variables and negations)
	 * @param f whether or not this clause is the final clause "False"
	 * */
	public Clause(int i,int a, int b, String clause, boolean f)
	{
		variables = new ArrayList<String>();
		negation = new ArrayList<Boolean>();
		lastResolution = 0;
		parentA = a;
		parentB = b;
		index = i;
		isfalse = f;
		parse(clause);
	}
	
	/*returns the indices of the parents as an int[]
	 * */
	public int[] getParents()
	{
		//changed from 0 to -1
		if (parentA == -1)
			return null;
		int[] parents = new int[2];
		parents[0] = parentA;
		parents[1] = parentB;
		return parents;
	}
	
	/* Takes the string that was passed to the clause and turns it into an array of variables
	 * and a parallel array of negations
	 * */
	private void parse(String clause)
	{
		Scanner scan = new Scanner(clause);
		while(scan.hasNext())	
		{
			String next = scan.next();
			if (next.charAt(0)=='~')
			{
				negation.add(true);
				variables.add(next.substring(1));
			}
			else
			{
				negation.add(false);
				variables.add(next);
			}
			
		}
		scan.close();
	}
	
	/*Returns whether or not this clause is "False". Meaning, the end of the solution.
	 * */
	public boolean isFalse()
	{
		return isfalse;
	}
	
	/*Returns the index of the last resolved clause. Resolve will never attempt to resolve 
	 * clauses before this index.
	 * */
	public int getLastResolution()
	{
		return lastResolution;
	}
	
	/*Tries to resolve this clause with the given clause, and returns either the resulting clause or null
	 * @param c the clause to resolve
	 * @param i the first parent's index
	 * @param j the second parent's index
	 * @param ind the index of the resulting clause
	 * */
	public Clause resolve(Clause c,int i, int j, int ind)
	{
		String s = "";
		//keeps track of the last clause that was resolved
		lastResolution = j;
		Clause newClause;
		//check if the two clauses resolve to false
		if(negation.size() == 1 && c.negation.size() == 1)
		{
			if(negation.get(0) != c.negation.get(0))
				if(variables.get(0).equals(c.variables.get(0)))
					s = "False";
		}
		else //check to see if the clauses can resolve
			for(int n = 0; n < negation.size(); n++)
			{
				String var = variables.get(n);
				if(c.variables.contains(var))
				{
					if(negation.get(n) != c.negation.get(c.variables.indexOf(var)))
					{
						for(int x = 0; x < negation.size(); x++)
						{
							if(x!=n)
							{
								if(negation.get(x))
									s = s + "~";
								s = s + variables.get(x);
								s = s + " ";
							}
						}
						for(int y = 0; y < c.negation.size(); y++)
						{
							if(y!=c.variables.indexOf(var))
							{
								if(c.negation.get(y))
									s = s + "~";
								s = s + c.variables.get(y);
								s = s + " ";
							}
						}
						s = s.substring(0, s.length()-1);
						newClause = new Clause(ind,i,j,s,false);
						if (newClause.simplify(var) == 1); //if the clause is valid, return it
							return newClause;
					}
				}
			}
		
		
		if(s.equals("False")) // the clauses resolved to false
			newClause = new Clause(ind,i,j,s,true);
		else // the clauses weren't able to be resolved
			newClause = null;	
		
		return newClause;
	}
	
	
	/*
	 * Removes any instances of  (s V ~s) or (s V s) and detects if there are any more
	 * @param s the variable that was resolved out of the clause, (ie (s V ~s) in the clause)
	 * */
	public int simplify(String s)
	{
		while(variables.contains(s))
		{
			negation.remove(variables.indexOf(s));
			variables.remove(s);
		}
		int size = negation.size();
		for(int i = 0; i < size; i++)
		{
			for(int j = i+1; j < size; j++)
			{
				if(variables.get(i).equals(variables.get(j)))
				{
					if(negation.get(i) == negation.get(j))
					{
						negation.remove(j);
						variables.remove(j);
						j--;
						size--;
					}
					//if another case of (x V ~x)
					else if(negation.get(i) != negation.get(j))
					{
						//returns -1 b/c the clause is not valid
						return -1;
					}
				}
			}	
		}
		return 1;
	}
	
	/* Returns the entire clause in string form
	 * */
	public String getClause()
	{
		String s = "";
		for(int i = 0; i < negation.size(); i++)
		{
			if(negation.get(i))
				s = s + "~";
			s = s + variables.get(i);
			s = s + " ";
		}
		return s.substring(0, s.length()-1);
	}
	
	/* Compares the indexes of the two clauses. That is, the order in which they were discovered
	 * @param c the clause to compare to
	 * */
	public int compareTo(Clause c)
	{
		return index - c.index;
	}
	
	/* Checks if the given clause is the same by checking every variable against every variable
	 * @param c the the clause to compare against
	 * */
	public boolean isEqual(Clause c)
	{
		if(c.variables.size()==variables.size())
		{
			int same = 0;
			for(int i = 0; i <variables.size(); i++)
			{
				for(int j =0; j < variables.size();j++)
				{
					if(variables.get(i).equals(c.variables.get(j)) && negation.get(i) == c.negation.get(j))
						same++;
				}
			}
			if(same == variables.size())
				return true;
		}
		return false;
	}
}
