package academy;

import academy.app.MazeApplication;
import academy.console.ConsoleUI;
import academy.exception.handler.CommandLineExceptionHandler;
import academy.validation.InputValidator;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "maze-app",
        version = "1.0",
        description = "Maze generator and solver CLI application.",
        mixinStandardHelpOptions = true,
        subcommands = {Application.GenerateCommand.class, Application.SolveCommand.class})
public class Application implements Runnable {

    @Option(
            names = {"--help"},
            description = "Вывод справки")
    private boolean help;

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new Application());
        cmd.setParameterExceptionHandler(new CommandLineExceptionHandler());
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        if (help) {
            System.out.println(
                    """
                Usage: maze-app [-hV] [COMMAND]
                Maze generator and solver CLI application.
                  -h, --help      Show this help message and exit.
                  -V, --version   Print version information and exit.
                Commands:
                  generate  Generate a maze with specified algorithm and dimensions.
                  solve     Solve a maze with specified algorithm and points.""");
        }
        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.start();
    }

    @Command(name = "generate", description = "Generate a maze with specified algorithm and dimensions.")
    static class GenerateCommand implements Runnable {

        @Option(
                names = {"--algorithm", "-a"},
                description = "Алгоритм генерации: dfs, prim, prim_simplified, prim_modified",
                required = true)
        String algorithm;

        @Option(
                names = {"--width", "-w"},
                description = "Ширина лабиринта",
                required = true)
        int width;

        @Option(
                names = {"--height", "-h"},
                description = "Высота лабиринта",
                required = true)
        int height;

        @Option(
                names = {"--output", "-o"},
                description = "Файл для сохранения (опционально)")
        String output;

        @Option(
                names = {"-u", "--unicode"},
                description = "Рендер с помощью Unicode псевдографики")
        boolean unicode;

        @Override
        public void run() {
            MazeApplication app = new MazeApplication();
            app.generate(algorithm, width, height, output, unicode);
        }
    }

    @Command(name = "solve", description = "Solve a maze with specified algorithm and points.")
    static class SolveCommand implements Runnable {

        @Option(
                names = {"--algorithm", "-a"},
                required = true,
                description = "Алгоритм решения: dijkstra, astar")
        String algorithm;

        @Option(
                names = {"--file", "-f"},
                description = "Файл с лабиринтом")
        String file;

        @Option(
                names = {"-t", "--text"},
                description = "Лабиринтный ASCII-текст (рендеринг) вместо файла")
        String text;

        @Option(
                names = {"--start", "-s"},
                required = true,
                description = "Начальная точка в формате x,y")
        String start;

        @Option(
                names = {"--end", "-e"},
                required = true,
                description = "Конечная точка в формате x,y")
        String end;

        @Option(
                names = {"--output", "-o"},
                description = "Файл для сохранения решения (опционально)")
        String output;

        @Option(
                names = {"-u", "--unicode"},
                description = "Рендер с помощью Unicode псевдографики")
        boolean unicode;

        @Override
        public void run() {
            try {
                InputValidator.parsePoint(start);
                InputValidator.parsePoint(end);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid point format: " + start + ", expected format: x,y");
                return;
            }

            MazeApplication app = new MazeApplication();
            if (text != null && !text.isBlank()) {
                app.solveFromString(algorithm, text, start, end, output, unicode);
            } else {
                app.solveFromFile(algorithm, file, start, end, output, unicode);
            }
        }
    }
}
