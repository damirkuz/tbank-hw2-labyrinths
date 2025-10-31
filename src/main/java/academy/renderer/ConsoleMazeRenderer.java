package academy.renderer;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;

public class ConsoleMazeRenderer implements MazeRenderer{
    public String render(Maze maze) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maze.cells().length; i++) {
            for (int j = 0; j < maze.cells()[0].length; j++) {
                if (maze.cells()[i][j].equals(CellType.WALL)) {
                    sb.append('#');
                } else {
                    sb.append(' ');
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
