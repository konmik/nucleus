package nucleus.example.base;

import dagger.ObjectGraph;

import java.util.HashMap;

public class Injector {
    private static ObjectGraph graph;

    public static void setGraph(ObjectGraph graph) {
        Injector.graph = graph;
    }

    public static ObjectGraph getGraph() {
        return graph;
    }

    public static <T> void inject(T target) {
        graph.inject(target);
    }
}
