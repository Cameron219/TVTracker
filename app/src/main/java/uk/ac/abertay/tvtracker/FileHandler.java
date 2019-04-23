package uk.ac.abertay.tvtracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler {
    private static final String FOLDER_NAME = "TVTracker";
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FOLDER_NAME + "/";

    public static void save_to_external_storage(Bitmap bitmap, String name) {
        //check_folders();
        File file = new File(path, name);
        File folder = file.getParentFile();
        if(!folder.isDirectory()) folder.mkdirs();
        if(file.exists()) file.delete();
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap load_from_external_storage(String name) {
        String b_path = path + "/" + name;
        File file = new File(b_path);

        if (file.exists()) {
            return BitmapFactory.decodeFile(b_path);
        } else return null;
    }
}
