package academy.generator;

import academy.maze.dto.Maze;

/**
 * Simplified Prim's (cell-based frontier).
 */
public class PrimSimplifiedGenerator extends BaseGenerator {

    @Override
    public Maze generate(int width, int height) {
        PrimModifiedGenerator generator = new PrimModifiedGenerator();
        return generator.generatePrimWithChanceTakeNewest(width, height, 0);
    }
}
