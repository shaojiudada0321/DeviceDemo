package com.ocom.devicedemo.views.pay;


import android.content.Context;

import com.ocom.devicedemo.views.OnShowViewBackListener;
import com.ocom.ocomdevicesdk.device.keybroad.KeybroadContent;
import com.ocom.ocomdevicesdk.device.screen.BaseSerialPortView;
import com.ocom.ocomdevicesdk.device.screen.ScreenCommands;
import com.ocom.ocomdevicesdk.device.screen.ScreenPosition;



/**
 * 支付界面展示
 */
public class PayView extends BaseSerialPortView {

    private OnShowViewBackListener mBackListener = null;



    public void init(Context context, OnShowViewBackListener backListener) {
        mBackListener = backListener;
        initDouble(context);
    }


    /**
     * 跳转至该界面
     */
    public void jumpPage() {
        jumpPageByClear(
                ScreenCommands.PAGE_PAY,//跳转的支付界面
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_POSITION_A100,"A100内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_POSITION_A200,"A200内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_POSITION_A300,"A300"),//注意！A300只能显示数字
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_POSITION_A400,"A400内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_POSITION_A500,"A500内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_POSITION_A600,"A600内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_POSITION_A700,"A700内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.PAY_POSITION_A800,"A800内容展示")
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
