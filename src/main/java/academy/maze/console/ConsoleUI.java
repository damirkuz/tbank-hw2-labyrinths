package academy.maze.console;

import academy.maze.generator.GeneratorAlgorithm;
import academy.maze.service.MazeService;
import academy.maze.solver.SolverAlgorithm;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final MazeService app;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.app = new MazeService();
    }

    public void start() {
        while (true) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> generateMaze();
                case "2" -> showHelp();
                case "0" -> {
                    System.out.println("\nДо свидания!");
                    close();
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }

            System.out.println();
        }
    }

    private void printMainMenu() {
        System.out.print(
                """
            Главное меню:
              1. Сгенерировать лабиринт
              2. Справка
              0. Выход
            Ваш выбор:""");
    }

    private void generateMaze() {
        System.out.printf(
                "Доступные алгоритмы генерации:%n  1. %s%n  2. %s%n  3. %s%n  4. %s%nВыберите алгоритм (1-4): ",
                GeneratorAlgorithm.DFS.getForConsoleView(),
                GeneratorAlgorithm.PRIM_TRUE.getForConsoleView(),
                GeneratorAlgorithm.PRIM_SIMPLE.getForConsoleView(),
                GeneratorAlgorithm.PRIM_MODIFY.getForConsoleView());

        String algoChoice = scanner.nextLine().trim();
        String algorithm =
                switch (algoChoice) {
                    case "1" -> GeneratorAlgorithm.DFS.getValue();
                    case "2" -> GeneratorAlgorithm.PRIM_TRUE.getValue();
                    case "3" -> GeneratorAlgorithm.PRIM_SIMPLE.getValue();
                    case "4" -> GeneratorAlgorithm.PRIM_MODIFY.getValue();
                    default -> {
                        System.out.println("Неверный выбор. Используется DFS.");
                        yield GeneratorAlgorithm.DFS.getValue();
                    }
                };

        int width = readPositiveInt("Введите ширину лабиринта: ");
        int height = readPositiveInt("Введите высоту лабиринта: ");

        boolean unicode = askSomething("Включить псевдографику Unicode?");

        System.out.println();
        Optional<String> mazeMaybe = app.generate(algorithm, width, height, null, unicode);

        mazeMaybe.ifPresent(ms -> {
            System.out.println(mazeMaybe.orElse("Лабиринта нет"));
            boolean solutionChoice = askSomething("Желаете решить лабиринт?");
            if (solutionChoice) {
                solveMaze(ms, unicode);
            }
        });
    }

    private boolean askSomething(String question) {
        System.out.print(question + " (y/n): ");
        String s = scanner.nextLine().trim().toLowerCase();
        return s.equals("y") || s.equals("yes");
    }

    private void solveMaze(String mazeText, boolean unicode) {
        System.out.printf(
                "Доступные алгоритмы решения:%n  1. %s%n  2. %s%n  3. %s%nВыберите алгоритм (1-3): %n",
                SolverAlgorithm.DIJKSTRA.getForConsoleView(),
                SolverAlgorithm.A_STAR.getForConsoleView(),
                SolverAlgorithm.BFS.getForConsoleView());

        String algoChoice = scanner.nextLine().trim();
        String algorithm =
                switch (algoChoice) {
                    case "1" -> SolverAlgorithm.DIJKSTRA.getValue();
                    case "2" -> SolverAlgorithm.A_STAR.getValue();
                    case "3" -> SolverAlgorithm.BFS.getValue();
                    default -> {
                        System.out.println("Неверный выбор. Используется Dijkstra.");
                        yield SolverAlgorithm.DIJKSTRA.getValue();
                    }
                };

        System.out.print("Введите начальную точку (формат: x,y): ");
        String startStr = scanner.nextLine().trim();

        System.out.print("Введите конечную точку (формат: x,y): ");
        String endStr = scanner.nextLine().trim();

        String outputFile = null;
        boolean saveChoice = askSomething("Сохранить решение в файл?");
        if (saveChoice) {
            System.out.print("Введите имя файла (например, solution.txt): ");
            outputFile = scanner.nextLine().trim();
            if (outputFile.isEmpty()) {
                outputFile = "solution.txt";
            }
        }

        System.out.println();

        app.solveFromString(algorithm, mazeText, startStr, endStr, outputFile, unicode)
                .ifPresentOrElse(System.out::println, () -> System.out.println("Решения нет"));
    }

    private void showHelp() {
        System.out.print(
                """

            Справка
            Приложение позволяет генерировать и решать лабиринты.

            ГЕНЕРАЦИЯ:
              - Алгоритм: DFS или варианты Prim
              - Размеры: ширина и высота (положительные целые)
              - Результат можно сохранить в файл или показать на экране

            РЕШЕНИЕ:
              - Введите путь к файлу с лабиринтом
              - Алгоритм: Dijkstra или A*
              - Точки: формат x,y
              - Начало координат в левом верхнем углу, при движении вниз растёт y, вправо - x
              - Решение можно сохранить в файл или показать на экране

            ОБОЗНАЧЕНИЯ:
              # — стена
              (пробел) — проход
              O — начальная точка
              X — конечная точка
              . — найденный путь

            """);
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.println("Число должно быть положительным.");
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите корректное число.");
            }
        }
    }

    public void close() {
        scanner.close();
    }
}
