package academy.maze.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Direction;
import java.util.ArrayList;
import java.util.List;

/**
 * Модифицированная версия алгоритма Прима для генерации лабиринтов. Использует фронтир узлов-кандидатов (parent →
 * child): - Стартует со случайной камеры - Ведёт список пар (родитель, кандидат) для потенциального соединения - На
 * каждом шаге случайно выбирает пару из списка и соединяет камеры - Добавляет новых кандидатов от только что
 * подключённой камеры
 */
public class PrimModifiedGenerator extends BaseGenerator {

    private record Node(int parentX, int parentY, int cellX, int cellY) {}

    @Override
    protected void privateGenerate(CellType[][] out, int innerW, int innerH) {
        int CW = getCellGridWidth(innerW), CH = getCellGridHeight(innerH);
        boolean[][] vis = new boolean[CH][CW];
        List<Node> frontier = new ArrayList<>();

        int sx = random.nextInt(CW);
        int sy = random.nextInt(CH);
        vis[sy][sx] = true;
        out[toGridY(sy)][toGridX(sx)] = CellType.PATH;
        addFrontier(sx, sy, CW, CH, vis, frontier);

        while (!frontier.isEmpty()) {
            int idx = random.nextInt(frontier.size());
            Node n = frontier.remove(idx);

            if (vis[n.cellY][n.cellX]) continue;

            carvePassage(out, n.parentX, n.parentY, n.cellX, n.cellY, vis);
            addFrontier(n.cellX, n.cellY, CW, CH, vis, frontier);
        }
    }

    private void addFrontier(
            int cellX, int cellY, int cellGridWidth, int cellGridHeight, boolean[][] visited, List<Node> frontier) {
        for (Direction d : Direction.values()) {
            int nx = cellX + d.getX(), ny = cellY + d.getY();
            if (isCellInBounds(nx, ny, cellGridWidth, cellGridHeight) && !visited[ny][nx]) {
                frontier.add(new Node(cellX, cellY, nx, ny));
            }
        }
    }
}
