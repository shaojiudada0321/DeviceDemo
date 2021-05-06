package com.ocom.devicedemo.views.demo;



import android.content.Context;

import com.ocom.ocomdevicesdk.device.keybroad.KeybroadContent;
import com.ocom.ocomdevicesdk.device.screen.BaseSerialPortView;
import com.ocom.ocomdevicesdk.device.screen.ScreenCommands;
import com.ocom.ocomdevicesdk.device.screen.ScreenPosition;


/**
 * demo功能页面
 */
public class DemoView extends BaseSerialPortView {



    private OnDemoSettingListener mListener = null;

    public void init(Context context, OnDemoSettingListener listener) {
        initDouble(context);
        mListener = listener;
    }


    public void jumpPage(){
        jumpPageByClear(
                ScreenCommands.PAGE_MENU,
                ScreenCommands.pageMenuCmd(ScreenPosition.MENU_POSITION_3000,"   1、初始化界面"),
                ScreenCommands.pageMenuCmd(ScreenPosition.MENU_POSITION_3100,"   2、待机界面"),
                ScreenCommands.pageMenuCmd(ScreenPosition.MENU_POSITION_3200,"   3、支付界面"),
                ScreenCommands.pageMenuCmd(ScreenPosition.MENU_POSITION_3300,"   4、支付结果界面"),
                ScreenCommands.pageMenuCmd(ScreenPosition.MENU_POSITION_3400,"   5、菜单界面"),
                ScreenCommands.pageMenuCmd(ScreenPosition.MENU_POSITION_3500,"   6、更改信号图标"),
                ScreenCommands.pageMenuCmd(ScreenPosition.MENU_POSITION_3600,"   7、更新时间"),
                ScreenCommands.pageMenuCmd(ScreenPosition.MENU_POSITION_3700,"   8、更新版本号"),
                ScreenCommands.pageMenuCmd(ScreenPosition.MENU_POSITION_3800,"   9、更新机器号")
        );
    }


    @Override
    public void keyPress(String s) {
        switch (s){
            case KeybroadContent.KEY_1:{//键盘 1 展示初始化界面
                mListener.showInitView();
                break;
            }
            case KeybroadContent.KEY_2:{//键盘 2 展示待机界面
                mListener.showStanbyView();
                break;
            }
            case KeybroadContent.KEY_3:{//键盘 3 展示支付界面
                mListener.showPayView();
                break;
            }
            case KeybroadContent.KEY_4:{//键盘 4 展示支付结果界面
                mListener.showPayResultView();
                break;
            }
            case KeybroadContent.KEY_5:{//键盘 5 展示菜单界面
                mListener.showMenuView();
                break;
            }
            case KeybroadContent.KEY_6:{//键盘 6 更改信号图标
                mListener.changeSignal();
                break;
            }
            case KeybroadContent.KEY_7:{//键盘 7 更新时间
                mListener.upDateTime();
                break;
            }
            case KeybroadContent.KEY_8:{//键盘 8 更新版本号
                mListener.upDateVersionCode();
                break;
            }
            case KeybroadContent.KEY_9:{//键盘 9 更新机器号
                mListener.upDateMachinNo();
                break;
            }
            case KeybroadContent.KEY_MENU :{//设置背景图片
                mListener.setPageMenuPackground();
                break;
            }
        }
    }
}
