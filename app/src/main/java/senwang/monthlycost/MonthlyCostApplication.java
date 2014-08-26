package senwang.monthlycost;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by hitbe_000 on 8/18/2014.
 */
public class MonthlyCostApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		AVOSCloud.initialize(this, "h62exjctbce5run77re92owwr45eny3pooindij6sgfvwqk7", "kvrq53yy9tq5onod5hdl7ys9d6ed5tqccvs9je19lbevvmfv");
	}
}
