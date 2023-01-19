package com.coderium.updatemanager.utilities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import static com.coderium.updatemanager.utilities.Constant.contactDataList;

import com.coderium.updatemanager.R;

public class Contact {

    public static Intent facebookIntent(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(0)));
            context.startActivity(intent);
        } catch(Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(1))));
        }
        return null;
    }

    public static void instagramIntent(Context context){

        try {
            Uri uri = Uri.parse(contactDataList.get(1));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.instagram.android");

            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(contactDataList.get(2))));
            }
        }catch (Exception ex){
            Toast.makeText(context, context.getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public static void snapchatIntent(Context context){

        try {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(4)));
                intent.setPackage("com.snapchat.android");
                context.startActivity(intent);
            } catch (Exception e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(4))));
            }
        }catch (Exception ex){
            Toast.makeText(context, context.getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public static void LinkedInIntent(Context context){

        try {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(3)));
                intent.setPackage("com.snapchat.android");
                context.startActivity(intent);
            } catch (Exception e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(3))));
            }
        }catch (Exception ex){
            Toast.makeText(context, context.getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public static void githubIntent(Context context){

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(0)));
            context.startActivity(browserIntent);
        }catch (Exception ex){
            Toast.makeText(context, context.getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public static void telegramIntent(Context context){

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(5)));
            context.startActivity(intent);
        }catch (Exception ex){
            Toast.makeText(context, context.getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public static void websiteIntent(Context context){

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(6)));
            context.startActivity(browserIntent);
        }catch (Exception ex){
            Toast.makeText(context, context.getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public static void twitterIntent(Context context){

        try {
            // get the Twitter app if possible
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(6)));
            context.startActivity(intent);

        } catch (Exception e) {
            // no Twitter app, revert to browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactDataList.get(7)));
            context.startActivity(intent);
        }

    }

    public static void hireIntent(Context context){

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.hire_subject));
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.hire_body));

        try {
            context.startActivity(Intent.createChooser(intent, "send mail"));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "E-mail App Not Found !", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(context, "Unexpected Error !", Toast.LENGTH_SHORT).show();
        }
    }
}
