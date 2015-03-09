package nucleus.example.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import nucleus.example.R;
import nucleus.example.network.ServerAPI;
import nucleus.example.utils.ViewFn;
import nucleus.manager.RequiresPresenter;
import nucleus.view.NucleusActivity;

@RequiresPresenter(MainPresenter.class)
public class MainActivity extends NucleusActivity<MainPresenter> {

    CheckedTextView check1;
    CheckedTextView check2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        check1 = (CheckedTextView)findViewById(R.id.check1);
        check2 = (CheckedTextView)findViewById(R.id.check2);

        check1.setText(MainPresenter.NAME_1);
        check2.setText(MainPresenter.NAME_2);

        check1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().toggleTo(MainPresenter.NAME_1);
            }
        });
        check2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().toggleTo(MainPresenter.NAME_2);
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

        check1.setChecked(user.equals(MainPresenter.NAME_1));
        check2.setChecked(user.equals(MainPresenter.NAME_2));

        listView.setAdapter(new ArrayAdapter<>(this, R.layout.item, items));
    }
}
