package academy.console;

import academy.app.MazeApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }

            System.out.println();
        }
    }

    /**
     * Печатает главное меню (упрощённый вывод)
     */
    private void printMainMenu() {
        System.out.println("Главное меню:");
        System.out.println("  1. Сгенерировать лабиринт");
        System.out.println("  2. Справка");
        System.out.println("  0. Выход");
        System.out.print("Ваш выбор: ");
    }

    /**
     * Обрабатывает генерацию лабиринта
     */
    private void generateMaze() {
        System.out.println("\n=== Генерация лабиринта ===\n");

        // Выбор алгоритма
        System.out.println("Доступные алгоритмы генерации:");
        System.out.println("  1. DFS (Depth-First Search)");
        System.out.println("  2. Prim True");
        System.out.println("  3. Prim Simplified");
        System.out.println("  4. Prim Modified");
        System.out.print("Выберите алгоритм (1-4): ");

        String algoChoice = scanner.nextLine().trim();
        String algorithm = switch (algoChoice) {
            case "1" -> "dfs";
            case "2" -> "prim";
            case "3" -> "prim_simplified";
            case "4" -> "prim_modified";
            default -> {
                System.out.println("Неверный выбор. Используется DFS.");
                yield "dfs";
            }
        };

        // Ввод размеров
        int width = readPositiveInt("Введите ширину лабиринта: ");
        int height = readPositiveInt("Введите высоту лабиринта: ");


        // Генерация
        System.out.println();
        Optional<String> mazeString = app.generate(algorithm, width, height, null);
        System.out.println(mazeString.get());

        // Опциональное решение лабиринта
        System.out.print("Желаете решить лабиринт? (y/n): ");
        String saveChoice = scanner.nextLine().trim().toLowerCase();

        if (saveChoice.equals("y") || saveChoice.equals("yes")) {
            solveMaze(mazeString.get());
        }
    }

    /**
     * Обрабатывает решение лабиринта
     */
    private void solveMaze(String mazeText) {
        System.out.println("\n=== Решение лабиринта ===\n");

        // Выбор алгоритма
        System.out.println("\nДоступные алгоритмы решения:");
        System.out.println("  1. Dijkstra");
        System.out.println("  2. A* (A-Star)");
        System.out.print("Выберите алгоритм (1-2): ");

        String algoChoice = scanner.nextLine().trim();
        String algorithm = switch (algoChoice) {
            case "1" -> "dijkstra";
            case "2" -> "astar";
            default -> {
                System.out.println("Неверный выбор. Используется Dijkstra.");
                yield "dijkstra";
            }
        };

        // Координаты
        System.out.print("Введите начальную точку (формат: x,y): ");
        String startStr = scanner.nextLine().trim();

        System.out.print("Введите конечную точку (формат: x,y): ");
        String endStr = scanner.nextLine().trim();

        String outputFile = null;
        System.out.print("Сохранить решение в файл? (y/n): ");
        String saveChoice = scanner.nextLine().trim().toLowerCase();
        if (saveChoice.equals("y") || saveChoice.equals("yes")) {
            System.out.print("Введите имя файла (например, solution.txt): ");
            outputFile = scanner.nextLine().trim();
            if (outputFile.isEmpty()) {
                outputFile = "solution.txt";
            }
        }

        // Решение
        Optional<String> solutionString = app.solveFromString(algorithm, mazeText, startStr, endStr, outputFile);
        if (solutionString.isEmpty()) {
            System.out.println("Решения нет");
        } else {
            System.out.println(solutionString.get());
        }
    }


    private void showHelp() {
        System.out.println("\nСправка");
        System.out.println("Приложение позволяет генерировать и решать лабиринты.");
        System.out.println();
        System.out.println("ГЕНЕРАЦИЯ:");
        System.out.println("  - Алгоритм: DFS или варианты Prim");
        System.out.println("  - Размеры: ширина и высота (положительные целые)");
        System.out.println("  - Результат можно сохранить в файл или показать на экране");
        System.out.println();
        System.out.println("РЕШЕНИЕ:");
        System.out.println("  - Введите путь к файлу с лабиринтом");
        System.out.println("  - Алгоритм: Dijkstra или A*");
        System.out.println("  - Точки: формат x,y");
        System.out.println("  - Решение можно сохранить в файл или показать на экране");
        System.out.println();
        System.out.println("ОБОЗНАЧЕНИЯ:");
        System.out.println("  # — стена");
        System.out.println("  (пробел) — проход");
        System.out.println("  O — начальная точка");
        System.out.println("  X — конечная точка");
        System.out.println("  . — найденный путь");
        System.out.println();
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
