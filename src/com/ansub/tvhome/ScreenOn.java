package com.ansub.tvhome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

// Turning the TV on (out of standby) always lands on the home screen, not on whatever app was
// open before. Android only sends "screen on" to receivers registered while the app runs, so it's
// registered once per process, from the home screen or from boot.
final class ScreenOn extends BroadcastReceiver {
    private static ScreenOn registered;

    static synchronized void listen(Context app) {
        if (registered != null) return;
        registered = new ScreenOn();
        app.getApplicationContext().registerReceiver(registered, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    // Starting it from the background needs "display over other apps" (granted by hand with appops).
    static void bringHome(Context c) {
        try {
            c.startActivity(new Intent(c, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        } catch (Exception ignored) {}
    }

    @Override
    public void onReceive(Context c, Intent i) {
        bringHome(c.getApplicationContext());
    }
}
