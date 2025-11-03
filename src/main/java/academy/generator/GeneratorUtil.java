package academy.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneratorUtil {

    public static final Random random = new Random();

    public static void fill(Maze maze, CellType value) {
        for (int i = 0; i < maze.cells().length; i++) {
            Arrays.fill(maze.cells()[i], value);
        }
    }

    public static Maze getGrid(int width, int height) {
        CellType[][] cells = new CellType[toGrid(height)][toGrid(width)];
        return new Maze(cells);
    }

    public static int toGrid(int cellIndex) {
        return cellIndex * 2 + 1;
    }

    public static int fromGrid(int index) {
        return (index - 1) / 2;
    }

    public static Point getStartPoint(int width, int height) {
        return new Point(random.nextInt(width), random.nextInt(height));
    }

    public static void markCell(Maze maze, Point point, CellType cellType) {
        maze.cells()[toGrid(point.y())][toGrid(point.x())] = cellType;
    }

    public static List<Point> getUnvisitedNeighbours(Maze maze, Point point) {
        List<Point> res = new ArrayList<>(4);
        for (Point p : getPointNeighbours(point)) {
            if (pointInMaze(p, maze) && getPointType(p, maze) == CellType.WALL) {
                res.add(p);
            }
        }
        return res;
    }

    public static Point getRandomPointFromList(List<Point> points) {
        return points.get(random.nextInt(points.size()));
    }

    public static void markBetweenInclusivePoints(Point first, Point second, CellType cellType, Maze maze) {
        int betweenX = (toGrid(first.x()) + toGrid(second.x())) / 2;
        int betweenY = (toGrid(first.y()) + toGrid(second.y())) / 2;
        maze.cells()[betweenY][betweenX] = cellType;
        markCell(maze, second, cellType);
    }

    public static CellType getPointType(Point point, Maze maze) {
        return maze.cells()[toGrid(point.y())][toGrid(point.x())];
    }

    public static List<Point> getPointNeighbours(Point point) {
        List<Point> neighbours = new ArrayList<>(4);
        neighbours.add(new Point(point.x(), point.y() - 1)); // UP
        neighbours.add(new Point(point.x(), point.y() + 1)); // DOWN
        neighbours.add(new Point(point.x() - 1, point.y())); // LEFT
        neighbours.add(new Point(point.x() + 1, point.y())); // RIGHT
        return neighbours;
    }

    public static boolean pointInMaze(Point point, Maze maze) {
        int width = fromGrid(maze.cells()[0].length);
        int height = fromGrid(maze.cells().length);
        return point.x() >= 0 && point.y() >= 0 && point.x() < width && point.y() < height;
    }

    public static Point nextInDirection(Point from, Point to) {
        int dx = to.x() - from.x();
        int dy = to.y() - from.y();
        return new Point(to.x() + dx, to.y() + dy);
    }
}
