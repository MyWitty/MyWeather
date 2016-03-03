package application;

import cn.bmob.v3.Bmob;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		context = getApplicationContext();
		Bmob.initialize(this, "d74e11e720ca3900b5e5bec49c7c0c19");//��ʼ��������sdk������
		super.onCreate();
	}

	public static Context getContext() {
		return context;
	}
}
