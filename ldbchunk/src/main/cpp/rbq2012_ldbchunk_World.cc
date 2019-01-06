//
// Created by barco on 2018/3/22.
//

#include <jni.h>
#include <ldbchunkjni.h>
#include <World.h>
#include <qstr.h>

#define PTR_TO_WORLD(x) reinterpret_cast<World *>(x)

static jlong nativeBegin(JNIEnv *env, jclass clazz, jstring dbpath) {
    const char *path = env->GetStringUTFChars(dbpath, 0);
    World *world = new World(path);
    env->ReleaseStringUTFChars(dbpath, path);
    return reinterpret_cast<jlong>(world);
}

static jint nativeOpenDb(JNIEnv *env, jclass clazz, jlong ptr) {
    World *world = PTR_TO_WORLD(ptr);
    return world->openDb();
}

static void nativeRegisterLayers(JNIEnv *env, jclass clazz, jlong ptr, jbyteArray data) {
    World *world = PTR_TO_WORLD(ptr);
    jbyte *buf = env->GetByteArrayElements(data, 0);
    world->setLayers(static_cast<unsigned int>(env->GetArrayLength(data)),
                     reinterpret_cast<unsigned char *>(buf));
    env->ReleaseByteArrayElements(data, buf, JNI_ABORT);
}

static jint nativeGetTile(JNIEnv *env, jclass clazz, jlong ptr, jint x, jint y, jint z, jint dim) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) return world->getTile(x, y, z, dim);
    return 0;
}

static jint nativeGetData(JNIEnv *env, jclass clazz, jlong ptr, jint x, jint y, jint z, jint dim) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) return world->getData(x, y, z, dim);
    return 0;
}

static void
nativeSetTile(JNIEnv *env, jclass clazz, jlong ptr, jint x, jint y, jint z, jint dim, jint id,
              jint data) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) world->setTile(x, y, z, dim, id, data);
}

static void
nativeSetData(JNIEnv *env, jclass clazz, jlong ptr, jint x, jint y, jint z, jint dim, jint data) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) world->setData(x, y, z, dim, data);
}

static void
nativeSetMaxChunksCount(JNIEnv *env, jclass clazz, jlong ptr, jint limit) {
    World *world = PTR_TO_WORLD(ptr);
    world->setMaxChunksCount(limit);
}

static void nativeCloseDb(JNIEnv *env, jclass clazz, jlong ptr) {
    World *world = PTR_TO_WORLD(ptr);
    world->closeDb();
}

static void nativeEnd(JNIEnv *env, jclass clazz, jlong ptr) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) {
        delete world;
        LOGE("Database closed normally.");
    } else {
        LOGE("Warning: multiple closing database.");
    }
}

static void nativeTest(JNIEnv *env, jclass clazz, jlong ptr) {
    World *world = PTR_TO_WORLD(ptr);
    LOGE("Running test.");
    const char *result = world->test();
    LOGE("%s", result);
    LOGE("Test done.");
}

static void nativeChflat(JNIEnv *env, jclass clazz, jlong ptr, jbyteArray bnew) {
    World *world = PTR_TO_WORLD(ptr);
    jbyte *buf = env->GetByteArrayElements(bnew, 0);
    world->changeFlatLayers(env->GetArrayLength(bnew), (unsigned char *) buf);
    env->ReleaseByteArrayElements(bnew, buf, JNI_ABORT);
}

static jbyteArray nativeGetRaw(JNIEnv *env, jclass clazz, jlong ptr, jbyteArray key) {
    World *world = PTR_TO_WORLD(ptr);
    jbyte *buf = env->GetByteArrayElements(key, 0);
    qstr k{(unsigned int) env->GetArrayLength(key), (char *) buf};
    qstr v = world->getRaw(k);
    jbyteArray jba = env->NewByteArray(v.length);
    env->SetByteArrayRegion(jba, 0, v.length, (jbyte *) v.str);
    delete v.str;
    env->ReleaseByteArrayElements(key, buf, JNI_ABORT);
    return jba;
}

static void
nativePutRaw(JNIEnv *env, jclass clazz, jlong ptr, jbyteArray key, jbyteArray value) {
    World *world = PTR_TO_WORLD(ptr);
    jbyte *kb, *vb;
    kb = env->GetByteArrayElements(key, 0);
    vb = env->GetByteArrayElements(value, 0);
    qstr k{(unsigned int) env->GetArrayLength(key), (char *) kb};
    qstr v{(unsigned int) env->GetArrayLength(value),
           (char *) vb};
    world->putRaw(k, v);
    env->ReleaseByteArrayElements(key, kb, JNI_ABORT);
    env->ReleaseByteArrayElements(value, vb, JNI_ABORT);
}

static jlong nativeIterator(JNIEnv *env, jclass clazz, jlong ptr) {
    World *db = reinterpret_cast<World *>(ptr);
    return reinterpret_cast<jlong>(db->iterator());
}

static JNINativeMethod sMethods[] =
    {
        {"nativeBegin",             "(Ljava/lang/String;)J", (void *) nativeBegin},
        {"nativeOpenDb",            "(J)I",                  (void *) nativeOpenDb},
        {"nativeRegisterLayers",    "(J[B)V",                (void *) nativeRegisterLayers},
        {"nativeGetTile",           "(JIIII)I",              (void *) nativeGetTile},
        {"nativeGetData",           "(JIIII)I",              (void *) nativeGetData},
        {"nativeSetTile",           "(JIIIIII)V",            (void *) nativeSetTile},
        {"nativeSetData",           "(JIIIII)V",             (void *) nativeSetData},
        {"nativeSetMaxChunksCount", "(JI)V",                 (void *) nativeSetMaxChunksCount},
        {"nativeCloseDb",           "(J)V",                  (void *) nativeCloseDb},
        {"nativeEnd",               "(J)V",                  (void *) nativeEnd},
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
