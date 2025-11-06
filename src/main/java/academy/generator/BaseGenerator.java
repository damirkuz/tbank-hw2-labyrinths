package academy.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class BaseGenerator implements Generator {

    protected final Random random = new Random();
    static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    @Override
    public Maze generate(int width, int height) {
        // Создаём сетку лабиринта
        CellType[][] out = new CellType[height + 2][width + 2];
        // Заполняем стенами
        for (int y = 0; y < height + 2; y++) {
            for (int x = 0; x < width + 2; x++) {
                out[y][x] = CellType.WALL;
            }
        }

        // запускаем алгоритм генерации из наследника
        privateGenerate(out, width, height);
        return new Maze(out);
    }

    protected abstract void privateGenerate(CellType[][] out, int innerWidth, int innerHeight);

    // Координатные преобразования
    protected int ox(int cx) {
        return 1 + 2 * cx;
    }

    protected int oy(int cy) {
        return 1 + 2 * cy;
    }

    protected int camW(int innerWidth) {
        return (innerWidth + 1) / 2;
    }

    protected int camH(int innerHeight) {
        return (innerHeight + 1) / 2;
    }

    protected boolean inCamBounds(int cx, int cy, int CW, int CH) {
        return cx >= 0 && cx < CW && cy >= 0 && cy < CH;
    }

    /** Пробивает перегородку между двумя соседними камерами. */
    protected void carvePassage(CellType[][] out, int cx, int cy, int nx, int ny, boolean[][] vis) {
        int dx = Integer.signum(nx - cx);
        int dy = Integer.signum(ny - cy);

        out[oy(cy)][ox(cx)] = CellType.PATH; // исходная камера
        out[oy(cy) + dy][ox(cx) + dx] = CellType.PATH; // перегородка
        out[oy(ny)][ox(nx)] = CellType.PATH; // целевая камера
        vis[ny][nx] = true;
    }

    /** Перемешивает массив направлений. */
    protected void shuffleDirections(int[][] dirs) {
        for (int i = dirs.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] t = dirs[i];
            dirs[i] = dirs[j];
            dirs[j] = t;
        }
    }

    /** Получает всех непосещённых соседей камеры. */
    protected List<int[]> getUnvisitedNeighbors(int cx, int cy, int CW, int CH, boolean[][] vis) {
        List<int[]> neighbors = new ArrayList<>();
        for (int[] d : DIRS) {
            int nx = cx + d[0], ny = cy + d[1];
            if (inCamBounds(nx, ny, CW, CH) && !vis[ny][nx]) {
                neighbors.add(new int[] {nx, ny, d[0], d[1]});
            }
        }
        return neighbors;
    }
}
