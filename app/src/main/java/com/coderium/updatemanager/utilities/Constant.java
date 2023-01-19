package com.coderium.updatemanager.utilities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

import com.coderium.updatemanager.R;
import com.coderium.updatemanager.extraClasses.Contributor;

public class Constant {

    public static boolean updateDialogNotVisible = true;
    public static int Selected_APK_Position = 0; // This is pointing to the index of current selected item from the list

    public static List<String> contactDataList = new ArrayList<>(); // This list contains the link of social media account url
    public static List<String> waUpdateManagerList = new ArrayList<>(); // This list contains data related to app like app share description and link etc
    public static List<String> releaseVersionList = new ArrayList<>(); // This list contains the name of latest release versions of apps
    public static List<String> downloadStatsList = new ArrayList<>(); // This list contains the stats of how many times each app downloaded
    public static List<String> apkUrlList = new ArrayList<>(); // This list contains the link of APK Files

    public static void shareApp(Context context){

        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String body = waUpdateManagerList.get(3)+" " + waUpdateManagerList.get(0);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            context.startActivity(Intent.createChooser(intent, "choose one"));
        }catch (Exception ex){
            Toast.makeText(context, context.getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void deleteAPK(Context context , String name){

        File parentDir = new File(String.valueOf(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)));
        File[] files;

        files = parentDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if(file.getName().subSequence(0,2).equals(name.substring(0,2))){
                    try {
                        FileUtils.forceDelete(file);
                        Toast.makeText(context, "Wiped Out Old Version APK !", Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Good Job \uD83D\uDC4D", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void downloadFileFromURL(Context context, String fileURL,String name,String version){

//        System.out.println(FilenameUtils.getBaseName(url.getPath())); // -> file
//        System.out.println(FilenameUtils.getExtension(url.getPath())); // -> xml
//        System.out.println(FilenameUtils.getName(url.getPath())); // -> file.xml

        // Downloading File from Url

        try {

            URL url = new URL(fileURL);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileURL + ""));
            request.setTitle(name+" "+version);
            request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(fileURL));
            request.allowScanningByMediaScanner();
            request.setAllowedOverMetered(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name+" "+version+".apk");
            DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(context, "Downloading "+name+" "+version, Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Great Job \uD83E\uDD19", Toast.LENGTH_SHORT).show();


        } catch (MalformedURLException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public static void vibrator(Context context,int frequency){

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(frequency, VibrationEffect.DEFAULT_AMPLITUDE));
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
