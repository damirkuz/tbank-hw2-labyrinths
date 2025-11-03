package academy.io;

import academy.maze.dto.Maze;
import academy.renderer.ConsoleMazeRenderer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MazeFileWriter {

    private final ConsoleMazeRenderer renderer = new ConsoleMazeRenderer();

    public void write(Maze maze, String filename) throws IOException {
        String content = renderer.render(maze);
        Files.write(Path.of(filename), content.getBytes());
    }
}
