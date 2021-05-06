package com.ocom.devicedemo.views.payresult;

import android.content.Context;

import com.ocom.devicedemo.views.OnShowViewBackListener;
import com.ocom.ocomdevicesdk.device.keybroad.KeybroadContent;
import com.ocom.ocomdevicesdk.device.screen.BaseSerialPortView;
import com.ocom.ocomdevicesdk.device.screen.ScreenCommands;
import com.ocom.ocomdevicesdk.device.screen.ScreenPosition;


public class PayResultView extends BaseSerialPortView {


    private OnShowViewBackListener mBackListener = null;


    public void init(Context context, OnShowViewBackListener backListener) {
        initDouble(context);
        mBackListener = backListener;
    }

    /**
     * 跳转至该界面
     */
    public void jumpPage(){
        jumpPageByClear(
                ScreenCommands.PAGE_PAY_RESULT,
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_RESULT_POSITION_5200,"5200内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_RESULT_POSITION_5300,"5300内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_RESULT_POSITION_5400,"5400"),//注意！5400只能显示数字
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_RESULT_POSITION_6000,"6000内容展示"),
                ScreenCommands.pagePayResultImageCmd(ScreenPosition.PAY_RESULT_IMAGE_SUCCESS)//支付成功的图片 可以更改为 成功 PAY_RESULT_IMAGE_SUCCESS
                                                                                                                 // 超时 PAY_RESULT_IMAGE_TIMEOUT
                                                                                                                 // 失败 PAY_RESULT_IMAGE_FAIL
        );
    }




    @Override
    public void keyPress(String s) {
        switch (s) {
            case KeybroadContent.KEY_CLEAR: {//清除
                mBackListener.onBack();
                break;
            }
        }
    }
}
