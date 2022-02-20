package com.codex.updatemanager;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class Constant {


    public static boolean updateDialogNotVisible = true;
    public static boolean dataReady = false;
    public static boolean internetAvailable = false;
    public static boolean privacy = false;
    public static boolean hasStoragePermission = false;
    public static boolean permissionDenied = false;

    public static List<String> contactDataList = new ArrayList<>();
    public static List<String> waUpdateManager = new ArrayList<>();
    public static List<String> installedVersion = new ArrayList<>();
    public static List<String> latestVersion = new ArrayList<>();
    public static List<String> readableVersion = new ArrayList<>();
    public static List<String> downloadStats = new ArrayList<>();
    public static ArrayList<String> apkUrls = new ArrayList<>();

    public static void shareApp(Context context){

        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String body = waUpdateManager.get(3)+" " + waUpdateManager.get(0);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            context.startActivity(Intent.createChooser(intent, "choose one"));
        }catch (Exception ex){
            Toast.makeText(context, context.getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
        }

    }

    // Not using this method currently
    public static boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void deleteAPK(Context context , String name){

        File parentDir = new File(String.valueOf(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS+"/WA Update Manager/")));
        File[] files;
        files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if(file.getName().subSequence(0,2).equals(name.substring(0,2))){
                    try {
                        FileUtils.forceDelete(file);
                        Toast.makeText(context, "Older Version Deleted !", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void downloadFile(Context context,String url,String name,String version){

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(name+"_"+version);
        request.setDescription("Downloading ...!");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS+"/WA Update Manager/",name+"_"+version+".apk");

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Toast.makeText(context, name+"_"+version+" Starts Downloading !", Toast.LENGTH_SHORT).show();
    }


    public static void vibrator(Context context,int frequency){

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(frequency, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(frequency);
        }
    }


    public static String [] title = {
            "Aero WhatsApp"
            ,"FM WhatsApp"
            ,"Fouad WhatsApp"
            ,"GB WhatsApp"
            ,"Yo WhatsApp"
    };

    public static String [] packageName = {
            "com.aero"
            ,"com.fmwhatsapp"
            ,"com.whatsapp"
            ,"com.gbwhatsapp"
            ,"com.yowhatsapp"
    };

    public static String [] description = {
            "Modified WhatsApp client with the most customized UI and a key feature of an SMS bomber !"
            ,"Modified WhatsApp client with the most realistic default UI !"
            ,"Modified WhatsApp client that replaces original WhatsApp with lots of useful features !"
            ,"Modified WhatsApp client with lots of useful features !"
            ,"Modified WhatsApp client with lots of advanced features !"
    };

}
