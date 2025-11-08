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

    private record Edge(int cx, int cy, int nx, int ny) {}

    @Override
    protected void privateGenerate(CellType[][] out, int innerW, int innerH) {
        int CW = camW(innerW), CH = camH(innerH);
        boolean[][] vis = new boolean[CH][CW];
        List<Edge> frontier = new ArrayList<>();

        int sx = 0;
        int sy = 0;
        vis[sy][sx] = true;
        out[oy(sy)][ox(sx)] = CellType.PATH;

        // добавить рёбра из старта
        addEdges(sx, sy, CW, CH, vis, frontier);

        while (!frontier.isEmpty()) {
            int idx = random.nextInt(frontier.size());
            Edge e = frontier.remove(idx);
            if (vis[e.ny][e.nx]) continue;

            carvePassage(out, e.cx, e.cy, e.nx, e.ny, vis);
            // добавляем рёбра дальше
            addEdges(e.nx, e.ny, CW, CH, vis, frontier);
        }
    }

    private void addEdges(int cx, int cy, int CW, int CH, boolean[][] vis, List<Edge> frontier) {
        for (Direction d : Direction.values()) {
            int nx = cx + d.getX(), ny = cy + d.getY();
            if (inCamBounds(nx, ny, CW, CH) && !vis[ny][nx]) {
                frontier.add(new Edge(cx, cy, nx, ny));
            }
        }
    }
}
