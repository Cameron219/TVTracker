package uk.ac.abertay.tvtracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler {
    private static final String FOLDER_NAME = "TVTracker";
    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FOLDER_NAME;

    public static void save_to_external_storage(Bitmap bitmap, String slug) {
        check_folders();
        File file = new File(path + "/poster", slug + ".png");
        if(!file.exists()) {
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
        Log.d("BP", "Saved " + slug + " to " + path);
    }

    public static Bitmap load_from_external_storage(String name) {
        String b_path = path + "/" + name;
        File file = new File(b_path);

        if (file.exists()) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bitmap = BitmapFactory.decodeFile(b_path, options);

            return BitmapFactory.decodeFile(b_path);
        } else return null;
    }

    private static void check_folders() {
        File root_folder = new File(path);
        if(!root_folder.isDirectory()) {
            if(root_folder.mkdir()) Log.d("FILE", "mkdir: " + root_folder.getAbsolutePath());
            else Log.d("FILE", "!mkdir: " + root_folder.getAbsolutePath());
        }

        File poster_folder = new File(path + "/poster");
        if(!poster_folder.isDirectory()) {
            if(poster_folder.mkdir()) Log.d("FILE", "mkdir: " + poster_folder.getAbsolutePath());
            else Log.d("FILE", "!mkdir: " + poster_folder.getAbsolutePath());
        }
    }
}
