package com.ansub.tvhome;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

// Pixel-art icons on a 12x12 grid, drawn as solid squares: each icon is a list of x, y, w, h runs.
final class PixelIcon extends Drawable {
    static final int[] ANTENNA = {3, 0, 1, 1, 4, 1, 1, 1, 8, 0, 1, 1, 7, 1, 1, 1, 5, 2, 2, 1,
            1, 3, 10, 1, 1, 10, 10, 1, 1, 3, 1, 8, 10, 3, 1, 8, 3, 5, 2, 1};
    static final int[] PLAY = {2, 1, 2, 10, 4, 2, 2, 8, 6, 3, 2, 6, 8, 4, 2, 4, 10, 5, 1, 2};
    static final int[] HDMI = {1, 4, 10, 1, 1, 4, 1, 3, 10, 4, 1, 3, 2, 7, 1, 1, 9, 7, 1, 1, 3, 8, 6, 1, 3, 6, 6, 1};
    static final int[] SETTINGS = {1, 3, 10, 1, 7, 2, 2, 3, 1, 8, 10, 1, 3, 7, 2, 3};
    static final int[] PLUS = {5, 2, 2, 8, 2, 5, 8, 2};
    static final int[] WIFI = {3, 2, 6, 1, 1, 3, 2, 1, 9, 3, 2, 1, 0, 4, 1, 1, 11, 4, 1, 1,
            4, 5, 4, 1, 2, 6, 2, 1, 8, 6, 2, 1, 5, 9, 2, 2};
    static final int[] WIFI_2 = {4, 5, 4, 1, 2, 6, 2, 1, 8, 6, 2, 1, 5, 9, 2, 2};
    static final int[] WIFI_1 = {5, 9, 2, 2};
    static final int[] SLASH = {1, 1, 2, 1, 2, 2, 2, 1, 3, 3, 2, 1, 4, 4, 2, 1, 5, 5, 2, 1, 6, 6, 2, 1,
            7, 7, 2, 1, 8, 8, 2, 1, 9, 9, 2, 1};
    static final int[] ETHERNET = {1, 2, 10, 1, 1, 2, 1, 7, 10, 2, 1, 7, 1, 8, 3, 1, 8, 8, 3, 1,
            3, 8, 1, 2, 8, 8, 1, 2, 3, 10, 6, 1, 3, 4, 1, 2, 5, 4, 2, 2, 8, 4, 1, 2};

    private final int[] runs;
    private final Paint paint = new Paint();

    PixelIcon(int[] runs, int color) {
        this.runs = runs;
        paint.setColor(color);
    }

    @Override
    public void draw(Canvas c) {
        float s = getBounds().width() / 12f;
        float x0 = getBounds().left, y0 = getBounds().top;
        for (int i = 0; i + 3 < runs.length; i += 4)
            c.drawRect(x0 + runs[i] * s, y0 + runs[i + 1] * s, x0 + (runs[i] + runs[i + 2]) * s,
                    y0 + (runs[i + 1] + runs[i + 3]) * s, paint);
    }

    @Override public void setAlpha(int a) { paint.setAlpha(a); }
    @Override public void setColorFilter(ColorFilter cf) { paint.setColorFilter(cf); }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
    @Override public int getIntrinsicWidth() { return 48; }
    @Override public int getIntrinsicHeight() { return 48; }
}
