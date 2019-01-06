//
// Created by barco on 2018/3/29.
//

#include <jni.h>
#include <ldbchunkjni.h>
#include <World.h>
#include <BlockNames.h>

static void
registerBlockName(JNIEnv *env, jclass clazz, jstring jname, jbyte jid) {
    const char *name = env->GetStringUTFChars(jname, 0);
    char *entry = BlockNames::names[(unsigned char) jid];
    for (int i = 0;; i++) {
        char ch = name[i];
        if (ch == '\0') {
            entry[30] = i;
            entry[31] = 0xff;
            break;
        }
        entry[i] = name[i];
    }
    env->ReleaseStringUTFChars(jname, name);
}

static JNINativeMethod sMethods[] =
        {
                {"registerBlockName", "(Ljava/lang/String;B)V", (void *) registerBlockName}
        };

int register_rbq2012_ldbchunk_Names(JNIEnv *env) {
    jclass clazz = env->FindClass("rbq2012/ldbchunk/Names");
    if (!clazz) {
        LOGE("Can't find class rbq2012.ldbchunk.Names");
        return 0;
    }

    return env->RegisterNatives(clazz, sMethods, NELEM(sMethods));
}
