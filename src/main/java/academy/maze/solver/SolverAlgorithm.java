package academy.maze.solver;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum SolverAlgorithm {
    DIJKSTRA("dijkstra", "Dijkstra"),
    A_STAR("astar", "A* (A-Star)"),
    BFS("bfs", "BFS");

    private static final Map<String, SolverAlgorithm> BY_VALUE =
            Arrays.stream(values()).collect(Collectors.toMap(e -> e.value.toLowerCase(), e -> e));
    private final String value;
    private final String forConsoleView;

    SolverAlgorithm(String value, String forConsoleView) {
        this.value = value;
        this.forConsoleView = forConsoleView;
    }

    public static Optional<SolverAlgorithm> fromValue(String s) {
        if (s == null) return Optional.empty();
        return Optional.ofNullable(BY_VALUE.get(s.toLowerCase()));
    }

    public String getValue() {
        return value;
    }

    public String getForConsoleView() {
        return forConsoleView;
    }
}
