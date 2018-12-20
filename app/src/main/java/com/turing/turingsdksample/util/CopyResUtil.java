package com.turing.turingsdksample.util;

import android.content.Context;
import android.util.Log;

import com.qdreamer.utils.FileUtils;
import com.qdreamer.utils.ZipUtils;
import com.turing.turingsdksample.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
奇梦的 资源解压
 */
public class CopyResUtil {

    public static void copyRes(Context context,String  mPath) {
        String res = "qvoice";
        String fn;
        InputStream in;
        fn = mPath + res;
        if (!FileUtils.existes(fn)) {
            try {
                fn = mPath + "qvoice.zip";
                in = context.getResources().openRawResource(R.raw.qvoice);
                long t1 = System.currentTimeMillis();
                FileUtils.copyFile2(fn, in);
                in.close();
                Logger.d("CopyResUtil", "复制资源时间：" + (System.currentTimeMillis() - t1));
                ZipUtils.unZip(mPath, fn);
                Logger.d("CopyResUtil", "解压资源时间：" + (System.currentTimeMillis() - t1));
                FileUtils.rmFile(mPath + "qvoice.zip");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
