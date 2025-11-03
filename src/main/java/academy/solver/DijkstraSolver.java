package academy.solver;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import java.util.*;

public class DijkstraSolver extends BaseSolver {

    @Override
    public Path solve(Maze maze, Point start, Point end) {

        if (validateEndpoints(maze, start, end)) return null;

        Map<Point, Integer> dist = new HashMap<>(); // расстояния
        Map<Point, Point> previous = new HashMap<>(); // для пути
        Set<Point> visited = new HashSet<>(); // обработаны

        // Очередь по минимальному dist
        PriorityQueue<Point> pq =
                new PriorityQueue<>(Comparator.comparingInt(p -> dist.getOrDefault(p, Integer.MAX_VALUE)));

        // Инициализируем только проходимые клетки
        for (int y = 0; y < height(maze); y++) {
            for (int x = 0; x < width(maze); x++) {
                Point p = new Point(x, y);
                if (cell(maze, p) != CellType.WALL) {
                    dist.put(p, Integer.MAX_VALUE);
                }
            }
        }

        dist.put(start, 0);
        pq.offer(start);

        while (!pq.isEmpty()) {
            Point cur = pq.poll();
            if (visited.contains(cur)) continue;
            visited.add(cur);

            if (cur.equals(end)) break;

            for (Point nb : neighbors4(cur, maze)) {
                if (visited.contains(nb) || cell(maze, nb) == CellType.WALL) continue;

                int nd = dist.get(cur) + 1; // вес ребра = 1
                int od = dist.getOrDefault(nb, Integer.MAX_VALUE);
                if (nd < od) {
                    dist.put(nb, nd);
                    previous.put(nb, cur);
                    pq.offer(nb);
                }
            }
        }

        return reconstructPath(end, start, previous);
    }
}
