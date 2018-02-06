import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

public class GS {
	private PriorityQueue<State> queue;
	private ArrayList<String> visited;
	private String goal;
	
	
	public GS(String start)
	{
		State starter = new State(start, null, -1,0,0,0,0);
		queue = new PriorityQueue<State>();
		queue.add(starter);
		visited = new ArrayList<String>();
		visited.add(start);
		goal = getGoal(start);
	}
	/*The same for all searches
	 * Returns true if the goal is found*/
	public boolean search()
	{
		while (!queue.isEmpty())
		{
			State move = queue.remove();
			String next = move.getValue();
			if(goal.equals(next))
			{
				System.out.println("\nFinal Result for GS:");
				printPath(move);
				return true;
			}
			successors(move);
			
		}
		return false;
	}
	/*The same for all searches
	 * Calls move function for all child states of the given parent node*/
	private void successors(State parent)
	{
		String s = parent.getValue();
		char[] chars = s.toCharArray();
		for(int i = 0; i < s.length(); i++)
		{
			if(chars[i]!='X')
				move(i,parent);
		}
	}
	/*Performs the switch between X and either W or B,
	 * Also determines the cost of the move and passes it to the state,
	 * And then adds the new state to the queue*/
	private void move(int i, State parent)
	{
		String s = parent.getValue();
		int x = s.indexOf('X');
		char[] chars = s.toCharArray();
		chars[x] = chars[i];
		chars[i] = 'X';
		String next = new String(chars);
		
		if(!visited.contains(next))
		{
			//finds the number out of place, h
			int wrong =0;
			for(int j = 0; j < s.length(); j++)
			{
				if(next.charAt(j)!=goal.charAt(j) && next.charAt(j)!='X')
					wrong++;
			}
			visited.add(next);
			queue.add(new State(next, parent,i,wrong,parent.getDepth()+1,0,0));
		}
	}
	/*The same for all searches.
	 * returns the goal given the start state, for any combination of Bs, Ws, and an X*/
	private String getGoal(String s)
	{
		int b = 0;
		for(int i = 0; i < s.length(); i++)
		{
			if (s.charAt(i) == 'B')
				b++;
		}
		char[] chars = new char[s.length()];
		for(int i = 0; i < b; i++)
			chars[i] = 'B';
		chars[b] = 'X'; //BBBxWWW x is at index 3
		for(int i = b+1; i < s.length(); i++)
			chars[i] = 'W';	
		return new String(chars);
	}
	/*Prints out the solution path, tracing the parents from the goal state
	 * */
	private void printPath(State state)
	{
		Stack<State> stack = new Stack<State>();
		int i = 0;
		while(state.hasParent())
		{
			stack.push(state);
			state = state.getParent();
		}
		System.out.println(state.getValue());
		while(!stack.empty())
		{
			State s = stack.pop();
			System.out.println("Step "+i+": move "+s.getMove()+" "+s.getValue());
			i++;
		}
	}
}
