package academy.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class BaseGenerator implements Generator {

    protected Maze initializeMaze(int width, int height) {
        validateDimensions(width, height);
        Maze maze = GeneratorUtil.getGrid(width, height);
        GeneratorUtil.fill(maze, CellType.WALL);
        return maze;
    }

    protected void validateDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Длина и ширина должны быть положительными");
        }
    }

    protected List<Point> getMazeNeighbors(Maze maze, Point cell) {
        List<Point> neighbors = GeneratorUtil.getPointNeighbours(cell);
        List<Point> mazeNeighbors = new ArrayList<>();
        for (Point n : neighbors) {
            if (GeneratorUtil.pointInMaze(n, maze) && GeneratorUtil.getPointType(n, maze) == CellType.PATH) {
                mazeNeighbors.add(n);
            }
        }
        return mazeNeighbors;
    }

    protected List<Point> getUnvisitedNeighbors(Maze maze, Point cell) {
        return GeneratorUtil.getUnvisitedNeighbours(maze, cell);
    }

    protected void addToFrontier(Maze maze, Point cell, List<Point> frontier, Set<Point> frontierSet) {
        for (Point n : getUnvisitedNeighbors(maze, cell)) {
            if (!frontierSet.contains(n)) {
                frontier.add(n);
                frontierSet.add(n);
            }
        }
    }

    protected <T> T pickRandom(List<T> list) {
        int idx = GeneratorUtil.random.nextInt(list.size());
        return list.get(idx);
    }

    protected <T> T pickRandomAndRemove(List<T> list) {
        int idx = GeneratorUtil.random.nextInt(list.size());
        return list.remove(idx);
    }

    protected void carvePassage(Maze maze, Point from, Point to) {
        GeneratorUtil.markBetweenInclusivePoints(from, to, CellType.PATH, maze);
    }
}
