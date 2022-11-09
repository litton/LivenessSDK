Android-SDK-活体检测
----------------

1. 在项目的 `app` 模块`build.gradle`增加库依赖：

        dependencies {
            ...
               implementation fileTree(dir: 'libs', include: ['*.jar','*.aar']) 
               implementation files('libs/iziliveness.aar')
               def camerax_version = "1.2.0-alpha04"
               implementation 'com.google.mlkit:face-detection:16.1.5'
               implementation("androidx.camera:camera-core:${camerax_version}")
               implementation("androidx.camera:camera-camera2:${camerax_version}")
               implementation("androidx.camera:camera-lifecycle:${camerax_version}")
               implementation("androidx.camera:camera-view:${camerax_version}")
               implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
        }

2. 将LivenessSDK项目中的iziliveness.aar拷贝到自己工程的libs下面：



3. 在您的 Activity 中参照如下代码启动活体检测：

        class LivenessDemoActivity : AppCompatActivity(), View.OnClickListener {

                lateinit var mActionButton: Button
                override fun onCreate(savedInstanceState: Bundle?) {
                         super.onCreate(savedInstanceState)
                         setContentView(R.layout.activity_live_home_layout)
                         mActionButton = findViewById(R.id.button_live_detection)
                         mActionButton.setOnClickListener(this)

                }

                override fun onClick(v: View?) {
                       LivenessDetectionSDK.from(this@LivenessDemoActivity)
                       .setLicense(license)
                       .setRequestCode(REQUEST_LIVENESS_CODE)
                       .start()
                }

                override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                        super.onActivityResult(requestCode, resultCode, data)
                        if (requestCode == REQUEST_LIVENESS_CODE) {
                        if (LivenessResult.isSuccess()) {
                                val bitmap = LivenessResult.getLivenessBitmap()
                        } else {
                                val errorMsg = LivenessResult.getErrorMsg()  
                        }
                }
        }



### 集成 SDK 到您的工程中

1.  初始化与鉴权。

    > SDK 提供License授权方式。


    *   **License 授权**：
        
        1.  初始化 SDK
            
             > 在from中传入接收活体识别结果的Activity对象，并传入服务端返回的合法license值

             LivenessDetectionSDK.from(activity)
                       .setLicense(license)
            
        2.  校验 license
            
            > license 由您的服务端调用我们 openAPI 获取，启动活体检测之前调用
            
            SDK内部启动活体检测流程前，会对License进行有效性的校验，检验失败则会退出活体检测流程

2.  启动活体检测与获取检测结果。

    > SDK 默认不检查相机权限，如果您希望权限的申请交由 SDK 处理，可在 SDK 初始化方法调用后，调用如下方法，则 LivenessActivity 中会执行权限申请的操作：

            LivenessDetectionSDK.enableSDKHandleCameraPermission();

    > 每次进行活体检测成功后，会返回本次检测的 600\*600 像素的清晰正面照片。

    * **您需要将 图片 传给您的服务端，由服务端调用 openAPI 获取本次检测的分值。**
    * 您可以通过 SDK 提供的方法直接获取图片，也可以由服务端调用 API 获取。

            /**
             * 请求状态码
             */
            const val  REQUEST_CODE_LIVENESS = xxxx;
            
            /**
             * 启动活体检测
             */
            private fun startLivenessActivity() {
                LivenessDetectionSDK.from(activity)
                          //设置活体检测Activity背景颜色
                         .setMainBgColor(Color.BLACK)
                          //设置活体检测界面标题颜色
                         .setTitleColor(Color.RED)
                          //设置CameraPreview的宽度占比默认是0.7f
                         .setPreviewWidthPercent(0.7f)
                          //设置动作提示文字颜色
                         .setGuideTextColor(Color.WHITE)
                          //设置活体检测倒计时组件颜色
                         .setProgressBarColor(Color.WHITE)
                          //设置本次活体检测的有效License
                         .setLicense(license).start();
                  }
            
            /**
            * 获取检测结果
            */
            @Override
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                      super.onActivityResult(requestCode, resultCode, data)
                      if (requestCode == REQUEST_LIVENESS_CODE) {
                        if (LivenessResult.isSuccess()) {
                                val bitmap = LivenessResult.getLivenessBitmap()
                        } else {
                                val errorMsg = LivenessResult.getErrorMsg()  
                        }
                }

3.  动作顺序说明

    > 默认的动作顺序是 \[张嘴\]->\[摇头\]

4.  自定义返回的图像尺寸

        // 支持设置的入参范围：[300,1000]，单位：像素
        LivenessDetectionSDK.setResultPictureSize(600);

5. 自定义动作检测超时时间

   > 支持自定义动作的检测时间，设置后所有动作都生效，单位：毫秒，SDK中默认10000

        LivenessDetectionSDK.setActionTimeoutMills(10000);

6. 国际化

   SDK 支持英文，印尼文，跟随手机系统语言自动切换，无需代码设置。

   若没有跟随系统语言自动切换 ，请检查手机语言设置，确保【地区】和【语言】均已切换至对应的语言。  
   若仍然出现语言国际化问题，同时 app 仅支持某种语言，可以通过在`build.gradle`中增加如下配置，过滤掉不需要的语言

        android {
            defaultConfig {
                ...
                resConfigs("in-rID") // 以仅支持印尼语为例
            }
        
        }

7. 运行时权限

   本 SDK 需要如下权限，并且已经在 aar 的清单文件中做了配置。

       <uses-feature android:name="android.hardware.camera" />
       <uses-permission android:name="android.permission.INTERNET" />
       <uses-permission android:name="android.permission.CAMERA" />

8. 关于 App Bundle 打包

   SDK 中包含 .so 文件，为了减少包大小，在上架 Google Play 时可以选择使用 Bundle 打包，请验证打包后的 APK 在各个架构的手机上都可以运行，如果出现提示【该设备不支持】，则表示组装 APK 时缺失了 .aar 中的 .so 文件。

9. 代码混淆

   SDK 已经做好了代码混淆，无需额外增加配置。

10. SDK 兼容性

11. 最低 Android 版本：5.0+ (API Level:21)
12. SDK 编译版本：API Level：32
13. 支持的 CPU 架构：`armeabi-v7a`，`arm64-v8a`，`x86`，`x86_64`，`armeabi`
14. 采集图像大小：默认采集图像分辨率 600px\*600px，大小约为 300KB，支持自定义图像尺寸范围：300px~1000px

* * *

**Error Code:**

|错误码| 解释 | 解决办法 |
| ---- | ---- |------|
|FACE\_MISSING|检测过程中人脸丢失| /    |
|ACTION\_TIMEOUT | 动作超时| /    |
| MULTIPLE\_FACE | 检测过程中出现多张人脸 | / |
| MUCH\_MOTION | 检测过程中动作幅度过大 | / |
| LICENSE\_FAILED | License校验失败 | / |
| DEVICE\_NOT\_SUPPORT | 该设备不支持活体检测,设备无前置摄像头或不可用 | / |
| USER\_GIVE\_UP | 用户中途放弃检测 | / |
| INVALID\_INPUT | 入参缺失 | / |
| INVALID\_APPID | APP包名校验失败 | / |
| ACCOUNT\_CLOSED | 账户被关停 | / |
| INVALID\_SIGNATURE | 签名校验失败 | / |
| FAIL | 其他错误 | / | 

# LivenessSDKDemo
