package senwang.monthlycost;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewRecordActivity extends Activity {

	private RadioButton mExpenseRadio;
	private EditText mTagEdit;
	private GridView mTagGrid;
	private EditText mAmountEdit;
	private Button mOkButton;

	private List<AVObject> mExpenseTags;
	private List<AVObject> mIncomeTags;
	private List<String> mExpenseTagNames;
	private List<String> mIncomeTagNames;

	private static final Set<String> EMPTY_TAG_SET = new HashSet<String>();
	static {
		EMPTY_TAG_SET.add(null);
		EMPTY_TAG_SET.add("");
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_new_record);

		mExpenseRadio = (RadioButton) findViewById(R.id.expense_radio_button);
		mTagEdit = (EditText) findViewById(R.id.tags_edit);
		mTagGrid = (GridView) findViewById(R.id.tags_grid);
		mAmountEdit = (EditText) findViewById(R.id.amount_edit);
		mOkButton = (Button) findViewById(R.id.ok_button);

		mTagGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		mExpenseTags = new ArrayList<AVObject>();
		mIncomeTags = new ArrayList<AVObject>();
		mExpenseTagNames = new ArrayList<String>();
		mIncomeTagNames = new ArrayList<String>();
		queryTagsInBackground();
		mTagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				changeTagStringOnTagClicked((TextView) view, position);
			}
		});

		mTagEdit.addTextChangedListener(new TextWatcher() {

			private Set<String> mTagSetBeforeTextChanged;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				mTagSetBeforeTextChanged = new HashSet<String>(Arrays.asList(s.toString().split("\\s*,\\s*")));
				mTagSetBeforeTextChanged.removeAll(EMPTY_TAG_SET);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Set<String> tagSet = new HashSet<String>(new HashSet<String>(Arrays.asList(s.toString().split("\\s*,\\s*"))));
				tagSet.removeAll(EMPTY_TAG_SET);
				if (!tagSet.equals(mTagSetBeforeTextChanged)) {
					Log.d(Utils.getTag(), "TagSet Changed");
					if (mExpenseRadio.isChecked()) {
						for (int i = 0; i < mExpenseTagNames.size(); i++) {
							if (tagSet.contains(mExpenseTagNames.get(i))) {
								mTagGrid.setItemChecked(i, true);
							} else {
								mTagGrid.setItemChecked(i, false);
							}
						}
					} else {
						for (int i = 0; i < mIncomeTagNames.size(); i++) {
							if (tagSet.contains(mIncomeTagNames.get(i))) {
								mTagGrid.setItemChecked(i, true);
							} else {
								mTagGrid.setItemChecked(i, false);
							}
						}
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mOkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newRecord();
			}
		});
	}

	/**
	 * Remove empty and duplicate tags in list
	 * @param tagList
	 */
	private void removeInvalidTag(List<String> tagList) {
		tagList.removeAll(EMPTY_TAG_SET);
		tagList.retainAll(new HashSet<String>(tagList));
	}

	private void changeTagStringOnTagClicked(TextView clickedTagView, int position) {
		String tagStr = mTagEdit.getText().toString().trim();
		// Split tag string into a string set
		List<String> tagList = new ArrayList<String>(Arrays.asList(tagStr.split("\\s*,\\s*")));
		removeInvalidTag(tagList);
		String clickedTag = clickedTagView.getText().toString();
		if (mTagGrid.isItemChecked(position)) {
			// Tag checked, add tag if not in list
			if (!tagList.contains(clickedTag)) {
				tagList.add(clickedTag);
			}
		} else {
			tagList.removeAll(Collections.singleton(clickedTag));
		}
		// Build the new tag string from set
		StringBuilder stringBuilder = new StringBuilder();
		for (String tag : tagList) {
			stringBuilder.append(tag).append(", ");
		}
		String newTagStr = stringBuilder.toString();
		Log.d(Utils.getTag(), "Joined text: " + newTagStr);
		mTagEdit.setText(newTagStr);
	}

	private void queryTagsInBackground() {
		AVQuery<AVObject> query = new AVQuery<AVObject>(AVConstants.CLASS_TAG);
		setProgressBarIndeterminateVisibility(true);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> tags, AVException e) {
				setProgressBarIndeterminateVisibility(false);

				if (e == null) {
					for (AVObject tag : tags) {
						if (tag.getBoolean(AVConstants.KEY_TYPE_IS_EXPENSE)) {
							mExpenseTags.add(tag);
							mExpenseTagNames.add(tag.getString(AVConstants.KEY_TAG_NAME));
						} else {
							mIncomeTags.add(tag);
							mIncomeTagNames.add(tag.getString(AVConstants.KEY_TAG_NAME));
						}
					}
					ArrayList<String> tagNames = new ArrayList<String>(mExpenseTagNames);
					ArrayAdapter<String> tagAdapter = new ArrayAdapter<String>(NewRecordActivity.this, android.R.layout.simple_list_item_checked, tagNames);
					mTagGrid.setAdapter(tagAdapter);
				} else {
					Log.w(Utils.getTag(), getString(R.string.find_tags_failed_log), e);
					Toast.makeText(getApplicationContext(), R.string.find_tags_failed_tost, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void onRadioButtonClicked(View view) {
		ArrayAdapter<String> tagAdapter = (ArrayAdapter<String>) mTagGrid.getAdapter();
		tagAdapter.clear();
		ArrayList<String> tagNames = null;
		if (mExpenseRadio.isChecked()) {
			tagNames = new ArrayList<String>(mExpenseTagNames);
		} else {
			tagNames = new ArrayList<String>(mIncomeTagNames);
		}
		tagAdapter.addAll(tagNames);
		tagAdapter.notifyDataSetChanged();
	}

	private void newRecord() {
		AVObject record = new AVObject(AVConstants.CLASS_RECORD);

		// Prepare data
		// 1. Is Type Expense?
		boolean typeIsExpense = mExpenseRadio.isChecked();
		// 2. The amount of money
		String amount = mAmountEdit.getText().toString();
		if (TextUtils.isEmpty(amount) || amount.equals(".")) {
			Toast.makeText(getApplicationContext(), R.string.invalid_amount_toast, Toast.LENGTH_LONG).show();
			return;
		}
		if (typeIsExpense) {
			amount = "-" + amount;
		}
		// 2. Record time
		long time = new Date().getTime();
		// 3. Record tags
		List<AVObject> tags = new ArrayList<AVObject>();
		String tagString = mTagEdit.getText().toString();
		Set<String> tagNameSet = new HashSet<String>(Arrays.asList(tagString.split("\\s*,\\s*")));
		tagNameSet.removeAll(EMPTY_TAG_SET);
		if (tagNameSet.size() == 0) {
			Toast.makeText(getApplicationContext(), R.string.empty_tag_toast, Toast.LENGTH_LONG).show();
			return;
		}
		List<AVObject> previousTags;
		if (mExpenseRadio.isChecked()) {
			previousTags = mExpenseTags;
		} else {
			previousTags = mIncomeTags;
		}
		for (AVObject tag : previousTags) {
			String tagName = tag.getString(AVConstants.KEY_TAG_NAME);
			if (tagNameSet.contains(tagName)) {
				tags.add(tag);
				tagNameSet.remove(tagName);
			}
		}
		for (String tagName : tagNameSet) {
			AVObject newTag = new AVObject(AVConstants.CLASS_TAG);
			newTag.put(AVConstants.KEY_TYPE_IS_EXPENSE, mExpenseRadio.isChecked());
			newTag.put(AVConstants.KEY_TAG_NAME, tagName);
			tags.add(newTag);
		}

		// Put data into record
		record.put(AVConstants.KEY_TYPE_IS_EXPENSE, typeIsExpense);
		record.put(AVConstants.KEY_AMOUNT, amount);
		record.put(AVConstants.KEY_TIME, time);
		record.put(AVConstants.KEY_TAGS, tags);

		setProgressBarIndeterminateVisibility(true);
		record.saveInBackground(new SaveCallback() {
			@Override
			public void done(AVException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					Toast.makeText(getApplicationContext(),
							R.string.new_record_success_toast, Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Log.w(Utils.getTag(), getString(R.string.save_record_failed_log_text), e);
					Toast.makeText(getApplicationContext(),
							R.string.new_record_failed_toast, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
