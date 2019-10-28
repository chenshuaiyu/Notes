# Retrofit

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

| 角色                       | 作用                                                         | 备注                                                         |
| -------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 网络请求执行器Call         | 创建HTTP网络请求                                             | Retrofit默认为OkHttp3.Call                                   |
| 网络请求适配器CallAdapter  | 网络请求执行器Call的适配器，将默认的网络请求执行器OkHttpCall转换成适合被不同平台来调用的网络请求形式 | Retrofit支持Android、RxJava、Java8和Guava四个平台：提供四种CallAdapterFactory：ExecutorCallAdapterFactory（Android默认）、GuavaCallAdapterFactory、Java8CallAdapterFactory、RxJava2CallAdapterFactory；<br />网络适配器作用：一开始Retrofit只打算利用OkHttpCall通过ExecutorCallbackCall切换线程，但后来发现使用RxJava更加方便（不需要Handler切换线程）。 |
| 数据转换器Converter        | 将返回数据解析成需要的数据类型                               | 支持XMl、Gson、JSON、protobuf等等                            |
| 回调执行器CallBackExecutor | 线程切换（子线程 -> 主线程）                                 | 将最后OkHttp的请求结果通过callbackExecutor使用Handler异步回调传回主线程 |

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

#### 1.步骤一

```java
public final class Retrofit {
  //网络请求配置对象（对网络接口中方法注解进行解析后得到的对象）
  //作用：存储网络请求相关的配置，如网络请求的方法、数据转换器、网络请求适配器、网络请求工厂、基地址等
  private final Map<Method, ServiceMethod<?>> serviceMethodCache = new ConcurrentHashMap<>();

  //网络请求器的工厂
  //作用：生产网络请求器（Call）
  //Retrofit是默认使用okhttp
  final okhttp3.Call.Factory callFactory;
    
  //网络请求的url地址
  final HttpUrl baseUrl;
    
  //数据转换器工厂的集合
  //作用：放置数据转换器工厂
  //数据转换器工厂作用：生产数据转换器（converter）
  final List<Converter.Factory> converterFactories;
    
  //网络请求适配器工厂的集合
  //作用：放置网络请求适配器工厂
  //网络请求适配器工厂作用：生产网络请求适配器（CallAdapter）
  final List<CallAdapter.Factory> callAdapterFactories;
    
  //回调方法执行器
  final @Nullable Executor callbackExecutor;
    
  //标志位：是否提前对业务接口中的注解进行验证转换的标志位
  final boolean validateEagerly;

  //Retrofit构造函数
  Retrofit(okhttp3.Call.Factory callFactory, HttpUrl baseUrl,
      List<Converter.Factory> converterFactories, List<CallAdapter.Factory> callAdapterFactories,
      @Nullable Executor callbackExecutor, boolean validateEagerly) {
    this.callFactory = callFactory;
    this.baseUrl = baseUrl;
    this.converterFactories = converterFactories; // Copy+unmodifiable at call site.
    this.callAdapterFactories = callAdapterFactories; // Copy+unmodifiable at call site.
    this.callbackExecutor = callbackExecutor;
    this.validateEagerly = validateEagerly;
  }
}
```

配置：

1. serviceMethodCache：包含所有网络请求信息的对象
2. baseUrl：网络请求的url地址
3. callFactory：网络请求工厂
4. callAdapterFactories：网络请求适配器工厂的集合
5. converterFactories：数据转换器工厂的集合
6. callbackExecutor：回调方法执行器

##### CallAdapter：网络请求执行器Call的适配器

1. Call在Retrofit里默认是OkHttpCall。
2. 在Retrofit中提供四种CallAdapterFactory：ExecutorCallAdapterFactory（默认）、GuavaCallAdapterFactory、Java8CallAdapterFactory、RxJava2CallAdapterFactory。

作用：将默认的网络执行请求器（OkHttpCall）转换成适合被不同平台来调用的网络请求执行器的模式。

原因：一开始Retrofit只打算利用OkHttpCall通过ExecutorCallbackCall切换线程，但后来发现RxJava更加方便（不需要Handler来切换线程）。

#### 2.步骤二

```java
public static final class Builder {
  private Platform platform;
  private okhttp3.Call.Factory callFactory;
  private HttpUrl baseUrl;
  private List<Converter.Factory> converterFactories = new ArrayList<>();
  private List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();
  private Executor callbackExecutor;
  private boolean validateEagerly;

  //Builder有参构造方法
  Builder(Platform platform) {
  	this.platform = platform;
  }
    
  //Builder无参构造方法
  public Builder() {
      this(Platform.get());
  }
  ...
}

class Platform {
  private static final Platform PLATFORM = findPlatform();

  static Platform get() {
    return PLATFORM;
  }

  private static Platform findPlatform() {
    try {
      Class.forName("android.os.Build");
      if (Build.VERSION.SDK_INT != 0) {
        return new Android();
      }
    } catch (ClassNotFoundException ignored) {
    }
      
    try {
      Class.forName("java.util.Optional");
      return new Java8();
    } catch (ClassNotFoundException ignored) {
    }
      
    return new Platform();
  }
  ...
}

//用于接收服务器数据后切换回主线程
static class Android extends Platform {

    @Override public Executor defaultCallbackExecutor() {
        //返回主线程执行器：从子线程切换至主线程，并在主线程中执行回调方法
        return new MainThreadExecutor();
    }

    @Override List<? extends CallAdapter.Factory> defaultCallAdapterFactories(
        @Nullable Executor callbackExecutor) {
        if (callbackExecutor == null) throw new AssertionError();
        DefaultCallAdapterFactory executorFactory = new DefaultCallAdapterFactory(callbackExecutor);
        return Build.VERSION.SDK_INT >= 24
            ? asList(CompletableFutureCallAdapterFactory.INSTANCE, executorFactory)
            : singletonList(executorFactory);
    }

    @Override List<? extends Converter.Factory> defaultConverterFactories() {
        return Build.VERSION.SDK_INT >= 24
            ? singletonList(OptionalConverterFactory.INSTANCE)
            : Collections.<Converter.Factory>emptyList();
    }

    static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override public void execute(Runnable r) {
            handler.post(r);
        }
    }
}
```

Builder设置了默认的：

- 平台类型对象：Android
- 网络请求适配器工厂：CallAdapterFactory，CallAdapter用于对原始Call进行再次封装，如Call\<R>到Observable\<R>
- 数据转换工厂：converterFactory
- 回调执行器：callbackExecutor

这里只是设置了默认值，未真正配置到具体的Retrofit类的成员变量中去。

#### 3.步骤三

```java
public Builder baseUrl(String baseUrl) {
    checkNotNull(baseUrl, "baseUrl == null");
    return baseUrl(HttpUrl.get(baseUrl));
}

public Builder baseUrl(HttpUrl baseUrl) {
    checkNotNull(baseUrl, "baseUrl == null");
    List<String> pathSegments = baseUrl.pathSegments();
    if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
        throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
    }
    this.baseUrl = baseUrl;
    return this;
}
```

将传入的String类型url转化为适合OkHttp的HttpUrl的url。

#### 4.步骤四

```java
public final class GsonConverterFactory extends Converter.Factory {

  public static GsonConverterFactory create() {
    return create(new Gson());
  }

  public static GsonConverterFactory create(Gson gson) {
    if (gson == null) throw new NullPointerException("gson == null");
    return new GsonConverterFactory(gson);
  }

  private final Gson gson;

  private GsonConverterFactory(Gson gson) {
    this.gson = gson;
  }
```

```java
public Builder addConverterFactory(Converter.Factory factory) {
    converterFactories.add(checkNotNull(factory, "factory == null"));
    return this;
}
```

创建一个含有Gson对象实例的GsonConverterFactory并放入到数据转换器工厂converterFactories里。

1. 即Retrofit默认使用Gson进行解析
2. 若使用其他解析方式（如Json、XML或Protocobuf），也可通过自定义数据解析器来实现（必须继承 Converter.Factory）

#### 5.步骤五

```java
public Retrofit build() {
    if (baseUrl == null) {
        throw new IllegalStateException("Base URL required.");
    }
	
    okhttp3.Call.Factory callFactory = this.callFactory;
    if (callFactory == null) {
        callFactory = new OkHttpClient();
    }

    Executor callbackExecutor = this.callbackExecutor;
    if (callbackExecutor == null) {
        callbackExecutor = platform.defaultCallbackExecutor();
    }

    List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>(this.callAdapterFactories);
    callAdapterFactories.addAll(platform.defaultCallAdapterFactories(callbackExecutor));

    List<Converter.Factory> converterFactories = new ArrayList<>(
        1 + this.converterFactories.size() + platform.defaultConverterFactoriesSize());

    converterFactories.add(new BuiltInConverters());
    converterFactories.addAll(this.converterFactories);
    converterFactories.addAll(platform.defaultConverterFactories());

    return new Retrofit(callFactory, baseUrl, unmodifiableList(converterFactories),
                        unmodifiableList(callAdapterFactories), callbackExecutor, validateEagerly);
}
```

将Retrofit类的所有成员变量都配置完毕，成功创建了Retrofit的实例。

#### 6.创建Service

```java
public <T> T create(final Class<T> service) {
  Utils.validateServiceInterface(service);
  if (validateEagerly) {
    eagerlyValidateMethods(service);
  }
  return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
      new InvocationHandler() {
        private final Platform platform = Platform.get();
        private final Object[] emptyArgs = new Object[0];

        @Override public @Nullable Object invoke(Object proxy, Method method,
            @Nullable Object[] args) throws Throwable {
          // If the method is a method from Object then defer to normal invocation.
          if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
          }
          if (platform.isDefaultMethod(method)) {
            return platform.invokeDefaultMethod(method, service, proxy, args);
          }
          return loadServiceMethod(method).invoke(args != null ? args : emptyArgs);
        }
      });
}
```

```java
private void eagerlyValidateMethods(Class<?> service) {
  Platform platform = Platform.get();
  for (Method method : service.getDeclaredMethods()) {
      if (!platform.isDefaultMethod(method) && !Modifier.isStatic(method.getModifiers())) {
          loadServiceMethod(method);
      }
  }
}
```





