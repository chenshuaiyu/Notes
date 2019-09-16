# Android权限

### 1.所有权限

[Android所有权限](https://developer.android.google.cn/reference/android/Manifest.permission)

### 2.危险权限

|  权限组名  |         权限名         |
| :--------: | :--------------------: |
|  CALENDAR  |     READ_CALENDAR      |
|            |     WRITE_CALENDAR     |
|   CAMERA   |         CAMERA         |
|  CONTACTS  |     READ_CONTACTS      |
|            |     WRITE_CONTACTS     |
|            |      GET_ACCOUNTS      |
|  LOCATION  |  ACCESS_FINE_LOCATION  |
|            | ACCESS_COARSE_LOCATION |
| MICROPHONE |      RECORD_AUDIO      |
|   PHONE    |    READ_PHONE_STATE    |
|            |       CALL_PHONE       |
|            |     READ_CALL_LOG      |
|            |     WRITE_CALL_LOG     |
|            |     ADD_VOICEMAIL      |
|            |        USE_SIP         |
|            | PROCESS_OUTGOING_CALLS |
|  SENSORS   |      BODY_SENSORS      |
|    SMS     |        SEND_SMS        |
|            |      RECEIVE_SMS       |
|            |        READ_SMS        |
|            |    RECEIVE_WAP_PUSH    |
|            |      RECEIVE_MMS       |
|  STORAGE   | READ_EXTERNAL_STORAGE  |
|            | WRITE_EXTERNAL_STORAGE |

**注意**：在进行运行时权限处理时使用的是权限名，一旦同意授权之后，那么该权限所对应的权限组中所有的其他权限也会同时被授权。

### 3.封装权限工具类

`RequestPermissions`

```java
public class RequestPermissions {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static PermissionListener mlistener;

    public static void requestPermissions(Activity activity, String[] permissions,
                                          PermissionListener listener) {
        if (null == activity) {
            return;
        }
        mlistener = listener;
        List<String> permissionList = new ArrayList<>();

        //检查没有允许的权限
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        //申请权限
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), PERMISSION_REQUEST_CODE);
        } else {
            //所有权限已允许，回调权限结果
            listener.onAllGranted();
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                  @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    //统计被允许的和被拒绝的权限

                    //被允许的权限
                    List<String> grantedPermissions = new ArrayList<>();
                    //被拒绝的权限
                    List<String> deniedPermissions = new ArrayList<>();

                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        } else {
                            grantedPermissions.add(permission);
                        }
                    }
                    //回调权限结果
                    if (deniedPermissions.isEmpty()) {
                        mlistener.onAllGranted();
                    } else {
                        mlistener.onGranted(grantedPermissions);
                        mlistener.onDenied(deniedPermissions);
                    }
                }
                break;
            default:
                break;
        }
    }

    interface PermissionListener {

        //所有权限已准许
        void onAllGranted();

        //权限已准许
        void onGranted(List<String> grantedPermissions);

        //权限被拒绝
        void onDenied(List<String> deniedPermissions);
    }
}
```

