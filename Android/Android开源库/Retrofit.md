Retrofit

## 一、使用

### 1. Retrofit入门

```java
//1.创建Retrofit实例
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(baseURL)
        .build();

//2.接口定义
public interface Api {
    @GET("{id}")
    Call<ResponseBody> get(@Path("id") int id);
}

//创建代理对象
Api api = retrofit.create(Api.class);

//3.接口调用
Call<ResponseBody> call = api.get(1);
call.enqueue(new Callback<ResponseBody>() {
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        
    }
});
```

### 2. Retrofit注解

#### 1.HTTP请求方法

GET，POST，PUT，DELETE，PATCH，HEAD，OPTIONS，HTTP(可替换前7个)。

除HTTP外都对应了HTTP标准的请求方法，而HTTP可以代替以上任何一个注解，method，path，hasBody。

```java
public interface Api {
    @HTTP(method = "GET", path = "{id}", hasBody = false)
    Call<ResponseBody> get(@Path("id") int id);
}
```

#### 2.标记类

| 分类     | 名称           | 备注                                   |
| -------- | -------------- | -------------------------------------- |
| 表单请求 | FormUrlEncoded | 表示请求体是一个Form表单               |
|          | Multipart      | 表示请求体是一个支持文件上传的Form表单 |
| 标记     | Streaming      | 表示请求体的数据用流的形式返回         |

#### 3.参数类

| 分类                           | 名称     | 备注                     |
| ------------------------------ | -------- | ------------------------ |
| 作用于方法                     | Headers  | 用于添加请求头           |
| 作用于方法参数，这里指的是形参 | Header   | 用于添加不固定类得Header |
|                                | Body     | 用于非表单请求体         |
|                                | Field    | 用于表单字段             |
|                                | FieldMap |                          |
|                                | Part     |                          |
|                                | PartMap  |                          |
|                                | Path     | 用于URL                  |
|                                | Query    |                          |
|                                | QueryMap |                          |
|                                | Url      |                          |

### 3. Gson与Converter

在默认情况下，Retrofit只支持将HTTP的响应体转换为ResponseBody，而Converter就会Retrofit提供用于将ResponseBody转换为其它类型，

添加依赖`implementation 'com.squareup.retrofit2:converter-gson:2.4.0'`

```java
public interface Api {
    @GET("{id}")
    Call<Result<Book>> get(@Path("id") int id);
}

Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(GsonConverterFactory.create())
      .build();
```

### 4. RxJava与CallAdapter

`implementation 'com.squareup.retrofit2:adapter-rxjava2:2.4.0'`

```java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

public interface Api {
    @GET("{id}")
    Observable<Result<Book>> get(@Path("id") int id);
}

Api api = retrofit.create(Api.class);

api.get(1)
    .subscribeOn(Schedulers.io())
    .subscribe(new Subscriber<Result<Book>>() {
      @Override
      public void onCompleted() {
          
      }

      @Override
      public void onError(Throwable e) {
          
      }

      @Override
      public void onNext(Result<Book> blogsResult) {
          
      }
  });
```

## 二、源码分析

1. 通过解析网络请求接口的注解，配置网络请求参数
2. 通过动态代理生成网络请求对象
3. 通过网络适配器将网络请求对象进行平台适配（Android、RxJava、Guava、Java8）
4. 通过网络请求执行器发送网络请求
5. 通过数据转换器解析服务器返回的数据
6. 通过回调执行器切换线程（子线程 -> 主线程）
7. 用户在主线程处理返回结果

| 角色                       | 作用                                                         | 备注                       |
| -------------------------- | ------------------------------------------------------------ | -------------------------- |
| 网络请求执行器Call         | 创建HTTP网络请求                                             | Retrofit默认为OkHttp3.Call |
| 网络请求适配器CallAdapter  | 网络请求执行器Call的适配器，将默认的网络请求执行器OkHttpCall转换成适合被不同平台来调用的网络请求形式 |                            |
| 数据转换器Converter        | 将返回数据解析成需要的数据类型                               |                            |
| 回调执行器CallBackExecutor | 线程切换（子线程 -> 主线程）                                 |                            |



使用Retrofit的使用步骤：

1. 创建Retrofit实例
2. 创建网络请求接口实例并配置网络请求参数
3. 发送网络请求（封装了数据转换，线程切换）
4. 处理服务器返回的数据

```java
//建造者模式			
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl()
    .addConverterFactory(GsonConverterFactory.create())
    .build();

//分为5个步骤
1. new Retrofit
2. .Builder()
3. .baseUrl()
4. .addConverterFactory(GsonConverterFactory.create())
5. .build();
```

#### 步骤一

```java
public final class Retrofit {
  
private final Map<Method, ServiceMethod> serviceMethodCache = new LinkedHashMap<>();
  // 网络请求配置对象（对网络请求接口中方法注解进行解析后得到的对象）
  // 作用：存储网络请求相关的配置，如网络请求的方法、数据转换器、网络请求适配器、网络请求工厂、基地址等
  
  private final HttpUrl baseUrl;
  // 网络请求的url地址

  private final okhttp3.Call.Factory callFactory;
  // 网络请求器的工厂
  // 作用：生产网络请求器（Call）
  // Retrofit是默认使用okhttp
  
   private final List<CallAdapter.Factory> adapterFactories;
  // 网络请求适配器工厂的集合
  // 作用：放置网络请求适配器工厂
  // 网络请求适配器工厂作用：生产网络请求适配器（CallAdapter）
  // 下面会详细说明


  private final List<Converter.Factory> converterFactories;
  // 数据转换器工厂的集合
  // 作用：放置数据转换器工厂
  // 数据转换器工厂作用：生产数据转换器（converter）

  private final Executor callbackExecutor;
  // 回调方法执行器

private final boolean validateEagerly; 
// 标志位
// 作用：是否提前对业务接口中的注解进行验证转换的标志位


<-- Retrofit类的构造函数 -->
Retrofit(okhttp3.Call.Factory callFactory, HttpUrl baseUrl,  
      List<Converter.Factory> converterFactories, List<CallAdapter.Factory> adapterFactories,  
      Executor callbackExecutor, boolean validateEagerly) {  
    this.callFactory = callFactory;  
    this.baseUrl = baseUrl;  
    this.converterFactories = unmodifiableList(converterFactories); 
    this.adapterFactories = unmodifiableList(adapterFactories);   
    // unmodifiableList(list)近似于UnmodifiableList<E>(list)
    // 作用：创建的新对象能够对list数据进行访问，但不可通过该对象对list集合中的元素进行修改
    this.callbackExecutor = callbackExecutor;  
    this.validateEagerly = validateEagerly;  
  ...
  // 仅贴出关键代码
}
```

配置：

1. serviceMethod：包含所有网络请求信息的对象
2. baseUrl：网络请求的url地址
3. callFactory：网络请求工厂
4. adapterFactories：网络请求适配器工厂的集合
5. converterFactories：数据转换器工厂的集合
6. callbackExecutor：回调方法执行器

#### CallAdapter：网络请求执行器Call的适配器

1. Call在Retrofit里默认是OkHttpCall。
2. 在Retrofit中提供四种CallAdapterFactory：ExecutorCallAdapterFactory（默认）、GuavaCallAdapterFactory、Java8CallAdapterFactory、RxJavaCallAdapterFactory。

作用：将默认的网络执行请求器（OkHttpCall转换成适合被不同平台来调用的网络请求执行器的模式）

#### 步骤二

```java
public static final class Builder {
    private Platform platform;
    private okhttp3.Call.Factory callFactory;
    private HttpUrl baseUrl;
    private List<Converter.Factory> converterFactories = new ArrayList<>();
    private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
    private Executor callbackExecutor;
    private boolean validateEagerly;

// 从上面可以发现， Builder类的成员变量与Retrofit类的成员变量是对应的
// 所以Retrofit类的成员变量基本上是通过Builder类进行配置
// 开始看步骤1

<-- 步骤1 -->
// Builder的构造方法（无参）
 public Builder() {
      this(Platform.get());
// 用this调用自己的有参构造方法public Builder(Platform platform) ->>步骤5（看完步骤2、3、4再看）
// 并通过调用Platform.get（）传入了Platform对象
// 继续看Platform.get()方法 ->>步骤2
// 记得最后继续看步骤5的Builder有参构造方法
    }
...
}

<-- 步骤2 -->
class Platform {

  private static final Platform PLATFORM = findPlatform();
  // 将findPlatform()赋给静态变量

  static Platform get() {
    return PLATFORM;    
    // 返回静态变量PLATFORM，即findPlatform() ->>步骤3
  }

<-- 步骤3 -->
private static Platform findPlatform() {
    try {

      Class.forName("android.os.Build");
      // Class.forName(xxx.xx.xx)的作用：要求JVM查找并加载指定的类（即JVM会执行该类的静态代码段）
      if (Build.VERSION.SDK_INT != 0) {
        return new Android(); 
        // 此处表示：如果是Android平台，就创建并返回一个Android对象 ->>步骤4
      }
    } catch (ClassNotFoundException ignored) {
    }

    try {
      // 支持Java平台
      Class.forName("java.util.Optional");
      return new Java8();
    } catch (ClassNotFoundException ignored) {
    }

    try {
      // 支持iOS平台
      Class.forName("org.robovm.apple.foundation.NSObject");
      return new IOS();
    } catch (ClassNotFoundException ignored) {
    }

// 从上面看出：Retrofit2.0支持3个平台：Android平台、Java平台、IOS平台
// 最后返回一个Platform对象（指定了Android平台）给Builder的有参构造方法public Builder(Platform platform)  --> 步骤5
// 说明Builder指定了运行平台为Android
    return new Platform();
  }
...
}

<-- 步骤4 -->
// 用于接收服务器返回数据后进行线程切换在主线程显示结果

static class Android extends Platform {

    @Override
      CallAdapter.Factory defaultCallAdapterFactory(Executor callbackExecutor) {

      return new ExecutorCallAdapterFactory(callbackExecutor);
    // 创建默认的网络请求适配器工厂
    // 该默认工厂生产的 adapter 会使得Call在异步调用时在指定的 Executor 上执行回调
    // 在Retrofit中提供了四种CallAdapterFactory： ExecutorCallAdapterFactory（默认）、GuavaCallAdapterFactory、Java8CallAdapterFactory、RxJavaCallAdapterFactory
    // 采用了策略模式
    
    }

    @Override 
      public Executor defaultCallbackExecutor() {
      // 返回一个默认的回调方法执行器
      // 该执行器作用：切换线程（子->>主线程），并在主线程（UI线程）中执行回调方法
      return new MainThreadExecutor();
    }

    static class MainThreadExecutor implements Executor {
   
      private final Handler handler = new Handler(Looper.getMainLooper());
      // 获取与Android 主线程绑定的Handler 

      @Override 
      public void execute(Runnable r) {
        
        
        handler.post(r);
        // 该Handler是上面获取的与Android 主线程绑定的Handler 
        // 在UI线程进行对网络请求返回数据处理等操作。
      }
    }

// 切换线程的流程：
// 1. 回调ExecutorCallAdapterFactory生成了一个ExecutorCallbackCall对象
//2. 通过调用ExecutorCallbackCall.enqueue(CallBack)从而调用MainThreadExecutor的execute()通过handler切换到主线程
  }

// 下面继续看步骤5的Builder有参构造方法
<-- 步骤5 -->
//  Builder类的构造函数2（有参）
  public  Builder(Platform platform) {

  // 接收Platform对象（Android平台）
      this.platform = platform;

// 通过传入BuiltInConverters()对象配置数据转换器工厂（converterFactories）

// converterFactories是一个存放数据转换器Converter.Factory的数组
// 配置converterFactories即配置里面的数据转换器
      converterFactories.add(new BuiltInConverters());

// BuiltInConverters是一个内置的数据转换器工厂（继承Converter.Factory类）
// new BuiltInConverters()是为了初始化数据转换器
    }
```

Builder设置了默认的：

- 平台类型对象：Android
- 网络请求适配器工厂：CallAdapterFactory，CallAdapter用于对原始Call进行再次封装，如Call\<R>到Observable\<R>
- 数据转换工厂：converterFactory
- 回调执行器：callbackExecutor

这里只是设置了默认值，未真正配置到具体的Retrofit类的成员变量中去。

#### 步骤三

```java
public Builder baseUrl(String baseUrl) {

      // 把String类型的url参数转化为适合OKhttp的HttpUrl类型
      HttpUrl httpUrl = HttpUrl.parse(baseUrl);     

    // 最终返回带httpUrl类型参数的baseUrl（）
    // 下面继续看baseUrl(httpUrl) ->> 步骤2
      return baseUrl(httpUrl);
    }


<-- 步骤2 -->
    public Builder baseUrl(HttpUrl baseUrl) {

      //把URL参数分割成几个路径碎片
      List<String> pathSegments = baseUrl.pathSegments();   

      // 检测最后一个碎片来检查URL参数是不是以"/"结尾
      // 不是就抛出异常    
      if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
        throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
      }     
      this.baseUrl = baseUrl;
      return this;
    }
```

将传入的String类型url转化为适合OkHttp的HttpUrl的url。

#### 步骤四

```java
public final class GsonConverterFactory extends Converter.Factory {

<-- 步骤1 -->
  public static GsonConverterFactory create() {
    // 创建一个Gson对象
    return create(new Gson()); ->>步骤2
  }

<-- 步骤2 -->
  public static GsonConverterFactory create(Gson gson) {
    // 创建了一个含有Gson对象实例的GsonConverterFactory
    return new GsonConverterFactory(gson); ->>步骤3
  }

  private final Gson gson;

<-- 步骤3 -->
  private GsonConverterFactory(Gson gson) {
    if (gson == null) throw new NullPointerException("gson == null");
    this.gson = gson;
  }
```

GsonConverterFactory.creat()是创建了一个含有Gson对象实例的GsonConverterFactory，并返回给`addConverterFactory（）`

```java
// 将上面创建的GsonConverterFactory放入到 converterFactories数组
// 在第二步放入一个内置的数据转换器工厂BuiltInConverters(）后又放入了一个GsonConverterFactory
  public Builder addConverterFactory(Converter.Factory factory) {
      converterFactories.add(checkNotNull(factory, "factory == null"));
      return this;
    }
```

创建一个含有Gson对象实例的GsonConverterFactory并放入到数据转换器工厂converterFactories里。

1. 即Retrofit默认使用Gson进行解析
2. 若使用其他解析方式（如Json、XML或Protocobuf），也可通过自定义数据解析器来实现（必须继承 Converter.Factory）

#### 步骤五

```java
public Retrofit build() {
 
 <--  配置网络请求执行器（callFactory）-->
      okhttp3.Call.Factory callFactory = this.callFactory;
      // 如果没指定，则默认使用okhttp
      // 所以Retrofit默认使用okhttp进行网络请求
      if (callFactory == null) {
        callFactory = new OkHttpClient();
      }

 <--  配置回调方法执行器（callbackExecutor）-->
      Executor callbackExecutor = this.callbackExecutor;
      // 如果没指定，则默认使用Platform检测环境时的默认callbackExecutor
      // 即Android默认的callbackExecutor
      if (callbackExecutor == null) {
        callbackExecutor = platform.defaultCallbackExecutor();
      }

 <--  配置网络请求适配器工厂（CallAdapterFactory）-->
      List<CallAdapter.Factory> adapterFactories = new ArrayList<>(this.adapterFactories);
      // 向该集合中添加了步骤2中创建的CallAdapter.Factory请求适配器（添加在集合器末尾）
      adapterFactories.add(platform.defaultCallAdapterFactory(callbackExecutor));
    // 请求适配器工厂集合存储顺序：自定义1适配器工厂、自定义2适配器工厂...默认适配器工厂（ExecutorCallAdapterFactory）

 <--  配置数据转换器工厂：converterFactory -->
      // 在步骤2中已经添加了内置的数据转换器BuiltInConverters(）（添加到集合器的首位）
      // 在步骤4中又插入了一个Gson的转换器 - GsonConverterFactory（添加到集合器的首二位）
      List<Converter.Factory> converterFactories = new ArrayList<>(this.converterFactories);
      // 数据转换器工厂集合存储的是：默认数据转换器工厂（ BuiltInConverters）、自定义1数据转换器工厂（GsonConverterFactory）、自定义2数据转换器工厂....

// 注：
//1. 获取合适的网络请求适配器和数据转换器都是从adapterFactories和converterFactories集合的首位-末位开始遍历
// 因此集合中的工厂位置越靠前就拥有越高的使用权限

      // 最终返回一个Retrofit的对象，并传入上述已经配置好的成员变量
      return new Retrofit(callFactory, baseUrl, converterFactories, adapterFactories,
          callbackExecutor, validateEagerly);
    }
```

将Retrofit类的所有成员变量都配置完毕，成功创建了Retrofit的实例。





