package com.fangtang.tv.sdk.ui.tips.drawable;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;

public class ColorFilteredStateDrawable extends StateListDrawable {

    private final int[][] states;
    private final int[] colors;

    public ColorFilteredStateDrawable(Drawable drawable, int[][] states, int[] colors) {
        super();
        drawable.mutate();
        this.states = states;
        this.colors = colors;
        for (int i = 0; i < states.length; i++) {
            addState(states[i], drawable);
        }
    }

    public ColorFilteredStateDrawable(Drawable[] drawables, int[][] states, int[] colors) {
        super();
        this.states = states;
        this.colors = colors;
        for (int i = 0; i < states.length; i++) {
            addState(states[i], drawables[i]);
        }
    }


    @Override
    protected boolean onStateChange(int[] states) {
        if (this.states != null) {
            for (int i = 0; i < this.states.length; i++) {
                if (StateSet.stateSetMatches(this.states[i], states)) {
                    super.setColorFilter(this.colors[i], PorterDuff.Mode.SRC_IN);
                    return super.onStateChange(states);
                }
            }
            super.clearColorFilter();
        }
        return super.onStateChange(states);
    }

    @Override
    public boolean isStateful() {
        return true;
    }

}
