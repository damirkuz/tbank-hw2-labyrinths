package academy;

import academy.app.MazeApplication;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "maze-app",
        version = "1.0",
        description = "Maze generator and solver CLI application.",
        mixinStandardHelpOptions = true,
        subcommands = {Main.GenerateCommand.class, Main.SolveCommand.class})
public class Main implements Runnable {

    @Override
    public void run() {
        // Запускаем консольный режим при запуске без параметров
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
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

    @Command(name = "solve", description = "Решает лабиринт")
    static class SolveCommand implements Runnable {

        @Option(names = {"--algorithm", "-a"}, required = true,
            description = "Алгоритм решения: dijkstra, astar")
        String algorithm;

        @Option(names = {"--file", "-f"}, description = "Файл с лабиринтом")
        String file;

        @Option(names = {"-t", "--text"},
            description = "Лабиринтный ASCII-текст (рендеринг) вместо файла")
        String text;

        @Option(names = {"--start", "-s"}, required = true,
            description = "Начальная точка в формате x,y")
        String start;

        @Option(names = {"--end", "-e"}, required = true,
            description = "Конечная точка в формате x,y")
        String end;

        @Option(names = {"--output", "-o"},
            description = "Файл для сохранения решения (опционально)")
        String output;

        @Option(names = {"-u", "--unicode"},
            description = "Рендер с помощью Unicode псевдографики")
        boolean unicode;

        @Override
        public void run() {
            // Предвалидация для точного сообщения и отсутствия стека
            if (isValidPoint(start)) {
                System.out.println("Invalid point format: " + start + ", expected format: x,y");
                return;
            }
            if (isValidPoint(end)) {
                System.out.println("Invalid point format: " + end + ", expected format: x,y");
                return;
            }

            MazeApplication app = new MazeApplication();
            if (text != null && !text.isBlank()) {
                app.solveFromString(algorithm, text, start, end, output, unicode);
            } else {
                app.solveFromFile(algorithm, file, start, end, output, unicode);
            }
        }

        // Допускаем пробелы вокруг запятой и знаки минус; запрещаем всё остальное
        private static boolean isValidPoint(String p) {
            if (p == null) return true;
            String[] parts = p.trim().split("\\s*,\\s*");
            if (parts.length != 2) return true;
            try {
                Integer.parseInt(parts[0]);
                Integer.parseInt(parts[1]);
                return false;
            } catch (NumberFormatException e) {
                return true;
            }
        }
    }

}
