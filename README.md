# 项目介绍
## 项目架构
此APP是一个金字塔型，共分为4层，只允许上层依赖下层，严禁同层模块之间互相依赖。

- APP层：应用APP层，它可以依赖任何其它模块，但是它应该足够的简单，不应该有任何的业务逻辑。
  - GVideoApplication：App初始化入口
  - GVideoEnvironment：服务器环境配置
  - NetworkInitialization：接口请求初始化入口
  - 其他

- features: 业务层，可以依赖kernels层和libraries层
  - account：账号、个人中心、设置等
  - feed：feed流与频道页
  - home：主页面
  - pptv：pptv
  - record：ugc拍摄
  - search：搜索
  - share：分享工具模块
  - test：侧边栏调试入口
  - video：播放详情页、活动直播详情页
  - webview：内置WebView
  - live：互动直播模块
 

- kernels: 一些基础组件，包含企业级中台服务，以及多个业务层需要使用的功能，可以依赖libraries层。
  - base: 存放整个APP所有的基类，绝对不允许添加任何业务代码
  - event： 消息总线模块。以GVideoEventBus作为入口。
  - liteav： 播放器相关模块。以KSVideoView作为入口。
  - media： 自动播放相关模块。
  - network： 网络请求相关模块。
  - runtime：上下文模块。
  - stat：统计模块。以GVideoStatManager作为入口。
  - push：消息推送模块
  
- res： 资源相关模块。放置一些共用资源。
   
- libraries: 通用库层，沉淀一些没有业务的代码，这部分定位是未来可以开源的内容
  - crop: 图片裁剪工具模块。
  - ioc: 模块依赖库，用来各模块解耦。
  - utils: 工具类。
  - widget: 里面包含了气泡，dialog，沉浸式，输入框，toast，popup等ui组件
  - videocache: 视频预加载、视频离线缓存库


- tuikit: 带UI的腾讯IM聊天库  


--- 
## 配置信息
  - 配置入口 app/build.gradle
  - App初始化入口 GVideoApplication
  - 三方库配置入口 versions.gradle
  - 编辑脚本 ./gradlew assembleDebug assembleRelease
---
## URL路由协议
支持从H5页面直接跳转到App某页面，比如分享H5页可以唤起App详情页
外链跳转分发逻辑详见: H5EntryPlugin

已支持外链跳转:  
```
PGC 页面：  jinshipin://pgc/<authorId>       // 如： jinshipin://pgc/123，authorId：用户 ID
UGC 页面：  jinshipin://ugc/<authorId>       // 如： jinshipin://ugc/123，authorId：用户 ID

首页垂类：  jinshipin://home/<mediaTabId> // 如： jinshipin://home/-1，mediaTabId：垂类 ID
直播垂类：  jinshipin://live/<mediaTabId> // 如： jinshipin://live/2，mediaTabId：垂类 ID

//已废弃
视频垂类：  jinshipin://video/<mediaTabId> // 如： jinshipin://video/123，mediaTabId：垂类 ID
//已废弃
FM 垂类：  jinshipin://fm/<mediaTabId>    // 如： jinshipin://fm/123，mediaTabId：垂类 ID

进入通知列表详情：  jinshipin://notification?msgType=2&title=互动消息
                 msgType 消息类型，用于api/notification/list接口请求数据
                 title   消息界面标题

内容详情页:  jinshipin://detail/{mediaId}?mediaType=2
  mediaId 为资源id, 必选
  mediaType 资源大类 ，必选
  1 长视频 
  2 短视频 
  3 长音频 
  4 短音频
  5 活动直播横屏 
  6 活动直播竖屏
  7 互动直播横屏 
  8 互动直播竖屏
  9、10 图文动态详情
  11、12 新闻详情
  20 社区详情
  21 话题详情
  22 放心爱详情
  23 自建看电视详情
  24 看电视详情 

web页:  jinshipin://web?gv_url=URLEcoded(realUrl)
  在app内打开某web落地页，参数为gv_url
```
---
## JS交互协议
使用内嵌WebView时，开放部分js交互API由H5控制页面形式，比如可以在意见反馈页面隐藏原生标题栏
JS协议处理逻辑详见：WebJavaScriptHandler

已支持的JS交互协议：
```
// 关闭当前页面（异步调用）
jinshipin://h5-native/commands/closePage
// 隐藏导航栏（异步调用）
jinshipin://h5-native/commands/hideNavigationBar
// 设置标题（异步调用）
jinshipin://h5-native/commands/setNavTitle?title=标题
// 打开分享弹窗
jinshipin://h5-native/commands/share?title=标题&url=分享链接&image=缩略图&description=描述
// 当前 App 版本（同步调用）
jinshipin://h5-native/datas/appVersion
// 当前登录用户token
jinshipin://h5-native/datas/userToken
```
H5调用方式参考：
```
// 异步调用，无返回值
function callNativeAction(url) {
  // 调用 iOS 使用
  window.webkit.messageHandlers.callNative.postMessage(url);
  // 调用 android 使用
  window.GVJsBridge.callNativeMethod(url); // AndroidJsBridge 安卓注册到 H5，直接使用便可
}
// 同步调用，同步获取放回值
function getNativeData(url) {
  // 调用 iOS & android
  var version = prompt(url)
}
```
---
## 埋点
对于某些客户端行为进行记录，上传到数据统计后台，统计后台结合业务数据进行处理，展示统计数据。

### 基本原理 入口参见 GVideoStatManager
- 埋点服务器 接收客户端上传批量埋点信息，支持gzip压缩；
- 缓存埋点形式：使用缓存埋点数据库；
- 上传方式：批量埋点打包上传，server记录，后续按照协议拆解。 上传成功后清除本地数据库。
- 埋点上传打包形式：数组，形式类似{通用信息+具体信息1，通用信息+具体信息2，...}；
- 埋点上传时机：前后台切换时上传所有本地缓存埋点，缓存埋点达到100条时；

### 埋点通用信息 详情参见 StatGroupEntity
```
String uid;/** 今视频号 */
String cid;/** 设备终端deviceId */
Date timestamp;/** 时间戳ms */
String networkType;/** 网络类型，例如WIFI，移动4G，电信5G */
String appName;/** 应用包名 */
String appVersion;/** 应用的版本 */
String channel;/** 渠道号 */
String manufacturer;/** 设备制造商，例如Apple */
String model;/** 设备型号，例如iphone6 */
String os;/** 操作系统，例如iOS */
String osVersion;/** 操作系统版本，例如8.1.1 */
```
### 具体埋点信息 详情参见 StatEntity
```
String pid; /** 页面id */
String sessionId; /** 生命周期id */
Date timestamp;/** 时间戳ms */
String ev; /** 事件id */
String ds;/** 事件参数 */
@StatType String type; /** 埋点类型 */
```
### StatType 详情参见 StatConstants
- "a" App生命周期变化
- "c" 主动点击
- "e" 事件曝光

### Pid 详情参照 StatPid
---
## RN
项目初始设计时, 会有RN模块。但目前RN未使用。若无需要, 可移除

---
## 第三方库介绍 参见 versions.gradle
---
