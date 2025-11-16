package academy.maze.generator;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum GeneratorAlgorithm {
    DFS("dfs", "DFS (Depth-First Search)"),
    PRIM_TRUE("prim", "Prim True"),
    PRIM_SIMPLE("prim_simplified", "Prim Simplified"),
    PRIM_MODIFY("prim_modified", "Prim Modified");

    private static final Map<String, GeneratorAlgorithm> BY_VALUE =
            Arrays.stream(values()).collect(Collectors.toMap(e -> e.value.toLowerCase(), e -> e));

    private final String value;
    private final String forConsoleView;

    GeneratorAlgorithm(String value, String forConsoleView) {
        this.value = value;
        this.forConsoleView = forConsoleView;
    }

    public static Optional<GeneratorAlgorithm> fromValue(String s) {
        if (s == null) return java.util.Optional.empty();
        return Optional.ofNullable(BY_VALUE.get(s.toLowerCase()));
    }

    public String getValue() {
        return value;
    }

    public String getForConsoleView() {
        return forConsoleView;
    }
}
