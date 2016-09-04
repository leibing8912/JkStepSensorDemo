package cn.jianke.jkstepsensordemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;
import cn.jianke.jkstepsensor.common.data.DataCache;
import cn.jianke.jkstepsensor.common.data.bean.StepModel;

/**
 * @className: HistoryActivity
 * @classDescription: 历史列表
 * @author: leibing
 * @createTime: 2016/09/04
 */
public class HistoryActivity extends AppCompatActivity {
    // 历史列表
    private ListView mHistoryStepLv;
    // 数据源
    private ArrayList<StepModel> mData;
    // 适配器
    private HistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // findview
        mHistoryStepLv = (ListView) findViewById(R.id.lv_history_step);
        // 初始化数据源
        mData = new ArrayList<>();
        // 初始化适配器
        mAdapter = new HistoryAdapter(this, mData);
        // 绑定适配器
        mHistoryStepLv.setAdapter(mAdapter);
        // 读取缓存
        DataCache.getInstance().getAllCache(this, new DataCache.DataCacheListener() {
            @Override
            public void readListCache(StepModel stepModel) {

            }

            @Override
            public void readAllCache(ArrayList<StepModel> mData) {
                mAdapter.setData(mData);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
