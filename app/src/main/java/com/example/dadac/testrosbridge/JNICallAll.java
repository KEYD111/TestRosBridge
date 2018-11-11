package com.example.dadac.testrosbridge;

/**
 * @ Create by dadac on 2018/10/8.
 * @Function:   存放所有的 JNI 调用的文件
 * @Return:
 */
public class JNICallAll {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-test");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();



}
