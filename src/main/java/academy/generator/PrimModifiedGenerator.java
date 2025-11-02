package academy.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Modified Prim's (cell-based, choose among multiple connections).
 */
public class PrimModifiedGenerator extends BaseGenerator {

    @Override
    public Maze generate(int width, int height) {
        return generatePrimWithChanceTakeNewest(width, height, 0.8);
    }

    protected Maze generatePrimWithChanceTakeNewest(int width, int height, double chance) {
        Maze maze = initializeMaze(width, height);

        Point start = GeneratorUtil.getStartPoint(width, height);
        GeneratorUtil.markCell(maze, start, CellType.PATH);

        List<Point> frontier = new ArrayList<>();
        Set<Point> frontierSet = new HashSet<>();
        addToFrontier(maze, start, frontier, frontierSet);

        while (!frontier.isEmpty()) {
            Point cur;
            if (GeneratorUtil.random.nextDouble() < chance) {
                cur = frontier.removeLast();
            } else {
                cur = pickRandomAndRemove(frontier);
            }

            frontierSet.remove(cur);
            var mazeNeighbors = getMazeNeighbors(maze, cur);
            if (!mazeNeighbors.isEmpty()) {
                var connectTo = GeneratorUtil.getRandomPointFromList(mazeNeighbors);
                carvePassage(maze, connectTo, cur);
                addToFrontier(maze, cur, frontier, frontierSet);
            }
        }

        return maze;
    }
}
