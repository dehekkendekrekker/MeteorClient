package com.example.parallax.meteorclient;

import android.app.Application;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.db.memory.InMemoryDatabase;

public class MainApplication extends Application{

    private Meteor meteor;

    @Override
    public void onCreate() {
        super.onCreate();
        meteor = new Meteor(this, "http://192.168.43.209:3000/websocket", new InMemoryDatabase());

        CertificateFactory cf;

        try {
            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(getResources().openRawResource(R.raw.ca));
            Certificate ca = cf.generateCertificate(caInput);
            meteor.trustCaCert(ca);
        } catch (CertificateException certException) {
            System.out.print("DDP-SSL: " + certException.getMessage());
        }

    }

    public Meteor getMeteor() {
        return meteor;
    }




}
