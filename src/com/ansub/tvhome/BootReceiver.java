package com.ansub.tvhome;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

// This Skyworth/MediaTek TV starts the home screen at boot, then a MediaTek service puts Live TV on
// top before the home screen's process even starts. When boot finishes, if nothing but Live TV has
// come up since (nobody picked an app), bring the home screen to the front.
// It needs usage access (granted by hand with appops) to tell; without it, it does nothing.
// It also starts listening for the TV being turned on, see ScreenOn.
public class BootReceiver extends BroadcastReceiver {
    private static final String LIVE_TV = "com.smartdevice.livetv";
    // Android's own "starting up" screen, shown for a moment at every boot.
    private static final String FALLBACK_HOME = "com.android.tv.settings.system.FallbackHome";
    private static boolean handled; // LOCKED_BOOT_COMPLETED and BOOT_COMPLETED: act on the first

    @Override
    public void onReceive(Context c, Intent i) {
        String a = i.getAction();
        if (!Intent.ACTION_BOOT_COMPLETED.equals(a) && !Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(a)) return;
        Context app = c.getApplicationContext();
        ScreenOn.listen(app);
        if (handled) return;
        Boolean onlyLive = onlyLiveTvSinceBoot(app);
        if (onlyLive == null) return; // usage access not ready yet (or not granted): try on the next one
        handled = true;
        if (onlyLive) ScreenOn.bringHome(app);
    }

    // True if only Live TV came up since boot, false if something else did, null if it can't tell.
    private static Boolean onlyLiveTvSinceBoot(Context c) {
        try {
            UsageStatsManager usm = (UsageStatsManager) c.getSystemService(Context.USAGE_STATS_SERVICE);
            long now = System.currentTimeMillis();
            UsageEvents ev = usm.queryEvents(now - SystemClock.elapsedRealtime(), now);
            UsageEvents.Event e = new UsageEvents.Event();
            while (ev.hasNextEvent()) {
                ev.getNextEvent(e);
                if (e.getEventType() != UsageEvents.Event.ACTIVITY_RESUMED) continue;
                String p = e.getPackageName();
                if (LIVE_TV.equals(p) || c.getPackageName().equals(p) || FALLBACK_HOME.equals(e.getClassName())) continue;
                return false; // something else was opened
            }
            return true;
        } catch (Exception ex) {
            return null;
        }
    }
}
