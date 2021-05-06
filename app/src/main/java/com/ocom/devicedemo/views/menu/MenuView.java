package com.ocom.devicedemo.views.menu;

import android.content.Context;

import com.ocom.devicedemo.views.OnShowViewBackListener;
import com.ocom.ocomdevicesdk.device.keybroad.KeybroadContent;
import com.ocom.ocomdevicesdk.device.screen.BaseSerialPortView;
import com.ocom.ocomdevicesdk.device.screen.ScreenCommands;
import com.ocom.ocomdevicesdk.device.screen.ScreenPosition;

/**
 * 菜单界面展示 一般用来显示
 */
public class MenuView extends BaseSerialPortView {

    private OnShowViewBackListener mBackListener = null;


    public void init(Context context,OnShowViewBackListener backListener) {
        mBackListener = backListener;
        initDouble(context);
    }


    /**
     * 跳转至菜单界面
     */
    public void jumpPage() {
        jumpPageByClear(
                ScreenCommands.PAGE_MENU,//跳转的菜单界面
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3000,"3000内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3100,"3100内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3200,"3200内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3300,"3300内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3400,"3400内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3500,"3500内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3600,"3600内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3700,"3700内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3800,"3800内容展示")
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
