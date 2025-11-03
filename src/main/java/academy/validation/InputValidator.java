package academy.validation;

import academy.maze.dto.Point;

public class InputValidator {

    /** Парсит строку вида "x,y" и возвращает Point. */
    public static Point parsePoint(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Координаты не могут быть пустыми");
        }

        String[] parts = input.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Координаты должны быть в формате: x,y");
        }

        try {
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());

            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Координаты не должны быть отрицательными");
            }

            return new Point(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Координаты должны быть целыми числами");
        }
    }

    public static void validateDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Ширина и высота должны быть положительными");
        }

        if (width > 1000 || height > 1000) {
            throw new IllegalArgumentException("Размеры лабиринта слишком большие (максимум 1000x1000)");
        }
    }
}
