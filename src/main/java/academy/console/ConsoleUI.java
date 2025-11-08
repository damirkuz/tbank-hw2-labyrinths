package academy.console;

import academy.app.MazeApplication;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final MazeApplication app;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.app = new MazeApplication();
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
        System.out.print(
                """
            Доступные алгоритмы генерации:
              1. DFS (Depth-First Search)
              2. Prim True
              3. Prim Simplified
              4. Prim Modified
            Выберите алгоритм (1-4):""");

        String algoChoice = scanner.nextLine().trim();
        String algorithm =
                switch (algoChoice) {
                    case "1" -> "dfs";
                    case "2" -> "prim";
                    case "3" -> "prim_simplified";
                    case "4" -> "prim_modified";
                    default -> {
                        System.out.println("Неверный выбор. Используется DFS.");
                        yield "dfs";
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
        System.out.print(
                """
            \nДоступные алгоритмы решения:
              1. Dijkstra
              2. A* (A-Star)
            Выберите алгоритм (1-2):""");

        String algoChoice = scanner.nextLine().trim();
        String algorithm =
                switch (algoChoice) {
                    case "1" -> "dijkstra";
                    case "2" -> "astar";
                    default -> {
                        System.out.println("Неверный выбор. Используется Dijkstra.");
                        yield "dijkstra";
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
