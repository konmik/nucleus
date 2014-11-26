package nucleus.example.base;

import android.app.Application;

import android.widget.Toast;
import dagger.ObjectGraph;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Injector.setGraph(ObjectGraph.create(new AppModule(this)));
    }

    public static void reportError(String description) {
        Toast.makeText(instance, description, Toast.LENGTH_LONG).show();
    }
}
