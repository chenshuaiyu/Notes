# HandleThread

经常执行耗时操作，很消耗性能，

1. 使用线程池
2. 使用HandleThread

### 使用场景

```java
//1.创建HandleThread的实例对象，参数为线程名
HandlerThread handlerThread = new HandlerThread("myHandlerThread");
//2.启动HandleThread线程
handlerThread。start();
//3.绑定HandleThread和Handle
handlerThread = new Handler(handlerThread.getLooper()) {
    @Override
    public void handleMessage(Message msg) {
        checkForUpdate();
        if(isUpdate){
            mThreadHandler.sendEmptyMessage(MSG_UPDATE_INFO);
        }
    }
};
```

