package com.tokyonth.weather.presenter;

import android.content.Context;

import com.tokyonth.weather.model.LocationModel;
import com.tokyonth.weather.model.LocationModelImpl;
import com.tokyonth.weather.model.LocationMonitor;

public class LocationPresenterImpl implements LocationPresenter {

    private LocationModel locationModel;

    public LocationPresenterImpl(){
        locationModel = new LocationModelImpl();
    }

    @Override
    public void loadLocation(Context context, LocationMonitor locationMonitor) {
        locationModel.accessLocation(context, locationMonitor);
    }

}
