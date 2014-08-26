package senwang.monthlycost;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by hitbe_000 on 8/20/2014.
 */
public class RecordAdapter extends BaseAdapter {

	private Context mContext;

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	private class ViewHolder {
		public TextView type_text;
		public TextView amount_text;
		public TextView time_text;
	}
}
