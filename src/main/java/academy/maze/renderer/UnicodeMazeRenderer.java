package academy.maze.renderer;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;

public class UnicodeMazeRenderer implements MazeRenderer {

    @Override
    public String render(Maze maze) {
        StringBuilder sb = new StringBuilder();
        CellType[][] cells = maze.cells();
        int h = cells.length;
        int w = cells[0].length;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                CellType t = cells[y][x];
                sb.append(
                        switch (t) {
                            case START -> '◉'; // старт
                            case END -> '★'; // финиш
                            case ROUTE -> '•'; // найденный путь
                            case PATH -> ' '; // пустота
                            case WALL -> wallChar(cells, x, y); // псевдографика
                        });
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private char wallChar(CellType[][] cells, int x, int y) {
        int h = cells.length, w = cells[0].length;

        boolean up = isWall(cells, x, y - 1, w, h);
        boolean down = isWall(cells, x, y + 1, w, h);
        boolean left = isWall(cells, x - 1, y, w, h);
        boolean right = isWall(cells, x + 1, y, w, h);

        int deg = (up ? 1 : 0) + (down ? 1 : 0) + (left ? 1 : 0) + (right ? 1 : 0);

        // 4-соединение
        if (up && down && left && right) return '┼';

        // Тройники (T-образные соединения): символ указывает сторону, куда нет линии
        if (deg == 3) {
            if (!up) return '┬';
            if (!down) return '┴';
            if (!left) return '├';
            /* !right */ return '┤';
        }

        // Два направления: противоположные (линии) или смежные (углы)
        if (deg == 2) {
            // Противоположные
            if (up && down && !left && !right) return '│';
            if (left && right && !up && !down) return '─';

            // Углы (смежные)
            if (up && right) return '└';
            if (up && left) return '┘';
            if (down && right) return '┌';
            /* down && left */ return '┐';
        }

        // Окончания линий
        if (deg == 1) {
            if (up) return '╵'; // линия вверх от текущей точки
            if (down) return '╷'; // линия вниз от текущей точки
            if (left) return '╴'; // линия влево от текущей точки
            /* right */ return '╶'; // линия вправо от текущей точки
        }

        // Нет соседей-стен
        return '■';
    }

    private boolean isWall(CellType[][] cells, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x >= w || y >= h) return false;
        return cells[y][x] == CellType.WALL;
    }
}
