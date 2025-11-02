package academy.app;

import academy.generator.*;
import academy.io.MazeFileReader;
import academy.io.MazeFileWriter;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import academy.renderer.ConsoleMazeRenderer;
import academy.renderer.MazeRenderer;
import academy.solver.*;
import academy.validation.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Главное приложение для работы с лабиринтами.
 * Поддерживает команды:
 * - generate: генерирует новый лабиринт
 * - solve: решает существующий лабиринт
 */
public class MazeApplication {

    private static final Logger log = LoggerFactory.getLogger(MazeApplication.class);

    private final MazeRenderer renderer = new ConsoleMazeRenderer();
    private final MazeFileWriter writer = new MazeFileWriter();
    private final MazeFileReader reader = new MazeFileReader();

    public void generate(String algorithm, int width, int height, String outputFile) {
        try {
            InputValidator.validateDimensions(width, height);

            // Создаём нужный генератор
            Generator generator = createGenerator(algorithm);
            if (generator == null) {
                log.error("Ошибка: неизвестный алгоритм генерации '{}'", algorithm);
                log.error("Доступные алгоритмы: dfs, prim, prim_simplified, prim_modified");
                return;
            }

            // Генерируем лабиринт
            log.info("Генерирую лабиринт {}x{} методом {}...", width, height, algorithm);
            Maze maze = generator.generate(width, height);

            // Выводим или сохраняем результат
            if (outputFile != null && !outputFile.isEmpty()) {
                writer.write(maze, outputFile);
                log.info("Лабиринт сохранён в файл: {}", outputFile);
            } else {
                log.info("Сгенерированный лабиринт:");
                // Сам ASCII-рендер выводим в info, чтобы видеть в логе
                log.info("\n{}", renderer.render(maze));
            }

        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации: {}", e.getMessage());
        } catch (IOException e) {
            log.error("Ошибка при работе с файлом: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка генерации", e);
        }
    }

    public void solve(String algorithm, String inputFile, String startStr, String endStr, String outputFile) {
        try {
            Point start = InputValidator.parsePoint(startStr);
            Point end = InputValidator.parsePoint(endStr);

            log.info("Читаю лабиринт из файла: {}", inputFile);
            Maze maze = reader.read(inputFile);

            if (start.x() >= maze.cells()[0].length || start.y() >= maze.cells().length ||
                end.x() >= maze.cells()[0].length || end.y() >= maze.cells().length) {
                log.error("Ошибка: координаты выходят за границы лабиринта");
                return;
            }

            Solver solver = createSolver(algorithm);
            if (solver == null) {
                log.error("Ошибка: неизвестный алгоритм решения '{}'", algorithm);
                log.error("Доступные алгоритмы: dijkstra, astar");
                return;
            }

            log.info("Ищу путь от {} к {} методом {}...", startStr, endStr, algorithm);
            var path = solver.solve(maze, start, end);

            if (path == null || path.points().length == 0) {
                log.warn("Решение не найдено — пути нет");
                return;
            }

            markPathOnMaze(maze, path, start, end);

            if (outputFile != null && !outputFile.isEmpty()) {
                writer.write(maze, outputFile);
                log.info("Решение сохранено в файл: {}", outputFile);
                log.info("Длина пути: {}", path.points().length);
            } else {
                log.info("Лабиринт с решением:");
                log.info("\n{}", renderer.render(maze));
                log.info("Длина пути: {}", path.points().length);
            }

        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации: {}", e.getMessage());
        } catch (IOException e) {
            log.error("Ошибка при работе с файлом: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при решении лабиринта", e);
        }
    }

    private void markPathOnMaze(Maze maze, academy.maze.dto.Path path, Point start, Point end) {
        Point[] points = path.points();

        for (Point p : points) {
            if (!p.equals(start) && !p.equals(end)) {
                maze.cells()[p.y()][p.x()] = CellType.ROUTE;
            }
        }

        maze.cells()[start.y()][start.x()] = CellType.START;
        maze.cells()[end.y()][end.x()] = CellType.END;
    }

    private Generator createGenerator(String algorithm) {
        return switch (algorithm.toLowerCase()) {
            case "dfs" -> new DfsGenerator();
            case "prim", "prim_true" -> new PrimTrueGenerator();
            case "prim_simplified" -> new PrimSimplifiedGenerator();
            case "prim_modified" -> new PrimModifiedGenerator();
            default -> null;
        };
    }

    private Solver createSolver(String algorithm) {
        return switch (algorithm.toLowerCase()) {
            case "dijkstra" -> new DijkstraSolver();
            case "astar", "a*" -> new AStarSolver();
            default -> null;
        };
    }
}
