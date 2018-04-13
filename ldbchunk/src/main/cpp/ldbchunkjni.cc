//
// Created by barco on 2018/3/22.
//

#include "ldbchunkjni.h"

extern int register_rbq2012_ldbchunk_DB(JNIEnv *env);

extern int register_rbq2012_ldbchunk_Names(JNIEnv *env);

jint
throwException(JNIEnv *env) {
    const char *exceptionClass;

    exceptionClass = "java/io/IOException";

    jclass clazz = env->FindClass(exceptionClass);
    if (!clazz) {
        LOGE("Can't find exception class %s", exceptionClass);
        return -1;
    }

    return env->ThrowNew(clazz, exceptionClass);
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    register_rbq2012_ldbchunk_DB(env);
    register_rbq2012_ldbchunk_Names(env);

    return JNI_VERSION_1_6;
}
