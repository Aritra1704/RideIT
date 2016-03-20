package com.example.arpaul.rideit.Utilities;

import android.content.Context;

import com.example.arpaul.rideit.Common.AppConstant;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by ARPaul on 19-03-2016.
 */
public class FileUtils {
    public void writeToFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){
        File file = new File(AppConstant.FOLDER_Directory,AppConstant.FOLDER_PATH);
        if(!file.exists()){
            file.mkdir();
        }

        try{
            File gpxfile = new File(file, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();

        }catch (Exception e){

        }
    }
}
