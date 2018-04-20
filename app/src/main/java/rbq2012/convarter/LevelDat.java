package rbq2012.convarter;

import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteOrder;

/**
 * Created by barco on 2018/3/15.
 */

public final class LevelDat {

    private File file;
    private CompoundTag root;
    private byte[] first8bytes;

    public LevelDat(File file) {
        this.file = file;
        root = null;
        first8bytes = new byte[8];
    }

    public boolean load() {
        FileInputStream fis;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            bis = new BufferedInputStream(fis);
            bis.read(first8bytes, 0, 8);
            NBTInputStream nis = new NBTInputStream(bis, false, ByteOrder.LITTLE_ENDIAN);
            root = (CompoundTag) nis.readTag();
            bis.close();
            return true;
        } catch (Exception e) {
            try {
                bis.close();
            } catch (Exception but_why) {
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        FileOutputStream fos;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            fos.write(first8bytes, 0, 8);
            NBTOutputStream nos = new NBTOutputStream(bos, false, ByteOrder.LITTLE_ENDIAN);
            nos.writeTag(root);
            bos.close();
            return true;
        } catch (Exception e) {
            try {
                bos.close();
            } catch (Exception why_could) {
            }
            e.printStackTrace();
            return false;
        }
    }

    public CompoundTag getRoot() {
        return root;
    }

}
