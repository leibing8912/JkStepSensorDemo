package cn.jianke.jkstepsensordemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import cn.jianke.customcache.utils.StringUtil;
import cn.jianke.jkstepsensor.common.data.bean.StepModel;

/**
 * @className: HistoryAdapter
 * @classDescription: 历史列表适配器
 * @author: leibing
 * @createTime: 2016/09/04
 */
public class HistoryAdapter extends BaseAdapter{
    // 数据源
    private ArrayList<StepModel> mData;
    // 布局
    private LayoutInflater mLayoutInflater;

    /**
     * 构造函数
     * @author leibing
     * @createTime 2016/09/04
     * @lastModify 2016/09/04
     * @param context 上下文
     * @param mData 数据源
     * @return
     */
    public HistoryAdapter(Context context, ArrayList<StepModel> mData){
        mLayoutInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData !=null?mData.size():0;
    }

    @Override
    public Object getItem(int i) {
        return mData !=null?mData.get(i):null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * 设置数据源
     * @author leibing
     * @createTime 2016/09/04
     * @lastModify 2016/09/04
     * @param mData 数据源
     * @return
     */
    public void setData(ArrayList<StepModel> mData){
        this.mData = mData;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = mLayoutInflater.inflate(R.layout.adapter_history_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (i < mData.size())
            viewHolder.updateUI(mData.get(i));

        return view;
    }

    static class ViewHolder{
        // 日期、步数
        private TextView mDateTv,mStepTv;

        public ViewHolder(View view){
            mDateTv = (TextView) view.findViewById(R.id.tv_date);
            mStepTv = (TextView) view.findViewById(R.id.tv_step);
        }

        public void updateUI(StepModel stepModel){
            if (stepModel != null){
                if (StringUtil.isNotEmpty(stepModel.getDate()))
                    mDateTv.setText("日期: " + stepModel.getDate());
                if (StringUtil.isNotEmpty(stepModel.getStep()))
                    mStepTv.setText("步数: " + stepModel.getStep());
            }
        }
    }
}
