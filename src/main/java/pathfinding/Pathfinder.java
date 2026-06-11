import java.util.*;
public interface Pathfinder {
	public abstract void buildPathfindingGrid(ArrayList<Block> blockList);
	public abstract ArrayList<Position> findRoute(Position src, Position dst, boolean allowIllegalPaths);
	public abstract ArrayList<Position> plotRandomPath(Position src, int range);
}
