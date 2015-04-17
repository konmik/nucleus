package nucleus.example;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import nucleus.view.NucleusActivity;
import nucleus.view.RequiresPresenter;

/**
 * Created by rharter on 4/17/15.
 */
@RequiresPresenter(ExampleRxPresenter.class)
public class ExampleRxActivity extends NucleusActivity<ExampleRxPresenter> {

    TextView counter;
    ListView list;
    View empty;

    ArrayAdapter<String> adapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        counter = (TextView) findViewById(R.id.counter);
        list = (ListView) findViewById(R.id.list);
        empty = findViewById(R.id.empty);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getPresenter().onPlanetClicked(adapter.getItem(position));
            }
        });

        // initialize with an empty view
        showEmptyView();
    }

    public void showPlanets(List<String> planets) {
        adapter.clear();

        if (planets == null || planets.isEmpty()) {
            showEmptyView();
            return;
        }

        adapter.addAll(planets);
        showListView();
    }

    private void showListView() {
        empty.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }

    private void showEmptyView() {
        list.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
    }

    public void updateCounter(int count) {
        counter.setText(getString(R.string.counter, count));
    }

    public void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }
}
