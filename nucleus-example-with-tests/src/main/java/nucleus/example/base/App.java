package nucleus.example.base;

import android.app.Application;

import dagger.ObjectGraph;

public class App extends Application {

    private static ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(new AppModule());
    }

    public static void setObjectGraph(ObjectGraph objectGraph) {
        App.objectGraph = objectGraph;
    }

    public static void inject(Object o) {
        objectGraph.inject(o);
    }
}
