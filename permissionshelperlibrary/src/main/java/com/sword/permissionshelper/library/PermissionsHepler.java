package com.sword.permissionshelper.library;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SwordJian on 2016/12/2.
 *
 * @author SwordJian
 * @editor SwordJian on 2016/12/2
 */

public class PermissionsHepler {

    private Activity mActivity;
    private static PermissionsHepler sHepler;

    private String permissionDes;
    private String missPermissionDes;
    private int permissionRequestCode = 88;
    public boolean isShowPermission = false;
    private PermissionCallback permissionCallback;

    Map<String, Integer> permissionMap;

    public interface PermissionCallback {
        void hasPermission();

        void noPermission(String code);
    }

    public static PermissionsHepler getInstance(Activity pAct) {
        if (sHepler == null) {
            sHepler = new PermissionsHepler();
        }
        sHepler.mActivity = pAct;
        sHepler.permissionMap = new HashMap<>();
        return sHepler;
    }

    public PermissionsHepler() {
    }

    private Activity getActivity() {
        return mActivity;
    }

    /**
     * Android M运行时权限请求封装
     *
     * @param permissionDes 权限描述
     * @param callback      请求权限回调
     * @param permissions   请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, PermissionCallback callback, @NonNull String... permissions) {
        isShowPermission = true;
        this.permissionDes = getActivity().getResources().getString(R.string.permission_tips);
        this.permissionDes = String.format(this.permissionDes, permissionDes);
        this.missPermissionDes = getActivity().getResources().getString(R.string.miss_permission_tips);
        this.missPermissionDes = String.format(this.missPermissionDes, permissionDes);
        if (permissions.length == 0) return;
//        this.permissionrequestCode = requestCode;
        this.permissionCallback = callback;

        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || checkPermissionGranted(permissions)) {
            if (permissionCallback != null) {
                permissionCallback.hasPermission();
                permissionCallback = null;
            }
        } else {
            //permission has not been granted.
            requestPermission(permissionRequestCode, permissions);
        }

    }

    /**
     * @param permissions
     * @return 当前权限是否已经获取
     */
    public boolean checkPermissionGranted(@NonNull String... permissions) {
        boolean flag = true;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), p) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * @param permissions
     * @return 是否弹出提获取权限的对话框
     */
    public boolean shouldShowRequestPermissionRationale(String... permissions) {
        boolean flag = false;
        for (String p : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), p)) {
                flag = true;
                break;
            } else {
                if (permissionMap.get(p) == null || permissionMap.get(p) == 0) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    private void requestPermission(final int requestCode, final String[] permissions) {
        if (shouldShowRequestPermissionRationale(permissions)) {
            new AlertDialog.Builder(getActivity()).setMessage(permissionDes)
                    .setPositiveButton(R.string.action_keepon, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
                        }
                    }).show();
        } else {
            new AlertDialog.Builder(getActivity()).setTitle(android.R.string.dialog_alert_title).setMessage(missPermissionDes)
                    .setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                            mActivity.startActivity(intent);

                            permissionCallback.noPermission("setting");
                            isShowPermission = false;
                            permissionCallback = null;
                        }
                    }).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    permissionCallback.noPermission("cancel");
                    isShowPermission = false;
                    permissionCallback = null;
                }
            }).show();
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                              @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (verifyPermissions(grantResults)) {
                if (permissionCallback != null) {
                    permissionCallback.hasPermission();
                    permissionCallback = null;
                }
            } else {
                if (permissionCallback != null) {
                    permissionCallback.noPermission("");
//                    permissionCallback = null;
                }
                for (String p : permissions) {
                    permissionMap.put(p, 1);
                }
            }
            return true;
        } else {
//            mActivity.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return false;
        }

    }

    public boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
