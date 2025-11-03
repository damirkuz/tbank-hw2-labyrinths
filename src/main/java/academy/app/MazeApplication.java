package academy.app;

import academy.generator.*;
import academy.io.MazeFileReader;
import academy.io.MazeFileWriter;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import academy.renderer.ConsoleMazeRenderer;
import academy.renderer.MazeRenderer;
import academy.renderer.UnicodeMazeRenderer;
import academy.solver.*;
import academy.validation.InputValidator;
import java.io.IOException;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Главное приложение для работы с лабиринтами. Поддерживает: - generate: генерация лабиринта - solveFromFile: решение
 * из файла - solveFromString: решение из ASCII-строки (renderer.render)
 */
public class MazeApplication {

    private static final Logger log = LoggerFactory.getLogger(MazeApplication.class);

    private final MazeFileWriter writer = new MazeFileWriter();
    private final MazeFileReader reader = new MazeFileReader();
    private final MazeRenderer asciiRenderer = new ConsoleMazeRenderer();
    private final MazeRenderer unicodeRenderer = new UnicodeMazeRenderer();

    public Optional<String> generate(String algorithm, int width, int height, String outputFile, boolean unicode) {
        try {
            InputValidator.validateDimensions(width, height);
            Generator gen = createGenerator(algorithm);
            if (gen == null) {
                log.error("Ошибка: неизвестный алгоритм генерации '{}'", algorithm);
                return Optional.empty();
            }
            Maze maze = gen.generate(width, height);
            if (outputFile != null && !outputFile.isEmpty()) {
                try {
                    writer.write(maze, outputFile);
                    log.info("Лабиринт сохранён в файл: {}", outputFile);
                    return Optional.empty();
                } catch (IOException ioe) {
                    log.error("Не удалось сохранить в файл '{}': {}", outputFile, ioe.getMessage());
                    String out = (unicode ? unicodeRenderer : asciiRenderer).render(maze);
                    log.info("Лабиринт:\n{}", out);
                    return Optional.of(out);
                }
            } else {
                String out = (unicode ? unicodeRenderer : asciiRenderer).render(maze);
                log.info("Лабиринт:\n{}", out);
                return Optional.of(out);
            }
        } catch (Exception e) {
            log.error("Ошибка генерации", e);
            return Optional.empty();
        }
    }

    public Optional<String> generate(String algorithm, int width, int height, String outputFile) {
        return generate(algorithm, width, height, outputFile, false);
    }

    // solveFromFile/solveFromString – тоже с флагом
    public Optional<String> solveFromFile(
            String algorithm, String inputFile, String startStr, String endStr, String outputFile, boolean unicode) {
        return solveInternal(algorithm, inputFile, null, startStr, endStr, outputFile, unicode);
    }

    public Optional<String> solveFromString(
            String algorithm, String mazeText, String startStr, String endStr, String outputFile, boolean unicode) {
        return solveInternal(algorithm, null, mazeText, startStr, endStr, outputFile, unicode);
    }

    public Optional<String> solveFromString(String a, String t, String s, String e, String o) {
        return solveInternal(a, null, t, s, e, o, false);
    }

    // внутри solveInternal в местах рендера используем выбранный рендерер
    private Optional<String> solveInternal(
            String algorithm,
            String inputFile,
            String mazeText,
            String startStr,
            String endStr,
            String outputFile,
            boolean unicode) {
        try {
            Point start = InputValidator.parsePoint(startStr);
            Point end = InputValidator.parsePoint(endStr);
            Maze maze = (mazeText != null) ? loadMazeFromString(mazeText) : reader.read(inputFile);

            if (!validateBounds(maze, start, end)) return Optional.empty();

            Solver solver = createSolver(algorithm);
            if (solver == null) return Optional.empty();

            var path = solver.solve(maze, start, end);
            if (path == null || path.points().length == 0) return Optional.empty();

            markPathOnMaze(maze, path, start, end);
            String out = (unicode ? unicodeRenderer : asciiRenderer).render(maze);

            if (outputFile != null && !outputFile.isEmpty()) {
                try {
                    writer.write(maze, outputFile);
                    return Optional.empty();
                } catch (IOException ioe) {
                    log.error("Не удалось записать решение в файл '{}': {}", outputFile, ioe.getMessage());
                    log.info("Лабиринт:\n{}", out);
                    return Optional.of(out);
                }
            } else {
                log.info("Лабиринт\n{}", out);
                return Optional.of(out);
            }
        } catch (Exception e) {
            log.error("Ошибка решения", e);
            return Optional.empty();
        }
    }

    private boolean validateBounds(Maze maze, Point start, Point end) {
        int H = maze.cells().length;
        int W = maze.cells()[0].length;
        return start.x() >= 0
                && start.y() >= 0
                && end.x() >= 0
                && end.y() >= 0
                && start.x() < W
                && end.x() < W
                && start.y() < H
                && end.y() < H;
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
        return getCellType(ch);
    }

    @NotNull
    public static CellType getCellType(char ch) {
        return switch (ch) {
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
