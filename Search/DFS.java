import java.util.ArrayList;
import java.util.Stack;

public class DFS {
	private Stack<State> queue;
	private ArrayList<String> visited;
	private String goal;
	
	
	public DFS(String start)
	{
		State starter = new State(start, null, -1,0,0,0,0);
		queue = new Stack<State>();
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
			State move = queue.pop();
			String next = move.getValue();
				
			if(goal.equals(next))
			{
				System.out.println("\nFinal Result for BFS:");
				printPath(move);
				return true;
			}
			visited.add(next);
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
		for(int i = s.length()-1; i >-1; i--)
		{
			if(chars[i]!='X')
				move(i,parent);
		}
	}
	/*Performs the switch between X and either W or B,
	 * There is no special cost function for BFS or DFS
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
			//visited.add(next);
			queue.push(new State(next, parent,i,0,0,0,0));
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