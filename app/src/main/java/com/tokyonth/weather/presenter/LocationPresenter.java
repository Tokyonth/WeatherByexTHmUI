package com.tokyonth.weather.presenter;

import android.content.Context;

import com.tokyonth.weather.model.LocationMonitor;

public interface LocationPresenter {

    void loadLocation(Context context, LocationMonitor locationMonitor);

}
