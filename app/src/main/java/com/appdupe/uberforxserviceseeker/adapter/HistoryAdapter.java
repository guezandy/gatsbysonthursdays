package com.appdupe.uberforxserviceseeker.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.appdupe.uberforxserviceseeker.HomeActivity;
import com.appdupe.uberforxserviceseeker.R;
import com.appdupe.uberforxserviceseeker.fragment.HistoryDetailFragment;
import com.appdupe.uberforxserviceseeker.model.ClientHistory;

/**
 * @author hardik a bhalodi
 * 
 */
public class HistoryAdapter extends BaseAdapter {
	private ArrayList<ClientHistory> listHistory;
	private ViewHolder holder;
	private HomeActivity activity;

	public HistoryAdapter(HomeActivity activity,
			ArrayList<ClientHistory> listHistory) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.listHistory = listHistory;
	}

	@Override
	public int getCount() {
		return listHistory.size();
	}

	@Override
	public Object getItem(int position) {
		return listHistory.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater infalter = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalter.inflate(R.layout.history_list_layout,
					parent, false);

			holder = new ViewHolder();
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.tvTripId = (TextView) convertView
					.findViewById(R.id.tvTripId);
			holder.btnShowDetail = (Button) convertView
					.findViewById(R.id.btnHistoryShowDetail);
			holder.btnShowDetail.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					HistoryDetailFragment accFrag = (HistoryDetailFragment) activity
							.getSupportFragmentManager().findFragmentByTag(
									"HISTORY_DETAIL_FRAGMENT");
					if (accFrag != null && accFrag.isVisible()) {
						return;
					}
					FragmentTransaction transaction = activity
							.getSupportFragmentManager().beginTransaction();
					transaction.addToBackStack(null);

					HistoryDetailFragment fragment = HistoryDetailFragment
							.newInstance();
					Bundle bundleHistory = new Bundle();
					bundleHistory.putSerializable("history",
							listHistory.get(position));
					fragment.setArguments(bundleHistory);
					transaction.replace(R.id.home_container, fragment,
							"HISTORY_DETAIL_FRAGMENT").commit();

				}
			});
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvTripId.setText(listHistory.get(position).getRandom_id());
		holder.tvTime.setText(listHistory.get(position).getDate().split(" ")[0]
				.toString()
				+ "\n"
				+ formatTime(listHistory.get(position).getTime_of_pickup()));
		return convertView;

	}

	private class ViewHolder {

		TextView tvTripId, tvTime;
		Button btnShowDetail;

	}

	private String formatTime(String millisecond) {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.parseLong(millisecond));
		return sdf.format(cal.getTime());

	}

}
