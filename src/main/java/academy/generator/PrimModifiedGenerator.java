package academy.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PrimModifiedGenerator extends BaseGenerator {

    @Override
    public Maze generate(int width, int height) {
        Maze maze = initializeMaze(width, height);

        Point start = GeneratorUtil.getStartPoint(width, height);
        GeneratorUtil.markCell(maze, start, CellType.PATH);

        List<Point> frontier = new ArrayList<>();
        Set<Point> frontierSet = new HashSet<>();
        addToFrontier(maze, start, frontier, frontierSet);

        while (!frontier.isEmpty()) {
            Point cur = pickRandomAndRemove(frontier);
            frontierSet.remove(cur);

            List<Point> mazeNeighbors = getMazeNeighbors(maze, cur);
            if (!mazeNeighbors.isEmpty()) {
                Point connectTo = GeneratorUtil.getRandomPointFromList(mazeNeighbors);
                carvePassage(maze, connectTo, cur);
                addToFrontier(maze, cur, frontier, frontierSet);
            }
        }

        return maze;
    }
}
