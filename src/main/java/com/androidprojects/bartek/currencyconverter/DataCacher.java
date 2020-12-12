package com.androidprojects.bartek.currencyconverter;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class DataCacher {

    public String readFile(Context context, String fileName){
        FileInputStream fileInputS = null;
        try {
            fileInputS = context.openFileInput(fileName);
            InputStreamReader reader = new InputStreamReader(fileInputS);
            BufferedReader buffReader = new BufferedReader(reader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while((line = buffReader.readLine()) != null){
                stringBuffer.append(line);
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean fileFound(Context context, String fileName){
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        if (file.exists()){
            return true;
        } else {
            return false;
        }
    }



    public boolean saveFile(Context context, String fileName, String jsonStr){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            if (jsonStr != null){
                fileOutputStream.write(jsonStr.getBytes());
            }
            fileOutputStream.close();
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
