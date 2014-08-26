package senwang.monthlycost;

import android.content.Context;
import android.database.Cursor;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;

import com.avos.avoscloud.AVObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hitbe_000 on 8/20/2014.
 */
public class ComboBox extends LinearLayout {

	private AutoCompleteTextView mText;
	private ImageButton mButton;

	public ComboBox(Context context) {
		super(context);
		createChildControls(context);
	}

	public ComboBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		createChildControls(context);
	}

	private void createChildControls(Context context) {
		setOrientation(HORIZONTAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		mText = new AutoCompleteTextView(context);
		mText.setSingleLine();
		mText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
		mText.setRawInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		addView(mText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));

		mButton = new ImageButton(context);
		mButton.setImageResource(android.R.drawable.arrow_down_float);
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mText.showDropDown();
			}
		});
		addView(mButton, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}

	/**
	 * Sets the source for DDLB suggestions.
	 * Cursor MUST be managed by supplier!!
	 *
	 * @param source Source of suggestions.
	 * @param column Which column from source to show.
	 */
	public void setSuggestionSource(Cursor source, String column) {
		String[] from = new String[]{column};
		int[] to = new int[] {android.R.id.text1};
		SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getContext(),
				android.R.layout.simple_dropdown_item_1line, source, from, to);
		cursorAdapter.setStringConversionColumn(source.getColumnIndex(column));
		mText.setAdapter(cursorAdapter);
	}

	/**
	 * Gets the text in the combo box.
	 *
	 * @return Text.
	 */
	public String getText() {
		return mText.getText().toString();
	}

	/**
	 * Sets the text in combo box.
	 */
	public void setText(String text) {
		mText.setText(text);
	}

	public void setSuggestionSource(List<AVObject> tags) {
		List<String> tagNames = new ArrayList<String>();
		for (AVObject tag : tags) {
			tagNames.add(tag.getString(AVConstants.KEY_TAG_NAME));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_dropdown_item_1line, tagNames);
		mText.setAdapter(adapter);
	}

	public void setHint(int hintResId) {
		mText.setHint(hintResId);
	}
}
