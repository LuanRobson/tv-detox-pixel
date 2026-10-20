package com.ansub.tvhome;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

// A tile in the 8-bit look: corners cut in two pixel steps, a solid frame, an optional hard drop
// shadow (drawn past the bounds, so the parent must not clip) and inside either a fill or a picture.
final class PixelFrame extends Drawable {
    private final Paint paint = new Paint(); // no anti-aliasing: edges stay crisp
    private final Path ring = new Path(), inner = new Path(), shadowPath = new Path();
    private final int step; // corner cut, in px; the inner edge uses half
    private int frame, frameW, shadow, shadowOff;
    private int fill;
    private Drawable content;

    PixelFrame(int step, int frame, int frameW, int fill) {
        this.step = step;
        this.frame = frame;
        this.frameW = frameW;
        this.fill = fill;
    }

    PixelFrame content(Drawable d) { content = d; invalidateSelf(); return this; }

    // Frame color and width, and the shadow's offset and color (offset 0: none).
    void set(int frame, int frameW, int shadowOff, int shadow) {
        this.frame = frame;
        this.frameW = frameW;
        this.shadowOff = shadowOff;
        this.shadow = shadow;
        invalidateSelf();
    }

    void fill(int color) { fill = color; invalidateSelf(); }

    @Override
    public void draw(Canvas c) {
        Rect b = getBounds();
        float l = b.left, t = b.top, r = b.right, bt = b.bottom;
        float fl = l + frameW, ft = t + frameW, fr = r - frameW, fb = bt - frameW;
        if (shadowOff > 0 && (shadow >>> 24) != 0) {
            shadowPath.reset();
            notch(shadowPath, l + shadowOff, t + shadowOff, r + shadowOff, bt + shadowOff, step);
            paint.setColor(shadow);
            c.drawPath(shadowPath, paint);
        }
        inner.reset();
        notch(inner, fl, ft, fr, fb, Math.max(1, step / 2));
        if ((fill >>> 24) != 0) {
            paint.setColor(fill);
            c.drawPath(inner, paint);
        }
        if (content != null) {
            c.save();
            c.clipPath(inner);
            content.setBounds(Math.round(fl), Math.round(ft), Math.round(fr), Math.round(fb));
            content.draw(c);
            c.restore();
        }
        if (frameW > 0 && (frame >>> 24) != 0) {
            ring.reset();
            ring.setFillType(Path.FillType.EVEN_ODD);
            notch(ring, l, t, r, bt, step);
            ring.addPath(inner);
            paint.setColor(frame);
            c.drawPath(ring, paint);
        }
    }

    // A rectangle whose corners are cut in two steps of s/2, like a sprite's.
    private static void notch(Path p, float l, float t, float r, float b, float s) {
        float h = s / 2;
        p.moveTo(l + s, t);
        p.lineTo(r - s, t); p.lineTo(r - s, t + h); p.lineTo(r - h, t + h); p.lineTo(r - h, t + s); p.lineTo(r, t + s);
        p.lineTo(r, b - s); p.lineTo(r - h, b - s); p.lineTo(r - h, b - h); p.lineTo(r - s, b - h); p.lineTo(r - s, b);
        p.lineTo(l + s, b); p.lineTo(l + s, b - h); p.lineTo(l + h, b - h); p.lineTo(l + h, b - s); p.lineTo(l, b - s);
        p.lineTo(l, t + s); p.lineTo(l + h, t + s); p.lineTo(l + h, t + h); p.lineTo(l + s, t + h);
        p.close();
    }

    @Override public void setAlpha(int a) { paint.setAlpha(a); }
    @Override public void setColorFilter(ColorFilter cf) { paint.setColorFilter(cf); }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}
