package com.ocom.devicedemo.views.background_image;


import android.annotation.SuppressLint;
import android.content.Context;

import com.ocom.devicedemo.views.OnShowViewBackListener;
import com.ocom.ocomdevicesdk.device.keybroad.KeybroadContent;
import com.ocom.ocomdevicesdk.device.screen.BaseSerialPortView;
import com.ocom.ocomdevicesdk.device.screen.ScreenCommands;
import com.ocom.ocomdevicesdk.device.screen.ScreenPosition;

/**
 * 用来设置menu页的背景图
 *   BackgroundImageHelper.sendBackgroundImage 该方法用于设置屏幕背景图
 *   传输过程中无法向该屏幕串口发送任何指令，直到传输完成
 *
 *
 */
public class BackgroundImageView extends BaseSerialPortView {
    public static final String TAG = BackgroundImageView.class.getSimpleName();

    private OnShowViewBackListener mBackListener = null;

    private String[] fileNames = new String[]{"bg01.jpg", "bg02.jpg", "bg03.jpg", "bg04.jpg", "bg05.jpg"};//assets下的背景资源文件名

    private int nowSelectedIndex = 0;

    private boolean isDowloading = false;//正在下载图片


    public void init(Context context,OnShowViewBackListener listener) {
        initDouble(context);
        mBackListener = listener;
    }


    /**
     * 跳转至菜单界面
     */
    public void jumpPage() {
        jumpPageByClear(
                ScreenCommands.PAGE_MENU,//跳转的菜单界面
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3700, "   【确认】修改menu背景"),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3800, "   【取消】返回")
        );
    }

    /**
     * 下载界面
     */
    private void jumpDownloading() {
        sendCommand(
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3300, ScreenCommands._clearScreenByte),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3300, "    正在更新...")
        );
    }

    /**
     * 下载结果界面
     */
    private void jumpResult(Boolean isSuccess) {
        String resultStr;
        if (isSuccess) {
            resultStr = "    修改成功！";
        } else {
            resultStr = "    修改失败！";
        }
        sendCommand(
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3300, ScreenCommands._clearScreenByte),
                ScreenCommands.pagePayCmd(ScreenPosition.MENU_POSITION_3300, resultStr)
        );
    }


    /**
     * 设置背景图片
     */
    @SuppressLint("CheckResult")
    private void setBackgroundImage() {
/*        if (!isDowloading) {
            jumpDownloading();
            isDowloading = true;
            //屏幕1传输背景图任务
            Observable screenObservable1 = Observable.create((ObservableOnSubscribe<Boolean>) observableEmitter -> {
                AssetManager assetManager = mContext.getApplicationContext().getAssets();
                InputStream imageInputStream;
                try {
                    imageInputStream = assetManager.open(fileNames[2]);//inputStread流也可以直接传图片File流
                    //发送背景图片需要放到子线程中 这一段是向Menu界面发送背景图片
                    BackgroundImageHelper.sendBackgroundImage(mScreenSerialPortUtils1, ScreenCommands.PAGE_PAY, imageInputStream);
                    observableEmitter.onNext(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    observableEmitter.onNext(false);
                }
            });
            //屏幕2传输背景图任务
            Observable screenObservable2 = Observable.create((ObservableOnSubscribe<Boolean>) observableEmitter -> {
                AssetManager assetManager = mContext.getApplicationContext().getAssets();
                InputStream imageInputStream;
                try {

                    imageInputStream = assetManager.open(fileNames[2]); //inputStread流也可以直接传图片File流
                    //发送背景图片需要放到子线程中 这一段是向Menu界面发送背景图片
                    BackgroundImageHelper.sendBackgroundImage(mScreenSerialPortUtils2, ScreenCommands.PAGE_PAY, imageInputStream);
                    observableEmitter.onNext(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    observableEmitter.onNext(false);
                }
            });

            //这里是为了确保两个屏幕都连接上才发送屏幕指令
            Observable.zip(screenObservable1, screenObservable2, (BiFunction<Boolean, Boolean, Boolean>) (screen1Result, screen2Result) -> {
                        nowSelectedIndex++;
                        Log.i(TAG, "屏幕1 设置结果：" + screen1Result + "      屏幕2 设置结果：" + screen2Result);
                        jumpResult(screen1Result && screen2Result);
                        isDowloading = false;
                        return false;
                    }
            ).observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }*/

    }


    @Override
    public void keyPress(String s) {
        switch (s) {
            case KeybroadContent.KEY_CLEAR: {//返回
                mBackListener.onBack();
                break;
            }

            case KeybroadContent.KEY_CONFIRM: {//确定
                setBackgroundImage();//设置背景图
            }
        }


    }
}
