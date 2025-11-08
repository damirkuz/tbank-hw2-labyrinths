package academy.maze.solver;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import java.util.*;

public class BFSSolver extends BaseSolver {

    @Override
    public Path solve(Maze maze, Point start, Point end) {

        if (validateEndpoints(maze, start, end)) return null;

        Map<Point, Point> previous = new HashMap<>();
        Set<Point> visited = new HashSet<>();

        Queue<Point> queue = new LinkedList<>();

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            // Финиш найден — восстанавливаем путь
            if (current.equals(end)) {
                return reconstructPath(end, start, previous);
            }

            for (Point neighbor : neighbors(current, maze)) {
                if (cell(maze, neighbor) == CellType.WALL || visited.contains(neighbor)) continue;

                // Отмечаем как посещённую до добавления в очередь
                visited.add(neighbor);
                previous.put(neighbor, current);
                queue.add(neighbor);
            }
        }

        // Пути нет
        return null;
    }
}
