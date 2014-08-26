package senwang.monthlycost;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MyActivity extends ListActivity {

	private static final String TAG = MyActivity.class.getSimpleName();

	private TextView mBalanceTextView;

	private List<AVObject> mRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_my);

		mBalanceTextView = (TextView) findViewById(R.id.balance_text_view);
	}

	@Override
	protected void onResume() {
		super.onResume();

		AVQuery<AVObject> query = new AVQuery<AVObject>(AVConstants.CLASS_RECORD);
		query.include(AVConstants.KEY_TAGS);
		query.addDescendingOrder(AVConstants.KEY_TIME);
		setProgressBarIndeterminateVisibility(true);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> records, AVException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					mRecords = records;
					BigDecimal sum = sumRecords(mRecords);
					mBalanceTextView.setText(sum.toString());

					fillRecentRecordList(mRecords.subList(0, 5));
				} else {
					Log.w(Utils.getTag(), getString(R.string.find_all_records_failed_log_text), e);
					Toast.makeText(getApplicationContext(), R.string.find_all_records_failed_toast, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private BigDecimal sumRecords(List<AVObject> records) {
		BigDecimal sum = new BigDecimal("0");
		ArrayList<HashMap<String, String>> recentRecords = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < records.size(); i++) {
			String amountStr = records.get(i).getString(AVConstants.KEY_AMOUNT);
			BigDecimal amount = new BigDecimal(amountStr);
			sum = sum.add(amount);
		}
		return sum;
	}

	/**
	 *
	 * @param recentRecords RecentRecords must have been sorted with descend order
	 */
	private void fillRecentRecordList(List<AVObject> recentRecords) {
		ArrayList<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();
		for (AVObject record : recentRecords) {
			HashMap<String, String> row = new HashMap<String, String>();
			// Get record type
			boolean typeIsExpense = record.getBoolean(AVConstants.KEY_TYPE_IS_EXPENSE);
			if (typeIsExpense) {
				row.put("type", getString(R.string.expense));
			} else {
				row.put("type", getString(R.string.income));
			}
			// Get amount of money and record type
			String amountStr = record.getString(AVConstants.KEY_AMOUNT);
			row.put(AVConstants.KEY_AMOUNT, amountStr);
			// Get record tag
			AVObject tag = record.getAVObject(AVConstants.KEY_TAGS);
			String tagName = tag.getString(AVConstants.KEY_TAG_NAME);
			row.put(AVConstants.KEY_TAG_NAME, tagName);
			// Get record time
			long timeMillis = record.getLong(AVConstants.KEY_TIME);
			Date time = new Date(timeMillis);
			DateFormat df = DateFormat.getDateTimeInstance();
			row.put(AVConstants.KEY_TIME, df.format(time));

			rows.add(row);
		}
		// Adapt list
		SimpleAdapter adapter = new SimpleAdapter(MyActivity.this, rows,
				R.layout.record_list_item,
				new String[] {"type", AVConstants.KEY_AMOUNT, AVConstants.KEY_TAG_NAME, AVConstants.KEY_TIME},
				new int[] {R.id.type_text, R.id.amount_text, R.id.tag_text, R.id.time_text});
		setListAdapter(adapter);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
		switch (id) {
		case R.id.action_new_record:
			Intent intent = new Intent(this, NewRecordActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
        return super.onOptionsItemSelected(item);
    }
}
