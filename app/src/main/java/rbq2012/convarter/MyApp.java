package rbq2012.convarter;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 喵喵喵 on 2018/3/1.
 */

public class MyApp extends Application implements Thread.UncaughtExceptionHandler {

    static private String LOG_FILE = "log_convarter.txt";
    private Thread.UncaughtExceptionHandler def_han = null;

    public MyApp() {
        def_han = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    static private File getCrashDir(File filesDir) {
        return new File(filesDir, "crash");
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        File dir = getCrashDir(getFilesDir());
        dir.mkdirs();
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(cDate);
        File logfile = new File(dir, "crash_" + fDate + ".txt");
        try {
            FileWriter fw = new FileWriter(logfile, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.write("====Exception====\n");
            Date date = new Date(System.currentTimeMillis());
            pw.write(date.toString());
            throwable.printStackTrace(pw);
            pw.close();
        } catch (Throwable th) {
        }
        throwable.printStackTrace();
        System.exit(-1);
    }
}
