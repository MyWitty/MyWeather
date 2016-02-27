package activity;

import service.AutoUpdateService;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

import com.example.myweather.R;

import android.app.Activity;
import android.app.SearchManager.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherAcitity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;// ��ʾ������
	private TextView publishText;// ������ʾ����ʱ��
	private TextView weatherDespText;// �����������������
	private TextView tempHighText, tempLowText;// �������¶�
	private TextView currentDateText;// ������ʾ��ǰ����
	private Button switchCity;// �л�����
	private Button refreshWeather;// ˢ������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �Զ����ɵķ������
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		tempHighText = (TextView) findViewById(R.id.temp1);
		tempLowText = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		// closeStrictMode();
		String countryCode = getIntent().getStringExtra("country_code");// �ֶ�ѡ�����ʱ��ô�������country_code
		if (!TextUtils.isEmpty(countryCode)) {
			// ���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����......");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		} else {
			// û���ؼ�����ʱ��ֱ����ʾ��������
			showWeather();
		}
		//���� AutoUpdateService ����ʵ�ֺ�̨��ʱ���µĹ���
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

	/*
	 * private void closeStrictMode() { StrictMode.setThreadPolicy(new
	 * StrictMode.ThreadPolicy.Builder() .detectAll().penaltyLog().build()); }
	 */

	/**
	 * ��ѯ�ؼ���������Ӧ����������
	 * 
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countryCode + ".xml";
		queryFromServer(address, "countryCode");
	}

	/**
	 * ��ѯ������������Ӧ������
	 * 
	 * @param weatherCode
	 */
	public void queryWeatherInfo(String weatherCode) {
		String address = "http://wthrcdn.etouch.cn/weather_mini?citykey="
				+ weatherCode;
		Log.d("weatherCode--->", weatherCode);

		queryFromServer(address, "weatherCode");
	}

	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ ���裺�Ȼ��������Ӧ���������ţ��ٸ�����������ȥ����������Ϣ
	 * 
	 * @param address
	 * @param string
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				if ("countryCode".equals(type)) {

					if (!TextUtils.isEmpty(response)) {
						// �ӷ��������ص������н�������������
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							SharedPreferences.Editor sp = PreferenceManager
									.getDefaultSharedPreferences(
											WeatherAcitity.this).edit();
							sp.putString("weather_code", weatherCode);
							sp.commit();
							queryWeatherInfo(weatherCode);
							Log.d("countryCode��Ӧ��response--->", response);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// ������������ص�������Ϣ
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Utility.handleWeatherResponse(WeatherAcitity.this,
									response);
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}

	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(sharedPreferences.getString("city_name", ""));
		tempHighText.setText(sharedPreferences.getString("temp_high", ""));
		tempLowText.setText(sharedPreferences.getString("temp_low", ""));
		weatherDespText.setText(sharedPreferences.getString(
				"weather_description", ""));
		publishText.setText("���� "
				+ sharedPreferences.getString("publish_Time", "") + "����");
		currentDateText
				.setText(sharedPreferences.getString("current_data", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weatherActivity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("����ˢ��......");
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = sharedPreferences
					.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;

		default:
			break;
		}
	}

}
