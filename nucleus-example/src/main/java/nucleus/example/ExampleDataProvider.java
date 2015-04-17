package nucleus.example;

import android.os.SystemClock;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by rharter on 4/17/15.
 */
public class ExampleDataProvider {

    private static final List<String> PLANETS = Arrays.asList("Mercury", "Venus", "Earth", "Mars",
            "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto");

    private static final int[] COLORS = new int[] {
            0xFFB0805A, 0xFFF7E9A7, 0xFF84C56B, 0xFF039FAB, 0xFF4A3E53,
            0xFF450053, 0xFFE1215C, 0xFFC4D964, 0xFFF3E53E
    };

    public static Observable<List<String>> getPlanets() {
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override public void call(Subscriber<? super List<String>> subscriber) {

                // don't ever do this for reals, this is just
                // simulating a slow network connection
                SystemClock.sleep(1500);

                subscriber.onNext(PLANETS);
                subscriber.onCompleted();
            }
        });
    }

    public static int getPlanetColor(String planet) {
        return COLORS[PLANETS.indexOf(planet)];
    }
}
