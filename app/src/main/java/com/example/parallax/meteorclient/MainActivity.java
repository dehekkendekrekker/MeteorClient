package com.example.parallax.meteorclient;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.parallax.meteorclient.factories.Task;
import com.example.parallax.meteorclient.factories.TaskFactory;
import com.example.parallax.meteorclient.liststuff.Adapter;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.db.memory.InMemoryDatabase;

//import im.delight.android.ddp.Meteor;
//import im.delight.android.ddp.MeteorCallback;
//import im.delight.android.ddp.db.memory.InMemoryDatabase;

public class MainActivity extends AppCompatActivity implements MeteorCallback {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<Task> listTasks =new ArrayList<Task>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    Adapter adapter;

    private Meteor mMeteor;

    private ListView list;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = getWindow().getDecorView().getRootView();

        //test();

        list = (ListView) findViewById(R.id.list);

        //listTasks.add(new Task("testniewitem"));

        adapter = new Adapter(this, listTasks);
        list.setAdapter(adapter);

        //adapter.add(new Task("testItem"));

        // create a new instance
        mMeteor = new Meteor(this, "https://cuscopay.com/websocket", new InMemoryDatabase());

        CertificateFactory cf;

        try {
            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(getResources().openRawResource(R.raw.ca));
            Certificate ca = cf.generateCertificate(caInput);
            mMeteor.trustCaCert(ca);
        } catch (CertificateException certException) {
            System.out.print("DDP-SSL: " + certException.getMessage());
        }

        // register the callback that will handle events and receive messages
        mMeteor.addCallback(this);

        // establish the connection
        mMeteor.connect();



    }




    @Override
    public void onConnect(boolean signedInAutomatically) {

        Snackbar snackbar = Snackbar.make(rootView, "Connected ...", Snackbar.LENGTH_SHORT);
        snackbar.show();

        String subscriptionId = mMeteor.subscribe("tasks");
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onException(Exception e) {
        Snackbar snackbar = Snackbar.make(rootView, "Could not connect to server, check connectivity", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onDataAdded(String collectionName, String documentID, String newValuesJson) {
        Task task = TaskFactory.create(documentID, newValuesJson);
        adapter.add(task);
    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        Task task = adapter.getItem(documentID);

        task = TaskFactory.update(task, updatedValuesJson);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
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
