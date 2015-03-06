package in.kubryant.andhocchat;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.UUID;

import in.kubryant.andhoclib.src.AndHocMessage;
import in.kubryant.andhoclib.src.AndHocMessageListener;
import in.kubryant.andhoclib.src.AndHocMessenger;
import in.kubryant.andhoclib.src.AndHocService;

public class MainActivity extends Activity {
    private EditText editTextMessage;
    private ToggleButton mSwitchBroadcast;
    private ToggleButton mSwitchListen;

    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> messageList = new ArrayList<>();
    private ArrayList<String> repeatCheck = new ArrayList<>();

    //private String mUser = UUID.randomUUID().toString();
    private AndHocMessenger mMessenger;
    private boolean mBroadcast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwitchBroadcast = (ToggleButton) findViewById(R.id.broadcastButton);
        mSwitchListen = (ToggleButton) findViewById(R.id.listenButton);

        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        ListView messageListView = (ListView) findViewById(R.id.messageListView);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messageList);
        messageListView.setAdapter(mAdapter);

        mMessenger = new AndHocMessenger(this);

        AndHocService.addListener(new AndHocMessageListener() {
            @Override
            public void onNewMessage(AndHocMessage msg) {
                String message = msg.get("msg");
                String user = msg.get("user");

                if (!repeatCheck.contains(user)) {
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if(mSwitchBroadcast.isChecked()) {
            mBroadcast = true;
        } else {
            mBroadcast = false;
            mMessenger.stopBroadcast(this);
        }
    }

    public void onClickListen(View view) {
        if(mSwitchListen.isChecked()) {
            AndHocService.startAndHocService(this);
        } else {
            AndHocService.stopAndHocService(this);
        }
    }

    public void onClickSend(View view) {
        if(mBroadcast) {
            String message = editTextMessage.getText().toString();
            editTextMessage.setText("");

            if (!message.equals("")) {
                AndHocMessage record = new AndHocMessage();
                record.add("user", UUID.randomUUID().toString());
                record.add("msg", message);
                mMessenger.broadcast(this, record);
            }
        } else {
            Toast.makeText(this, "Broadcasting is off!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickClear(View view) {
        messageList.clear();
        mAdapter.notifyDataSetChanged();
    }
}