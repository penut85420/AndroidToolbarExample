#include "jni.h"
#include <cstring>

bool checkExc(JNIEnv *env) {
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe(); // writes to logcat
        env->ExceptionClear();
        return true;
    }
    return false;
}

extern "C"
JNIEXPORT jstring JNICALL Java_org_example_test_toolbarexample_MainActivity_hello(
        JNIEnv *env, __unused jobject thiz, jstring string
) {
    const char *name = env->GetStringUTFChars(string, nullptr);
    char msg[60] = "Hello ";
    jstring result;

    strcat(msg, name);
    env->ReleaseStringUTFChars(string, name);
    result = env->NewStringUTF(msg);
    checkExc(env);
    return result;
}
