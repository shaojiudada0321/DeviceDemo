package com.ocom.devicedemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.castle.serialport.SerialPortManager;
import com.ocom.devicedemo.views.background_image.BackgroundImageView;
import com.ocom.devicedemo.views.common.CommonView;
import com.ocom.devicedemo.views.demo.DemoView;
import com.ocom.devicedemo.views.demo.OnDemoSettingListener;
import com.ocom.devicedemo.views.init.InitView;
import com.ocom.devicedemo.views.menu.MenuView;
import com.ocom.devicedemo.views.pay.PayView;
import com.ocom.devicedemo.views.payresult.PayResultView;
import com.ocom.devicedemo.views.stanby.StanbyView;
import com.ocom.ocomdevicesdk.device.OCOMDeviceLoader;
import com.ocom.ocomdevicesdk.device.card_reader.CardReaderDevice;
import com.ocom.ocomdevicesdk.device.card_reader.CardReaderMessageManager;
import com.ocom.ocomdevicesdk.device.card_reader.OnCardProximityStatusListener;
import com.ocom.ocomdevicesdk.device.card_reader.OnProcessCPUCardListener;
import com.ocom.ocomdevicesdk.device.card_reader.OnProcessM1CardListener;
import com.ocom.ocomdevicesdk.device.card_reader.OnReadCardListener;
import com.ocom.ocomdevicesdk.device.keybroad.KeybroadDevice;
import com.ocom.ocomdevicesdk.device.keybroad.OnKeybroadListener;
import com.ocom.ocomdevicesdk.device.keybroad.USBReceiver;
import com.ocom.ocomdevicesdk.device.qrcode_scanner.OnQRCodeScannerListener;
import com.ocom.ocomdevicesdk.device.qrcode_scanner.ScannerQRCodeDevice;
import com.ocom.ocomdevicesdk.utils.UsbChecker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    //当前设备处于哪个界面
    public static final int PAGE_INITING = 10001;//初始化界面
    public static final int PAGE_STANDBY = 10002;//待机界面
    public static final int PAGE_PAY = 10003;//支付界面
    public static final int PAGE_PAY_RESULT = 10004;//支付界面 结果
    public static final int PAGE_MENU = 10005;//菜单界面

    public static final int PAGE_DEMO = 10006;//demo菜单展示页

    public static final int PAGE_BACKGROUND = 10007;//设置背景


    //--------------------------------屏幕相关

    private InitView mInitView = null;//初始化界面
    private StanbyView mStanbyView = null;//待机界面
    private PayView mPayView = null;//支付界面
    private PayResultView mPayResultView = null;//支付结果界面
    private MenuView mMenuView = null;//菜单界面

    private CommonView mCommonView = null;//用于更改时间 信号 版本号等信息 独立于展示屏之外 可以与界面更新指令并行

    private DemoView mDemoView = null;//demo菜单页

    private BackgroundImageView mBackgroundView = null;//设置背景图片模块


    private int nowPage = PAGE_DEMO;//记录当前是在哪个界面 默认开启为待机界面

    private View mOpenReadCardNoBtn = null;//开启卡号读取功能

    private View mCloseReadCardNoBtn = null;//关闭卡号读取功能

    private View mDeviceInfoBtn = null;//获取设备号和设备类型


    /**
     * 注意！！！
     * 无论是M1卡和CPU卡操作，必须要先寻卡，获取卡号以后才能进行相关操作！！！！
     */

    private View mM1VerifyBtn = null;//M1卡验证
    private View mM1ReadBtn = null;//M1卡读操作
    private View mM1WriteBtn = null;//M1卡写操作

    private View mCpuSwitchAPDUBtn = null;//CPU卡转APDU模式
    private View mCpuOperationAPDUBtn = null;//CPU卡APDU操作 选应用
    private View mCpuOperationAPDU2Btn = null;//选16文件

    private TextView mLogTv = null;//显示Log

    private String cardNumber = null;//读取到的卡号
    private int writeCount = 0;

    private static boolean sdkInited = false;//sdk初始化标识

    private USBReceiver mUsbReceiver = new USBReceiver();

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //1.注册广播接收器
        mUsbReceiver.regist(this);
        //2.调用sdk初始化
        OCOMDeviceLoader.init(this, () -> {
            //相关硬件类初始化以及使用，需要等待sdk初始化完毕
            initScreenViews();
            initKeybroad();
            iniQRCodeScanner();
            initCardReader();
            initViews();
            sdkInited = true;
            KeybroadDevice.getInstance().getDeviceInfoForInit();//需要对键盘调用一次获取设备号
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUsbReceiver.unregist(this);
    }

    /**
     * 初始化安卓控件
     */

    int index = 0;
    private Vector<byte[]> cardData = new Vector<>();

    private void initViews() {

        mDeviceInfoBtn = findViewById(R.id.deviceInfoBtn);
        mDeviceInfoBtn.setOnClickListener(v -> {
            if (sdkInited){//必须要等待sdk初始化完毕以后才能获取硬件设备号 设备类型信息
                KeybroadDevice.getInstance().getDeviceInfo();//获取设备号 设备类型
            }

        });

        //--------------------------------获取卡号
        mOpenReadCardNoBtn = findViewById(R.id.openBtn);
        mOpenReadCardNoBtn.setOnClickListener(v -> {//开始读取卡号
            writeCount = 0;
            cardData.clear();
            cardNumber = null;
            clearLogText();
            addLogText("正在等待刷卡...");
            CardReaderDevice.getInstance().startReadCardNo();
        });
        mCloseReadCardNoBtn = findViewById(R.id.closeBtn);
        mCloseReadCardNoBtn.setOnClickListener(v -> {//停止读取卡号
            cardNumber = null;
            clearLogText();
            addLogText("已停止卡号读取");
            CardReaderDevice.getInstance().stopReadCardNo();
        });

        //--------------------------------M1卡
        mM1VerifyBtn = findViewById(R.id.m1VerifyBtn);
        mM1VerifyBtn.setOnClickListener(v -> {//m1卡验证
            if (cardNumber != null) {
                CardReaderDevice.getInstance().m1_verify(
                        CardReaderMessageManager.M1_VERIFY_MOD_KEY_A,// 模式  M1_VERIFY_MOD_KEY_A 或者 M1_VERIFY_MOD_KEY_A
                        3,//扇区号（0—15）
                        new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}//密码（6字节）这边demo测试的密码默认6字节FF，如果自己有协议就可以按着自己的协议填写就行了
                );
            } else {
                addLogText("请先获取卡号");
            }
        });

        mM1ReadBtn = findViewById(R.id.m1ReadBtn);

        mM1ReadBtn.setOnClickListener(v -> {//读取区块内的数据
            if (index == 63) {
                index = 0;
            }
            if (cardNumber != null) {
                CardReaderDevice.getInstance().m1_readCard(index);//扇区（0—63）
                index++;
            } else {
                addLogText("请先获取卡号");
                index = 0;
            }
        });

        mM1WriteBtn = findViewById(R.id.m1WriteBtn);
        mM1WriteBtn.setOnClickListener(v -> {//往区块内写数据
            if (cardNumber != null) {
/*              //写入单个块的数据
                CardReaderDevice.getInstance().m1_writeCard(
                        0,//块号(0-63)
                        new byte[]{
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01}//存入的内容（16字节）
                );
*/
                //写入多个块的数据
                HashMap<Integer, byte[]> map = new HashMap<>();
                map.put(12, new byte[]{
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01});
                map.put(13, new byte[]{
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01});

                map.put(14, new byte[]{
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01});

                CardReaderDevice.getInstance().m1_writeCard(map);
            } else {
                addLogText("请先获取卡号");
            }
        });

        //--------------------------------CPU卡
        mCpuSwitchAPDUBtn = findViewById(R.id.cpuSwitchAPDUBtn);
        mCpuSwitchAPDUBtn.setOnClickListener(v -> {//切换apdu模式
            if (cardNumber != null) {
                CardReaderDevice.getInstance().cpu_switchAPDU();
            } else {
                addLogText("请先获取卡号");
            }
        });


        mCpuOperationAPDUBtn = findViewById(R.id.cpuOperationAPDUBtn);
        mCpuOperationAPDUBtn.setOnClickListener(v -> {//操作APDU：选应用
            if (cardNumber != null) {
                CardReaderDevice.getInstance().cpu_operationAPDU(
                        16,//APDU长度（APDU字节长度）
                        new byte[]{0x32, 0x0E, 0x00, (byte) 0xA4, 0x04, 0x00, 0x09, 0x44, 0x58, 0x43, 0x2E, 0x50, 0x41, 0x59, 0x30, 0x31}//APDU内容
                );
            } else {
                addLogText("请先获取卡号");
            }
        });
        mCpuOperationAPDU2Btn = findViewById(R.id.cpuOperationAPDU2Btn);
        mCpuOperationAPDU2Btn.setOnClickListener(v -> {//APDU操作：选文件
            if (cardNumber != null) {
                CardReaderDevice.getInstance().cpu_operationAPDU(
                        16,//APDU长度（APDU字节长度）
                        new byte[]{0x32, 0x05, 0x00, (byte) 0xB0, (byte) 0x96, 0x00, 0x60}//APDU内容
                );
            } else {
                addLogText("请先获取卡号");
            }
        });
        //--------------------------------LogTextView
        mLogTv = findViewById(R.id.logTv);


    }


    /**
     * 初始化读卡器
     */
    private void initCardReader() {
        CardReaderDevice.getInstance().setOnReadCardListener(new OnReadCardListener() {
            /**
             * 读取到卡号
             * @param s
             */
            @Override
            public void readCardNo(String s) {

                CardReaderDevice.getInstance().stopReadCardNo();//关闭卡号读取
                Log.i(TAG, "cardReader 读取到卡号：" + s);

                runOnUiThread(() -> {//回调结果在子线程
                    addLogText("已读取到卡号：" + s);
                });
                  writeCount = 0;
                cardData.clear();
                cardNumber = s;
              CardReaderDevice.getInstance().m1_verify(
                        CardReaderMessageManager.M1_VERIFY_MOD_KEY_A,// 模式  M1_VERIFY_MOD_KEY_A 或者 M1_VERIFY_MOD_KEY_A
                        3,//扇区号（0—15）
                        new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}//密码（6字节）这边demo测试的密码默认6字节FF，如果自己有协议就可以按着自己的协议填写就行了
                );

            }

            @Override
            public void originData(int i, byte[] bytes) {

            }
        });
        CardReaderDevice.getInstance().setOnProcessM1CardListener(new OnProcessM1CardListener() {//M1卡的操作结果回调
            /**
             * 验证回调
             * @param isSuccess 是否验证成功
             */
            @Override
            public void onVerifyDone(boolean isSuccess) {
                runOnUiThread(() -> {
                    addLogText("M1卡" + cardNumber + "验证结果：" + isSuccess);
                });
                if (isSuccess) {
                    CardReaderDevice.getInstance().m1_readCard(12, 13, 14);
                } else {
                    CardReaderDevice.getInstance().startReadCardNo();
                }

            }

            /**
             * 读卡 回调
             * @param isSuccess 成功结果
             * @param blockData 块数据 失败时返回null
             */

            @Override
            public void onReadCardDone(boolean isSuccess, byte[] blockData) {
                runOnUiThread(() -> addLogText("M1卡" + cardNumber + "读卡结果：" + isSuccess + "        块数据内容" + Arrays.toString(blockData)));
                if (isSuccess) {
                    cardData.add(blockData);
                    if (cardData.size() == 3) {//读完内容
                        HashMap<Integer, byte[]> map = new HashMap<>();
                        map.put(12, new byte[]{
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01});
                        map.put(13, new byte[]{
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01});

                        map.put(14, new byte[]{
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                                (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01});

                        CardReaderDevice.getInstance().m1_writeCard(map);
                    }
                } else {
                    CardReaderDevice.getInstance().startReadCardNo();//重新读卡
                }
            }


            /**
             * 写卡 回调
             * @param isSuccess 写入是否成功
             */
            @Override
            public void onWriteCardDone(boolean isSuccess) {
                runOnUiThread(() -> addLogText("M1卡" + cardNumber + "写卡结果：" + isSuccess));
                if (isSuccess) {
                    writeCount++;
                    if (writeCount == 3) {
                        CardReaderDevice.getInstance().startReadCardNo();
                    }
                } else {
                    CardReaderDevice.getInstance().startReadCardNo();
                }
            }
        });

        CardReaderDevice.getInstance().setOnCardProximityStatusListener(new OnCardProximityStatusListener() {
            @Override
            public void laidDown(String s) {
                Log.i("lxy测试", "卡" + cardNumber + "放置状态：放下      " + s);
            }

            @Override
            public void keeping(String s) {
                Log.i("lxy测试", "卡" + cardNumber + "放置状态：保持      " + s);
            }

            @Override
            public void pickedUp(String s) {
                Log.i("lxy测试", "卡" + cardNumber + "放置状态：拿起      " + s);
            }
        });


        CardReaderDevice.getInstance().setOnProcessCPUCardListener(new OnProcessCPUCardListener() {
            /**
             * 转APDU 回调
             * @param isSuccess 操作结果
             * @param dataLenth 数据长度 失败时返回 -1
             * @param data 数据内容 失败时返回null
             */
            @Override
            public void onSwitchAPDUDone(boolean isSuccess, int dataLenth, byte[] data) {
                runOnUiThread(() -> addLogText("CPU卡" + cardNumber + "转APDU结果：" + isSuccess + "    数据长度：" + dataLenth + "    数据内容：" + Arrays.toString(data)));
            }

            /**
             *操作APDU 回调
             * @param isSuccess 操作结果
             * @param dataLenth 数据长度 失败时返回 -1
             * @param data 数据内容 失败时返回null
             */
            @Override
            public void onOperationAPDUDone(boolean isSuccess, int dataLenth, byte[] data) {
                runOnUiThread(() -> addLogText("CPU卡" + cardNumber + "操作APDU结果：" + isSuccess + "    数据长度：" + dataLenth + "    数据内容：" + Arrays.toString(data)));
            }
        });


    }


    /**
     * 初始化扫码头
     */
    private void iniQRCodeScanner() {
        ScannerQRCodeDevice.getInstance().setOnQRCodeScannerListener(new OnQRCodeScannerListener() {

            /**
             * 扫码器扫到二维码
             * @param s
             */
            @Override
            public void scanContent(String s) {
                Log.i(TAG, "qrcodeScanner 扫描到内容：" + s);
            }
        });

    }


    /**
     * 初始化键盘
     */
    private void initKeybroad() {
        KeybroadDevice.getInstance().setOnKeybroadListener(new OnKeybroadListener() {
            /**
             * 键盘按下返回键值
             * @param s
             */
            @Override
            public void keyPress(String s) {
                switch (nowPage) {
                    case PAGE_INITING: {
                        mInitView.keyPress(s);
                        break;
                    }
                    case PAGE_STANDBY: {
                        mStanbyView.keyPress(s);
                        break;
                    }
                    case PAGE_PAY: {
                        mPayView.keyPress(s);
                        break;
                    }
                    case PAGE_PAY_RESULT: {
                        mPayResultView.keyPress(s);
                        break;
                    }
                    case PAGE_MENU: {
                        mMenuView.keyPress(s);
                        break;
                    }
                    case PAGE_DEMO: {
                        mDemoView.keyPress(s);
                        break;
                    }
                    case PAGE_BACKGROUND: {
                        mBackgroundView.keyPress(s);
                    }
                }

            }

            /**
             * 获取键盘回传参数内容（直接用keyPress）
             * @param bytes
             */
            @Override
            public void originData(byte[] bytes) {
                //当源数据bytes == null时 需要重新获取设备号
                if (bytes == null){
                    KeybroadDevice.getInstance().getDeviceInfoForInit();
                    return;
                }


                if (KeybroadDevice.checkDeviceInfoMessage(bytes)) {//先检查数据
                    int deviceType = KeybroadDevice.getDeviceType(bytes);
                    Log.e("lxy测试", "获取设备参数" + Arrays.toString(bytes));
                    Log.e("lxy测试", "设备号：" + KeybroadDevice.getDeviceNumber(bytes));
                    if (deviceType == KeybroadDevice.DEVICE_TYPE_HORIZONTAL) {
                        Log.e("lxy测试", "设备类型：横屏");
                    } else if (deviceType == KeybroadDevice.DEVICE_TYPE_VERTICAL) {
                        Log.e("lxy测试", "设备类型：竖屏");
                    } else {
                        Log.e("lxy测试", "设备类型：未知设备");
                    }
                }
            }
        });
    }

    /**
     * 初始化屏幕View
     */
    @SuppressLint("CheckResult")
    private void initScreenViews() {

        mInitView = new InitView();
        mStanbyView = new StanbyView();
        mPayView = new PayView();
        mPayResultView = new PayResultView();
        mMenuView = new MenuView();
        mCommonView = new CommonView();
        mDemoView = new DemoView();
        mBackgroundView = new BackgroundImageView();

        mInitView.init(this, () -> {//初始化界面
            goExampleView(PAGE_DEMO);
        });
        mStanbyView.init(this, () -> {//待机界面
            goExampleView(PAGE_DEMO);

        });
        mPayView.init(this, () -> {//支付界面
            goExampleView(PAGE_DEMO);
        });

        mPayResultView.init(this, () -> {//支付结果界面
            goExampleView(PAGE_DEMO);
        });
        mMenuView.init(this, () -> {//菜单界面
            goExampleView(PAGE_DEMO);
        });

        mDemoView.init(this, new OnDemoSettingListener() {//demo菜单页
            /**
             * 显示初始化界面
             */
            @Override
            public void showInitView() {
                goExampleView(PAGE_INITING);
            }

            /**
             * 显示菜单界面
             */
            @Override
            public void showMenuView() {
                goExampleView(PAGE_MENU);
            }

            /**
             * 显示支付界面
             */
            @Override
            public void showPayView() {
                goExampleView(PAGE_PAY);
            }

            /**
             * 显示支付结果界面
             */
            @Override
            public void showPayResultView() {
                goExampleView(PAGE_PAY_RESULT);
            }

            /**
             * 显示待机界面
             */
            @Override
            public void showStanbyView() {
                goExampleView(PAGE_STANDBY);
            }

            /**
             * 显示待机界面
             */
            @Override
            public void upDateTime() {
                mCommonView.upDateTime();
            }

            /**
             * 更改信号图标
             */
            @Override
            public void changeSignal() {
                mCommonView.changeSignal();
            }

            /**
             * 更新版本号
             */
            @Override
            public void upDateVersionCode() {
                mCommonView.updateVersionCode();
            }

            /**
             * 更新机器号
             */
            @Override
            public void upDateMachinNo() {
                mCommonView.upDateMachinNo();
            }

            /**
             * 更改menu界面的背景
             */
            @Override
            public void setPageMenuPackground() {
                goExampleView(PAGE_BACKGROUND);
            }
        });

        mBackgroundView.init(this, () -> goExampleView(PAGE_DEMO));
        mCommonView.init(this);
        nowPage = PAGE_DEMO;
        mDemoView.jumpPage();
    }


    /**
     * 跳转到样例界面
     */
    private void goExampleView(int page) {
        Log.i("lxy测试", "当前界面：" + nowPage + "    即将跳转的界面：" + page);
        nowPage = page;
        switch (page) {
            case PAGE_INITING: {
                mInitView.jumpPage();
                break;
            }
            case PAGE_STANDBY: {
                mStanbyView.jumpPage();
                break;
            }
            case PAGE_PAY: {
                mPayView.jumpPage();
                break;
            }
            case PAGE_PAY_RESULT: {
                mPayResultView.jumpPage();
                break;
            }
            case PAGE_MENU: {
                mMenuView.jumpPage();
                break;
            }
            case PAGE_DEMO: {
                mDemoView.jumpPage();
                break;
            }
            case PAGE_BACKGROUND: {
                mBackgroundView.jumpPage();
                break;
            }
        }
    }


    /**
     * 添加操作卡的log
     */
    @SuppressLint("SetTextI18n")
    private void addLogText(String log) {
        Log.i("lxy测试", "Log：" + log);
        mLogTv.setText(mLogTv.getText().toString() + log + "\n");
    }

    /**
     * 清空卡操作log
     */
    private void clearLogText() {
        mLogTv.setText("");
    }


}
