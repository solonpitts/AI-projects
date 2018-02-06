import java.util.*;
import java.io.*;

public class TileSearch {

	public static void main(String[] args)throws Exception {
		
		boolean cost = false;
		String filename = "";
		String type = "";
		if(args[0].equals("-cost"))
		{
			cost = true;
			filename = args[2];
			type = args[1];
		}
		else 
		{
			type = args[0];
			filename = args[1];
		}
		Scanner fscan = new Scanner(new File(filename));
		String start = fscan.nextLine();

		
		switch(type.toUpperCase()){
		case("BFS"): BFS bfs = new BFS(start); bfs.search();
		break;
		case("DFS"): DFS dfs = new DFS(start); dfs.search();
		break;
		case("UCS"): UCS ucs = new UCS(start,cost); ucs.search();
		break;
		case("GS"): GS gs = new GS(start); gs.search();
		break;
		case("A-STAR"): AStar astar = new AStar(start,cost); astar.search();
		break;
		}
		fscan.close();
	}
	
}
