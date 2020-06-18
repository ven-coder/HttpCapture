# okhttp抓包库
### 抓取自身APP的网络请求，方便测试、后端、及自己调试查看APP的网络请求情况
![效果动图](https://images.gitee.com/uploads/images/2020/0615/211612_bd2bd9cf_1032805.gif "ezgif.com-gif-maker.gif")

 **看不到动图可以到我的码云仓库看[点击跳转码云](https://gitee.com/BDWen/HttpCapture)**

## 集成
 **1. 项目根目录的build.gradle中添加**

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
 **2. app目录中build.gradle中添加**

```
dependencies{
    implementation 'com.github.Levine1992:HttpCapture:1.0.5'
}
```

 **3. application中进行初始化**

```
@Override
    public void onCreate() {
        super.onCreate();
        HCLib.INSTANCE.init(this);
    }
```
 **4. okhttp添加拦截器**

```
mBuilder = new OkHttpClient.Builder();
mBuilder.addInterceptor(new HttpCaptureInterceptor());
```
 **这样，打开你的app的时候就可以像上面的动画一样有一个黑色浮窗按钮了，点击随时可以查看app的网络请求信息了**
### 浮窗控制
```
//禁止显示浮窗
//kotlin写法
HCLib.isEnableActivityFloatView(false)
//java写法
HCLib.INSTANCE.isEnableActivityFloatView(false)

//以下以kotlin为例
//隐藏浮窗
HCLib.hideActivityFloatView(activity)
//显示浮窗
HCLib.showActivityFloatView(activity)
```
#### 有一种情况就是你的接口是加密的，不处理的话查看时候只会看到一堆密文

这个时候自己实现一下 **_HCNetDataConvert_** 接口，对数据进行解密

```
public class NetDataConvert implements HCNetDataConvert{
    ...
}
```
然后在拦截器中实例化

```
mBuilder.addInterceptor(new HttpCaptureInterceptor(new NetDataConvert()));
```

 **其实这个很简单，但是自己一直没发现有人写，所以自己就写了个，方便大家，如果有人写了而且写的功能更强大，请告诉我学习学习**



