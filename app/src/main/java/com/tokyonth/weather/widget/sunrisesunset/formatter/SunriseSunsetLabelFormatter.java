package com.tokyonth.weather.widget.sunrisesunset.formatter;

import com.tokyonth.weather.widget.sunrisesunset.model.Time;

/**
 * 日出日落标签格式化
 */
public interface SunriseSunsetLabelFormatter {

    String formatSunriseLabel(Time sunrise);

    String formatSunsetLabel(Time sunset);

}
