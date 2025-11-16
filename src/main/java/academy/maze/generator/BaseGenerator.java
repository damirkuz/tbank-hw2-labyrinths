package academy.maze.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Direction;
import academy.maze.dto.Maze;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class BaseGenerator implements Generator {

    protected final Random random = new Random();

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
    protected int toGridX(int cellX) {
        return 1 + 2 * cellX;
    }

    protected int toGridY(int cellY) {
        return 1 + 2 * cellY;
    }

    protected int getCellGridWidth(int innerWidth) {
        return (innerWidth + 1) / 2;
    }

    protected int getCellGridHeight(int innerHeight) {
        return (innerHeight + 1) / 2;
    }

    protected boolean isCellInBounds(int cellX, int cellY, int cellWidth, int cellHeight) {
        return cellX >= 0 && cellX < cellWidth && cellY >= 0 && cellY < cellHeight;
    }

    /** Пробивает перегородку между двумя соседними камерами. */
    protected void carvePassage(CellType[][] out, int cx, int cy, int nx, int ny, boolean[][] vis) {
        int dx = Integer.signum(nx - cx);
        int dy = Integer.signum(ny - cy);

        out[toGridY(cy)][toGridX(cx)] = CellType.PATH; // исходная камера
        out[toGridY(cy) + dy][toGridX(cx) + dx] = CellType.PATH; // перегородка
        out[toGridY(ny)][toGridX(nx)] = CellType.PATH; // целевая камера
        vis[ny][nx] = true;
    }

    protected Direction[] getRandomDirections() {
        Direction[] dirs = Direction.values();
        for (int i = dirs.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Direction temp = dirs[i];
            dirs[i] = dirs[index];
            dirs[index] = temp;
        }
        return dirs;
    }

    /** Получает всех непосещённых соседей камеры. */
    protected List<int[]> getUnvisitedNeighbors(int cx, int cy, int CW, int CH, boolean[][] vis) {
        List<int[]> neighbors = new ArrayList<>();
        for (Direction d : Direction.values()) {
            int nx = cx + d.getX(), ny = cy + d.getY();
            if (isCellInBounds(nx, ny, CW, CH) && !vis[ny][nx]) {
                neighbors.add(new int[] {nx, ny, d.getX(), d.getY()});
            }
        }
        return neighbors;
    }
}
