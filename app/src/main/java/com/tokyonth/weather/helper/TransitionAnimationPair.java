package com.tokyonth.weather.helper;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

public class TransitionAnimationPair extends Pair<View, String> {
    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public TransitionAnimationPair(@Nullable View first, @Nullable String second) {
        super(first, second);
    }

}
