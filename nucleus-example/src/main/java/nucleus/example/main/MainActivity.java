package nucleus.example.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import nucleus.example.R;
import nucleus.example.network.ServerAPI;
import nucleus.example.utils.ViewFn;
import nucleus.view.NucleusActivity;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MainActivity extends NucleusActivity<MainPresenter> {

    private static final String NAME_1 = "Chuck Norris";
    private static final String NAME_2 = "Jackie Chan";
    public static final String DEFAULT_NAME = NAME_1;

    CheckedTextView check1;
    CheckedTextView check2;

    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        check1 = (CheckedTextView)findViewById(R.id.check1);
        check2 = (CheckedTextView)findViewById(R.id.check2);

        check1.setText(NAME_1);
        check2.setText(NAME_2);

        check1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().toggleTo(NAME_1);
            }
        });
        check2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().toggleTo(NAME_2);
            }
        });
    }

    public void publishCounter(int i) {
        TextView counter = (TextView)findViewById(R.id.counter);
        counter.setText("" + i);
    }

    public void publishItems(ServerAPI.Item[] items, String user) {

        ListView listView = (ListView)findViewById(R.id.listView);

        if (items == null) {
            listView.setVisibility(View.INVISIBLE);
            return;
        }

        ViewFn.fadeIn(listView);

        check1.setChecked(user.equals(NAME_1));
        check2.setChecked(user.equals(NAME_2));

        listView.setAdapter(new ArrayAdapter<ServerAPI.Item>(this, R.layout.item, items));
    }
}
