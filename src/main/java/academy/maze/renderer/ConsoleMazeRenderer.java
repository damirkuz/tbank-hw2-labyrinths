package academy.maze.renderer;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;

public class ConsoleMazeRenderer implements MazeRenderer {

    @Override
    public String render(Maze maze) {
        StringBuilder sb = new StringBuilder();
        CellType[][] cells = maze.cells();

        for (CellType[] row : cells) {
            for (CellType cell : row) {
                sb.append(cellToChar(cell));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private char cellToChar(CellType cellType) {
        if (cellType == null) {
            return ' ';
        }

        return switch (cellType) {
            case WALL -> '#';
            case START -> 'O';
            case END -> 'X';
            case ROUTE -> '.';
            default -> ' ';
        };
    }
}
