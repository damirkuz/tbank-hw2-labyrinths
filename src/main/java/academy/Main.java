package academy;

import academy.app.MazeApplication;
import academy.console.ConsoleUI;
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
        // Запускаем консольный режим при запуске без параметров
        ConsoleUI ui = new ConsoleUI();
        try {
            ui.start();
        } finally {
            ui.close();
        }
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

        @Option(
            names = {"-u","--unicode"},
            description = "Рендер с помощью Unicode псевдографики")
        boolean unicode;


        @Override
        public void run() {
            MazeApplication app = new MazeApplication();
            app.generate(algorithm, width, height, output, unicode);
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
            names = {"-t","--text"},
            description = "Лабиринтный ASCII-текст (рендеринг) вместо файла")
        String text;

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

        @Option(
            names = {"-u","--unicode"},
            description = "Рендер с помощью Unicode псевдографики")
        boolean unicode;

        @Override public void run() {
            MazeApplication app = new academy.app.MazeApplication();
            if (text != null && !text.isBlank()) {
                app.solveFromString(algorithm, text, start, end, output, unicode);
            } else {
                app.solveFromFile(algorithm, file, start, end, output, unicode);
            }
        }
    }
}
