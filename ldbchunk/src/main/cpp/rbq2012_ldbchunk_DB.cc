//
// Created by barco on 2018/3/22.
//

#include <jni.h>
#include <ldbchunkjni.h>
#include <SavDb.h>

static jlong nativeOpen(JNIEnv *env, jclass clazz, jstring dbpath) {
    const char *path = env->GetStringUTFChars(dbpath, 0);
    SavDb *db = new SavDb(path);
    env->ReleaseStringUTFChars(dbpath, path);
    return reinterpret_cast<jlong>(db);
}

static void nativeRegisterLayers(JNIEnv *env, jclass clazz, jlong ptr, jbyteArray data) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    db->setLayers(env->GetArrayLength(data), (unsigned char *) env->GetByteArrayElements(data, 0));
}

static jint nativeGetTile(JNIEnv *env, jclass clazz, jlong ptr, jint x, jint y, jint z, jint dim) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    if (db != nullptr) return db->getTile(x, y, z, dim);
    return 0;
}

static jint nativeGetData(JNIEnv *env, jclass clazz, jlong ptr, jint x, jint y, jint z, jint dim) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    if (db != nullptr) return db->getData(x, y, z, dim);
    return 0;
}

static void
nativeSetTile(JNIEnv *env, jclass clazz, jlong ptr, jint x, jint y, jint z, jint dim, jint id,
              jint data) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    if (db != nullptr) db->setTile(x, y, z, dim, id, data);
}

static void
nativeSetData(JNIEnv *env, jclass clazz, jlong ptr, jint x, jint y, jint z, jint dim, jint data) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    if (db != nullptr) db->setData(x, y, z, dim, data);
}

static void
nativeSetMaxChunksCount(JNIEnv *env, jclass clazz, jlong ptr, jint limit) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    db->setMaxChunksCount(limit);
}

static void nativeClose(JNIEnv *env, jclass clazz, jlong ptr) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    if (db != nullptr) {
        delete db;
        LOGE("Database closed normally.");
    } else {
        LOGE("Warning: multiple closing database.");
    }
}

static void nativeTest(JNIEnv *env, jclass clazz, jlong ptr) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    LOGE("Running test.");
    const char *result = db->test();
    LOGE("%s", result);
    LOGE("Test done.");
}

static void nativeChflat(JNIEnv *env, jclass clazz, jlong ptr, jbyteArray bnew) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
};

static JNINativeMethod sMethods[] =
    {
        {"nativeOpen",              "(Ljava/lang/String;)J", (void *) nativeOpen},
        {"nativeRegisterLayers",    "(J[B)V",                (void *) nativeRegisterLayers},
        {"nativeGetTile",           "(JIIII)I",              (void *) nativeGetTile},
        {"nativeGetData",           "(JIIII)I",              (void *) nativeGetData},
        {"nativeSetTile",           "(JIIIIII)V",            (void *) nativeSetTile},
        {"nativeSetData",           "(JIIIII)V",             (void *) nativeSetData},
        {"nativeSetMaxChunksCount", "(JI)V",                 (void *) nativeSetMaxChunksCount},
        {"nativeClose",             "(J)V",                  (void *) nativeClose},
        {"nativeTest",              "(J)V",                  (void *) nativeTest},
        {"nativeChflat",            "(J[B)V",                (void *) nativeChflat}
    };

int register_rbq2012_ldbchunk_DB(JNIEnv *env) {
    jclass clazz = env->FindClass("rbq2012/ldbchunk/DB");
    if (!clazz) {
        LOGE("Can't find class rbq2012.ldbchunk.DB");
        return 0;
    }

    return env->RegisterNatives(clazz, sMethods, NELEM(sMethods));
}
