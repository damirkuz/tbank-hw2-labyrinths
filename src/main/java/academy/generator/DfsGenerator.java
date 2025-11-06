package academy.generator;

import academy.maze.dto.CellType;

/**
 * Генератор лабиринтов на основе алгоритма поиска в глубину (DFS). Использует рекурсивный обход: - Начинает с камеры
 * (0,0) и случайно выбирает направление движения - Рекурсивно углубляется в непосещённые соседние камеры - При тупике
 * возвращается назад и пробует другие направления
 */
public class DfsGenerator extends BaseGenerator {

    @Override
    protected void privateGenerate(CellType[][] out, int innerWidth, int innerHeight) {
        int CW = camW(innerWidth);
        int CH = camH(innerHeight);
        // создаём сетку "камер"
        boolean[][] visited = new boolean[CH][CW];

        // Запускаем DFS от (0,0) в логическом пространстве
        dfsCarve(out, 0, 0, visited, CW, CH);
    }

    private void dfsCarve(CellType[][] out, int cx, int cy, boolean[][] visited, int CW, int CH) {
        // помечаем текущую клетку как посещённую
        visited[cy][cx] = true;
        out[oy(cy)][ox(cx)] = CellType.PATH;

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        shuffleDirections(dirs);

        for (int[] d : dirs) {
            // берём случайную соседнюю камеру
            int ncx = cx + d[0], ncy = cy + d[1];
            if (!inCamBounds(ncx, ncy, CW, CH) || visited[ncy][ncx]) continue;

            // пробиваем проход между текущей камерой и случайной
            carvePassage(out, cx, cy, ncx, ncy, visited);
            // рекурсивно повторяем
            dfsCarve(out, ncx, ncy, visited, CW, CH);
        }
    }
}
