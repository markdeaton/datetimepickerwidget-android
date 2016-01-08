/**
 * Copyright 2010 Lukasz Szmit <devmail@szmit.eu>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

/* MED, 1/5/2014
 * Changes made:
 * 1. Updated layout xml to hide calendarView from datePicker
 * 2. Add ability to set, initialize time in ms (epoch)
 * 
 */
package com.esri.apl.datetimepickerwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ViewSwitcher;

import java.util.Calendar;

public class DateTimePicker
        extends RelativeLayout
        implements View.OnClickListener, OnDateChangedListener, OnTimeChangedListener {
	private static final String TAG = "DateTimePicker";
	// DatePicker reference
	private DatePicker		datePicker;
	// TimePicker reference
	private TimePicker		timePicker;
	// ViewSwitcher reference
	private ViewSwitcher	viewSwitcher;
	// Calendar reference
	private Calendar		mCalendar;
	// Min/max dates allowed; no null for longs, so use Long.MIN_VALUE instead
	private long			minDate = Long.MIN_VALUE, maxDate = Long.MIN_VALUE;

	// Constructor start
	public DateTimePicker(Context context) {
		this(context, null);
	}

	public DateTimePicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DateTimePicker(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs, defStyle, Calendar.getInstance().getTimeInMillis());
	}
	
	public DateTimePicker(Context context, AttributeSet attrs, int defStyle, long dateTimeMillis) {
		super(context, attrs, defStyle);

		// Get LayoutInflater instance
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inflate myself
		inflater.inflate(R.layout.datetimepicker, this, true);

		// Inflate the date and time picker views
//		Log.d(TAG, "Inflate date and time pickers - DateTimePicker constructor");
		LinearLayout datePickerView = (LinearLayout) inflater.inflate(R.layout.datepicker, null);
		datePicker = (DatePicker) datePickerView.findViewById(R.id.DatePicker);
		LinearLayout timePickerView = (LinearLayout) inflater.inflate(R.layout.timepicker, null);
		timePicker = (TimePicker) timePickerView.findViewById(R.id.TimePicker);

		// Grab a Calendar instance
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(dateTimeMillis);
		
		// Init date picker
		datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);

		// Init time picker
		timePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
		timePicker.setOnTimeChangedListener(this);

		// Grab the ViewSwitcher so we can attach our picker views to it
		viewSwitcher = (ViewSwitcher) this.findViewById(R.id.DateTimePickerVS);

		// Handle button clicks
		((Button) findViewById(R.id.SwitchToTime)).setOnClickListener(this); // shows the time picker
		((Button) findViewById(R.id.SwitchToDate)).setOnClickListener(this); // shows the date picker

		// Populate ViewSwitcher
		viewSwitcher.addView(datePickerView, 0);
		viewSwitcher.addView(timePickerView, 1);
	}
	// Constructor end

	// Called every time the user changes DatePicker values
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		// Update the internal Calendar instance
		mCalendar.set(year, monthOfYear, dayOfMonth, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
	}

	// Called every time the user changes TimePicker values
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		// Update the internal Calendar instance
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
	}

	// Handle button clicks
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.SwitchToDate) {
			v.setEnabled(false);
            viewSwitcher.setOutAnimation(this.getContext(), android.R.anim.slide_out_right);
            viewSwitcher.setInAnimation(this.getContext(), android.R.anim.slide_in_left);
			findViewById(R.id.SwitchToTime).setEnabled(true);
			viewSwitcher.showPrevious();
		} else if (id == R.id.SwitchToTime) {
			v.setEnabled(false);
            viewSwitcher.setOutAnimation(this.getContext(), R.anim.slide_out_left);
            viewSwitcher.setInAnimation(this.getContext(), R.anim.slide_in_right);
			findViewById(R.id.SwitchToDate).setEnabled(true);
			viewSwitcher.showNext();
		}
	}

	// Convenience wrapper for internal Calendar instance
	public int get(final int field) {
		return mCalendar.get(field);
	}

	// Reset DatePicker, TimePicker and internal Calendar instance
	public void reset() {
/*		final Calendar c = Calendar.getInstance();
		updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		updateTime(c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));*/
		setDateTimeMillis(Calendar.getInstance().getTimeInMillis());
	}

	public DatePicker getDatePicker() {
		return datePicker;
	}

	public TimePicker getTimePicker() {
		return timePicker;
	}

	// Convenience wrapper for internal Calendar instance
	public long getDateTimeMillis() {
		return mCalendar.getTimeInMillis();
	}
	public void setDateTimeMillis(long dateTimeMillis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateTimeMillis);
		updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		updateTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
	}

	// Convenience wrapper for internal TimePicker instance
	public void setIs24HourView(boolean is24HourView) {
		timePicker.setIs24HourView(is24HourView);
	}
	
	// Convenience wrapper for internal TimePicker instance
	public boolean is24HourView() {
		return timePicker.is24HourView();
	}

	/** If there's a minimum bound on the choosable date, this will return it; or Long.MIN_VALUE if no bound set **/
	public long getMinDate() {
		return minDate;
	}

	public void setMinDate(long minDate) {
		this.minDate = minDate;
		datePicker.setMinDate(minDate);
	}
	/** If there's a maximum bound on the choosable date, this will return it; or Long.MIN_VALUE if no bound set **/
	public long getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(long maxDate) {
		this.maxDate = maxDate;
		datePicker.setMaxDate(maxDate);
	}

	// Convenience wrapper for internal DatePicker instance
	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		datePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	// Convenience wrapper for internal TimePicker instance
	public void updateTime(int currentHour, int currentMinute) {
		timePicker.setCurrentHour(currentHour);
		timePicker.setCurrentMinute(currentMinute);
	}
}