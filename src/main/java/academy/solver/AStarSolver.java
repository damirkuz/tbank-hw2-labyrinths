package academy.solver;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;

import java.util.*;


public class AStarSolver extends BaseSolver {

    @Override
    public Path solve(Maze maze, Point start, Point end) {

        if (!validateEndpoints(maze, start, end)) return null;

        Map<Point, Integer> gScore = new HashMap<>();          // g: от старта
        Map<Point, Integer> fScore = new HashMap<>();          // f: g + h
        Map<Point, Point> previous = new HashMap<>();          // для пути
        Set<Point> openSet = new HashSet<>();                  // кандидаты
        Set<Point> closedSet = new HashSet<>();                // обработаны

        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));
        openSet.add(start);


        while (!openSet.isEmpty()) {
            // Берём точку с минимальным fScore
            Point current = null;
            int bestF = Integer.MAX_VALUE;
            for (Point p : openSet) {
                int f = fScore.getOrDefault(p, Integer.MAX_VALUE);
                if (f < bestF) { bestF = f; current = p; }
            }

            // Финиш найден — восстанавливаем путь
            if (current.equals(end)) {
                return reconstructPath(end, start, previous);
            }

            // Переносим current в closed
            openSet.remove(current);
            closedSet.add(current);


            for (Point nb : neighbors4(current, maze)) {
                if (cell(maze, nb) == CellType.WALL || closedSet.contains(nb)) continue;

                int curG = gScore.getOrDefault(current, Integer.MAX_VALUE);
                int tentativeG = (curG == Integer.MAX_VALUE ? Integer.MAX_VALUE : curG + 1);
                int oldG = gScore.getOrDefault(nb, Integer.MAX_VALUE);

                if (tentativeG < oldG) {
                    previous.put(nb, current);
                    gScore.put(nb, tentativeG);
                    fScore.put(nb, tentativeG + heuristic(nb, end));
                    openSet.add(nb);
                }
            }
        }

        // Пути нет
        return null;
    }
}
