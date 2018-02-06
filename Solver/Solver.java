import java.util.*;
import java.io.*;


public class Solver {
private static ArrayList<Var> vars;
private static ArrayList<String> cons;
private static ArrayList<Character> toPrint;
private static boolean forward;
private static int counter;

	public static void main(String[] args)throws Exception {
		counter = 0;
		vars = new ArrayList<Var>();
		cons = new ArrayList<String>();
		toPrint = new ArrayList<Character>();
		
		Scanner varScan = new Scanner(new File(args[0]));
		Scanner conScan = new Scanner(new File(args[1]));
		
		while(varScan.hasNextLine())
		{
			String next = varScan.nextLine();
			
			Var var = new Var(next.charAt(0));
			Scanner scan = new Scanner(next);
			scan.next();
			while(scan.hasNextInt())
			{
				var.addToDomain(scan.nextInt());
			}
			vars.add(var);
			scan.close();
		}
		
		while(conScan.hasNextLine())
		{
			cons.add(conScan.nextLine());
		}
		if(args[2].equals("fc"))
			forward = true;
		else
			forward = false;
		varScan.close();
		conScan.close();

		backtrackingSearch(new HashMap<Var,Integer>());
	}
	
	//recursive
	private static boolean backtrackingSearch(HashMap<Var,Integer> assignments)
	{ 
		
		if(counter > 30)
			return false;
		if(assignments.size()==vars.size() && checkConstraints(assignments))
		{
			for(char c : toPrint) {
				Var v = new Var(c);
				for(Var var : vars)
					if(var.getName() == c)
						v = var;
				System.out.print(c + "=" + assignments.get(v) + ", ");
			}
			System.out.println(" solution");
			return true;
		}
		Var var = getUnassignedVar(assignments);
		if(var!=null)
		for (int i: orderDomainValues(var, assignments))
		{
			resetDomains();
			if(checkConstraints(assignments))
			{
				toPrint.add(var.getName());
				assignments.put(var, i);
				boolean result = backtrackingSearch(assignments);
				if (result)
					return result;
				assignments.remove(var);
				toPrint.remove(toPrint.indexOf(var.getName()));
			}
		}
		counter++;
		if(counter > 30)
			return false;
		for(char c : toPrint) {
			Var v = new Var(c);
			for(Var va : vars)
				if(va.getName() == c)
					v = va;
			System.out.print(c + "=" + assignments.get(v) + ", ");
		}
		System.out.println(" failure");
		return false;
	}

	private static void resetDomains()
	{
		for(Var v: vars)
		{
			v.setNumConstraining(0);
			v.resetDomain();
		}
	}
	
	private static boolean checkConstraints(Map<Var,Integer> assignments)
	{
		for(String con : cons)
		{
			char c1 = con.charAt(0);
			char c2 = con.charAt(4);
			Var v1 = new Var(c1);
			Var v2 = new Var(c2);
			for(Var v: vars)
			{
				if(v.getName() == c1)
					v1 = v;
				if(v.getName() == c2)
					v2 = v;
			}
			if(assignments.containsKey(v1) && assignments.containsKey(v2)){
			char constraint = con.charAt(2);
			switch(constraint){
			case('='): if(assignments.get(v1) != assignments.get(v2))return false;
				break;
			case('!'): if(assignments.get(v1) == assignments.get(v2))return false;
				break;
			case('>'): if(assignments.get(v1) <= assignments.get(v2))return false;
				break;
			case('<'): if(assignments.get(v1) >= assignments.get(v2))return false;
				break;
			}}
		}
		return true;
	}
	
	/*
	 * @param constraining is the assignment of the constraining variable
	 * @param constrained is the variable to be constrained
	 * @param constraint is the type of constraint to perform
	 * possible problem = if domain passed by reference
	 */
	private static void constrain(int constraining, Var constrained, char constraint)
	{
		ArrayList<Integer> domain = new ArrayList<Integer>();
				domain.addAll(constrained.getCurrentDomain());
				
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
				
		switch(constraint){
			case('>'):
			{
				for(int i : domain)
				{
					if(i <= constraining)
						toRemove.add(i);
				}
			}
			break;
			case('<'):
			{
				for(int i : domain)
				{
					if(i >= constraining)
						toRemove.add(i);
				}
			}
			break;
			case('='):
			{
				for(int i : domain)
				{
					if(i != constraining)
						toRemove.add(i);
				}
			}
			break;
			case('!'):
			{
				for(int i : domain)
				{
					if(i == constraining)
						toRemove.add(i);
				}
			}
			break;
		}
		domain.removeAll(toRemove);
		constrained.setDomain(domain);
	}
	
	private static Var getUnassignedVar(Map<Var,Integer> assignments)
	{
		ArrayList<Var> unassigned = new ArrayList<Var>();
		for(Var v : vars)
			if(!assignments.containsKey(v))
				unassigned.add(v);
		for(String con : cons)
		{
			char c1 = con.charAt(0);
			char c2 = con.charAt(4);
			Var v1 = new Var(c1);
			Var v2 = new Var(c2);
			for(Var v: vars)
			{
				if(v.getName() == c1)
					v1 = v;
				if(v.getName() == c2)
					v2 = v;
			}
			if(unassigned.contains(v1) && unassigned.contains(v2))
			{
				v1.setNumConstraining(v1.getNumConstraining()+1);
				v2.setNumConstraining(v2.getNumConstraining()+1);
			}
			else if(forward && unassigned.contains(v1) && !unassigned.contains(v2))
			{
				char constraint = con.charAt(2);
				/*if(constraint == '>')
					constraint = '<';
				else if(constraint == '<')
					constraint = '>';*/
				
				constrain(assignments.get(v2),v1, constraint);
			}
			else if(forward && !unassigned.contains(v1) && unassigned.contains(v2))
			{
				//must switch the direction
				char constraint = con.charAt(2);
				if(constraint == '>')
					constraint = '<';
				else if(constraint == '<')
					constraint = '>';
				constrain(assignments.get(v1),v2,constraint);
			}
		}
		PriorityQueue<Var> queue = new PriorityQueue<Var>();
		for(Var v : unassigned)
		{
			//System.out.println(v.getName() + " " + v.getCurrentDomain() + " " + v.getNumConstraining());
			queue.add(v);
		}
		return  queue.peek();
	}
	
	private static ArrayList<Integer> orderDomainValues(Var var, Map<Var, Integer> assignments)
	{
		ArrayList<Integer> currentDomain = new ArrayList<Integer>();
		currentDomain.addAll(var.getCurrentDomain());
		ArrayList<Integer> values = new ArrayList<Integer>();
		Map<Integer,Integer> numConstraining = new HashMap<Integer,Integer>();
		for(int n : currentDomain)
			numConstraining.put(n, 0);
		
		if(currentDomain.size()<=1)
			return currentDomain;

		for(String con : cons)
		{
			char constraint = con.charAt(2);
			char c1 = con.charAt(0);
			char c2 = con.charAt(4);
			Var v1 = new Var(c1);
			Var v2 = new Var(c2);
			for(Var v: vars)
			{
				if(v.getName() == c1)
					v1 = v;
				if(v.getName() == c2)
					v2 = v;
			}

			if(var.equals(v1) && !assignments.containsKey(v2))
			{
				
				ArrayList<Integer> initialDomain = v2.getCurrentDomain();
				
				if(constraint == '!')
				{
					for(int i : currentDomain)
						if(initialDomain.contains(i))
							numConstraining.put(i, numConstraining.get(i)-1);
				}
				else if(constraint != '=')
				{
					//have to switch direction
					if(constraint == '>')
						constraint = '<';
					else if(constraint == '<')
						constraint = '>';
					
					for(int i : currentDomain)
					{
						constrain(i, v2, constraint);
						//increase the count of values constrained by the difference in size of the domain after constraining
						numConstraining.put(i, numConstraining.get(i) + (initialDomain.size() - v2.getCurrentDomain().size()));
						v2.setDomain(initialDomain);
					}
				}
			}
			else if(var.equals(v2) && !assignments.containsKey(v1))
			{
				//have to switch direction
				/*if(constraint == '>')
					constraint = '<';
				else if(constraint == '<')
					constraint = '>';*/
				
				ArrayList<Integer> initialDomain = v1.getCurrentDomain();
				
				if(constraint == '!')
				{
					for(int i : currentDomain)
						if(initialDomain.contains(i))
							numConstraining.put(i, numConstraining.get(i)-1);
				}
				else if(constraint != '=')
				{
					for(int i : currentDomain)
					{
						constrain(i, v1, constraint);
						//increase the count of values constrained by the difference in size of the domain after constraining
						numConstraining.put(i, numConstraining.get(i) + (initialDomain.size() - v1.getCurrentDomain().size()));
					//	System.out.println(v1.getCurrentDomain());
					//	System.out.println(initialDomain);
						v1.setDomain(initialDomain);
					}
				}	
			}	
		}
		
		for(int i : numConstraining.keySet())
		{
			
			boolean done = false;
			for(int j =0; j < values.size(); j++)
			{
				if(!done && numConstraining.get(i) < numConstraining.get(values.get(j)))
				{
					values.add(j, i);
					done = true;
				}
				else if (!done && numConstraining.get(i) == numConstraining.get(values.get(j)))
				{
					if(i < values.get(j)){
						values.add(j, i);
						done = true;}
				}
			}
			if(!done)
				values.add(i);
		}
	//	for(int i: values)
	//		System.out.println("var "+var.getName() +" " + i + " constrains " + numConstraining.get(i));
		return values;
	}
	
}
