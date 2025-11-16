package academy.maze.io;

import static academy.maze.service.MazeService.getCellType;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MazeFileReader {

    public Maze read(String filename) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IOException("Имя файла не может быть пустым");
        }

        // Читаем все строки из файла
        List<String> lines = Files.readAllLines(Path.of(filename));

        if (lines.isEmpty()) {
            throw new IOException("Файл пуст");
        }

        int height = lines.size();
        int width = lines.getFirst().length();

        if (width == 0) {
            throw new IOException("Первая строка файла пуста");
        }

        CellType[][] cells = new CellType[height][width];

        // Заполняем сетку, преобразуя символы в типы ячеек
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width && x < line.length(); x++) {
                char ch = line.charAt(x);
                cells[y][x] = charToCell(ch);
            }
        }

        return new Maze(cells);
    }

    private CellType charToCell(char ch) {
        return getCellType(ch);
    }
}
