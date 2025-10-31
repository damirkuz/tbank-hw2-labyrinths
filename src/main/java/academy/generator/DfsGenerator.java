package academy.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import java.util.List;
import java.util.Stack;

public class DfsGenerator implements Generator {

//    Достать из стека клетку и считать ее текущей
//    Если среди четырех клеток, смежных с текущей, имеются те, которые еще не были посещены:
//    Отправить текущую клетку в стек
//    Случайным образом выбрать одну из соседних непосещенных клеток
//    Удалить стенку между текущей и выбранной клеткой
//    Отметить выбранную клетку как посещенную и отправить ее в стек

    @Override
    public Maze generate(int width, int height) {
        Maze maze = GeneratorUtil.getGrid(width, height);
        GeneratorUtil.fill(maze, CellType.WALL);

        Point start = GeneratorUtil.getStartPoint(width, height);

        Stack<Point> stack = new Stack<>();

        GeneratorUtil.markCell(maze, start, CellType.PATH);
        stack.push(start);

        while (!stack.empty()) {

            Point nowPoint = stack.peek();
            List<Point> neighbours = GeneratorUtil.getUnvisitedNeighbours(maze, nowPoint);
            if (neighbours.isEmpty()) {
                stack.pop();
            } else {
                Point secondPoint = GeneratorUtil.getRandomPointFromList(neighbours);
                GeneratorUtil.markBetweenInclusivePoints(nowPoint, secondPoint, CellType.PATH, maze);
                stack.push(secondPoint);
            }
        }

        return maze;
    }
}
