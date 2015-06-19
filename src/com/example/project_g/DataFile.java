package com.example.project_g;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class DataFile {
    private File dir;
    
    public DataFile(Context context) {
        String path = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            path = Environment.getExternalStorageDirectory() + "/BluetoothLocation/";
        }else{
            File sdcard0 = new File("/storage/sdcard0");
            if(sdcard0.exists()) {
                path = "/storage/sdcard0/BluetoothLocation/";
            } else {
                Toast.makeText(context, "Cannot detect SD card!", Toast.LENGTH_SHORT).show();
            }
        }
        if(path != null) {
            dir = new File(path);
            if(!dir.exists()) {
                dir.mkdir();
            }
        }
    }

    public File save(String write_str) throws FileNotFoundException, IOException, Exception {
        if(dir == null) {
            throw new Exception("File not saved due to invalid SD card!");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(new Date());
        File file = new File(dir, date + ".txt");
        FileOutputStream fout = new FileOutputStream(file, false);
        byte [] bytes = write_str.getBytes();
        fout.write(bytes);
        fout.close();
        return file;
    }
    
    public File[] list() throws FileNotFoundException {
        if(dir != null) {
            return dir.listFiles();
        } else {
            throw new FileNotFoundException("Data folder not found!");
        }
    }
    
    public String read(File file) throws IOException{
        String res = "";
        try{
            FileInputStream fin = new FileInputStream(file);
            int length = fin.available();
            byte [] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    } 
}
