package academy.maze.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Упрощённая версия алгоритма Прима для генерации лабиринтов. Вместо фронтира рёбер использует список посещённых камер:
 * - Стартует с камеры (0,0) - На каждом шаге случайно выбирает любую посещённую камеру - Если у неё есть непосещённые
 * соседи, соединяет с одним из них - Если соседей нет, удаляет камеру из списка активных
 */
public class PrimSimplifiedGenerator extends BaseGenerator {

    @Override
    protected void privateGenerate(CellType[][] out, int innerWidth, int innerHeight) {
        int cellGridWidth = getCellGridWidth(innerWidth);
        int cellGridHeight = getCellGridHeight(innerHeight);
        boolean[][] vis = new boolean[cellGridHeight][cellGridWidth];
        List<Point> visitedCells = new ArrayList<>();

        // Стартуем с (0, 0)
        int startCellX = 0;
        int startCellY = 0;
        vis[startCellY][startCellX] = true;
        out[toGridY(startCellY)][toGridX(startCellX)] = CellType.PATH;
        visitedCells.add(new Point(startCellX, startCellY));

        while (!visitedCells.isEmpty()) {
            // Выбираем случайную посещённую камеру
            int idx = random.nextInt(visitedCells.size());
            Point cell = visitedCells.get(idx);
            int cellX = cell.x();
            int cellY = cell.y();

            // Ищем непосещённых соседей
            List<int[]> neighbors = getUnvisitedNeighbors(cellX, cellY, cellGridWidth, cellGridHeight, vis);

            if (neighbors.isEmpty()) {
                // У этой камеры нет непосещённых соседей — удаляем из списка
                visitedCells.remove(idx);
                continue;
            }

            // Выбираем случайного соседа
            int[] chosen = neighbors.get(random.nextInt(neighbors.size()));
            int neighborX = chosen[0];
            int neighborY = chosen[1];

            carvePassage(out, cellX, cellY, neighborX, neighborY, vis);
            visitedCells.add(new Point(neighborX, neighborY));
        }
    }
}
