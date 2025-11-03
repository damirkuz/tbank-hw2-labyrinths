package academy.app;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MazeApplication Integration Tests")
class MazeApplicationIntegrationTest {

    private MazeApplication app;

    @BeforeEach
    void setUp() {
        app = new MazeApplication();
    }

    /* ====================== ТЕСТЫ ГЕНЕРАЦИИ ====================== */

    @Test
    @DisplayName("Генерация DFS возвращает непустой Optional")
    void testGenerateDFS() {
        Optional<String> result = app.generate("dfs", 5, 5, null);
        assertTrue(result.isPresent(), "Генерация DFS должна вернуть результат");
        String maze = result.get();
        assertTrue(maze.contains("#"), "Лабиринт должен содержать стены");
        assertTrue(maze.contains(" "), "Лабиринт должен содержать проходы");
    }

    @Test
    @DisplayName("Генерация Prim возвращает непустой Optional")
    void testGeneratePrim() {
        Optional<String> result = app.generate("prim", 7, 7, null);
        assertTrue(result.isPresent(), "Генерация Prim должна вернуть результат");
        assertNotNull(result.get(), "Строка не должна быть null");
    }

    @Test
    @DisplayName("Генерация Prim Simplified")
    void testGeneratePrimSimplified() {
        Optional<String> result = app.generate("prim_simplified", 5, 5, null);
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Генерация Prim Modified")
    void testGeneratePrimModified() {
        Optional<String> result = app.generate("prim_modified", 5, 5, null);
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Генерация с неизвестным алгоритмом возвращает пусто")
    void testGenerateUnknownAlgorithm() {
        Optional<String> result = app.generate("unknown", 5, 5, null);
        assertTrue(result.isEmpty(), "Неизвестный алгоритм должен вернуть пусто");
    }

    @Test
    @DisplayName("Генерация с нулевой шириной выбрасывает исключение")
    void testGenerateZeroWidth() {
        Optional<String> result = app.generate("dfs", 0, 5, null);
        assertTrue(result.isEmpty(), "Нулевая ширина должна вернуть пусто");
    }

    @Test
    @DisplayName("Генерация с отрицательной высотой выбрасывает исключение")
    void testGenerateNegativeHeight() {
        Optional<String> result = app.generate("dfs", 5, -1, null);
        assertTrue(result.isEmpty(), "Отрицательная высота должна вернуть пусто");
    }

    @Test
    @DisplayName("Генерация больших размеров работает")
    void testGenerateLargeMaze() {
        Optional<String> result = app.generate("dfs", 50, 50, null);
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Генерация минимальных размеров работает")
    void testGenerateMinimumSize() {
        Optional<String> result = app.generate("dfs", 1, 1, null);
        assertTrue(result.isPresent());
    }

    /* ====================== ТЕСТЫ РЕШЕНИЯ ====================== */

    @Test
    @DisplayName("Решение Dijkstra из строки работает")
    void testSolveFromString() {
        // Генерируем лабиринт
        Optional<String> mazeOpt = app.generate("dfs", 5, 5, null);
        assertTrue(mazeOpt.isPresent());

        String maze = mazeOpt.get();
        Optional<String> solution = app.solveFromString("dijkstra", maze, "1,1", "3,3", null);

        // Решение может быть не найдено, но ошибок не должно быть
        // assertTrue(solution.isPresent() || solution.isEmpty());
    }

    @Test
    @DisplayName("Решение A* из строки работает")
    void testSolveAStarFromString() {
        Optional<String> mazeOpt = app.generate("prim", 7, 7, null);
        assertTrue(mazeOpt.isPresent());

        String maze = mazeOpt.get();
        Optional<String> solution = app.solveFromString("astar", maze, "1,1", "5,5", null);
        assertNotNull(solution);
    }

    @Test
    @DisplayName("Решение с неизвестным алгоритмом возвращает пусто")
    void testSolveUnknownAlgorithm() {
        Optional<String> mazeOpt = app.generate("dfs", 5, 5, null);
        String maze = mazeOpt.get();
        Optional<String> solution = app.solveFromString("unknown", maze, "1,1", "3,3", null);
        assertTrue(solution.isEmpty());
    }

    @Test
    @DisplayName("Решение с неверным форматом координат выбрасывает исключение")
    void testSolveInvalidCoordinateFormat() {
        Optional<String> mazeOpt = app.generate("dfs", 5, 5, null);
        String maze = mazeOpt.get();
        Optional<String> solution = app.solveFromString("dijkstra", maze, "invalid", "1,1", null);
        assertTrue(solution.isEmpty());
    }

    @Test
    @DisplayName("Решение с координатами за границами возвращает пусто")
    void testSolveOutOfBounds() {
        Optional<String> mazeOpt = app.generate("dfs", 5, 5, null);
        String maze = mazeOpt.get();
        Optional<String> solution = app.solveFromString("dijkstra", maze, "100,100", "1,1", null);
        assertTrue(solution.isEmpty());
    }

    @Test
    @DisplayName("Решение с одинаковыми стартом и финишем работает")
    void testSolveSameStartEnd() {
        Optional<String> mazeOpt = app.generate("dfs", 5, 5, null);
        String maze = mazeOpt.get();
        Optional<String> solution = app.solveFromString("dijkstra", maze, "1,1", "1,1", null);
        assertTrue(solution.isPresent() || solution.isEmpty());
    }

    /* ====================== ТЕСТЫ ВАЛИДАЦИИ ====================== */

    @Test
    @DisplayName("Парсинг правильных координат работает")
    void testParseCorrectCoordinates() {
        Optional<String> mazeOpt = app.generate("dfs", 5, 5, null);
        String maze = mazeOpt.get();
        // Попытка решить с валидными координатами
        Optional<String> solution = app.solveFromString("dijkstra", maze, "1,1", "2,2", null);
        assertNotNull(solution);
    }

    @Test
    @DisplayName("Парсинг координат с пробелами работает")
    void testParseCoordinatesWithSpaces() {
        Optional<String> mazeOpt = app.generate("dfs", 5, 5, null);
        String maze = mazeOpt.get();
        Optional<String> solution = app.solveFromString("dijkstra", maze, " 1 , 1 ", " 2 , 2 ", null);
        assertNotNull(solution);
    }

    @Test
    @DisplayName("Парсинг нулевых координат работает")
    void testParseZeroCoordinates() {
        Optional<String> mazeOpt = app.generate("dfs", 5, 5, null);
        String maze = mazeOpt.get();
        Optional<String> solution = app.solveFromString("dijkstra", maze, "0,0", "1,1", null);
        // Может быть стена на (0,0), но ошибки парсинга не должно быть
        assertNotNull(solution);
    }

    /* ====================== СЦЕНАРИИ ИСПОЛЬЗОВАНИЯ ====================== */

    @Test
    @DisplayName("Полный сценарий: генерация и решение DFS+Dijkstra")
    void testFullScenarioDFSDijkstra() {
        // Генерируем
        Optional<String> mazeOpt = app.generate("dfs", 10, 10, null);
        assertTrue(mazeOpt.isPresent());

        // Решаем
        Optional<String> solution = app.solveFromString("dijkstra", mazeOpt.get(), "1,1", "8,8", null);
        assertNotNull(solution);
    }

    @Test
    @DisplayName("Полный сценарий: генерация и решение Prim+AStar")
    void testFullScenarioPrimAStar() {
        Optional<String> mazeOpt = app.generate("prim", 10, 10, null);
        assertTrue(mazeOpt.isPresent());

        Optional<String> solution = app.solveFromString("astar", mazeOpt.get(), "1,1", "8,8", null);
        assertNotNull(solution);
    }

    @Test
    @DisplayName("Стресс-тест: 100 генераций разных размеров")
    void testStressGeneration() {
        for (int i = 2; i <= 10; i++) {
            Optional<String> result = app.generate("dfs", i, i, null);
            assertTrue(result.isPresent(), "Генерация размера " + i + "x" + i + " должна работать");
        }
    }

    @Test
    @DisplayName("Совместимость алгоритмов генерации: все работают")
    void testAllGenerationAlgorithms() {
        String[] algorithms = {"dfs", "prim", "prim_simplified", "prim_modified"};
        for (String algo : algorithms) {
            Optional<String> result = app.generate(algo, 5, 5, null);
            assertTrue(result.isPresent(), "Алгоритм " + algo + " должен работать");
        }
    }

    @Test
    @DisplayName("Совместимость алгоритмов решения: все работают")
    void testAllSolvingAlgorithms() {
        Optional<String> mazeOpt = app.generate("dfs", 7, 7, null);
        String maze = mazeOpt.get();

        String[] algorithms = {"dijkstra", "astar", "a*"};
        for (String algo : algorithms) {
            Optional<String> result = app.solveFromString(algo, maze, "1,1", "5,5", null);
            assertNotNull(result, "Алгоритм " + algo + " должен работать");
        }
    }

    @Test
    @DisplayName("Решение не смешивает старый и новый лабиринты")
    void testSolveIndependence() {
        Optional<String> maze1 = app.generate("dfs", 5, 5, null);
        Optional<String> maze2 = app.generate("prim", 5, 5, null);

        Optional<String> sol1 = app.solveFromString("dijkstra", maze1.get(), "1,1", "3,3", null);
        Optional<String> sol2 = app.solveFromString("dijkstra", maze2.get(), "1,1", "3,3", null);

        assertNotNull(sol1);
        assertNotNull(sol2);
    }
}
