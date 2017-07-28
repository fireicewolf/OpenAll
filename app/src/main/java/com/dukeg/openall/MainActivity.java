package com.dukeg.openall;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "OpenAllAccessibility";

    private TextView text;
    private TextView title;
    private Button runAllApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.runAllApps = ((Button)findViewById(R.id.run_all_apps));
        this.text = ((TextView)findViewById(R.id.package_names));
        this.title = ((TextView)findViewById(R.id.Title));
        this.text.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    protected void onResume() {
        super.onResume();
        PackageManager mPackageManger = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List <ResolveInfo> resolveInfoList = mPackageManger.queryIntentActivities(intent, 0);
        final ArrayList<String> packageNameList = new ArrayList<>();
        for (Object o : ((List) resolveInfoList)) {
            ResolveInfo resolveInfo = (ResolveInfo) o;
            if (!packageNameList.contains(resolveInfo.activityInfo.packageName)) {
                packageNameList.add(resolveInfo.activityInfo.packageName);
            }
        }
        packageNameList.remove("com.dukeg.openall");
        StringBuilder stringBuilder = new StringBuilder();
        for (Object aPackageNameList : packageNameList) {
            String packageName = (String) aPackageNameList;
            stringBuilder.append(packageName).append("\n");
        }
        this.text.setText(stringBuilder.toString());
        StringBuilder titleDescription = new StringBuilder(packageNameList.size() + " apps can be viewed in launcher.");
        this.title.setText(titleDescription);

        runAllApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Do you want to open all apps？")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < packageNameList.size(); i++) {
                                    String logs = "No " + i + " " + packageNameList.get(i) + " launched";
                                    Log.i(TAG,logs);
                                    doStartApplicationWithPackageName(packageNameList.get(i));
                                    SystemClock.sleep(5000);
                                }
                                doStartApplicationWithPackageName("蹦");
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        });
    }

    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;

            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

        }
    }
}
