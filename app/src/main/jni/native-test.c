#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_dadac_testrosbridge_JNICallAll_stringFromJNI(JNIEnv *env, jobject instance) {

    char *a = "wen";

    return (*env)->NewStringUTF(env, a);
}