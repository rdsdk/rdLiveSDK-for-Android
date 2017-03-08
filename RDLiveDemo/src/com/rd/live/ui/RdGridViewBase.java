package com.rd.live.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.rd.flash.FlashAdapter;
import com.rd.flash.FlashItem;
import com.rd.flash.FlashViewHolder;

/**
 * 自定义人脸面具的显示layout
 *
 * @author JIAN
 */
public class RdGridViewBase extends LinearLayout {

    private ArrayList<FlashItem> mdata = new ArrayList<FlashItem>();
    private int pageItemCount = 10;// 每页10条数据
    private Context mContext;

    private final int MNUMCOLUMNS = 5;// 每行显示4列数据
    private final int LINECOUNT = 2;// 只显示两行

    public RdGridViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
        pageItemCount = MNUMCOLUMNS * LINECOUNT;

    }

    private String TAG = "RDGridViewBase";
    public static final String FLASH_NONE = "无";//关闭人脸

    /**
     * 初始化listview数据
     *
     * @param _tempList
     */
    public void initData(ArrayList<FlashItem> _tempList) {

        FlashItem none = new FlashItem(FLASH_NONE);
        int len = _tempList.size();
        mdata.clear();
        mdata.add(none);
        for (int i = 0; i < len; i++) {
            mdata.add(_tempList.get(i));
        }
        adapters = new ArrayList<FlashAdapter>();
        this.addView(getView(mContext));

    }

    private ArrayList<FlashAdapter> adapters = null;

    private RdGiftPage getView(Context context) {

        RdGiftPage mgridview = new RdGiftPage(context);

        mgridview.setNumColumns(MNUMCOLUMNS);
        mgridview.setCacheColorHint(0);
        mgridview.setVerticalSpacing(5);
        mgridview.setHorizontalSpacing(5);

        FlashAdapter mgiftadapter = new FlashAdapter(context);

        mgridview.setAdapter(mgiftadapter);
        adapters.add(mgiftadapter);
        RdItemClickListener mItemClickListener = new RdItemClickListener(
                mgiftadapter);
        mgridview.setOnItemClickListener(mItemClickListener);
        mgiftadapter.update(mdata, true);

        return mgridview;
    }

    private class RdItemClickListener implements OnItemClickListener {

        private FlashAdapter adpter;

        public RdItemClickListener(FlashAdapter _adpter) {
            this.adpter = _adpter;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            FlashViewHolder vh = (FlashViewHolder) view.getTag();

            if (null != vh) {
                FlashAdapter.setChecked(vh);
            }

            if (null != iOnItemclickListener) {
                iOnItemclickListener.onItemClick(adpter.getItem(position));
            }

        }
    }

    ;

    public interface IOnItemClickListener {
        public void onItemClick(FlashItem info);
    }

    private IOnItemClickListener iOnItemclickListener;

    public void setIOnItemClicklistener(IOnItemClickListener listener) {
        iOnItemclickListener = listener;
    }

}
