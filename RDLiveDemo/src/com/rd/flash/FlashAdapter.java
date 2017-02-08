package com.rd.flash;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rdlivedemo.R;
import com.rd.cache.GalleryImageFetcher;
import com.rd.cache.ImageCache.ImageCacheParams;
import com.rd.cache.ImageResizer;

public class FlashAdapter extends BaseAdapter {

	private Context mContext;
	private List<FlashItem> mlist;
	private LayoutInflater inflater;
	private ImageResizer mIResizer;

	private  int WIDTH=50,HEIGHT=50;
	public FlashAdapter(Context context) {
		mContext = context;
		this.mlist = new ArrayList<FlashItem>();
		inflater = LayoutInflater.from(context);
		ImageCacheParams cacheParams = new ImageCacheParams(mContext,
				"flash_icon");
		cacheParams.setFormat(CompressFormat.JPEG);
		cacheParams.setMemCacheSizePercent(0.1f);
		mIResizer = new GalleryImageFetcher(mContext,
				WIDTH, HEIGHT);
		mIResizer.setImageFadeIn(false);
		mIResizer.addImageCache(mContext, cacheParams);
	}

	public void update(List<FlashItem> msList, boolean notifi) {
		mlist = msList;
		this.notifyDataSetChanged();
	}

	/**
	 * Actvity生命周期
	 */
	void onPause() {
		mIResizer.setExitTasksEarly(true);
		mIResizer.flushCache();
	}

	void onResume() {
		mIResizer.setExitTasksEarly(false);
	}

	void onDestroy() {
		mlist.clear();
		mIResizer.cleanUpCache();
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public FlashItem getItem(int arg0) {
		if (arg0 >= getCount()) {
			return null;
		}
		return mlist.get(arg0);

	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	private static FlashViewHolder edHolder;

	public static void setChecked(FlashViewHolder _edHolder) {

		if (null != edHolder) {
			edHolder.ged.setVisibility(View.GONE);

		}
		_edHolder.ged.setVisibility(View.VISIBLE);
		edHolder = _edHolder;

	}

	static void clearChecked() {
		if (null != edHolder) {
			edHolder.ged.setVisibility(View.GONE);
			edHolder = null;
		}
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		FlashViewHolder vh = null;

		if (null == convertView) {
			convertView = inflater.inflate(R.layout.flash_item_layout, null);

			vh = new FlashViewHolder();
			vh.none = (TextView) convertView.findViewById(R.id.flash_none);
			vh.icon = (ImageView) convertView.findViewById(R.id.flash_src);

			vh.ged = (ImageView) convertView.findViewById(R.id.flash_src_ed);
			convertView.setTag(vh);
		} else {
			vh = (FlashViewHolder) convertView.getTag();
		}

		if (0 == arg0) {
			vh.none.setVisibility(View.VISIBLE);
			vh.icon.setVisibility(View.GONE);
		} else {
			vh.none.setVisibility(View.GONE);
			vh.icon.setVisibility(View.VISIBLE);
			mIResizer.loadImage(getItem(arg0).getBmpPath(), vh.icon);
		}

		return convertView;
	}

}
