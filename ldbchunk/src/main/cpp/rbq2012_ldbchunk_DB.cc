//
// Created by barco on 2018/3/22.
//

#include <jni.h>
#include <ldbchunkjni.h>
#include <SavDb.h>
#include <qstr.h>

static jlong nativeOpen(JNIEnv *env, jclass clazz, jstring dbpath) {
    const char *path = env->GetStringUTFChars(dbpath, 0);
    SavDb *db = new SavDb(path);
    env->ReleaseStringUTFChars(dbpath, path);
    return reinterpret_cast<jlong>(db);
}

static void nativeRegisterLayers(JNIEnv *env, jclass clazz, jlong ptr, jbyteArray data) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    jbyte *buf = env->GetByteArrayElements(data, 0);
    db->setLayers(static_cast<unsigned int>(env->GetArrayLength(data)),
                  reinterpret_cast<unsigned char *>(buf));
    env->ReleaseByteArrayElements(data, buf, JNI_ABORT);
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
    jbyte *buf = env->GetByteArrayElements(bnew, 0);
    db->changeFlatLayers(env->GetArrayLength(bnew), (unsigned char *) buf);
    env->ReleaseByteArrayElements(bnew, buf, JNI_ABORT);
}

static jbyteArray nativeGetRaw(JNIEnv *env, jclass clazz, jlong ptr, jbyteArray key) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    jbyte *buf = env->GetByteArrayElements(key, 0);
    qstr k{(unsigned int) env->GetArrayLength(key), (char *) buf};
    qstr v = db->getRaw(k);
    jbyteArray jba = env->NewByteArray(v.length);
    env->SetByteArrayRegion(jba, 0, v.length, (jbyte *) v.str);
    delete v.str;
    env->ReleaseByteArrayElements(key, buf, JNI_ABORT);
    return jba;
}

static void
nativePutRaw(JNIEnv *env, jclass clazz, jlong ptr, jbyteArray key, jbyteArray value) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    jbyte *kb, *vb;
    kb = env->GetByteArrayElements(key, 0);
    vb = env->GetByteArrayElements(value, 0);
    qstr k{(unsigned int) env->GetArrayLength(key), (char *) kb};
    qstr v{(unsigned int) env->GetArrayLength(value),
           (char *) vb};
    db->putRaw(k, v);
    env->ReleaseByteArrayElements(key, kb, JNI_ABORT);
    env->ReleaseByteArrayElements(value, vb, JNI_ABORT);
}

static jlong nativeIterator(JNIEnv *env, jclass clazz, jlong ptr) {
    SavDb *db = reinterpret_cast<SavDb *>(ptr);
    return reinterpret_cast<jlong>(db->iterator());
}

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
        {"nativeChflat",            "(J[B)V",                (void *) nativeChflat},
        {"nativeIterator",          "(J)J",                  (void *) nativeIterator},
        {"nativeGetRaw",            "(J[B)[B",               (void *) nativeGetRaw},
        {"nativePutRaw",            "(J[B[B)V",              (void *) nativePutRaw}
    };

int register_rbq2012_ldbchunk_DB(JNIEnv *env) {
    jclass clazz = env->FindClass("rbq2012/ldbchunk/DB");
    if (!clazz) {
        LOGE("Can't find class rbq2012.ldbchunk.DB");
        return 0;
    }

    return env->RegisterNatives(clazz, sMethods, NELEM(sMethods));
}
