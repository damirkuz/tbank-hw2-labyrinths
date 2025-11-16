package academy.maze.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Direction;
import java.util.ArrayList;
import java.util.List;

/**
 * Классическая реализация алгоритма Прима для генерации лабиринтов. Использует фронтир рёбер (edge frontier): -
 * Стартует с камеры (0,0) - Ведёт список всех рёбер между посещёнными и непосещёнными камерами - На каждом шаге
 * случайно выбирает ребро из фронтира - Соединяет камеры через выбранное ребро и обновляет фронтир
 */
public class PrimTrueGenerator extends BaseGenerator {

    private record Edge(int cellX, int cellY, int neighbourX, int neighborY) {}

    @Override
    protected void privateGenerate(CellType[][] out, int innerWidth, int innerHeight) {
        int cellGridWidth = getCellGridWidth(innerWidth);
        int cellGridHeight = getCellGridHeight(innerHeight);
        boolean[][] vis = new boolean[cellGridHeight][cellGridWidth];
        List<Edge> frontier = new ArrayList<>();

        int sx = 0;
        int sy = 0;
        vis[sy][sx] = true;
        out[toGridY(sy)][toGridX(sx)] = CellType.PATH;

        // добавить рёбра из старта
        addEdges(sx, sy, cellGridWidth, cellGridHeight, vis, frontier);

        while (!frontier.isEmpty()) {
            int idx = random.nextInt(frontier.size());
            Edge e = frontier.remove(idx);
            if (vis[e.neighborY][e.neighbourX]) continue;

            carvePassage(out, e.cellX, e.cellY, e.neighbourX, e.neighborY, vis);
            // добавляем рёбра дальше
            addEdges(e.neighbourX, e.neighborY, cellGridWidth, cellGridHeight, vis, frontier);
        }
    }

    private void addEdges(int cellX, int cellY, int cellGridWidth, int cellGridHeight, boolean[][] visited, List<Edge> frontier) {
        for (Direction d : Direction.values()) {
            int nx = cellX + d.getX(), ny = cellY + d.getY();
            if (isCellInBounds(nx, ny, cellGridWidth, cellGridHeight) && !visited[ny][nx]) {
                frontier.add(new Edge(cellX, cellY, nx, ny));
            }
        }
    }
}
