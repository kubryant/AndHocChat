package in.kubryant.andhocchat;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.UUID;

import in.kubryant.andhoclib.src.AndHocMessage;
import in.kubryant.andhoclib.src.AndHocMessageListener;
import in.kubryant.andhoclib.src.AndHocService;

public class MainActivity extends ActionBarActivity {
    private EditText editTextMessage;

    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> messageList = new ArrayList<>();
    private ArrayList<String> repeatCheck = new ArrayList<>();

    //private String mUser = UUID.randomUUID().toString();
    private AndHocService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        ListView messageListView = (ListView) findViewById(R.id.messageListView);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messageList);
        messageListView.setAdapter(mAdapter);

        mService = new AndHocService(this);
        mService.addListener(new AndHocMessageListener() {
            @Override
            public void onNewMessage(AndHocMessage msg) {
                String message = msg.getMessage().get("msg");
                String user = msg.getMessage().get("user");

                if(!repeatCheck.contains(user)) {
                    repeatCheck.add(user);
                    if (!message.equals("")) {
                        messageList.add(message);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mService.stopBroadcast(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mService.stopBroadcast(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mService.stopBroadcast(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void onClickBroadcast(View view) {
        String message = editTextMessage.getText().toString();

        if(!message.equals("")) {
            AndHocMessage record = new AndHocMessage();
            record.add("user", UUID.randomUUID().toString());
            record.add("msg", message);
            mService.broadcast(this, record);
        }
    }

    public void onClickListen(View view) {
        mService.listen(this);
    }

    public void onClickStop(View view) {
        mService.stopBroadcast(this);
    }

    public void onClickClear(View view) { mService.stopListen(this); }
}