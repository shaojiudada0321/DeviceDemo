package com.ocom.devicedemo.views.demo;

/**
 * DemoView的回调方法
 */
public interface OnDemoSettingListener {

    /**
     * 展示初始化界面
     */
    void showInitView();

    /**
     * 展示菜单界面
     */
    void showMenuView();

    /**
     * 展示支付界面
     */
    void showPayView();

    /**
     * 展示支付结果界面
     */
    void showPayResultView();

    /**
     * 展示待机界面
     */
    void showStanbyView();

    /**
     * 更新时间
     */
    void upDateTime();

    /**
     * 改变信号图标
     */
    void changeSignal();

    /**
     * 更新版本号
     */
    void upDateVersionCode();

    /**
     * 更新机器号
     */
    void upDateMachinNo();

    /**
     * 设置menu界面的背景
     */
    void setPageMenuPackground();


}
