package academy.maze.service;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import academy.maze.generator.DfsGenerator;
import academy.maze.generator.Generator;
import academy.maze.generator.GeneratorAlgorithm;
import academy.maze.generator.PrimModifiedGenerator;
import academy.maze.generator.PrimSimplifiedGenerator;
import academy.maze.generator.PrimTrueGenerator;
import academy.maze.io.MazeFileReader;
import academy.maze.io.MazeFileWriter;
import academy.maze.renderer.ConsoleMazeRenderer;
import academy.maze.renderer.MazeRenderer;
import academy.maze.renderer.UnicodeMazeRenderer;
import academy.maze.solver.AStarSolver;
import academy.maze.solver.BFSSolver;
import academy.maze.solver.DijkstraSolver;
import academy.maze.solver.Solver;
import academy.maze.solver.SolverAlgorithm;
import academy.maze.validation.InputValidator;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MazeService {

    private static final Logger log = LoggerFactory.getLogger(MazeService.class);

    private final MazeFileWriter writer = new MazeFileWriter();
    private final MazeFileReader reader = new MazeFileReader();
    private final MazeRenderer asciiRenderer = new ConsoleMazeRenderer();
    private final MazeRenderer unicodeRenderer = new UnicodeMazeRenderer();

    public Optional<String> generate(String algorithm, int width, int height, String outputFile, boolean unicode) {
        try {
            InputValidator.validateDimensions(width, height);
            Generator gen = createGenerator(algorithm);
            Maze maze = gen.generate(width, height);
            if (outputFile != null && !outputFile.isEmpty()) {
                try {
                    writer.write(maze, outputFile);
                    return Optional.empty();
                } catch (IOException ioe) {
                    log.error("Не удалось сохранить в файл '{}'", outputFile, ioe);
                    String out = (unicode ? unicodeRenderer : asciiRenderer).render(maze);
                    return Optional.of(out);
                }
            } else {
                String out = (unicode ? unicodeRenderer : asciiRenderer).render(maze);
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

    public Optional<String> solveFromString(
            String algorithm, String mazeText, String startStr, String endStr, String outputFile) {
        return solveInternal(algorithm, null, mazeText, startStr, endStr, outputFile, false);
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

            Maze maze = loadMaze(mazeText, inputFile);
            if (!validateBounds(maze, start, end)) {
                return Optional.empty();
            }

            Solver solver = createSolver(algorithm);

            Path path = solver.solve(maze, start, end);
            if (path == null || path.points().length == 0) {
                return Optional.empty();
            }

            return renderAndSaveSolution(maze, path, start, end, outputFile, unicode);
        } catch (IllegalArgumentException e) {
            log.error("Не удалось спарсить точки", e);
        } catch (Exception e) {
            log.error("Ошибка решения", e);
        }
        return Optional.empty();
    }

    private Maze loadMaze(String mazeText, String inputFile) throws IOException {
        return (mazeText != null) ? loadMazeFromString(mazeText) : reader.read(inputFile);
    }

    private Optional<String> renderAndSaveSolution(
            Maze maze, Path path, Point start, Point end, String outputFile, boolean unicode) {

        markPathOnMaze(maze, path, start, end);
        String rendered = renderMaze(maze, unicode);
        writeSolutionToFileIfNeeded(maze, outputFile);
        return Optional.of(rendered);
    }

    private String renderMaze(Maze maze, boolean unicode) {
        return (unicode ? unicodeRenderer : asciiRenderer).render(maze);
    }

    private void writeSolutionToFileIfNeeded(Maze maze, String outputFile) {
        if (outputFile == null || outputFile.isEmpty()) {
            return;
        }
        try {
            writer.write(maze, outputFile);
        } catch (IOException ioe) {
            log.error("Не удалось записать решение в файл '{}'", outputFile, ioe);
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
                cells[y][x] = getCellType(ch);
            }
        }
        return new Maze(cells);
    }

    public static CellType getCellType(char ch) {
        return switch (ch) {
            // ASCII режим
            case ' ' -> CellType.PATH;
            case 'O' -> CellType.START;
            case 'X' -> CellType.END;
            case '.' -> CellType.ROUTE;
            case '#' -> CellType.WALL;

            // Unicode режим
            case '◉' -> CellType.START; // старт
            case '★' -> CellType.END; // финиш
            case '•' -> CellType.ROUTE; // найденный путь
            case '■' -> CellType.WALL; // стена

            // все варианты стен
            case '┼', '┬', '┴', '├', '┤', '│', '─', '└', '┘', '┌', '┐', '╵', '╷', '╴', '╶' -> CellType.WALL;

            default -> CellType.WALL;
        };
    }

    private void markPathOnMaze(Maze maze, Path path, Point start, Point end) {
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
        GeneratorAlgorithm alg = GeneratorAlgorithm.fromValue(algorithm).orElseThrow();
        return switch (alg) {
            case DFS -> new DfsGenerator();
            case PRIM_TRUE -> new PrimTrueGenerator();
            case PRIM_SIMPLE -> new PrimSimplifiedGenerator();
            case PRIM_MODIFY -> new PrimModifiedGenerator();
        };
    }

    private Solver createSolver(String algorithm) {
        SolverAlgorithm alg = SolverAlgorithm.fromValue(algorithm).orElseThrow();
        return switch (alg) {
            case DIJKSTRA -> new DijkstraSolver();
            case A_STAR -> new AStarSolver();
            case BFS -> new BFSSolver();
        };
    }
}
