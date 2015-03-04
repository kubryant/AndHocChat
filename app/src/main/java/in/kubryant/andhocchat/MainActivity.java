package in.kubryant.andhocchat;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
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
import in.kubryant.andhoclib.src.AndHocService;

public class MainActivity extends /*ActionBar*/Activity {
    private EditText editTextMessage;
    private ToggleButton mSwitchBroadcast;
    private ToggleButton mSwitchListen;

    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> messageList = new ArrayList<>();
    private ArrayList<String> repeatCheck = new ArrayList<>();

    //private String mUser = UUID.randomUUID().toString();
    private AndHocService mService;
    private ServiceConnection mConnection;
    private boolean mBound;
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

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Toast.makeText(getApplicationContext(), "AndHocService Connected", Toast.LENGTH_SHORT).show();
                Log.d("SHOUT", "AndHocService Connected");
                mService = ((AndHocService.AndHocBinder)service).getService();
                mBound = true;

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
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(getApplicationContext(), "AndHocService Disconnected", Toast.LENGTH_SHORT).show();
                Log.d("SHOUT", "AndHocService Disconnected");
                mService = null;
            }
        };
        Log.d("SHOUT", "Binding Service..");
        Intent intent = new Intent(this, AndHocService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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
        if(mBound) {
            mService.stopBroadcast();
            mService.stopListen();
            unbindService();
        }
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
        if(mBound) {
            if(mSwitchBroadcast.isChecked()) {
                mBroadcast = true;
            } else {
                mBroadcast = false;
                mService.stopBroadcast();
            }
        }
    }

    public void onClickListen(View view) {
        if(mBound) {
            if(mSwitchListen.isChecked()) {
                mService.listenTimer(10);
            } else {
                mService.stopListen();
            }
        }
    }

    public void onClickSend(View view) {
        if(mBroadcast) {
            String message = editTextMessage.getText().toString();

            if (!message.equals("")) {
                AndHocMessage record = new AndHocMessage();
                record.add("user", UUID.randomUUID().toString());
                record.add("msg", message);
                mService.broadcast(record);
            }
        } else {
            Toast.makeText(this, "Broadcasting is off!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickClear(View view) {
        messageList.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void unbindService() {
        if(mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
}