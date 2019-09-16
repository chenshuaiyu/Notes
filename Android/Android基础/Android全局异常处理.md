# 全局异常处理

### CrashHandle

```java
public class CrashHandle implements Thread.UncaughtExceptionHandler {	
    private static final String TAG = "CrashHandle";
    private static final boolean DEBUG = true;
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".trace";

    private String mPath;
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;


    public CrashHandle() {
    }

    public static CrashHandle getInstance() {
        return SingleHolder.sInstance;
    }

    private static class SingleHolder {
        private static final CrashHandle sInstance = new CrashHandle();
    }

    public void init(Context context) {
        mContext = context;
        mPath = Environment.getExternalStorageDirectory().getPath()
                + "/"
                + mContext.getResources().getString(R.string.app_name)
                + "/log/";
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当程序有未被捕获的异常时，系统将自动调用该方法
     *
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            //导出异常信息到SD卡
            dumpExceptionToSDCard(e);
            //上传异常信息到服务器
            uploadExceptionToServer();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.d(TAG, "sdcard unmounted, skip dump exception");
                return;
            }
        }

        File dir = new File(mPath);
        if (!dir.exists())
            dir.mkdirs();
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date(current));
        File file = new File(mPath + FILE_NAME + time + FILE_NAME_SUFFIX);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace();
            pw.close();
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);

        //应用版本号和版本名称
        pw.println("App Version：" + pi.versionCode + "_" + pi.versionCode);

        //Android 版本号
        pw.println("OS Version：" + Build.VERSION.RELEASE + "_" + SDK_INT);

        //手机制造商
        pw.println("Vendor: " + Build.MODEL);

        //CPU架构
        pw.println("CPU ABI: " + Build.CPU_ABI);
    }

    private void uploadExceptionToServer() {
        //Upload Exception Message To Web Server
    }
}
```

```java
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        //初始化
        CrashHandle.getInstance().init(mContext);
    }

    public static Context getInstance() {
        return mContext;
    }
}
```

