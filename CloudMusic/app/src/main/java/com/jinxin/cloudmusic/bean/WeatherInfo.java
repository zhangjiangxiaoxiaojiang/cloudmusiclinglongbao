package com.jinxin.cloudmusic.bean;

/**
 * Created by XTER on 2016/8/12.
 * 天气
 */
public class WeatherInfo {
	private String date;
	private String dayPictureUrl;
	private String nightPictureUrl;
	private String weather;
	private String wind;
	private String temperature;

	public WeatherInfo() {
	}

	public WeatherInfo(String date, String dayPictureUrl,
	                   String nightPictureUrl, String weather, String wind,
	                   String temperature) {
		this.date = date;
		this.dayPictureUrl = dayPictureUrl;
		this.nightPictureUrl = nightPictureUrl;
		this.weather = weather;
		this.wind = wind;
		this.temperature = temperature;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDayPictureUrl() {
		return dayPictureUrl;
	}

	public void setDayPictureUrl(String dayPictureUrl) {
		this.dayPictureUrl = dayPictureUrl;
	}

	public String getNightPictureUrl() {
		return nightPictureUrl;
	}

	public void setNightPictureUrl(String nightPictureUrl) {
		this.nightPictureUrl = nightPictureUrl;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	@Override
	public String toString() {
		return "WeatherInfo [date=" + date + ", dayPictureUrl=" + dayPictureUrl
				+ ", nightPictureUrl=" + nightPictureUrl + ", weather="
				+ weather + ", wind=" + wind + ", temperature=" + temperature
				+ "]";
	}

}
