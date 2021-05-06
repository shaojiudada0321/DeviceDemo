package com.ocom.devicedemo.views.common;

import android.content.Context;

import com.ocom.devicedemo.views.OnShowViewBackListener;
import com.ocom.ocomdevicesdk.device.screen.BaseSerialPortView;
import com.ocom.ocomdevicesdk.device.screen.ScreenCommands;

/**
 * 用于显示时间 版本号 信号等 独立于展示屏之外 发送不影响界面展示
 */
public class CommonView extends BaseSerialPortView {
    private OnShowViewBackListener mBackListener = null;
    private boolean isConnect = true;//模拟连接状态

    private int versionCode = 10086;//模拟版本号

    private int machinNo = 200; //模拟机器号

    private int networkTypeFlag = 0;//网络类型



    public void init(Context context){
        initDouble(context);
    }

    /**
     * 更新机器版本号
     */
    public void upDateMachinNo(){
        sendCommand(ScreenCommands.machinNoCmd(String.valueOf(machinNo)));
        machinNo++;
    }

    /**
     * 显示版本号
     */
    public void updateVersionCode(){
        sendCommand(ScreenCommands.versionCode("Ver."+versionCode));
        versionCode++;
    }

    /**
     * 更新时间
     */
    public void upDateTime(){
        sendCommand(ScreenCommands.dateCmd(System.currentTimeMillis()));
    }



    /**
     * 改变信号显示
     */
    public void changeSignal(){
        byte[] signalCmd ;//信号离线 在线标识
        byte[] networkTypeCmd ;//网络类型标识

        if (isConnect){
            isConnect = false;
            signalCmd = ScreenCommands.onlineCmd;//在线指令
            //signalCmd = ScreenCommands.onlineCmd_horizontal;//台式在线指令

        }else {
            isConnect = true;
            signalCmd = ScreenCommands.offlineCmd;//离线指令
            //signalCmd = ScreenCommands.offlineCmd_horizontal;//台式机离线指令
        }

        switch (networkTypeFlag){
            case 0:{
                networkTypeCmd = ScreenCommands.network_4g;//4G网络标识
                networkTypeFlag ++;
                break;
            }
            case 1:{
                networkTypeCmd = ScreenCommands.network_wifi;//wifi网络标识
                networkTypeFlag ++;
                break;
            }
            default:{
                networkTypeCmd = ScreenCommands.network_cable;//以太网网络标识
                networkTypeFlag =0;
                break;
            }
        }
        sendCommand(signalCmd,networkTypeCmd);
    }



    @Override
    public void keyPress(String s) {
    }
}
