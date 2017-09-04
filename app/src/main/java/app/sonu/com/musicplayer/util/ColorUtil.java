package app.sonu.com.musicplayer.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

/**
 * Created by sonu on 14/8/17.
 */

public class ColorUtil {

    private static final int BACKGROUND_COLOR_FALLBACK = Color.parseColor("#e0e0e0");
    private static final int TITLE_COLOR_FALLBACK = Color.parseColor("#484848");
    private static final int BODY_COLOR_FALLBACK = Color.parseColor("#757575");

    private static final int TITLE_COLOR_DARK = Color.parseColor("#484848");
    private static final int BODY_COLOR_DARK = Color.parseColor("#757575");

    private static final int TITLE_COLOR_LIGHT = Color.parseColor("#f5f5f5");
    private static final int BODY_COLOR_LIGHT = Color.parseColor("#eeeeee");

    @Nullable
    public static void generatePalette(Bitmap bitmap,
                                          Palette.PaletteAsyncListener paletteAsyncListener) {
        if (bitmap == null){
            return;
        }
        Palette.from(bitmap).generate(paletteAsyncListener);
    }

    public static Palette generatePalette(Bitmap bitmap) {
        if (bitmap == null){
            return null;
        }
        return Palette.from(bitmap).generate();
    }

    public static int getBackgroundColor(Palette.Swatch swatch) {
        if (swatch == null) {
            return BACKGROUND_COLOR_FALLBACK;
        }

        return swatch.getRgb();
    }

    public static int getTitleColor(Palette.Swatch swatch) {
        if (swatch == null) {
            return TITLE_COLOR_FALLBACK;
        }
        if (isColorDark(swatch.getRgb())) {
            return TITLE_COLOR_LIGHT;
        } else {
            return TITLE_COLOR_DARK;
        }
    }

    public static int getBodyColor(Palette.Swatch swatch) {
        if (swatch == null) {
            return BODY_COLOR_FALLBACK;
        }

        if (isColorDark(swatch.getRgb())) {
            return BODY_COLOR_LIGHT;
        } else {
            return BODY_COLOR_DARK;
        }
    }

    public static Palette.Swatch getColorSwatch(@Nullable Palette palette) {
        if (palette != null) {
            if (palette.getMutedSwatch() != null) {
                return palette.getMutedSwatch();
            } else
                if (palette.getDarkMutedSwatch() != null) {
                return palette.getDarkMutedSwatch();
            } else if (palette.getLightMutedSwatch() != null) {
                return palette.getLightMutedSwatch();
            }
        }
        return null;
    }

    private static boolean isColorDark(int color){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness < 0.2) {
            return false; // It's a light color
        } else {
            return true; // It's a dark color
        }
    }

    public static int makeColorTransparent(int color){
        return Color.argb(150, Color.red(color) , Color.green(color), Color.blue(color));
    }
}
