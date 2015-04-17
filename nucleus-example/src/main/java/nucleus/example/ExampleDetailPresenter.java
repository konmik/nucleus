package nucleus.example;

import android.os.Bundle;

import nucleus.presenter.Presenter;

/**
 * Created by rharter on 4/17/15.
 */
public class ExampleDetailPresenter extends Presenter<ExampleDetailActivity> {

    private static final String STATE_PLANET = "state_planet";
    private static final String STATE_COLOR = "state_color";

    String planet;
    int color;

    @Override protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (savedState != null) {
            planet = savedState.getString(STATE_PLANET);
            color = savedState.getInt(STATE_COLOR);
        }
    }

    @Override protected void onSave(Bundle state) {
        super.onSave(state);
        state.putString(STATE_PLANET, planet);
        state.putInt(STATE_COLOR, color);
    }

    @Override protected void onTakeView(ExampleDetailActivity view) {
        super.onTakeView(view);
        view.setTitle(planet);
        view.setBackgroundColor(color);
    }

    public void setPlanet(String planet) {
        this.planet = planet;
        this.color = ExampleDataProvider.getPlanetColor(planet);
    }
}
