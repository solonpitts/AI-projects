import java.util.*;
import java.io.*;

public class Resolve {
	
	private static ArrayList<Clause> clauses;
	private static String fileName;
	private static PriorityQueue<Clause> solution;
	
	public static void main(String[] args) {
		clauses = new ArrayList<Clause>();
		solution = new PriorityQueue<Clause>();
		//fileName = "task4.in";
		fileName = args[0];
		try{
		Scanner scan = new Scanner(new File(fileName));
		String out = fileName.substring(0,fileName.indexOf('.'));
		PrintWriter writer = new PrintWriter(out + ".out");
		
		while(scan.hasNextLine())
		{
			clauses.add(new Clause(clauses.size(),-1,-1,scan.nextLine(),false));
		}
		scan.close();
		
		//begin resolution
		boolean foundClause = true;
		boolean done = false;
		while(foundClause)
		{
			foundClause = false;
			for(int i = 0; i < clauses.size(); i++)
			{
				for(int j = clauses.get(i).getLastResolution()+1; j < clauses.size(); j++)
				{
					Clause newClause = clauses.get(i).resolve(clauses.get(j),i,j,clauses.size());

					if(newClause != null)
					{
						//if the clause is false, then we're done
						if(newClause.isfalse)
						{
							clauses.add(newClause);
							queueClauses(newClause);
							//exits the loops
							i = clauses.size();
							j = clauses.size();
							done = true;
							foundClause = false;
						}
						else	
						{
							//only add clause if it's not already in the list
							boolean b = true;
							for(int n = 0; n < clauses.size(); n++)
							{
								if(clauses.get(n).isEqual(newClause))
									b = false;
							}
							if(b)
							{
								foundClause = true;
								clauses.add(newClause);
								//System.out.println("found clause: " + newClause.getClause());
							}
						}
					}
						
				}
			}
		}
		if(!done)	//Failure
			writer.println("Failure");	
		else		//Solution
		{
			//print the solution
			while(!solution.isEmpty())
			{
				
				Clause c = solution.poll();
				int[] parents = c.getParents();
				if(parents != null)
					writer.println(clauses.indexOf(c)+1 + ". " +c.getClause() + "\t\t{" + (parents[0]+1) + "," + (parents[1]+1) + "}");
				else
					writer.println(clauses.indexOf(c)+1 + ". " +c.getClause() + "\t\t{}");
			}
			writer.println("Size of Final clause set: " + clauses.size());
		}
		writer.close();
		}
	    catch(FileNotFoundException e) {
	        System.out.println("Unable to open file: '" + fileName + "'");                
	    }

	}
	
	//Recursively add all the clauses in the solution tree to the queue
	private static void queueClauses(Clause c)
	{
		
		int[] parents = c.getParents();
		if(parents != null)
		{
			queueClauses(clauses.get(parents[0]));
			queueClauses(clauses.get(parents[1]));
		}
		if(!solution.contains(c))
			solution.add(c);	
		 
		
	}

}
