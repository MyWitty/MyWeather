package db;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.Country;
import model.Province;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/*
 * �����õ����ݿ������װ�������Ա����ʹ��
 */
public class MyWeatherDB {
	/**
	 * ���ݿ���
	 */
	public static final String DB_NAME = "My_weather";

	/**
	 * ���ݿ�汾
	 */
	public static final int VERSION = 1;
	private static MyWeatherDB myWeatherDB;
	private SQLiteDatabase db;

	/**
	 * �����췽��˽�л�,Ϊ����ģʽ��׼��
	 */
	private MyWeatherDB(Context context) {
		MyDbHelper dbHelper = new MyDbHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();// �����ڳ�ʼ����ʱ��ͽ�����
	}

	/**
	 * ��ȡMyWeatherDB��ʵ�� ʹ�õ���ģʽ
	 */
	public synchronized static MyWeatherDB getInstance(Context context) {
		if (myWeatherDB == null) {
			myWeatherDB = new MyWeatherDB(context);
		}
		return myWeatherDB;
	}

	/**
	 * ��Provinceʵ���洢�����ݿ�
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("province_name", province.getProvinceName());// key�뽨��ʱ������Ҫһ��
			contentValues.put("province_code", province.getProvinceCode());
			db.insert("Province", null, contentValues);
			// ����sql���Ҳ����
			// String
			// sql="insert into Province(province_name,province_code) values(?,?)";
			// db.execSQL(sql,new
			// String[]{province.getProvinceName(),province.getProvinceCode()});
		}
	}

	/**
	 * �����ݿ��ȡȫ�����е������Ϣ
	 */
	public List<Province> loadProvince() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor
						.getColumnIndexOrThrow("province_id")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndexOrThrow("province_code")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndexOrThrow("province_name")));
				list.add(province);
			} while (cursor.moveToNext());
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	/**
	 * ��Cityʵ���洢�����ݿ�
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("city_name", city.getCityName());
			contentValues.put("city_code", city.getCityCode());
			contentValues.put("province_id", city.getProvinceId());
			db.insertOrThrow("City", null, contentValues);
		}

	}

	/**
	 * �����ݿ��ȡĳʡ�µĳ�����Ϣ
	 */
	public List<City> loadCity(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id=?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndexOrThrow("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndexOrThrow("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());

		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	/**
	 * ��Countryʵ����ӵ����ݿ�
	 */

	public void saveCountry(Country country) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("country_code", country.getCountryCode());
		contentValues.put("country_name", country.getCountryName());
		contentValues.put("city_id", country.getCityId());
		db.insert("Country", null, contentValues);
	}

	/**
	 * �����ݿ���ĳ�����л�ȡ����Ϣ
	 */
	public List<Country> loadCountry(int cityId) {
		List<Country> list = new ArrayList<Country>();
		Cursor cursor = db.query("Country", null, "city_id=?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Country country = new Country();
				country.setCityId(cityId);
				country.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
				country.setCountryCode(cursor.getString(cursor
						.getColumnIndexOrThrow("country_code")));
				country.setCountryName(cursor.getString(cursor
						.getColumnIndexOrThrow("country_name")));
				list.add(country);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

}
