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
import java.util.Optional;

/**
 * Главное приложение для работы с лабиринтами.
 * Поддерживает:
 * - generate: генерация лабиринта
 * - solveFromFile: решение из файла
 * - solveFromString: решение из ASCII-строки (renderer.render)
 */
public class MazeApplication {

    private static final Logger log = LoggerFactory.getLogger(MazeApplication.class);

    private final MazeRenderer renderer = new ConsoleMazeRenderer();
    private final MazeFileWriter writer = new MazeFileWriter();
    private final MazeFileReader reader = new MazeFileReader();


    public Optional<String> generate(String algorithm, int width, int height, String outputFile) {
        try {
            InputValidator.validateDimensions(width, height);

            Generator generator = createGenerator(algorithm);
            if (generator == null) {
                log.error("Ошибка: неизвестный алгоритм генерации '{}'", algorithm);
                log.error("Доступные алгоритмы: dfs, prim, prim_simplified, prim_modified");
                return Optional.empty();
            }

            log.info("Генерирую лабиринт {}x{} методом {}...", width, height, algorithm);
            Maze maze = generator.generate(width, height);

            if (outputFile != null && !outputFile.isEmpty()) {
                try {
                    writer.write(maze, outputFile);
                    log.info("Лабиринт сохранён в файл: {}", outputFile);
                    return Optional.empty();
                } catch (IOException io) {
                    log.error("Не удалось сохранить в файл '{}': {}", outputFile, io.getMessage());
                    String mazeString = renderer.render(maze);
                    log.info("Вывожу лабиринт в консоль:\n{}", mazeString);
                    return Optional.of(mazeString);
                }
            } else {
                String mazeString = renderer.render(maze);
                log.info("Сгенерированный лабиринт:\n{}", mazeString);
                return Optional.of(mazeString);
            }

        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка генерации", e);
        }
        return Optional.empty();
    }


    // Точка входа из файла или строки
    public Optional<String> solve(String algorithm, String inputFile, String startStr, String endStr, String outputFile) {
        return solveInternal(algorithm, inputFile, null, startStr, endStr, outputFile);
    }

    public Optional<String> solveFromFile(String algorithm, String inputFile, String startStr, String endStr, String outputFile) {
        return solveInternal(algorithm, inputFile, null, startStr, endStr, outputFile);
    }

    public Optional<String> solveFromString(String algorithm, String mazeText, String startStr, String endStr, String outputFile) {
        return solveInternal(algorithm, null, mazeText, startStr, endStr, outputFile);
    }

    // Внутренний общий метод
    private Optional<String> solveInternal(String algorithm,
                                           String inputFile,
                                           String mazeText,
                                           String startStr,
                                           String endStr,
                                           String outputFile) {
        try {
            Point start = InputValidator.parsePoint(startStr);
            Point end = InputValidator.parsePoint(endStr);

            // Загрузка лабиринта: либо из файла, либо из строки
            Maze maze;
            if (mazeText != null) {
                log.info("Читаю лабиринт из строки (render)");
                maze = loadMazeFromString(mazeText);
            } else {
                log.info("Читаю лабиринт из файла: {}", inputFile);
                maze = reader.read(inputFile);
            }

            if (!validateBounds(maze, start, end)) {
                log.error("Ошибка: координаты выходят за границы лабиринта");
                return Optional.empty();
            }

            Solver solver = createSolver(algorithm);
            if (solver == null) {
                log.error("Ошибка: неизвестный алгоритм решения '{}'", algorithm);
                log.error("Доступные алгоритмы: dijkstra, astar");
                return Optional.empty();
            }

            log.info("Ищу путь от {} к {} методом {}...", startStr, endStr, algorithm);
            var path = solver.solve(maze, start, end);

            if (path == null || path.points().length == 0) {
                log.warn("Решение не найдено — пути нет");
                return Optional.empty();
            }

            markPathOnMaze(maze, path, start, end);
            String mazeString = renderer.render(maze);

            if (outputFile != null && !outputFile.isEmpty()) {
                try {
                    writer.write(maze, outputFile);
                    log.info("Решение сохранено в файл: {}", outputFile);
                    log.info("Длина пути: {}", path.points().length);
                    return Optional.of(mazeString);
                } catch (IOException io) {
                    log.error("Не удалось записать решение в файл '{}': {}", outputFile, io.getMessage());
                    log.info("Вывожу решение в консоль:\n{}", mazeString);
                    return Optional.of(mazeString);
                }
            } else {
                log.info("Лабиринт с решением:\n{}", mazeString);
                log.info("Длина пути: {}", path.points().length);
                return Optional.of(mazeString);
            }

        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации: {}", e.getMessage());
        } catch (IOException e) {
            log.error("Ошибка при работе с файлом: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при решении лабиринта", e);
        }
        return Optional.empty();
    }


    private boolean validateBounds(Maze maze, Point start, Point end) {
        int H = maze.cells().length;
        int W = maze.cells()[0].length;
        return start.x() >= 0 && start.y() >= 0 && end.x() >= 0 && end.y() >= 0
            && start.x() < W && end.x() < W && start.y() < H && end.y() < H;
    }

    private Maze loadMazeFromString(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Пустой текст лабиринта");
        }
        String norm = text.replace("\r\n", "\n");
        String[] lines = norm.split("\n", -1);
        if (lines.length > 0 && lines[lines.length - 1].isEmpty()) {
            String[] tmp = new String[lines.length - 1];
            System.arraycopy(lines, 0, tmp, 0, lines.length - 1);
            lines = tmp;
        }
        if (lines.length == 0) throw new IllegalArgumentException("Пустой текст лабиринта");

        int height = lines.length;
        int width = lines[0].length();
        CellType[][] cells = new CellType[height][width];
        for (int y = 0; y < height; y++) {
            String line = lines[y];
            if (line.length() != width) {
                throw new IllegalArgumentException("Неравномерная длина строк лабиринта");
            }
            for (int x = 0; x < width; x++) {
                char ch = line.charAt(x);
                cells[y][x] = charToCell(ch);
            }
        }
        return new Maze(cells);
    }

    private CellType charToCell(char ch) {
        return switch (ch) {
            case '#' -> CellType.WALL;
            case ' ' -> CellType.PATH;
            case 'O' -> CellType.START;
            case 'X' -> CellType.END;
            case '.' -> CellType.ROUTE;
            default -> CellType.WALL;
        };
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
