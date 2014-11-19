package com.appdupe.uberforxserviceseeker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appdupe.uberforxserviceseeker.HomeActivity;
import com.appdupe.uberforxserviceseeker.R;
import com.appdupe.uberforxserviceseeker.helper.Helper;
import com.appdupe.uberforxserviceseeker.helper.PreferenceHelper;
import com.appdupe.uberforxserviceseeker.model.ClientRequest;

public class FeedbackFragment extends Fragment implements OnClickListener {

	public static FeedbackFragment newInstance(ClientRequest clientRequest) {
		FeedbackFragment feedbackFragment = new FeedbackFragment();

		Bundle extras = new Bundle();
		extras.putParcelable(Helper.EXTRA_CLIENT_REQUEST, clientRequest);
		feedbackFragment.setArguments(extras);

		return feedbackFragment;
	}

	private ClientRequest getClientRequest() {
		return getArguments().getParcelable(Helper.EXTRA_CLIENT_REQUEST);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_thank_you, container,
				false);

		TextView tv_RefNO = (TextView) view.findViewById(R.id.tv_thanks_refno);
		tv_RefNO.setText(getClientRequest().getRandom_id());

		view.findViewById(R.id.btn_feedback).setOnClickListener(this);

		new PreferenceHelper(getActivity()).putClientRequestActive(false);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((HomeActivity) getActivity())
				.setMainBackground(R.drawable.bg_feedback);
		((HomeActivity) getActivity()).selectLocateMe();
		((HomeActivity) getActivity()).setTitle(getResources().getString(
				R.string.text_thankyou));
		((HomeActivity) getActivity())
				.setHeaderIcon(R.drawable.header_feedback_icon);
	}

	private void removeThisFragment() {
		try {
			getActivity().getSupportFragmentManager().beginTransaction()
					.remove(this).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_feedback) {
			removeThisFragment();

			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.home_container,
							RateFragment.newInstance(getClientRequest()),
							"RATE_FRAGMENT").commit();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		removeThisFragment();
	}
}
