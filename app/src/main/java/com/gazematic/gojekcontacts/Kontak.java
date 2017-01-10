package com.gazematic.gojekcontacts;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.gazematic.gojekcontacts.data.KontakAPIInterface;
import com.gazematic.gojekcontacts.data.KontakFactory;

import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by Karthi on 1/6/2017.
 */

public class Kontak extends Application {

    public void onCreate() {
        super.onCreate();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                .build();
        Fresco.initialize(this, config);
    }

    private KontakAPIInterface kontakAPIInterface;
    private Scheduler scheduler;

    private static Kontak get(Context context) {
        return (Kontak) context.getApplicationContext();
    }

    public static Kontak create(Context context) {
        return Kontak.get(context);
    }

    public KontakAPIInterface getContactsService() {
        if (kontakAPIInterface == null) kontakAPIInterface = KontakFactory.create();
        return kontakAPIInterface;
    }

    public Scheduler subscribeScheduler() {
        if (scheduler == null) scheduler = Schedulers.io();
        return scheduler;
    }

    public void setKontakAPIInterface(KontakAPIInterface kontakAPIInterface) {
        this.kontakAPIInterface = kontakAPIInterface;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
