package nucleus.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import nucleus.view.NucleusActivity;
import nucleus.view.RequiresPresenter;

/**
 * Created by rharter on 4/17/15.
 */
@RequiresPresenter(ExampleDetailPresenter.class)
public class ExampleDetailActivity extends NucleusActivity<ExampleDetailPresenter> {

    public static final String EXTRA_PLANET = "extra_planet";

    View content;
    TextView title;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        content = findViewById(R.id.content);
        title = (TextView) findViewById(R.id.title);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            getPresenter().setPlanet(intent.getStringExtra(EXTRA_PLANET));
        }
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setBackgroundColor(int color) {
        content.setBackgroundColor(color);
    }
}
