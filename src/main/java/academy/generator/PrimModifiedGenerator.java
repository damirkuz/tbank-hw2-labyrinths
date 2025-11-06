package academy.generator;

import academy.maze.dto.CellType;
import java.util.ArrayList;
import java.util.List;

/**
 * Модифицированная версия алгоритма Прима для генерации лабиринтов. Использует фронтир узлов-кандидатов (parent →
 * child): - Стартует со случайной камеры - Ведёт список пар (родитель, кандидат) для потенциального соединения - На
 * каждом шаге случайно выбирает пару из списка и соединяет камеры - Добавляет новых кандидатов от только что
 * подключённой камеры
 */
public class PrimModifiedGenerator extends BaseGenerator {

    private record Node(int px, int py, int cx, int cy) {}

    @Override
    protected void privateGenerate(CellType[][] out, int innerW, int innerH) {
        int CW = camW(innerW), CH = camH(innerH);
        boolean[][] vis = new boolean[CH][CW];
        List<Node> frontier = new ArrayList<>();

        int sx = random.nextInt(CW);
        int sy = random.nextInt(CH);
        vis[sy][sx] = true;
        out[oy(sy)][ox(sx)] = CellType.PATH;
        addFrontier(sx, sy, CW, CH, vis, frontier);

        while (!frontier.isEmpty()) {
            int idx = random.nextInt(frontier.size());
            Node n = frontier.remove(idx);

            if (vis[n.cy][n.cx]) continue;

            carvePassage(out, n.px, n.py, n.cx, n.cy, vis);
            addFrontier(n.cx, n.cy, CW, CH, vis, frontier);
        }
    }

    private void addFrontier(int cx, int cy, int CW, int CH, boolean[][] vis, List<Node> frontier) {
        for (int[] d : DIRS) {
            int nx = cx + d[0], ny = cy + d[1];
            if (inCamBounds(nx, ny, CW, CH) && !vis[ny][nx]) {
                frontier.add(new Node(cx, cy, nx, ny));
            }
        }
    }
}
