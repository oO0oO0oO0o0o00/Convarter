//
// Created by barco on 2018/3/22.
//

#include <jni.h>
#include <ldbchunkjni.h>
#include <World.h>
#include <qstr.h>
#include <macros.h>

#define PTR_TO_WORLD(x) reinterpret_cast<World *>(x)

static jlong nativeBegin(JNIEnv *env, jclass clazz UNUSED, jstring dbpath) {
    const char *path = env->GetStringUTFChars(dbpath, 0);
    World *world = new World(path);
    env->ReleaseStringUTFChars(dbpath, path);
    return reinterpret_cast<jlong>(world);
}

static jint nativeOpenDb(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    World *world = PTR_TO_WORLD(ptr);
    return world->openDb();
}

static void nativeRegisterLayers(JNIEnv *env, jclass clazz UNUSED, jlong ptr, jbyteArray data) {
    World *world = PTR_TO_WORLD(ptr);
    jbyte *buf = env->GetByteArrayElements(data, 0);
    world->setLayers(static_cast<unsigned int>(env->GetArrayLength(data)),
                     reinterpret_cast<unsigned char *>(buf));
    env->ReleaseByteArrayElements(data, buf, JNI_ABORT);
}

static jint
nativeGetTile(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z,
              jint dim) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) return world->getTile(x, y, z, dim);
    return 0;
}

static jint
nativeGetData(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z,
              jint dim) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) return world->getData(x, y, z, dim);
    return 0;
}

static void
nativeSetTile(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z, jint dim,
              jint id,
              jint data) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr)
        world->setTile(x, y, z, dim, static_cast<byte>(id), static_cast<byte>(data));
}

static void
nativeSetBlock(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z, jint dim,
               jint block) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) world->setBlock(x, y, z, dim, static_cast<uint16_t>(block));
}

static jint
nativeGetBlock(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z,
               jint dim) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) return world->getBlock(x, y, z, dim);
    return 0;
}

static void
nativeSetBlock3(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z,
                jint dim, jint layer,
                jint block) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) world->setBlock3(x, y, z, dim, layer, static_cast<uint16_t>(block));
}

static jint
nativeGetBlock3(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint x, jint y, jint z,
                jint dim,
                jint layer) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) return world->getBlock3(x, y, z, dim, layer);
    return 0;
}

static jint
nativeSpecialOperation(JNIEnv *env, jclass clazz UNUSED, jlong ptr, jint opcode, jintArray args) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) {
        jint *buf = env->GetIntArrayElements(args, 0);
        jint ret = world->specialOperation(opcode, buf);
        env->ReleaseIntArrayElements(args, buf, JNI_ABORT);
        return ret;
    }
    return 0;
}

static void
nativeSetMaxChunksCount(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr, jint limit) {
    World *world = PTR_TO_WORLD(ptr);
    world->setMaxChunksCount(static_cast<uint16_t>(limit));
}

static void nativeCloseDb(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    World *world = PTR_TO_WORLD(ptr);
    world->closeDb();
}

static void nativeEnd(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    World *world = PTR_TO_WORLD(ptr);
    if (world != nullptr) {
        delete world;
        LOGE("Database closed normally.");
    } else {
        LOGE("Warning: multiple closing database.");
    }
}

static void nativeTest(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
    World *world = PTR_TO_WORLD(ptr);
    LOGE("Running test.");
    const char *result = world->test();
    LOGE("%s", result);
    LOGE("Test done.");
}

static void nativeChflat(JNIEnv *env, jclass clazz UNUSED, jlong ptr, jbyteArray bnew) {
    World *world = PTR_TO_WORLD(ptr);
    jbyte *buf = env->GetByteArrayElements(bnew, 0);
    world->changeFlatLayers(static_cast<unsigned int>(env->GetArrayLength(bnew)),
                            (unsigned char *) buf);
    env->ReleaseByteArrayElements(bnew, buf, JNI_ABORT);
}

static jbyteArray nativeGetRaw(JNIEnv *env, jclass clazz UNUSED, jlong ptr, jbyteArray key) {
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
nativePutRaw(JNIEnv *env, jclass clazz UNUSED, jlong ptr, jbyteArray key, jbyteArray value) {
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

static jlong nativeIterator(JNIEnv *env UNUSED, jclass clazz UNUSED, jlong ptr) {
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
        {"nativeSetBlock",          "(JIIIII)V",             (void *) nativeSetBlock},
        {"nativeGetBlock3",         "(JIIIII)I",             (void *) nativeGetBlock},
        {"nativeSetBlock3",         "(JIIIIII)V",            (void *) nativeSetBlock3},
        {"nativeGetBlock",          "(JIIII)I",              (void *) nativeGetBlock3},
        {"nativeSpecialOperation",  "(JI[I)I",               (void *) nativeSpecialOperation},
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
