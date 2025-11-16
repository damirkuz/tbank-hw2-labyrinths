package academy.maze.io;

import static org.junit.jupiter.api.Assertions.*;

import academy.maze.dto.Maze;
import academy.maze.service.MazeService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MazeServiceFileIOTest {

    private MazeService app;
    private final MazeFileReader reader = new MazeFileReader();

    @BeforeEach
    void setUp() {
        app = new MazeService();
    }

    @TempDir
    Path tempDir;

    @Test
    void generateToFile() throws IOException {
        Path outputFile = tempDir.resolve("generated_maze.txt");

        Optional<String> result = app.generate("DFS", 5, 5, outputFile.toString(), false);

        assertTrue(result.isEmpty(), "Ожидался пустой Optional при успешной записи в файл");

        assertTrue(Files.exists(outputFile), "Файл лабиринта должен быть создан");
        assertTrue(Files.size(outputFile) > 0, "Файл лабиринта не должен быть пустым");

        Maze maze = reader.read(outputFile.toString());
        assertNotNull(maze, "Maze не должен быть null");
        assertTrue(maze.cells().length > 0, "Maze должен содержать хотя бы одну строку");
    }

    @Test
    void generateWithoutFile() {
        Path notUsedFile = tempDir.resolve("should_not_exist.txt");

        Optional<String> result = app.generate("dfs", 5, 5, null, false);

        String mazeText = result.orElseThrow(); // <-- вместо get()
        assertFalse(mazeText.isBlank(), "Сгенерированный лабиринт не должен быть пустой строкой");

        assertFalse(Files.exists(notUsedFile), "Файл не должен создаваться, если outputFile == null");
    }

    @Test
    void readAndSolveFromFileBFS() throws IOException {
        String mazeText =
                """
            #####
            #   #
            # # #
            #   #
            #####
            """;

        Path inputFile = tempDir.resolve("input_maze.txt");
        Files.writeString(inputFile, mazeText);

        Path outputFile = tempDir.resolve("solved_maze.txt");

        Optional<String> result =
                app.solveFromFile("BFS", inputFile.toString(), "1,1", "3,3", outputFile.toString(), false);

        String rendered = result.orElseThrow();
        assertFalse(rendered.isBlank(), "Текст решённого лабиринта не должен быть пустым");

        assertTrue(Files.exists(outputFile), "Файл с решением должен быть создан");
        assertTrue(Files.size(outputFile) > 0, "Файл с решением не должен быть пустым");

        String fileContents = Files.readString(outputFile);
        assertFalse(fileContents.isBlank(), "Содержимое файла с решением не должно быть пустым");
    }

    @Test
    void readAndSolveFromFileAstar() throws IOException {
        String mazeText =
                """
        #######
        #   # #
        # # # #
        # # # #
        # ### #
        #     #
        #######""";

        Path inputFile = tempDir.resolve("astar_input_maze.txt");
        Files.writeString(inputFile, mazeText);

        Path outputFile = tempDir.resolve("astar_solved_maze.txt");

        Optional<String> result =
                app.solveFromFile("astar", inputFile.toString(), "1,1", "4,5", outputFile.toString(), false);

        String text = result.orElseThrow();
        assertTrue(text.contains("O"), "Решение A* должно содержать стартовую точку 'O'");
        assertTrue(text.contains("X"), "Решение A* должно содержать конечную точку 'X'");
        assertTrue(text.contains("."), "Решение A* должно содержать отмеченный маршрут '.'");
    }
}
