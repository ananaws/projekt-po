import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;
class PfTests {
	@Test
	void distanceTest(){
		Position src = new Position(0,0);
		Position dst = new Position(0,5);
		assert(aStar.calculateDistance(src, dst) == 5);
	}

	@Test
	void pathDstTest(){
		Position src = new Position(0, 0);
		Position dst = new Position(0, 8);
		Pathfinder pf = new PfState(9, 9);
		assert(pf != null);
		ArrayList<Block> blockList = new ArrayList<>();
		pf.buildPathfindingGrid(blockList);
		ArrayList<Position> path = pf.findRoute(src, dst, false);
		assert(path != null);
		assert(path.size() == 8);
		assert(dst.equals(path.get(path.size() - 1)));
	}
	@Test
	void pathNoDstTest(){
		Position src = new Position(0, 0);
		Position dst = new Position(3, 0);
		Pathfinder pf = new PfState(4, 4);
		assert(pf != null);
		ArrayList<Block> blockList = new ArrayList<>();
		for(int i = 0; i < 4; ++i){
			blockList.add(new Block(new Position(1, i)));
		}
		pf.buildPathfindingGrid(blockList);
		ArrayList<Position> path = pf.findRoute(src, dst, false);
		assert(path == null);
	}
	@Test
	void randomPathTest(){
		Position src = new Position(2, 2);
		Pathfinder pf = new PfState(5, 5);
		assert(pf != null);
		ArrayList<Block> blockList = new ArrayList<>();
		pf.buildPathfindingGrid(blockList);
		var path = pf.plotRandomPath(src, 1);
		assert(path != null);
	}

}
