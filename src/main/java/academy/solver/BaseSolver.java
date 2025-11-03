package academy.solver;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseSolver implements Solver {

    // Валидация: точки внутри поля и не стены
    protected boolean validateEndpoints(Maze maze, Point start, Point end) {
        return isInside(maze, start)
                && isInside(maze, end)
                && cell(maze, start) != CellType.WALL
                && cell(maze, end) != CellType.WALL;
    }

    // Восстановление пути из таблицы previous (идём от конца к началу)
    protected Path reconstructPath(Point end, Point start, Map<Point, Point> previous) {
        List<Point> path = new ArrayList<>();
        Point cur = end;
        while (cur != null) {
            path.addFirst(cur);
            if (cur.equals(start)) break;
            cur = previous.get(cur);
        }
        if (path.isEmpty() || !path.getFirst().equals(start)) return null;
        return new Path(path.toArray(new Point[0]));
    }

    protected int heuristic(Point a, Point b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    protected List<Point> neighbors4(Point p, Maze maze) {
        List<Point> res = new ArrayList<>(4);
        int[][] d = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        for (int[] v : d) {
            Point q = new Point(p.x() + v[0], p.y() + v[1]);
            if (isInside(maze, q)) res.add(q);
        }
        return res;
    }

    protected boolean isInside(Maze maze, Point p) {
        return p.x() >= 0 && p.y() >= 0 && p.y() < height(maze) && p.x() < width(maze);
    }

    protected CellType cell(Maze maze, Point p) {
        return maze.cells()[p.y()][p.x()];
    }

    protected int width(Maze maze) {
        return maze.cells()[0].length;
    }

    protected int height(Maze maze) {
        return maze.cells().length;
    }
}
