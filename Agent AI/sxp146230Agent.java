package ctf.agent;


import ctf.common.AgentEnvironment;
import ctf.agent.Agent;
import java.util.*;
import ctf.common.AgentAction;

/**
 * My agent
 */
public class sxp146230Agent extends Agent {
	// implements Agent.getMove() interface

	private int[][] map;
	private int id;
	private Coord startPos;
	private Coord currentPos;
	private Coord lastPos;
	private int goalX;
	private int goalY;
	private int enemyBaseX;
	private int enemyBaseY;
	private Queue<Coord> path;
	private boolean pathed;
	private boolean flagCaptured;
	private boolean baseFound;
	
	public sxp146230Agent()
	{
		//don't know how big the map is, so guess 30x30
		map = new int[30][30];
		enemyBaseX = 0;
		enemyBaseY = 15;
		goalX = 0;
		goalY = 15;
		id = 0;
		pathed = false;
		path = new LinkedList<Coord>();
		flagCaptured = false;
		baseFound = false;
	}
	
	
	//if reset, path back to the last 
	
	public int getMove( AgentEnvironment inEnvironment ) {
		
		//only need to perform setup once, but neet AgentEnvironment to do it
		if(id == 0)
			guessPosition(inEnvironment);
		
		//print map
		
				for(int i = 0; i < 30; i++){
					for(int j = 0; j < 30; j ++)
						System.out.print(map[i][j]);
					System.out.println();
				}
		
		//if no flag or they have the flag and on enemy base, just chill
		if((!inEnvironment.hasFlag() || (inEnvironment.hasFlag(inEnvironment.ENEMY_TEAM) && flagCaptured && path.isEmpty())) && !inEnvironment.isBaseEast(inEnvironment.ENEMY_TEAM, false) && !inEnvironment.isBaseWest(inEnvironment.ENEMY_TEAM, false) &&
		!inEnvironment.isBaseNorth(inEnvironment.ENEMY_TEAM, false) && !inEnvironment.isBaseSouth(inEnvironment.ENEMY_TEAM, false) &&
		!inEnvironment.isAgentWest(inEnvironment.OUR_TEAM, true) && !inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM, true) &&
		!inEnvironment.isAgentEast(inEnvironment.OUR_TEAM, true) && !inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM, true))
			return AgentAction.DO_NOTHING;
		
		if(inEnvironment.hasFlag(inEnvironment.OUR_TEAM) && !inEnvironment.hasFlag())
			flagCaptured=true;
		//else flagcaptured  =false; 
		
		//if(!path.isEmpty())
		//	System.out.println("pathing...");
		
		//need to consider when we reset back to start pos
		if(!inEnvironment.hasFlag() && checkReset(inEnvironment)){
			//System.out.println("RESET");
			path.clear();
			goalX = enemyBaseX;
			goalY = enemyBaseY;
			/*
			for(int n = 0; n < 30; n++)
				for(int m = 0; m < 30; m++)
					if(map[n][m]>1 && map[n][m]<4)
						map[n][m]--;
						*/
			if(pathed)
				pathHome(currentPos);
			else if(lastPos.getX() != startPos.getX() || lastPos.getY() != startPos.getY())//havent found enemy goal yet and we've left this one
			{
				//enemyBaseY = goalY;
				goalX = lastPos.getX();
				goalY = lastPos.getY();
				pathHome(currentPos);
			}
		}
		else if(path.isEmpty() && !pathed)
		{
			goalX = enemyBaseX;
			goalY = enemyBaseY;
		}

		
		
		//check if the enemy base is in this row/col
		if(!inEnvironment.isBaseEast(inEnvironment.ENEMY_TEAM, false) && !inEnvironment.isBaseWest(inEnvironment.ENEMY_TEAM, false) && !baseFound)
		{
			baseFound = true;
			enemyBaseX = currentPos.getX();
			if(goalX != startPos.getX())
				goalX = currentPos.getX();
			if(startPos.getX()==0 && enemyBaseX!=29)
				for(int i = 0; i < 30; i++)
					map[i][enemyBaseX+1] = -1;
			else if(startPos.getX()==29 && enemyBaseX!=0)
				for(int i = 0; i < 30; i++)
					map[i][enemyBaseX-1] = -1;
		}
		if(!inEnvironment.isBaseNorth(inEnvironment.ENEMY_TEAM, false) && !inEnvironment.isBaseSouth(inEnvironment.ENEMY_TEAM, false) && !baseFound)
		{
			baseFound = true;
			goalY = currentPos.getY();
			enemyBaseY = goalY;
			if(startPos.getY() == 0) //for pathing reasons
				for (int i = enemyBaseY; i < 30; i++)
				{
					map[i][startPos.getX()] = -1;
				}
			else
				for (int i = enemyBaseY; i > 0; i--)
				{
					map[i][startPos.getX()] = -1;
				}
			
		}

		//check for flag, change goal to home base
		if(inEnvironment.hasFlag() && goalX != startPos.getX())
		{
			pathed = true;
			enemyBaseX = goalX;
			map[enemyBaseY][startPos.getX()] = 1;
			goalX = startPos.getX();
			plantBombs(inEnvironment);
		}
		else if(!inEnvironment.hasFlag() && goalX == startPos.getX() && pathed)
		{
			goalX = enemyBaseX;
		}
		
		//
		if(map[currentPos.getY()][currentPos.getX()]<4 && path.isEmpty() && !pathed)
			checkSurroundings(inEnvironment);
		
		
		if(inEnvironment.isBaseEast(inEnvironment.ENEMY_TEAM, true))
		{
			pathed = true;
			//map[currentPos.getY()][currentPos.getX()]= 1;
			enemyBaseX = currentPos.getX()+1;
		}
		if(inEnvironment.isBaseWest(inEnvironment.ENEMY_TEAM, true))
		{
			pathed = true;
			//map[currentPos.getY()][currentPos.getX()]= 1;
			enemyBaseX = currentPos.getX()-1;
		}
		if(inEnvironment.isBaseNorth(inEnvironment.ENEMY_TEAM, true))
		{
			pathed = true;
			//map[currentPos.getY()][currentPos.getX()]= 1;
			goalY = currentPos.getY()-1;
			enemyBaseY = goalY;
		}
		if(inEnvironment.isBaseSouth(inEnvironment.ENEMY_TEAM, true))
		{
			pathed = true;
			//map[currentPos.getY()][currentPos.getX()]= 1;
			goalY = currentPos.getY()+1;
			enemyBaseY = goalY;
		}
			
		
		//if(path.isEmpty() && pathExists())
		//	pathHome(currentPos); 
		//if(map[goalY][goalX] > 0) then there is a path
		//if(path.isEmpty() && pathed && !inEnvironment.hasFlag())
		//	pathHome(currentPos);

		//CHECK IF THERE IS A FRIENDLY AGENT ADJACENT
		if(inEnvironment.isAgentWest(inEnvironment.OUR_TEAM, true) && !inEnvironment.hasFlag())
			if(inEnvironment.hasFlag(inEnvironment.OUR_TEAM) || id==1)//if neither has it, one agent has to move forward
			{
				if(currentPos.getX() != 29 && map[currentPos.getY()][currentPos.getX()+1] > 0){
					//map[currentPos.getY()][currentPos.getX()+1]--;
					currentPos.newCoordinates(currentPos.getY(), currentPos.getX()+1); path.add(currentPos);
					return AgentAction.MOVE_EAST;}
				else if(currentPos.getY() != 29 && map[currentPos.getY()+1][currentPos.getX()] > 0){
					//map[currentPos.getY()+1][currentPos.getX()]--;
					currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());path.add(currentPos);
					return AgentAction.MOVE_SOUTH;}
				else if(currentPos.getY() != 0 && map[currentPos.getY()-1][currentPos.getX()] > 0){
					//map[currentPos.getY()-1][currentPos.getX()]--;
					currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());path.add(currentPos);
					return AgentAction.MOVE_NORTH;}
			}
		if(inEnvironment.isAgentEast(inEnvironment.OUR_TEAM, true) && !inEnvironment.hasFlag())
			if(inEnvironment.hasFlag(inEnvironment.OUR_TEAM) || id==1)//if neither has it, one agent has to move forward
			{
				if(currentPos.getX() != 0 && map[currentPos.getY()][currentPos.getX()-1] > 0){
					//map[currentPos.getY()][currentPos.getX()-1]--;
					currentPos.newCoordinates(currentPos.getY(), currentPos.getX()-1);path.add(currentPos);
					return AgentAction.MOVE_WEST;}
				else if(currentPos.getY() != 29 && map[currentPos.getY()+1][currentPos.getX()] > 0){
					//map[currentPos.getY()+1][currentPos.getX()]--;
					currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());path.add(currentPos);
					return AgentAction.MOVE_SOUTH;}
				else if(currentPos.getY() != 0 && map[currentPos.getY()-1][currentPos.getX()] > 0){
					//map[currentPos.getY()-1][currentPos.getX()]--;
					currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());path.add(currentPos);
					return AgentAction.MOVE_NORTH;}
			}
		if(inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM, true) && !inEnvironment.hasFlag())
			if(inEnvironment.hasFlag(inEnvironment.OUR_TEAM) || id==1)//if neither has it, one agent has to move forward
			{
				if(currentPos.getY() != 29 && map[currentPos.getY()+1][currentPos.getX()] > 0){
					//map[currentPos.getY()+1][currentPos.getX()]--;
					currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());path.add(currentPos);
					return AgentAction.MOVE_SOUTH;}
				else if(currentPos.getX() != 29 && map[currentPos.getY()][currentPos.getX()+1] > 0){
					//map[currentPos.getY()][currentPos.getX()+1]--;
					currentPos.newCoordinates(currentPos.getY(), currentPos.getX()+1);path.add(currentPos);
					return AgentAction.MOVE_WEST;}
				else if(currentPos.getX() != 0 && map[currentPos.getY()][currentPos.getX()-1] > 0){
					//map[currentPos.getY()][currentPos.getX()-1]--;
					currentPos.newCoordinates(currentPos.getY(), currentPos.getX()-1);path.add(currentPos);
					return AgentAction.MOVE_EAST;}
			}
		if(inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM, true) && !inEnvironment.hasFlag())
			if(inEnvironment.hasFlag(inEnvironment.OUR_TEAM) || id==1)//if neither has it, one agent has to move forward
			{
				if(currentPos.getY() != 0 && map[currentPos.getY()-1][currentPos.getX()] > 0){
					//map[currentPos.getY()-1][currentPos.getX()]--;
					currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());path.add(currentPos);
					return AgentAction.MOVE_NORTH;}
				else if(currentPos.getX() != 29 && map[currentPos.getY()][currentPos.getX()+1] > 0){
					//map[currentPos.getY()][currentPos.getX()+1]--;
					currentPos.newCoordinates(currentPos.getY(), currentPos.getX()+1);path.add(currentPos);
					return AgentAction.MOVE_WEST;}
				else if(currentPos.getX() != 0 && map[currentPos.getY()][currentPos.getX()-1] > 0){
					//map[currentPos.getY()][currentPos.getX()-1]--;
					currentPos.newCoordinates(currentPos.getY(), currentPos.getX()-1);path.add(currentPos);
					return AgentAction.MOVE_EAST;}
			}
				
				
		//switch on this? or check if there is another agent adjacent before using this
		if(path.isEmpty())
			return getNextCoord(inEnvironment);
		/*else if(path.isEmpty() && pathed)//path messed up somehow, commit sedoku
		{
			Coord sepuku = new Coord(currentPos.getY(), currentPos.getX());
			sepuku.bombHere();
			path.add(sepuku);
			path.add(currentPos);
		}*/
		else
		{
			Coord next = path.peek();
			//System.out.println("step: x: "+next.getX()+" y: " + next.getY());
			//only remove the coord if we dont want to bomb it
			
			if(next.getX() == currentPos.getX() -1){
				if(!inEnvironment.isAgentWest(inEnvironment.OUR_TEAM, true)){
					if(!next.isBomb())
						path.poll();
					currentPos.newCoordinates(currentPos.getY(), currentPos.getX()-1);
					return AgentAction.MOVE_WEST;
					}
				return AgentAction.DO_NOTHING;
			}
			if(next.getX() == currentPos.getX() +1){
				if(!inEnvironment.isAgentEast(inEnvironment.OUR_TEAM, true)){
					if(!next.isBomb())
						path.poll();
					currentPos.newCoordinates(currentPos.getY(), currentPos.getX()+1);
					return AgentAction.MOVE_EAST;	
				}
				return AgentAction.DO_NOTHING;
			}	
			if(next.getY() == currentPos.getY() -1){
				if(!inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM, true)){
					if(!next.isBomb())
						path.poll();
					currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());
					return AgentAction.MOVE_NORTH;
				}
				return AgentAction.DO_NOTHING;
			}
			if(next.getY() == currentPos.getY() +1){
				if(!inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM, true)){
					if(!next.isBomb())
						path.poll();
				currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());
				return AgentAction.MOVE_SOUTH;
				}
				return AgentAction.DO_NOTHING;
			}
			else //it's here
			{
				path.poll();
				//map[next.getY()][next.getX()] = -1;
				if(next.isBomb())
				{
					//path.poll();
					return AgentAction.PLANT_HYPERDEADLY_PROXIMITY_MINE;
				}
				else{
					Coord nextC = path.peek();
					if(nextC == null)
						return AgentAction.DO_NOTHING;
					//System.out.println("step: x: "+nextC.getX()+" y: " + nextC.getY());
					//only remove the coord if we dont want to bomb it
					
					if(nextC.getX() < currentPos.getX()){
						if(!inEnvironment.isAgentWest(inEnvironment.OUR_TEAM, true)){
							if(!nextC.isBomb())
								path.poll();
							currentPos.newCoordinates(currentPos.getY(), currentPos.getX()-1);
							return AgentAction.MOVE_WEST;
						}
						return AgentAction.DO_NOTHING;
					}
					if(nextC.getX() > currentPos.getX()){
						if(!inEnvironment.isAgentEast(inEnvironment.OUR_TEAM, true)){
							if(!nextC.isBomb())
								path.poll();
							currentPos.newCoordinates(currentPos.getY(), currentPos.getX()+1);
							return AgentAction.MOVE_EAST;
						}
						return AgentAction.DO_NOTHING;
					}
					if(nextC.getY() < currentPos.getY()){
						if(!inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM, true)){
							if(!nextC.isBomb())
								path.poll();
							currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());
							return AgentAction.MOVE_NORTH;
						}
						return AgentAction.DO_NOTHING;
					}
					if(nextC.getY() > currentPos.getY()){
						if(!inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM, true)){
							if(!nextC.isBomb())
								path.poll();
							currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());
							return AgentAction.MOVE_SOUTH;
						}
						return AgentAction.DO_NOTHING;
					}
					return AgentAction.DO_NOTHING;
				}
			}			
		}
		//return AgentAction.DO_NOTHING;
		}
	
	//find the known path IF GOING FROM START POSITION TO ENEMY BASE/CAN JUST FIND IN REVERSE FROM ENEMY BASE (TAKE ANSWERS AND PUT ON STACK)
	private void pathHome(Coord start)
	{
		ArrayList<Coord> stack = new ArrayList<Coord>();
		int x = start.getX();
		int y = start.getY();
		int gX = goalX;
		int gY = goalY;
		int killswitch = 0;
		boolean reverse = false;
		//if reverse
		if(startPos.getX() == start.getX())
		{
			reverse = true;
			x = goalX;
			y = goalY;
			gX = start.getX();
			gY = start.getY();
			stack.add(new Coord(y,x));
		}
		else
			stack.add(start);
		//System.out.println("x: " + x + " y: " + y);
		//System.out.println("goalX: " + gX + "goalY: " + gY);
		boolean pathFound = false;
		while(!pathFound)
		{
			killswitch++;
			if(killswitch>200) //can't find a path, reset everything
			{
				pathed = false;
				for(int row = 0; row < 30; row++)
					for(int col = 0; col < 30; col++)
						map[row][col] = 0;
				for(int r = 0; r < 30; r++)
					map[r][startPos.getX()] =1;
				pathFound = true;
				path.clear();
				lastPos = startPos;
				if(currentPos.getX() != startPos.getX() && currentPos.getY() != startPos.getY()){
				Coord sepuku = new Coord(currentPos.getY(), currentPos.getX());
				sepuku.bombHere();
				path.add(sepuku);
				path.add(currentPos);}
				return;
			}
			Coord next = new Coord(y,x);
				//move right
				if(x < gX)
				{
					//there is at least one path from here
					if(map[y][x+1]> 0 && !stack.contains(new Coord(y, x+1))){
						next.newCoordinates(y, x+1);}
					//move down
					else if(y < gY){
						if(map[y+1][x]> 0 && !stack.contains(new Coord(y+1, x))){
							next.newCoordinates(y+1, x);
						}
						//make an unblocked move
						else if(y != 0 &&  map[y-1][x]> 0 && !stack.contains(new Coord(y-1, x))){
							next.newCoordinates(y-1, x);
						}
						else if(x != 0 &&  map[y][x-1]> 0 && !stack.contains(new Coord(y, x-1))){
							next.newCoordinates(y, x-1);
						}
					}
					else{
						if(y != 0 && map[y-1][x]> 0 && !stack.contains(new Coord(y-1, x))){
							next.newCoordinates(y-1, x);
						}
						//make an unblocked move
						else if(y!=29 && map[y+1][x]> 0 && !stack.contains(new Coord(y+1, x))){
							next.newCoordinates(y+1, x);
						}
						else if(x != 0 && map[y][x-1]> 0 && !stack.contains(new Coord(y, x-1))){
							next.newCoordinates(y, x-1);
						}
					}
						
				}
				//move left
				else if(x > gX)
				{
					//there is at least one path from here
					if(map[y][x-1]> 0 && !stack.contains(new Coord(y, x-1))){
						next.newCoordinates(y, x-1);}
					//move down
					else if(y < gY){
						if(map[y+1][x]> 0 && !stack.contains(new Coord(y+1, x))){
							next.newCoordinates(y+1, x);
						}
						//make an unblocked move
						else if(y != 0 && map[y-1][x]> 0 && !stack.contains(new Coord(y-1, x))){
							next.newCoordinates(y-1, x);
						}
						else if(x != 29 && map[y][x+1]> 0 && !stack.contains(new Coord(y, x+1))){
							next.newCoordinates(y, x+1);
						}
						
					}
					else{
						if(y != 0 &&  map[y-1][x]> 0 && !stack.contains(new Coord(y-1, x))){
							next.newCoordinates(y-1, x);
						}
						//make an unblocked move
						else if(y!=29 && map[y+1][x]> 0 && !stack.contains(new Coord(y+1, x))){
							next.newCoordinates(y+1, x);
						}
						else if(x != 29 && map[y][x+1]> 0 && !stack.contains(new Coord(y, x+1))){
							next.newCoordinates(y, x+1);
						}
					}
				}
				else // x == goalX
				{
					//move down
					if(y < gY){
						if(map[y+1][x]> 0 && !stack.contains(new Coord(y+1, x))){
							next.newCoordinates(y+1, x);
						}
						else if(x != 0 && map[y][x-1]> 0 && !stack.contains(new Coord(y, x-1))){
							next.newCoordinates(y, x-1);
						}
						else if(x != 29 && map[y][x+1]> 0 && !stack.contains(new Coord(y, x+1))){
							next.newCoordinates(y, x+1);
						}
						else if(y != 0 && map[y-1][x]> 0 && !stack.contains(new Coord(y-1, x))){
							next.newCoordinates(y-1, x);
						}
						
					}
					else{
						if(y != 0 && map[y-1][x]> 0 && !stack.contains(new Coord(y-1, x))){
							next.newCoordinates(y-1, x);
						}
						else if(x != 0 && map[y][x-1]> 0 && !stack.contains(new Coord(y, x-1))){
							next.newCoordinates(y, x-1);
						}
						else if(x != 29 && map[y][x+1]> 0 && !stack.contains(new Coord(y, x+1))){
							next.newCoordinates(y, x+1);
						}					
						else if(y!=29 && map[y+1][x]> 0 && !stack.contains(new Coord(y+1, x))){
							next.newCoordinates(y+1, x);
						}
					}
				}
		//	if(!reverse)
		//		path.add(next);
		//	else
			stack.add(next);
			x = next.getX();
			y = next.getY();
			//System.out.println("id: "+id+" x: " + x + " y: " + y);
			/*
			try {
			    Thread.sleep(400);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}*/
			
			if(x == gX && y == gY)
				pathFound = true;
		}
	//	if(reverse)
	//		stack.add(start);
		for(int i = stack.size()-1;i>-1; i--){
			if(reverse)
				path.add(stack.remove(i));
			else
				path.add(stack.remove(0));
			//System.out.println("Sx: " + path.peek().getX() + " Sy: " + path.peek().getY());
		}
	}
	
	//plant bombs in all directions around, ending at the known path out
	private void plantBombs(AgentEnvironment inEnvironment)
	{
		checkSurroundings(inEnvironment);
		int x = currentPos.getX();
		int y = currentPos.getY();
		map[y][x]=0;
		
		if(currentPos.getX() != 0 && startPos.getX() != 29)
		{
			//get the way out first, and add it last
			Coord last = new Coord(0,0);
			if(map[y-1][x] > 0)
				last.newCoordinates(y-1, x);
			else if(map[y+1][x] > 0)
				last.newCoordinates(y+1, x);
			else if(map[y][x-1] > 0)
				last.newCoordinates(y, x-1);
			if(map[y-1][x] == 0){
				Coord bomb = new Coord(y-1,x);
				bomb.bombHere();
				if(!flagCaptured && !inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM,false)){
				path.add(bomb);
				path.add(new Coord(y,x));}
			}
			if(map[y+1][x] == 0){
				Coord bomb = new Coord(y+1,x);
				bomb.bombHere();
				if(!flagCaptured && !inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM,false)){
				path.add(bomb);
				path.add(new Coord(y,x));}
			}	
			if(map[y][x-1] == 0){
				Coord bomb = new Coord(y,x-1);
				bomb.bombHere();
				if(!flagCaptured){
					path.add(bomb);
					path.add(new Coord(y,x));}
			}
			//if(!flagCaptured)
				//last.bombHere();
			//path.add(last);
			path.add(new Coord(y,x));
			pathHome(last);
		}
		else //if(currentPos.getX() != 29)
		{
			//System.out.println("here "+ x + " " + y);
			Coord last = new Coord(0,0);
			if(map[y-1][x] > 0)
				last.newCoordinates(y-1, x);
			else if(map[y+1][x] > 0)
				last.newCoordinates(y+1, x);
			else if(map[y][x+1] > 0)
				last.newCoordinates(y, x+1);
			if(map[y-1][x] == 0){
				Coord bomb = new Coord(y-1,x);
				bomb.bombHere();
				if(!flagCaptured && !inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM,false)){
					path.add(bomb);
					path.add(new Coord(y,x));
				}
			}
			if(map[y+1][x] == 0){
				Coord bomb = new Coord(y+1,x);
				bomb.bombHere();
				if(!flagCaptured && !inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM,false)){
					path.add(bomb);
					path.add(new Coord(y,x));}
			}	
			if(map[y][x+1] == 0){
				Coord bomb = new Coord(y,x+1);
				bomb.bombHere();
				if(!flagCaptured){
					path.add(bomb);
					path.add(new Coord(y,x));}
			}
			//if(!flagCaptured)
				//last.bombHere();
			//path.add(last);
			path.add(new Coord(y,x));
			pathHome(last);
		}
		flagCaptured=true;
	}
	
	private int pathOut(AgentEnvironment inEnvironment, int x, int y)
	{
		for(int i = 0; i < 30; i++)
			for(int j = 0; j < 30; j++)
				if(map[i][j]>1)
					map[i][j]--;
		lastPos = startPos;
		if(currentPos.getX() != startPos.getX() && currentPos.getY() != startPos.getY()){
		Coord sepuku = new Coord(currentPos.getY(), currentPos.getX());
		sepuku.bombHere();
		path.add(sepuku);
		path.add(currentPos);}
		return AgentAction.DO_NOTHING;
	}
	private int getNextCoord(AgentEnvironment inEnvironment)
	{
		//horiz first, then vert
		
		int x = currentPos.getX();
		int y = currentPos.getY();
		
		
			//System.out.println("x " + x + " goalX " + goalX);
			//move right
			if(x < goalX)
			{
				//there is at least one path from here
				if(map[y][x+1]< 4 && map[y][x+1]>= 0){
					currentPos.newCoordinates(currentPos.getY(), currentPos.getX()+1);
					return AgentAction.MOVE_EAST;}
				//move down
				if(y < goalY){
					if(map[y+1][x]< 4 && map[y+1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());
						return AgentAction.MOVE_SOUTH;
					}
					//make an unblocked move
					if(y != 0 && map[y-1][x]< 4 && map[y-1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());
						return AgentAction.MOVE_NORTH;
					}
					if(x != 0 && map[y][x-1]< 4 && map[y][x-1]>= 0){
						currentPos.newCoordinates(currentPos.getY(), currentPos.getX()-1);
						return AgentAction.MOVE_WEST;
					}
					//THERE IS NO ADJACENT MOVE THAT IS UNBLOCKED, search for path out of blocked zone
					return pathOut(inEnvironment,x,y);
					
				}
				else{
					if(y != 0 && map[y-1][x]< 4 && map[y-1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());
						return AgentAction.MOVE_NORTH;
					}
					//make an unblocked move
					if(y!=29 && map[y+1][x]< 4 && map[y+1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());
						return AgentAction.MOVE_SOUTH;
					}
					if(x != 0 && map[y][x-1]< 4 && map[y][x-1]>= 0){
						currentPos.newCoordinates(currentPos.getY(), currentPos.getX()-1);
						return AgentAction.MOVE_WEST;
					}
					//THERE IS NO ADJACENT MOVE THAT IS UNBLOCKED, search for path out of blocked zone
					return pathOut(inEnvironment,x,y);
				}
					
			}
			//move left
			else if(x > goalX)
			{
				//there is at least one path from here
				if(map[y][x-1]< 4 && map[y][x-1]>= 0){
					currentPos.newCoordinates(currentPos.getY(), currentPos.getX()-1);
					return AgentAction.MOVE_WEST;}
				//move down
				if(y < goalY){
					if(map[y+1][x]< 4 && map[y+1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());
						return AgentAction.MOVE_SOUTH;
					}
					//make an unblocked move
					if(y != 0 && map[y-1][x]< 4 && map[y-1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());
						return AgentAction.MOVE_NORTH;
					}
					if(x != 29 && map[y][x+1]< 4 && map[y][x+1]>= 0){
						currentPos.newCoordinates(currentPos.getY(), currentPos.getX()+1);
						return AgentAction.MOVE_EAST;
					}
					//THERE IS NO ADJACENT MOVE THAT IS UNBLOCKED, search for path out of blocked zone
					return pathOut(inEnvironment,x,y);
					
				}
				else{
					if(y != 0 && map[y-1][x]< 4 && map[y-1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());
						return AgentAction.MOVE_NORTH;
					}
					//make an unblocked move
					if(y!=29 && map[y+1][x]< 4 && map[y+1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());
						return AgentAction.MOVE_SOUTH;
					}
					if(x != 29 && map[y][x+1]< 4 && map[y][x+1]>= 0){
						currentPos.newCoordinates(currentPos.getY(), currentPos.getX()+1);
						return AgentAction.MOVE_EAST;
					}
					//THERE IS NO ADJACENT MOVE THAT IS UNBLOCKED, search for path out of blocked zone
					return pathOut(inEnvironment,x,y);
				}
			}
			else // x == goalX
			{
				//move down
				if(y < goalY){
					if(map[y+1][x]< 4 && map[y+1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());
						return AgentAction.MOVE_SOUTH;
					}
					if(x != 0 && map[y][x-1]< 4 && map[y][x-1]>=0){
						currentPos.newCoordinates(currentPos.getY(), currentPos.getX()-1);
						return AgentAction.MOVE_WEST;
					}
					if(x != 29 && map[y][x+1]< 4 && map[y][x+1]>= 0){
						currentPos.newCoordinates(currentPos.getY(), currentPos.getX()+1);
						return AgentAction.MOVE_EAST;
					}
					if(y != 0 && map[y-1][x]< 4 && map[y-1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());
						return AgentAction.MOVE_NORTH;
					}
					//THERE IS NO ADJACENT MOVE THAT IS UNBLOCKED, search for path out of blocked zone
					return pathOut(inEnvironment,x,y);
					
				}
				else{
					if(y != 0 && map[y-1][x]< 4 && map[y-1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()-1, currentPos.getX());
						return AgentAction.MOVE_NORTH;
					}
					if(x != 0 && map[y][x-1]< 4 && map[y][x-1]>= 0){
						currentPos.newCoordinates(currentPos.getY(), currentPos.getX()-1);
						return AgentAction.MOVE_WEST;
					}
					if(x != 29 && map[y][x+1]< 4 && map[y][x+1]>= 0){
						currentPos.newCoordinates(currentPos.getY(), currentPos.getX()+1);
						return AgentAction.MOVE_EAST;
					}					
					if(y!=29 && map[y+1][x]< 4 && map[y+1][x]>= 0){
						currentPos.newCoordinates(currentPos.getY()+1, currentPos.getX());
						return AgentAction.MOVE_SOUTH;
					}
					//THERE IS NO ADJACENT MOVE THAT IS UNBLOCKED, search for path out of blocked zone
					return pathOut(inEnvironment,x,y);
				}
			}
		//no change to position
		//return AgentAction.DO_NOTHING;
	}
	
	private void guessPosition(AgentEnvironment inEnvironment)
	{
		if(inEnvironment.isBaseNorth(inEnvironment.OUR_TEAM, false ))
			if(inEnvironment.isBaseEast(inEnvironment.ENEMY_TEAM, false))
			{
				id = 2;
				//map[29][0] = id;
				startPos = new Coord(29,0);
				currentPos = new Coord(29,0);
				lastPos = new Coord(29,0);
				goalX = 29;
				enemyBaseX = 29;
				//the whole side is always passable
				for(int i = 0; i < 30; i++)
					map[i][0] = 1;
			}
			else
			{
				id = 2;
				//map[29][29] = id;
				startPos = new Coord(29,29);
				currentPos = new Coord(29,29);
				lastPos = new Coord(29,29);
				goalX = 0;
				enemyBaseX = 0;
				for(int i = 0; i < 30; i++)
					map[i][29] = 1;
			}
		else
			if(inEnvironment.isBaseEast(inEnvironment.ENEMY_TEAM, false))
			{
				id = 1;
				//map[0][0] = id;
				startPos = new Coord(0,0);
				currentPos = new Coord(0,0);
				lastPos = new Coord(0,0);
				goalX = 29;
				enemyBaseX = 29;
				for(int i = 0; i < 30; i++)
					map[i][0] = 1;
			}
			else
			{
				id = 1;
				//map[0][29] = id;
				startPos = new Coord(0,29);
				currentPos = new Coord(0,29);
				lastPos = new Coord(0,29);
				goalX = 0;
				enemyBaseX = 0;
				for(int i = 0; i < 30; i++)
					map[i][29] = 1;
			}
	}
	
	//could check for contradictions in here...
	private void checkSurroundings(AgentEnvironment inEnvironment)
	{
		int numBlocked = 1;
		int x = currentPos.getX();
		int y = currentPos.getY();
		if(map[y][x]>1)//2
		{
			map[y][x]++;
			return;
		}

		//check for obstacles in all directions and update map
		if(inEnvironment.isObstacleNorthImmediate()){
			if(y != 0)
				map[y-1][x] = -1;
			numBlocked++;
		}
		else if((y!=0 && (map[y-1][x]>3 || map[y-1][x]<0))){
			numBlocked++;
		}
		/*
		else //not an obst
			if(y != 0){
				if(map[y-1][x] > 2)
					numBlocked++;
				map[y-1][x]++;
			}*/
		if(inEnvironment.isObstacleSouthImmediate()){
			if(y != 29)
				map[y+1][x] = -1;
			numBlocked++;
		}
		else if( (y!=29 && (map[y+1][x]>3 || map[y+1][x]<0))){
			numBlocked++;
		}/*
		else //not an obst
			if(y != 29){
				if(map[y+1][x] > 2)
					numBlocked++;
				map[y+1][x]++;
			}*/
		if(inEnvironment.isObstacleWestImmediate()){
			if(x != 0)
				map[y][x-1] = -1;
			numBlocked++;
		}
		else if((x!=0 && (map[y][x-1]>3 || map[y][x-1]<0))){
			numBlocked++;
		}
		/*
		else //not an obst
			if(x != 0){
				if(map[y][x-1] > 2)
					numBlocked++;
				map[y][x-1]++;
			}*/
		if(inEnvironment.isObstacleEastImmediate()){
			if(x != 29)
				map[y][x+1] = -1;
			numBlocked++;
		}
		else if((x!=29 && (map[y][x+1]>3 || map[y][x+1]<0))){
			numBlocked++;
		}
		/*else //not an obst
			if(x != 29){
				if(map[y][x+1] > 2)
					numBlocked++;
				map[y][x+1]++;
			}*/
			
		//mark as checked
			map[y][x]=numBlocked;
	}
	
	private boolean checkReset(AgentEnvironment inEnvironment)
	{
		//in the correct collumn
		if(inEnvironment.isBaseEast(inEnvironment.OUR_TEAM,false) || inEnvironment.isBaseWest(inEnvironment.OUR_TEAM,false))
			return false;
		if(id == 1){
			//everything must be south
			if(inEnvironment.isBaseNorth(inEnvironment.OUR_TEAM,false) || inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM,false) || inEnvironment.isAgentNorth(inEnvironment.ENEMY_TEAM,false))
				return false;
			//check the obstacles
			if(startPos.getX()==0){
				//if no obstacles north or west, then false
				if(!inEnvironment.isObstacleNorthImmediate() || !inEnvironment.isObstacleWestImmediate())
					return false;
				//if other directions don't match
				if((inEnvironment.isObstacleEastImmediate() && map[0][1]!=-1) || (inEnvironment.isObstacleSouthImmediate() && map[1][0]!=-1))
					return false;
			}
			else{ //x == 29
				//if no obstacles north or east, then false
				if(!inEnvironment.isObstacleNorthImmediate() || !inEnvironment.isObstacleEastImmediate())
					return false;
				//if other directions don't match
				if((inEnvironment.isObstacleWestImmediate() && map[0][28]!=-1) || (inEnvironment.isObstacleSouthImmediate() && map[1][29]!=-1))
					return false;
			}
		}
		else //id == 2
		{
			//everything must be north
			if(inEnvironment.isBaseSouth(inEnvironment.OUR_TEAM,false) || inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM,false) || inEnvironment.isAgentSouth(inEnvironment.ENEMY_TEAM,false))
				return false;
			//check the obstacles
			if(startPos.getX()==0){
				//if no obstacles south or west, then false
				if(!inEnvironment.isObstacleSouthImmediate() || !inEnvironment.isObstacleWestImmediate())
					return false;
				//if other directions don't match
				if((inEnvironment.isObstacleEastImmediate() && map[29][1]!=-1) || (inEnvironment.isObstacleNorthImmediate() && map[28][0]!=-1))
					return false;
			}
			else{ //x == 29
				//if no obstacles south or east, then false
				if(!inEnvironment.isObstacleSouthImmediate() || !inEnvironment.isObstacleEastImmediate())
					return false;
				//if other directions don't match
				if((inEnvironment.isObstacleWestImmediate() && map[29][28]!=-1) || (inEnvironment.isObstacleNorthImmediate() && map[28][29]!=-1))
					return false;
			}
		}
		//System.out.println("reset" + id);
		if((currentPos.getX() != startPos.getX() || currentPos.getY() != startPos.getY()) && path.isEmpty())
			lastPos.newCoordinates(currentPos.getY(), currentPos.getX());
		currentPos.newCoordinates(startPos.getY(), startPos.getX());
		return true;
	}
	
}

class Coord
{
	private int x;
	private int y;
	
	private boolean bomb;
	
	public Coord(int n, int m)
	{
		y = n;
		x = m;
		bomb = false;
	}
	
	@Override
	public boolean equals(Object o)
	{
		Coord c = (Coord)o;
		return (y==c.y && x==c.x);
	}
	public boolean isBomb()
	{
		return bomb;
	}
	//if we want to plant a bomb here
	public void bombHere()
	{
		bomb = true;
	}
	
	public void newCoordinates(int n, int m)
	{
		y = n;
		x = m;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}