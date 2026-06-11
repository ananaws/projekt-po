//package ana.ekosystem;

import java.lang.Thread;
import java.util.*;
import java.io.*;
import com.opencsv.CSVWriter;
public class Board{
	/**
	 * The board class contains:
	 * - the map dimensions 
	 * - the list of entities present on the map
	 * - the list of blocks present on the map
	 * - a pathfinder object
	 */
	private int xDim;
	private int yDim;
	private ArrayList<Entity> entityList;
	private ArrayList<Block> blockList; // for the purposes of pathfinding it's an obstacle list
	private String[][] boardDisplayHelper;
	Pathfinder pfGrid;
	//
	/**
	 * Parametrized constructor for Board.
	 *
	 * @param	xDim	Height of the board.
	 * @param 	yDim	Width of the board.
	 */
	Board(int xDim, int yDim){
		this.xDim = xDim;
		this.yDim = yDim;
		this.entityList = new ArrayList<>();
		this.blockList = new ArrayList<>();
		this.boardDisplayHelper = new String[xDim][yDim];
		this.pfGrid = new PfState(this.xDim, this.yDim);
	}
	/**
	 *	This was cluttering up the main loop so i moved it here.
	 */
	public void addStuff(){
		for(int i = 1; i < this.yDim - 1; ++i){
			addRock(new Position(this.xDim/2, i));
		}	
		for(int i = 0; i < 3; ++i){
		      	Position pos = getValidSpawningPosition();
			addCatFood(pos);
		}
		//
	}

	/**
	 *	Main loop of the simulation. Runs for a specified amount of cycles and quits if entityList becomes empty.	
	 *
	 *	@param	cycles	Specifies the maximum simulation length
	 */	
	public void loop(int cycles, boolean log) {
		LogWriter writer = new LogWriter("./out.csv");
		for(int i = 0; i < cycles; ++i){	
			ArrayList<Entity> mitosisQueue = new ArrayList<>();
			// remove dead entities
			Iterator it = entityList.iterator();
			while(it.hasNext()){
				Entity ent = (Entity) it.next();
				if(ent instanceof Lifetime){
					Lifetime item = (Lifetime) ent;
					if(item.hasLifeCycleFinished()) it.remove();
				}
			}
			// main
			for(Entity ent : entityList){
				Animal obj;
				if(ent instanceof Animal){
					 obj = (Animal) ent;
					 // fall through
				} else {
					continue;
				}

				// give idle animals a path
				if(obj.hasMoveTarget() == false && obj.getStatus() == EAnimalStates.MOVING){
					pfGrid.buildPathfindingGrid(this.blockList);
					obj.setMoveTarget(pfGrid.plotRandomPath(obj.getPosition(), 5));
				}
				// give chasing animals an edible target
				if(obj.isChasing()){
					pfGrid.buildPathfindingGrid(this.blockList);
					Edible target;
					if(!obj.hasTargetEntity()) {
						target = selectRandomEdible(obj);
					} else {
						target = obj.getTargetEntity();
					}
					if(target != null) obj.setMoveTarget(pfGrid.findRoute(obj.getPosition(), target.getPosition(), false));	
					if(target != null && obj.isWithinTiles(target.getPosition(), 1)){
						obj.eat(target);
					}
				}
				if(obj.wantsToUndergoMitosis()){
					mitosisQueue.add(obj.reproduce());
				}
				// update the animal
				obj.tick();
				// write to csv
				String[] cycleLog = {Integer.toString(i), Integer.toString(obj.getCreatureId()), obj.getClass().getSimpleName(), obj.getStatus().name()}; 
				writer.writeLog(cycleLog);
			}
			entityList.addAll(mitosisQueue);
			// misc
			drawBoard(i);
			if(log) drawDebugMenu(1);

			if(entityList.isEmpty()) break;

			try {
				Thread.sleep(500);
			} catch (Exception e){
				System.out.println(e);
			}
		}
		System.out.println("Done");
		writer.close();
	}
	/**
	 * Checks if the entityList contains any objects.
	 *
	 * @return		boolean value
	 */
	private boolean isEntityListEmpty(){
		if(this.entityList.isEmpty()) return true;
		return false;
	}

	/**
	 * Checks if blockList contains any objects.
	 *
	 * @return		boolean value
	 */
	private boolean isBlockListEmpty(){
		if(this.blockList.isEmpty()) return true;
		return false;
	}

	/**
	 * Selects a random block from the blockList. Returns null if the blockList is empty.
	 *
	 * @return		random block
	 */
	private Block selectRandomBlock(){
		if (!isBlockListEmpty()) return blockList.get((int) Math.random() * blockList.size());
		return null;
	}
	
	/**
	 * Selects a random edible from the entityList that is not "notObj". Returned object implements the edible interface. Returns null if no suitable object is found.
	 * Functionality to exclude an object from the search exists to support entities implementing both Edible and Consumer interfaces.
	 *
	 * @param	notObj	An object to be excluded from the "search for food".	
	 * @return	Edible object
	 */
	public Edible selectRandomEdible(Animal notObj){
		ArrayList<Edible> edibleList = new ArrayList<>();
		Edible ret;
		for(Entity obj : entityList){
			if(obj instanceof Edible && !obj.equals(notObj)) edibleList.add((Edible) obj);
		}
		if(edibleList.size() == 0) return null;
		ret = edibleList.get((int) (Math.random() * edibleList.size()));
		return ret;
	}

	/**
	 * Checks whether a position is already occupied by an entity.
	 *
	 * @param pos	Position to check
	 * @return boolean value
	 */
	private boolean isValidSpawningPosition(Position pos){
		for(Block blk : this.blockList){
			if(pos.equals(blk.getPosition())) return false;
		}
		for(Entity anm : this.entityList){
			if(pos.equals(anm.getPosition())) return false;
		}
		if(pos.x >= this.xDim || pos.x < 0 || pos.y >= this.yDim || pos.y < 0) return false;
		return true;
	}

	/**
	 * Finds a suitable random position for spawning an animal.
	 *
	 *@return  a random, unoccupied position on the board.
	 */
	private Position getValidSpawningPosition(){
		int rdX;
		int rdY;
		Position pos;
		do {
			rdX = (int) (Math.random() * xDim);
			rdY = (int) (Math.random() * yDim);
			pos = new Position(rdX, rdY);
		} while (!isValidSpawningPosition(pos));
		return pos;
	}

	/**
	 * Places an amount of leopards on the board.
	 *
	 * @param n amount of entities to place
	 */
	public void placeLeopardsAtRandomPositions(int n){
		for(int i = 0; i < n; ++i){
			Position pos = getValidSpawningPosition();
			addLeopard(pos);
		}
	}

	/**
	 * Places an amount of fish on the board.
	 *
	 * @param n amount of entities to place
	 */
	public void placeFishAtRandomPositions(int n){
		for(int i = 0; i < n; ++i){
		      	Position pos = getValidSpawningPosition();
			addFish(pos);
		}
	}

	/**
	 * Prints a list of animals along with their respective status. For certain values of "status" extra information is displayed.
	 *
	 * @param depth	Verbosity of displayed information
	 */
	public void drawDebugMenu(int depth){
		for(Entity ent : this.entityList){
			Animal obj;
			if(ent instanceof Animal){
				 obj = (Animal) ent;
				 // fall through
			} else {
				continue;
			}

			switch(obj.getStatus()){
				case(EAnimalStates.MOVING):
					if(depth == 0) System.out.printf("%s\n", obj.toString());
					if(depth == 1 && obj.getMoveTarget() != null) System.out.printf("%s with destination %s (%d energy left)\n", obj.toString(), obj.getMoveTarget().toString(), obj.getEnergy());
					if(depth == 1 && obj.getMoveTarget() == null) System.out.printf("%s with no destination\n", obj.toString());
					break;
				case(EAnimalStates.SLEEPING):	
					System.out.printf("%s (%d energy)\n", obj.toString(), obj.getEnergy());
					break;
				case(EAnimalStates.CHASING):
					if(depth == 0) System.out.printf("%s\n", obj.toString());
					if(depth == 1 && obj.getMoveTarget() != null) System.out.printf("%s target at %s (%d energy left)\n", obj.toString(), obj.getMoveTarget().toString(), obj.getEnergy());
					if(depth == 1 && obj.getMoveTarget() == null) System.out.printf("%s with no target\n", obj.toString());
					break;
				default:
					System.out.printf("%s\n", obj.toString());
					break;
			}
		}
	}

	/**
	 * "0-initializes" the boardDisplayHelper. Utility function.
	 */
	private void initBlankBoard(){
	// Does not interact with the game state, used only for boardDisplayHelper
		for(String[] col : this.boardDisplayHelper){
			Arrays.fill(col, "  "); 
		}
	}
	
	private void _drawBoardHorizontalLine(){
	// Function called only in drawBoard, made so i dont have to repeat myself
		for(int i = 0; i < this.xDim; ++i){
			System.out.printf("_ ");
		}
		System.out.printf("\n");
	}

	private void drawBoard(){
		drawBoard(null);
	}

	private void drawBoard(ArrayList<Position> path){
		drawBoard(path, -1);
	}

	private void drawBoard(int cycle){
		drawBoard(null, cycle);
	}

	/**
	 * Function used for aggregating and drawing the contents of entityList and blockList on a simulated board. 
	 *
	 * @param path	Optional. Debug. Draws a list of positions on the board as a line.
	 * @param cycle	Optional. Displays the elapsed cycle count beneath the board.
	 */
	private void drawBoard(ArrayList<Position> path, int cycle){
	// Primary function to display the simulation state
		initBlankBoard();
		System.out.print("\033\143");
		// Transfer the contents of entityList onto boardDisplayHelper;
		for(Entity obj : blockList){
			this.boardDisplayHelper[obj.getPosition().x][obj.getPosition().y] = obj.getFace();
		}

		for(Entity obj : entityList){
			this.boardDisplayHelper[obj.getPosition().x][obj.getPosition().y] = obj.getFace();
		}
		
		if(path != null){
			for(Position pos : path){
				this.boardDisplayHelper[pos.x][pos.y] = "#";
			}
		}

		// Draw the board with a frame
		// _drawBoardHorizontalLine();
		for(int y = 0; y < yDim; ++y){
			// System.out.printf("| ");
			for(int x = 0; x < xDim; ++x){
				System.out.printf("%s ", boardDisplayHelper[x][y]);
			}
			// System.out.printf("\t|\n");
			System.out.printf("\n");
		}
		 //_drawBoardHorizontalLine();
		 System.out.println("Cycle: " + cycle);
	}
	private void addCatFood(Position pos){
		entityList.add(new CatFood(pos));
	}

	/**
	 * Adds a leopard to the board.
	 */
	private void addLeopard(Position pos){
		entityList.add(new Leopard(pos));
	}

	/**
	 * Adds a fish to the board.
	 */
	private void addFish(Position pos){
		entityList.add(new Fish(pos));
	}

	/**
	 * Adds a rock tile to the board.
	 */
	private void addRock(Position pos){
		blockList.add(new Block(pos));
	}

	/**
	 * Adds a water tile to the board.
	 */
	private void addWater(Position pos){
		blockList.add(new Water(pos));
	}

	private void addBlockRadius(boolean type, Position pos, int r){
		for(int y = pos.y - r; y < pos.y + r; ++y){
			for(int x = pos.x - r; x < pos.x + r; ++x){
				if(type == true) addRock(new Position(x, y));
				if(type == false) addWater(new Position(x, y));
			}
		}
	}

	
}
