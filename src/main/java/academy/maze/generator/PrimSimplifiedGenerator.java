package academy.maze.generator;

import academy.maze.dto.CellType;
import java.util.ArrayList;
import java.util.List;

/**
 * Упрощённая версия алгоритма Прима для генерации лабиринтов. Вместо фронтира рёбер использует список посещённых камер:
 * - Стартует с камеры (0,0) - На каждом шаге случайно выбирает любую посещённую камеру - Если у неё есть непосещённые
 * соседи, соединяет с одним из них - Если соседей нет, удаляет камеру из списка активных
 */
public class PrimSimplifiedGenerator extends BaseGenerator {

    @Override
    protected void privateGenerate(CellType[][] out, int innerW, int innerH) {
        int CW = camW(innerW), CH = camH(innerH);
        boolean[][] vis = new boolean[CH][CW];
        List<int[]> visitedCells = new ArrayList<>();

        // Стартуем с (0, 0)
        int sx = 0, sy = 0;
        vis[sy][sx] = true;
        out[oy(sy)][ox(sx)] = CellType.PATH;
        visitedCells.add(new int[] {sx, sy});

        while (!visitedCells.isEmpty()) {
            // Выбираем случайную посещённую камеру
            int idx = random.nextInt(visitedCells.size());
            int[] cell = visitedCells.get(idx);
            int cx = cell[0], cy = cell[1];

            // Ищем непосещённых соседей
            List<int[]> neighbors = getUnvisitedNeighbors(cx, cy, CW, CH, vis);

            if (neighbors.isEmpty()) {
                // У этой камеры нет непосещённых соседей — удаляем из списка
                visitedCells.remove(idx);
                continue;
            }

            // Выбираем случайного соседа
            int[] chosen = neighbors.get(random.nextInt(neighbors.size()));
            int nx = chosen[0], ny = chosen[1];

            carvePassage(out, cx, cy, nx, ny, vis);
            visitedCells.add(new int[] {nx, ny});
        }
    }
}
