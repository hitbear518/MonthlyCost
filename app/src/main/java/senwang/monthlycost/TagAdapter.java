package senwang.monthlycost;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.avos.avoscloud.AVObject;

import java.util.List;

/**
 * Created by hitbe_000 on 8/21/2014.
 */
public class TagAdapter extends BaseAdapter {

	private Context mContext;
	private List<AVObject> mTags;

	public TagAdapter(Context context, List<AVObject> tags) {
		mContext = context;
		mTags = tags;
	}

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
}
