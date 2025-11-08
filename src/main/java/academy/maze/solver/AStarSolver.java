package academy.maze.solver;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import java.util.*;

public class AStarSolver extends BaseSolver {

    @Override
    public Path solve(Maze maze, Point start, Point end) {

        if (validateEndpoints(maze, start, end)) return null;

        Map<Point, Integer> gScore = new HashMap<>();
        Map<Point, Integer> fScore = new HashMap<>();
        Map<Point, Point> previous = new HashMap<>();
        Set<Point> closedSet = new HashSet<>();

        // Очередь с приоритетом сортируем по fScore
        PriorityQueue<Point> openQueue = new PriorityQueue<>((a, b) -> {
            int fa = fScore.getOrDefault(a, Integer.MAX_VALUE);
            int fb = fScore.getOrDefault(b, Integer.MAX_VALUE);
            return Integer.compare(fa, fb);
        });

        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));
        openQueue.add(start);

        while (!openQueue.isEmpty()) {
            Point current = openQueue.poll();

            // Финиш найден — восстанавливаем путь
            if (current.equals(end)) {
                return reconstructPath(end, start, previous);
            }

            // Переносим current в closed
            closedSet.add(current);

            for (Point nb : neighbors(current, maze)) {
                if (cell(maze, nb) == CellType.WALL || closedSet.contains(nb)) continue;

                int curG = gScore.getOrDefault(current, Integer.MAX_VALUE);
                int tentativeG = curG == Integer.MAX_VALUE ? Integer.MAX_VALUE : curG + 1;
                int oldG = gScore.getOrDefault(nb, Integer.MAX_VALUE);

                if (tentativeG < oldG) {
                    previous.put(nb, current);
                    gScore.put(nb, tentativeG);
                    fScore.put(nb, tentativeG + heuristic(nb, end));
                    if (!openQueue.contains(nb)) {
                        openQueue.add(nb);
                    }
                }
            }
        }

        // Пути нет
        return null;
    }
}
