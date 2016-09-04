package cn.jianke.jkstepsensordemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.jianke.jkstepsensor.common.Constant;
import cn.jianke.jkstepsensor.module.service.StepService;

/**
 * @className: MainActivity
 * @classDescription: 计步首页
 * @author: leibing
 * @createTime: 2016/09/02
 */
public class MainActivity extends AppCompatActivity implements Handler.Callback,View.OnClickListener{
    // 显示计步
    private TextView mStepCountTv,mContentTipTv,mStepTipTv;
    // 开始停止计步按钮
    private Button mStartStepBtn, mStopStepBtn;
    // 用于与计步服务通信
    private Messenger messenger;
    private Messenger replyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler = new Handler(this);
    // 循环取当前时刻步数的间隔时间
    private long TIME_INTERVAL = 1000;
    // 确定是否退出app
    private int i = 0;
    // 服务连接
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                // 向服务端发送消息(Messenger通信)
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                msg.replyTo = replyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constant.MSG_FROM_SERVER:
                // 收到从服务端发来的计步数
                int stepCount = message.getData().getInt(StepService.STEP_KEY);
                // 更新界面上的步数
                mStepTipTv.setVisibility(View.VISIBLE);
                mContentTipTv.setVisibility(View.VISIBLE);
                mStepCountTv.setTextColor(getResources().getColor(cn.jianke.jkstepsensor.R.color.colorPrimary));
                mStepCountTv.setText(stepCount + "");
                // 循环向服务端请求数据
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case Constant.REQUEST_SERVER:
                try {
                    // 向服务端发送消息(Messenger通信)
                    Message serverMsg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                    serverMsg.replyTo = replyMessenger;
                    // bundle添加Notification配置信息（包括内容、标题、是否不可取消等）
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.CONTENT_KEY, "今日行走");
                    bundle.putSerializable(Constant.TICKER_KEY, "健客计步");
                    bundle.putSerializable(Constant.CONTENTTITLE_KEY, "健客计步");
                    bundle.putSerializable(Constant.PENDINGCLASS_KEY, MainActivity.class);
                    bundle.putSerializable(Constant.ISONGOING_KEY, true);
                    bundle.putSerializable(Constant.ICON_KEY, cn.jianke.jkstepsensor.R.mipmap.icon);
                    bundle.putSerializable(Constant.NOTIFYID_KEY, cn.jianke.jkstepsensor.R.string.app_name);
                    serverMsg.setData(bundle);
                    messenger.send(serverMsg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // findView
        mStepCountTv = (TextView) findViewById(R.id.tv_step_count);
        mContentTipTv = (TextView) findViewById(R.id.tv_content_tip);
        mStepTipTv = (TextView) findViewById(R.id.tv_step_tip);
        mStartStepBtn = (Button) findViewById(R.id.btn_start_step);
        mStopStepBtn = (Button) findViewById(R.id.btn_stop_step);
        // onClick
        findViewById(R.id.btn_start_step).setOnClickListener(this);
        findViewById(R.id.btn_stop_step).setOnClickListener(this);
        findViewById(R.id.btn_history_step).setOnClickListener(this);
        // 开始计步
        startService();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start_step:
                // 开始计步
                startService();
                break;
            case R.id.btn_stop_step:
                // 停止计步
                stopService();
                break;
            case R.id.btn_history_step:
                gotoHistory();
                break;
            default:
                break;
        }
    }

    /**
     * 启动服务
     * @author leibing
     * @createTime 2016/08/31
     * @lastModify 2016/08/31
     * @param
     * @return
     */
    private void gotoHistory() {
        Intent intent = new Intent();
        intent.setClass(this, HistoryActivity.class);
        startActivity(intent);
    }

    /**
     * 启动服务
     * @author leibing
     * @createTime 2016/08/31
     * @lastModify 2016/08/31
     * @param
     * @return
     */
    private void startService() {
        // 设置开始停止计步按钮是否可用
        mStopStepBtn.setEnabled(true);
        mStartStepBtn.setEnabled(false);
        final Intent intent = new Intent(this, StepService.class);
        // 绑定服务
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        // 启动服务
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        // 解绑服务
        if (conn != null)
            unbindService(conn);
        super.onDestroy();
    }

    /**
     * 停止服务
     * @author leibing
     * @createTime 2016/08/31
     * @lastModify 2016/08/31
     * @param
     * @return
     */
    private void stopService(){
        // 设置开始停止计步按钮是否可用
        mStopStepBtn.setEnabled(false);
        mStartStepBtn.setEnabled(true);
        if (conn != null) {
            // 解绑服务
            unbindService(conn);
        }
        // 发送停止计步服务广播(用于手动停止当前服务)
        Intent intent = new Intent();
        intent.setAction(StepService.ACTION_STOP_SERVICE);
        sendBroadcast(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        // 再按一次退出app
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            if(i==0){
                i++;
                String quit = getApplication().getString(R.string.comm_quit);
                Toast.makeText(MainActivity.this, quit, Toast.LENGTH_SHORT).show();
            }else if(i==1){
                finish();
            }
        }
        return true;
    }
}
