package com.yysp.ecandroid.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.accessibility.AccessibilityManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jkframework.control.JKToast;
import com.yysp.ecandroid.R;
import com.yysp.ecandroid.config.ECConfig;
import com.yysp.ecandroid.data.bean.DisBean;
import com.yysp.ecandroid.data.response.ECTaskResultResponse;
import com.yysp.ecandroid.net.ECNetSend;
import com.yysp.ecandroid.util.OthoerUtil;
import com.yysp.ecandroid.view.ECBaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.yysp.ecandroid.config.ECConfig.AliasName;

/**
 * Created by Administrator on 2017/4/15.
 */
@EActivity(R.layout.ecandroid_taskacticity)
public class ECTaskActivity extends ECBaseActivity {
    /**
     * 页面初始化
     */
    private boolean bInit = false;
    public static final String LauncherUI = "com.tencent.mm.ui.LauncherUI";
    public static final String MM = "com.tencent.mm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            bInit = savedInstanceState.getBoolean("Init", false);
        }
//        JKToast.Show("预发布:" + ECConfig.AliasName, 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
        outState.putBoolean("Init", bInit);
    }

    @AfterViews
    void InitData() {
        if (!bInit) {
            bInit = true;
        }
        ECConfig.OpenScreenOrder(this);
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (!accessibilityManager.isEnabled()) {
            OthoerUtil.doOfTaskEnd();
            OthoerUtil.AddErrorMsgUtil(AliasName + "   辅助未打开");
            new MaterialDialog.Builder(this).content("请打开空容器辅助功能!").positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    JKToast.Show("找到空容器APK辅助功能，然后开启服务即可", 0);
                    dialog.dismiss();
                }
            }).show();

        }
    }

    private void doOfTaskEnd(ECTaskResultResponse resultResponse) {


        ECNetSend.taskStatus(resultResponse, this).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<DisBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DisBean disBean) {
//                PerformClickUtils.performHome(ECTaskActivity.this);//任务完成进入home
                OthoerUtil.doOfTaskEnd();
            }


            @Override
            public void onError(Throwable e) {
                OthoerUtil.doOfTaskEnd();
                OthoerUtil.AddErrorMsgUtil("taskStatus" + e.getMessage());
            }

            @Override
            public void onComplete() {
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
