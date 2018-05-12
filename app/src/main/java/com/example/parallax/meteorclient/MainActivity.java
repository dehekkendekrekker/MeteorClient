package com.example.parallax.meteorclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parallax.meteorclient.factories.Task;
import com.example.parallax.meteorclient.factories.TaskFactory;
import com.example.parallax.meteorclient.liststuff.Adapter;

import java.util.ArrayList;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.ResultListener;

//import im.delight.android.ddp.Meteor;
//import im.delight.android.ddp.MeteorCallback;
//import im.delight.android.ddp.db.memory.InMemoryDatabase;

public class MainActivity extends AppCompatActivity implements MeteorCallback {

    public static final int LOGIN_REQUEST = 999;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<Task> listTasks =new ArrayList<Task>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    Adapter adapter;

    private Meteor mMeteor;

    private ListView list;

    private boolean loggedIn = false;

    private  Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final EditText ed = findViewById(R.id.add_task);
        ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    mMeteor.call("tasks.insert", new Object[] {ed.getText().toString()});
                    ed.setText("");
                }
                return false;
            }
        });

        list = (ListView) findViewById(R.id.list);

        // create a new instance
        MainApplication application = (MainApplication) getApplication();
        mMeteor = application.getMeteor();


        // establish the connection
        if (!mMeteor.isConnected()) {
            mMeteor.addCallback(this);
            mMeteor.connect();
        }

        adapter = new Adapter(this, listTasks, mMeteor);
        list.setAdapter(adapter);

        findViewById(R.id.list).requestFocus();
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        this.menu = menu;
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == LoginActivity.LOGGED_IN) { handleSignedIn(); }
        }
    }

    void handleSignedIn() {
        loggedIn = true;
        menu.findItem(R.id.action_sign_in).setEnabled(false).setVisible(false);
        menu.findItem(R.id.action_sign_out).setEnabled(true).setVisible(true);
        findViewById(R.id.add_task).setVisibility(EditText.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    void handleSignedOut() {
        loggedIn = false;
        menu.findItem(R.id.action_sign_in).setEnabled(true).setVisible(true);
        menu.findItem(R.id.action_sign_out).setEnabled(false).setVisible(false);
        findViewById(R.id.add_task).setVisibility(EditText.INVISIBLE);
        adapter.notifyDataSetChanged();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.my_toolbar), "Signed out", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void addTask(View view) {

    }

    /**
     *
     * @param menuItem
     */
    public void signIn(MenuItem menuItem)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST);
    }

    public void signOut(MenuItem menuItem) {
        mMeteor.logout(new ResultListener() {
            @Override
            public void onSuccess(String result) {
                handleSignedOut();
            }

            @Override
            public void onError(String error, String reason, String details) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.my_toolbar), "Unable to sign out", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    public boolean isLoggedIn() {
        return mMeteor.isLoggedIn();
    }

    public String getLoggedInUserId() {
        return mMeteor.getUserId();
    }


    @Override
    public void onConnect(boolean signedInAutomatically) {

        Snackbar snackbar = Snackbar.make(findViewById(R.id.my_toolbar), "Connected ...", Snackbar.LENGTH_SHORT);
        snackbar.show();

        mMeteor.subscribe("tasks");

        // See if we're logged in
        if (isLoggedIn()) handleSignedIn();
    }

    @Override
    public void onDisconnect() {
        adapter.clear();
        Snackbar snackbar = Snackbar.make(findViewById(R.id.my_toolbar), "Disconnected ...", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onException(Exception e) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.my_toolbar), "Could not connect to server, check connectivity", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onDataAdded(String collectionName, String documentID, String newValuesJson) {
        if (!collectionName.equals("tasks")) {
            return;
        }

        Task task = TaskFactory.create(documentID, newValuesJson);
        adapter.add(task);
    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        if (!collectionName.equals("tasks")) {
            return;
        }

        Task task = adapter.getItem(documentID);

        TaskFactory.update(task, updatedValuesJson);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        if (!collectionName.equals("tasks")) {
            return;
        }

        Task task = new Task();
        task.id = documentID;
        adapter.remove(task);

    }

    @Override
    public void onDestroy() {
        mMeteor.disconnect();
        mMeteor.removeCallback(this);
        // or
        // mMeteor.removeCallbacks();

        // ...

        super.onDestroy();
    }
}
