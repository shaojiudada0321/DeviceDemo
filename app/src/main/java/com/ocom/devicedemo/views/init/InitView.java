package com.ocom.devicedemo.views.init;

import android.content.Context;

import com.ocom.devicedemo.views.OnShowViewBackListener;
import com.ocom.ocomdevicesdk.device.keybroad.KeybroadContent;
import com.ocom.ocomdevicesdk.device.screen.BaseSerialPortView;
import com.ocom.ocomdevicesdk.device.screen.ScreenCommands;


/**
 * 初始化界面
 */
public class InitView extends BaseSerialPortView {


    private OnShowViewBackListener mBackListener = null;

    public void init(Context context,OnShowViewBackListener backListener) {
        initDouble(context);
        mBackListener = backListener;
    }

    /**
     * 跳转到初始化界面jumpPageByClear
     */
    public void jumpPage(){
        jumpPage(ScreenCommands.PAGE_INITING);
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
