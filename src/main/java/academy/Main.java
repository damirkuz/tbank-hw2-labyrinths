package academy;

import academy.app.MazeApplication;
import picocli.CommandLine;
import picocli.*;
import picocli.CommandLine.*;


@Command(
    name = "maze",
    version = "1.0",
    description = "Приложение для генерации и решения лабиринтов",
    subcommands = {Main.GenerateCommand.class, Main.SolveCommand.class}
)
public class Main implements Runnable {

    @Override
    public void run() {
        // Выводим справку при запуске без команд
        System.out.println("Используйте 'maze --help' для справки");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }


    @Command(
        name = "generate",
        description = "Генерирует новый лабиринт"
    )
    static class GenerateCommand implements Runnable {

        @Option(
            names = {"--algorithm", "-a"},
            description = "Алгоритм генерации: dfs, prim, prim_simplified, prim_modified",
            required = true
        )
        String algorithm;

        @Option(
            names = {"--width", "-w"},
            description = "Ширина лабиринта",
            required = true
        )
        int width;

        @Option(
            names = {"--height", "-h"},
            description = "Высота лабиринта",
            required = true
        )
        int height;

        @Option(
            names = {"--output", "-o"},
            description = "Файл для сохранения (опционально)"
        )
        String output;

        @Override
        public void run() {
            MazeApplication app = new MazeApplication();
            app.generate(algorithm, width, height, output);
        }
    }

    /**
     * Команда для решения лабиринта
     */
    @Command(
        name = "solve",
        description = "Решает лабиринт и находит путь"
    )
    static class SolveCommand implements Runnable {

        @Option(
            names = {"--algorithm", "-a"},
            description = "Алгоритм решения: dijkstra, astar",
            required = true
        )
        String algorithm;

        @Option(
            names = {"--file", "-f"},
            description = "Файл с лабиринтом",
            required = true
        )
        String file;

        @Option(
            names = {"--start", "-s"},
            description = "Начальная точка в формате x,y",
            required = true
        )
        String start;

        @Option(
            names = {"--end", "-e"},
            description = "Конечная точка в формате x,y",
            required = true
        )
        String end;

        @Option(
            names = {"--output", "-o"},
            description = "Файл для сохранения решения (опционально)"
        )
        String output;

        @Override
        public void run() {
            MazeApplication app = new MazeApplication();
            app.solve(algorithm, file, start, end, output);
        }
    }
}
