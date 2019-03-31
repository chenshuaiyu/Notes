# Retrofit

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

Api api = retrofit.create(Api.class);//代理对象

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

