package com.ocom.devicedemo.views.stanby;
import android.content.Context;

import com.ocom.devicedemo.views.OnShowViewBackListener;
import com.ocom.ocomdevicesdk.device.keybroad.KeybroadContent;
import com.ocom.ocomdevicesdk.device.screen.BaseSerialPortView;
import com.ocom.ocomdevicesdk.device.screen.ScreenCommands;
import com.ocom.ocomdevicesdk.device.screen.ScreenPosition;


/**
 * 待机界面  PAGE_STANDBY
 */
public class StanbyView extends BaseSerialPortView {


    private OnShowViewBackListener mBackListener = null;


    public void init(Context context, OnShowViewBackListener backListener) {
        mBackListener = backListener;
        initDouble(context);
    }

    /**
     * 跳转至该界面
     */
    public void jumpPage(){
        jumpPageByClear(
                ScreenCommands.PAGE_STANDBY,
                ScreenCommands.pagePayCmd(ScreenPosition.STANBY_POSITION_1100,"1100内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.STANBY_POSITION_1200,"1200内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.STANBY_POSITION_1300,"1300内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.STANBY_POSITION_1400,"1400"),//注意！1400只能显示数字
                ScreenCommands.pagePayCmd(ScreenPosition.STANBY_POSITION_1500,"1500内容展示"),//注意！台式机无法显示此位置内容
                ScreenCommands.pagePayCmd(ScreenPosition.STANBY_POSITION_1600,"1600内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.STANBY_POSITION_1700,"1700内容展示"),
                ScreenCommands.pagePayCmd(ScreenPosition.STANBY_POSITION_1800,"1800内容展示")
        );
    }

    private void clear(byte[] position){
        sendDeleteData(ScreenCommands.PAGE_STANDBY,position);
    }

    @Override
    public void keyPress(String s) {
        switch (s) {
            case KeybroadContent.KEY_1 :{
                clear(ScreenPosition.STANBY_POSITION_1100);
                break;
            }

            case KeybroadContent.KEY_2 :{
                clear(ScreenPosition.STANBY_POSITION_1200);
                break;
            }
            case KeybroadContent.KEY_3 :{
                clear(ScreenPosition.STANBY_POSITION_1300);
                break;
            }
            case KeybroadContent.KEY_4 :{
                clear(ScreenPosition.STANBY_POSITION_1400);
                break;
            }
            case KeybroadContent.KEY_5 :{
                clear(ScreenPosition.STANBY_POSITION_1500);
                break;
            }
            case KeybroadContent.KEY_6 :{
                clear(ScreenPosition.STANBY_POSITION_1600);
                break;
            }
            case KeybroadContent.KEY_7 :{
                clear(ScreenPosition.STANBY_POSITION_1700);
                break;
            }

            case KeybroadContent.KEY_8 :{
                clear(ScreenPosition.STANBY_POSITION_1800);
                break;
            }

            case KeybroadContent.KEY_CLEAR: {//清除
                mBackListener.onBack();
                break;
            }
        }

    }




}
