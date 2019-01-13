//
// Created by barco on 2018/3/29.
//

#ifndef CONVARTER_BLOCKNAMES_H
#define CONVARTER_BLOCKNAMES_H

class BlockNames {
private:
    static bool meow;

    static char names[256][32];

    static void fryCat();

public:

    static char* get(int index);

    static unsigned char resolve(qstr name);
};

#endif //CONVARTER_BLOCKNAMES_H
