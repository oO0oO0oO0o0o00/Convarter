//
// Created by barco on 2018/3/30.
//

#include <qstr.h>
#include <string.h>
#include <ldbchunkjni.h>
#include "BlockNames.h"

char BlockNames::names[256][32] = {0};

unsigned char BlockNames::resolve(qstr name) {
    if (name.str[9] == ':') {
        name.str += 10;
        name.length -= 10;
    }
    for (int i = 0; i < 256; i++) {
        char *nam = names[i];
        if (static_cast<char>(0xff) != nam[31])continue;
        if (memcmp(nam, name.str, name.length) != 0)continue;
        return i;
    }
    return 0;
}
