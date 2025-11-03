package academy.maze.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DTO Tests")
class MazeDTOTest {

    /* ====================== POINT ТЕСТЫ ====================== */

    @Test
    @DisplayName("Point создаётся с корректными координатами")
    void testPointCreation() {
        Point p = new Point(5, 10);
        assertEquals(5, p.x());
        assertEquals(10, p.y());
    }

    @Test
    @DisplayName("Point с нулевыми координатами")
    void testPointZero() {
        Point p = new Point(0, 0);
        assertEquals(0, p.x());
        assertEquals(0, p.y());
    }

    @Test
    @DisplayName("Point с большими координатами")
    void testPointLarge() {
        Point p = new Point(1000, 1000);
        assertEquals(1000, p.x());
        assertEquals(1000, p.y());
    }

    @Test
    @DisplayName("Два Point с одинаковыми координатами равны")
    void testPointEquality() {
        Point p1 = new Point(5, 10);
        Point p2 = new Point(5, 10);
        assertEquals(p1, p2);
    }

    @Test
    @DisplayName("Два Point с разными координатами не равны")
    void testPointInequality() {
        Point p1 = new Point(5, 10);
        Point p2 = new Point(5, 11);
        assertNotEquals(p1, p2);
    }

    /* ====================== MAZE ТЕСТЫ ====================== */

    @Test
    @DisplayName("Maze создаётся с правильной сеткой")
    void testMazeCreation() {
        CellType[][] cells = new CellType[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                cells[i][j] = CellType.WALL;
            }
        }
        Maze maze = new Maze(cells);
        assertEquals(5, maze.cells().length);
        assertEquals(5, maze.cells()[0].length);
    }

    @Test
    @DisplayName("Maze с минимальными размерами")
    void testMazeMinimum() {
        CellType[][] cells = new CellType[1][1];
        cells[0][0] = CellType.PATH;
        Maze maze = new Maze(cells);
        assertEquals(1, maze.cells().length);
        assertEquals(1, maze.cells()[0].length);
    }

    @Test
    @DisplayName("Maze с большими размерами")
    void testMazeLarge() {
        CellType[][] cells = new CellType[100][100];
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                cells[i][j] = CellType.PATH;
            }
        }
        Maze maze = new Maze(cells);
        assertEquals(100, maze.cells().length);
        assertEquals(100, maze.cells()[0].length);
    }

    @Test
    @DisplayName("Maze асимметричные размеры")
    void testMazeAsymmetric() {
        CellType[][] cells = new CellType[10][20];
        Maze maze = new Maze(cells);
        assertEquals(10, maze.cells().length);
        assertEquals(20, maze.cells()[0].length);
    }

    /* ====================== PATH ТЕСТЫ ====================== */

    @Test
    @DisplayName("Path создаётся с массивом точек")
    void testPathCreation() {
        Point[] points = {new Point(0, 0), new Point(1, 1), new Point(2, 2)};
        Path path = new Path(points);
        assertEquals(3, path.points().length);
        assertEquals(points[0], path.points()[0]);
    }

    @Test
    @DisplayName("Path с одной точкой")
    void testPathSinglePoint() {
        Point[] points = {new Point(5, 5)};
        Path path = new Path(points);
        assertEquals(1, path.points().length);
        assertEquals(5, path.points()[0].x());
    }

    @Test
    @DisplayName("Path с длинной последовательностью")
    void testPathLong() {
        int length = 100;
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = new Point(i, i);
        }
        Path path = new Path(points);
        assertEquals(length, path.points().length);
        assertEquals(0, path.points()[0].x());
        assertEquals(99, path.points()[99].x());
    }

    /* ====================== CELLTYPE ТЕСТЫ ====================== */

    @Test
    @DisplayName("CellType.WALL существует")
    void testCellTypeWall() {
        CellType cell = CellType.WALL;
        assertNotNull(cell);
        assertEquals(CellType.WALL, cell);
    }

    @Test
    @DisplayName("CellType.PATH существует")
    void testCellTypePath() {
        CellType cell = CellType.PATH;
        assertNotNull(cell);
        assertEquals(CellType.PATH, cell);
    }

    @Test
    @DisplayName("CellType.START существует")
    void testCellTypeStart() {
        CellType cell = CellType.START;
        assertNotNull(cell);
        assertEquals(CellType.START, cell);
    }

    @Test
    @DisplayName("CellType.END существует")
    void testCellTypeEnd() {
        CellType cell = CellType.END;
        assertNotNull(cell);
        assertEquals(CellType.END, cell);
    }

    @Test
    @DisplayName("CellType.ROUTE существует")
    void testCellTypeRoute() {
        CellType cell = CellType.ROUTE;
        assertNotNull(cell);
        assertEquals(CellType.ROUTE, cell);
    }

    @Test
    @DisplayName("Все CellType отличаются друг от друга")
    void testAllCellTypesDifferent() {
        assertNotEquals(CellType.WALL, CellType.PATH);
        assertNotEquals(CellType.WALL, CellType.START);
        assertNotEquals(CellType.PATH, CellType.END);
        assertNotEquals(CellType.START, CellType.ROUTE);
    }

    /* ====================== ИНТЕГРАЦИОННЫЕ СЦЕНАРИИ ====================== */

    @Test
    @DisplayName("Создание полного лабиринта с разными типами ячеек")
    void testFullMazeCreation() {
        CellType[][] cells = new CellType[3][3];
        cells[0][0] = CellType.START;
        cells[0][1] = CellType.PATH;
        cells[0][2] = CellType.END;
        cells[1][0] = CellType.WALL;
        cells[1][1] = CellType.ROUTE;
        cells[1][2] = CellType.WALL;
        cells[2][0] = CellType.WALL;
        cells[2][1] = CellType.WALL;
        cells[2][2] = CellType.WALL;

        Maze maze = new Maze(cells);
        assertEquals(3, maze.cells().length);
        assertEquals(CellType.START, maze.cells()[0][0]);
        assertEquals(CellType.ROUTE, maze.cells()[1][1]);
        assertEquals(CellType.END, maze.cells()[0][2]);
    }

    @Test
    @DisplayName("Создание пути между двумя точками")
    void testPathBetweenPoints() {
        Point start = new Point(0, 0);
        Point mid = new Point(1, 1);
        Point end = new Point(2, 2);

        Path path = new Path(new Point[] {start, mid, end});
        assertEquals(3, path.points().length);
        assertEquals(start, path.points()[0]);
        assertEquals(mid, path.points()[1]);
        assertEquals(end, path.points()[2]);
    }

    @Test
    @DisplayName("Модификация ячейки в лабиринте")
    void testCellModification() {
        CellType[][] cells = new CellType[2][2];
        cells[0][0] = CellType.WALL;
        Maze maze = new Maze(cells);

        // Изменяем ячейку
        maze.cells()[0][0] = CellType.PATH;
        assertEquals(CellType.PATH, maze.cells()[0][0]);
    }
}
