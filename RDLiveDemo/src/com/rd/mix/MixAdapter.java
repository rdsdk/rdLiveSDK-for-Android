package com.rd.mix;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rdlivedemo.R;

/**
 * Created by JIAN on 2017/1/19.
 */

public class MixAdapter extends BaseAdapter {
	ArrayList<MixInfo> list;
	private LayoutInflater inflater;
	private int color_n, color_ed;

	@SuppressLint("NewApi")
	public MixAdapter(Context mContext) {
		inflater = LayoutInflater.from(mContext);
		list = new ArrayList<MixInfo>();
		Resources res = mContext.getResources();
		color_n = res.getColor(R.color.white);
		color_ed = res.getColor(R.color.main_color);
	}

	public void update(ArrayList<MixInfo> mixs) {
		list.clear();
		list.addAll(mixs);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public MixInfo getItem(int position) {
		return list.get(position);
	}

	private int index = -1;

	public void checked(int position) {
		index = position;
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder vh;
		if (null == convertView) {
			vh = new ViewHolder();
			convertView = inflater.inflate(R.layout.mix_item_layout, null);
			vh.tvDuration = (TextView) convertView
					.findViewById(R.id.mix_duration);
			vh.tvName = (TextView) convertView.findViewById(R.id.mix_name);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();

		}
		MixInfo info = getItem(position);
		if (null != info) {
			vh.tvDuration.setText(info.getDuration());
			vh.tvName.setText(info.getName());
		}
		if (index == position && index != (getCount() - 1)) {
			vh.tvDuration.setTextColor(color_ed);
			vh.tvName.setTextColor(color_ed);
		} else {
			vh.tvDuration.setTextColor(color_n);
			vh.tvName.setTextColor(color_n);
		}

		return convertView;
	}

	class ViewHolder {
		TextView tvDuration, tvName;
	}

}
