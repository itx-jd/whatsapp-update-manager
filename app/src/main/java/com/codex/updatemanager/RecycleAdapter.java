package com.codex.updatemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION.SDK_INT;
import static com.codex.updatemanager.Constant.permissionDenied;
import static com.codex.updatemanager.Constant.apkUrls;
import static com.codex.updatemanager.Constant.dataReady;
import static com.codex.updatemanager.Constant.deleteAPK;
import static com.codex.updatemanager.Constant.description;
import static com.codex.updatemanager.Constant.downloadFile;
import static com.codex.updatemanager.Constant.hasStoragePermission;
import static com.codex.updatemanager.Constant.installedVersion;
import static com.codex.updatemanager.Constant.internetAvailable;
import static com.codex.updatemanager.Constant.latestVersion;
import static com.codex.updatemanager.Constant.packageName;
import static com.codex.updatemanager.Constant.readableVersion;
import static com.codex.updatemanager.Constant.title;
import static com.codex.updatemanager.Constant.vibrator;
import android.Manifest;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.recycleViewHolder>{

    Context context;
    Activity activity;

    public RecycleAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public recycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycle_item,parent,false);
        return new recycleViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull recycleViewHolder holder, int position) {
        holder.title.setText(title[position]);
        holder.description.setText(description[position]);
        holder.tv_package.setText(packageName[position]);

        if(internetAvailable){
            if(dataReady){
                holder.layout_check.setVisibility(View.GONE);
                if( installedVersion.get(position) == null ) {
                    holder.layout_download.setVisibility(View.VISIBLE);
                    holder.version.setText("Download ("+readableVersion.get(position)+")");
                    holder.icon.setImageResource(R.drawable.ic_baseline_download_24);
                }else{
                    if (installedVersion.get(position).equalsIgnoreCase(latestVersion.get(position))){
                        holder.layout_updated.setVisibility(View.VISIBLE);
                    }else{
                        holder.layout_download.setVisibility(View.VISIBLE);
                        holder.version.setText("Update ("+readableVersion.get(position)+")");
                        holder.icon.setImageResource(R.drawable.ic_baseline_upload_24);
                    }
                }
            }
        }else{
            holder.layout_check.setVisibility(View.GONE);
            holder.layout_internet.setVisibility(View.VISIBLE);
        }

        holder.layout_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator(context,60);

                storagePermission();

                if(hasStoragePermission){
                    if(getSwitchState() == 1){
                        deleteAPK(context,title[position]);
                    }
                    downloadFile(context,apkUrls.get(position),title[position],readableVersion.get(position));
                }
                if(permissionDenied){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",context.getPackageName(),null);
                    intent.setData(uri);
                    context.startActivity(intent);
                    Toast.makeText(context, "Allow Storage Permissions !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    public  class recycleViewHolder extends RecyclerView.ViewHolder {

        TextView title,description,version,tv_package;
        ImageView icon;
        LinearLayout layout_updated,layout_check,layout_download,layout_internet;

        public recycleViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_icon);
            tv_package = itemView.findViewById(R.id.tv_package);
            title = itemView.findViewById(R.id.title);
            version = itemView.findViewById(R.id.tv_version);
            description = itemView.findViewById(R.id.description);
            layout_updated = itemView.findViewById(R.id.layout_updated);
            layout_check = itemView.findViewById(R.id.layout_check);
            layout_download = itemView.findViewById(R.id.layout_download);
            layout_internet = itemView.findViewById(R.id.layout_internet);
        }
    }

    public int getSwitchState(){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("Smart_Pref", MODE_PRIVATE);
        return sharedPreferences.getInt("Smart_Pref", 3);
    }

    public void storagePermission(){
        Dexter.withContext(context).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            hasStoragePermission = true;
                            permissionDenied = false;
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            permissionDenied = true;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }

                }).check();
    }

}