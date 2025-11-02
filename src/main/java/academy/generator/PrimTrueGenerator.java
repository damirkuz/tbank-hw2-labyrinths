package academy.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class PrimTrueGenerator extends BaseGenerator {

    @Override
    public Maze generate(int width, int height) {
        Maze maze = initializeMaze(width, height);

        Point start = GeneratorUtil.getStartPoint(width, height);
        GeneratorUtil.markCell(maze, start, CellType.PATH);

        List<Wall> walls = new ArrayList<>();
        Set<Wall> wallSet = new HashSet<>();
        addAdjacentWalls(start, walls, wallSet, maze);

        while (!walls.isEmpty()) {
            Wall wall = pickRandomAndRemove(walls);
            wallSet.remove(wall);

            if (wall.separatesVisitedAndUnvisited(maze)) {
                Point visited = wall.getVisitedCell(maze);
                Point unvisited = wall.getUnvisitedCell(maze);
                carvePassage(maze, visited, unvisited);
                addAdjacentWalls(unvisited, walls, wallSet, maze);
            }
        }

        return maze;
    }

    private void addAdjacentWalls(Point cell, List<Wall> walls, Set<Wall> wallSet, Maze maze) {
        for (Point nb : GeneratorUtil.getPointNeighbours(cell)) {
            if (GeneratorUtil.pointInMaze(nb, maze)) {
                Wall w = new Wall(cell, nb);
                if (wallSet.add(w)) {
                    walls.add(w);
                }
            }
        }
    }

    private static class Wall {
        private final Point a;
        private final Point b;

        Wall(Point c1, Point c2) {
            if (c1.x() < c2.x() || (c1.x() == c2.x() && c1.y() < c2.y())) {
                this.a = c1;
                this.b = c2;
            } else {
                this.a = c2;
                this.b = c1;
            }
        }

        boolean separatesVisitedAndUnvisited(Maze maze) {
            if (!GeneratorUtil.pointInMaze(a, maze) || !GeneratorUtil.pointInMaze(b, maze)) {
                return false;
            }
            CellType t1 = GeneratorUtil.getPointType(a, maze);
            CellType t2 = GeneratorUtil.getPointType(b, maze);
            return (t1 == CellType.PATH && t2 == CellType.WALL) ||
                (t1 == CellType.WALL && t2 == CellType.PATH);
        }

        Point getVisitedCell(Maze maze) {
            return (GeneratorUtil.getPointType(a, maze) == CellType.PATH) ? a : b;
        }

        Point getUnvisitedCell(Maze maze) {
            return (GeneratorUtil.getPointType(a, maze) == CellType.WALL) ? a : b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Wall)) return false;
            Wall wall = (Wall) o;
            return a.equals(wall.a) && b.equals(wall.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }
}
