package com.kourou.fouraccessq;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.kourou.fouraccessq.R;

public class NearbyAdapter extends BaseAdapter {
	private ArrayList<FsqVenue> mVenueList;
	private LayoutInflater mInflater;

	public NearbyAdapter(Context c) {
        mInflater 			= LayoutInflater.from(c);
    }

	public void setData(ArrayList<FsqVenue> poolList) {
		mVenueList = poolList;
	}
	
	public int getCount() {
		return mVenueList.size();
	}

	public Object getItem(int position) {
		return mVenueList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView	=  mInflater.inflate(R.layout.nearby_list, null);
			holder = new ViewHolder();
			
			holder.mNameTxt 		= (TextView) convertView.findViewById(R.id.tv_name);
			holder.mAddressTxt 		= (TextView) convertView.findViewById(R.id.tv_address);
			holder.mCheckinsTxt 		= (TextView) convertView.findViewById(R.id.tv_checkins);
			holder.mDistanceTxt 	= (TextView) convertView.findViewById(R.id.tv_distance);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FsqVenue venue 	= mVenueList.get(position);

		holder.mNameTxt.setText(venue.name);
		holder.mAddressTxt.setText(venue.address);
		holder.mCheckinsTxt.setText("(" + String.valueOf(venue.checkins) + " chekins)");
		holder.mDistanceTxt.setText(formatDistance(venue.distance));

		
        return convertView;
	}
	
	private String formatDistance(double distance) {
		String result = "";
		
		DecimalFormat dF = new DecimalFormat("00");
		
		dF.applyPattern("0.#");
		
		if (distance < 1000)
			result = dF.format(distance) + " m";
		else {
			distance = distance / 1000.0;
			result   = dF.format(distance) + " km";
		}
		
		return result;
	}
	static class ViewHolder {
		TextView mNameTxt;
		TextView mAddressTxt;
		TextView mCheckinsTxt;
		TextView mDistanceTxt;
		ImageView mRibbonImg;
	}
}