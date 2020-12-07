package com.tokyonth.weather.model;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.tokyonth.weather.Constants;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

public class LocationModelImpl implements LocationModel {

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private LocationMonitor locationMonitor;

    @Override
    public void accessLocation(Context context, LocationMonitor locationMonitor) {
        // 初始化client
        locationClient = new AMapLocationClient(context);
        locationOption = getDefaultOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(new locationListener());
        this.locationMonitor = locationMonitor;
        startLocation();
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(6000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效(已设置6秒超时)
        mOption.setInterval(1000);//可选，设置定位间隔。默认为2秒(已设置1秒)
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    /**
     * 定位监听
     */
    class locationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (null != aMapLocation && aMapLocation.getErrorCode() == 0) {
                // errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                String cityName = aMapLocation.getCity();
                String districtName = aMapLocation.getDistrict();
                String longitude = String.valueOf(aMapLocation.getLongitude());
                String latitude = String.valueOf(aMapLocation.getLatitude());

                // 区级定位
                DefaultCity defaultCity = new DefaultCity(districtName, cityName, longitude, latitude);
                // 市级定位
                // DefaultCity defaultCity = new DefaultCity(cityName, districtName, longitude, latitude);
                // 临时解决极速数据郫都区不能识别为郫县问题
                if (defaultCity.getCityName().equals("郫都区")) {
                    defaultCity.setCityName("郫县");
                }

                if (LitePal.count(DefaultCity.class) == 0) {
                    defaultCity.save();
                } else {
                    defaultCity.updateAsync(1);
                }
                stopLocation();
                EventBus.getDefault().post(defaultCity);

                String precise_location = aMapLocation.getAddress().substring(aMapLocation.getAddress().indexOf(aMapLocation.getDistrict())
                        + aMapLocation.getDistrict().length(), aMapLocation.getAddress().lastIndexOf(""));
                FileUtil.saveFile(precise_location, Constants.PRECISE_LOCATION_NAME);
                locationMonitor.Success();
            } else {
                stopLocation();
                locationMonitor.Failure();
            }
        }

    }

    private void startLocation() {
        // 根据控件的选择，重新设置定位参数
        // resetOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
        destroyLocation();
    }

    private void destroyLocation() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

}
