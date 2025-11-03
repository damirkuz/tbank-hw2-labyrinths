package academy.generator;

import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class DfsGenerator extends BaseGenerator {

    @Override
    public Maze generate(int width, int height) {
        Maze maze = initializeMaze(width, height);
        Point start = GeneratorUtil.getStartPoint(width, height);

        Deque<Point> stack = new ArrayDeque<>();
        GeneratorUtil.markCell(maze, start, academy.maze.dto.CellType.PATH);
        stack.push(start);

        while (!stack.isEmpty()) {
            Point cur = stack.peek();
            List<Point> unvisited = getUnvisitedNeighbors(maze, cur);
            if (unvisited.isEmpty()) {
                stack.pop();
            } else {
                Point next = pickRandom(unvisited);
                carvePassage(maze, cur, next);
                stack.push(next);
            }
        }

        return maze;
    }
}
