package com.coderium.updatemanager.adapters;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;
import static com.coderium.updatemanager.utilities.Constant.Selected_APK_Position;
import static com.coderium.updatemanager.utilities.Constant.apkUrlList;
import static com.coderium.updatemanager.utilities.Constant.contactDataList;
import static com.coderium.updatemanager.utilities.Constant.deleteAPK;
import static com.coderium.updatemanager.utilities.Constant.description;
import static com.coderium.updatemanager.utilities.Constant.downloadFileFromURL;
import static com.coderium.updatemanager.utilities.Constant.downloadStatsList;
import static com.coderium.updatemanager.utilities.Constant.isNetworkAvailable;
import static com.coderium.updatemanager.utilities.Constant.packageName;
import static com.coderium.updatemanager.utilities.Constant.releaseVersionList;
import static com.coderium.updatemanager.utilities.Constant.title;
import static com.coderium.updatemanager.utilities.Constant.vibrator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coderium.updatemanager.R;
import com.coderium.updatemanager.extraClasses.Contributor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class ContributorAdapter extends RecyclerView.Adapter<ContributorAdapter.recycleViewHolder> {

    Context context;
    Contributor contributor;

    public ContributorAdapter(Context context, Contributor contributor) {
        this.context = context;
        this.contributor = contributor;
    }

    @NonNull
    @Override
    public recycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.contributor_item,parent,false);
        return new recycleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull recycleViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tv_contributor_name.setText(contributor.nameList.get(position));

        holder.rl_contributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contributor.socialLink.get(position)));
                    context.startActivity(browserIntent);
                }catch (Exception ex){
                    Toast.makeText(context, context.getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contributor.nameList.size();
    }

    public static class recycleViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_contributor;
        TextView tv_contributor_name , iv_contributor_social;

        public recycleViewHolder(@NonNull View itemView) {
            super(itemView);
            rl_contributor = itemView.findViewById(R.id.rl_contributor);
            tv_contributor_name = itemView.findViewById(R.id.tv_contributor_name);
        }
    }

}