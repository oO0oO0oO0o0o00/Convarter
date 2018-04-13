//
// Created by barco on 2018/3/22.
//

#ifndef CONVARTER_LDBCHUNKJNI_H
#define CONVARTER_LDBCHUNKJNI_H

#include <jni.h>
#include <android/log.h>
#include "leveldb/status.h"

#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#define  LOG_TAG    "LdbChunk"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define LOGG(x, ...) __android_log_print(ANDROID_LOG_ERROR,x,__VA_ARGS__)

jint throwException(JNIEnv *env);

#endif //CONVARTER_LDBCHUNKJNI_H
