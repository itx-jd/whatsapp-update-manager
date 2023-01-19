package com.coderium.updatemanager.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;
import static com.coderium.updatemanager.utilities.Constant.Selected_APK_Position;
import static com.coderium.updatemanager.utilities.Constant.downloadFileFromURL;
import static com.coderium.updatemanager.utilities.Constant.downloadStatsList;
import static com.coderium.updatemanager.utilities.Constant.apkUrlList;
import static com.coderium.updatemanager.utilities.Constant.deleteAPK;
import static com.coderium.updatemanager.utilities.Constant.description;
import static com.coderium.updatemanager.utilities.Constant.isNetworkAvailable;
import static com.coderium.updatemanager.utilities.Constant.packageName;
import static com.coderium.updatemanager.utilities.Constant.releaseVersionList;
import static com.coderium.updatemanager.utilities.Constant.title;
import static com.coderium.updatemanager.utilities.Constant.vibrator;

import android.widget.Toast;

import com.coderium.updatemanager.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class APKAdapter extends RecyclerView.Adapter<APKAdapter.recycleViewHolder> {

    Context context;
    Activity activity;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public APKAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public recycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycle_item,parent,false);
        return new recycleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull recycleViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.title.setText(title[position]);
        holder.description.setText(description[position]);
        holder.tv_package.setText(packageName[position]);

        holder.layout_download.setVisibility(View.GONE);
        holder.layout_check.setVisibility(View.VISIBLE);

        if(!releaseVersionList.isEmpty()){

            holder.layout_check.setVisibility(View.GONE);
            holder.layout_download.setVisibility(View.VISIBLE);

            holder.icon.setImageResource(R.drawable.ic_baseline_download_24);
            holder.version.setText("Download ("+ releaseVersionList.get(position)+")");

            holder.layout_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Selected_APK_Position = position;
                    download();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    public static class recycleViewHolder extends RecyclerView.ViewHolder {

        TextView title,description,version,tv_package;
        ImageView icon;
        LinearLayout layout_download,layout_check;


        public recycleViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_icon);
            tv_package = itemView.findViewById(R.id.tv_package);
            title = itemView.findViewById(R.id.title);
            version = itemView.findViewById(R.id.tv_version);
            description = itemView.findViewById(R.id.description);
            layout_download = itemView.findViewById(R.id.layout_download);
            layout_check = itemView.findViewById(R.id.layout_check);
        }
    }

    public void download(){

        vibrator(context, 60);

        if(isNetworkAvailable(context)){

            Dexter.withContext(context).withPermissions(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                if(getSwitchState() == 1){
                                    deleteAPK(context,title[Selected_APK_Position]);
                                }

                                downloadFileFromURL(context, apkUrlList.get(Selected_APK_Position),title[Selected_APK_Position], releaseVersionList.get(Selected_APK_Position));
                                updateCounter();

                            }
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                Toast.makeText(context, "Please Allow Storage Access", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }

                    }).check();

        }else {
            Toast.makeText(context, "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }

    }

    public int getSwitchState(){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("Smart_Pref", MODE_PRIVATE);
        return sharedPreferences.getInt("Smart_Pref", 1);
    }

    private void updateCounter() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("stats/mods/").child(title[Selected_APK_Position]);
        int value = Integer.parseInt(downloadStatsList.get(Selected_APK_Position)) + 1;
        databaseReference.setValue(String.valueOf(value));
    }
}