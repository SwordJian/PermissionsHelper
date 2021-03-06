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

/**
 * Created by SwordJian on 2016/12/2.
 *
 * @author SwordJian
 * @editor SwordJian on 2017/10/27
 * <p>
 * 封装Android M(6.0)以上的权限访问库，执行{@link #performCodeWithPermission(String, PermissionCallback, boolean, String...)}就可以弹出提示框。
 * 在用户选择拒绝再次提示之后，会弹出Dialog来引导用户前往设置打开权限。
 */

public class PermissionsHepler {

    /**
     * 授权后{@link #onRequestPermissionsResult(int, String[], int[])} 的回调Code
     */
    public static final int permissionRequestCode = 88;

    private Activity mActivity;

    private static PermissionsHepler sHepler;

    /**
     * 弹出的第一个Dialog文字
     */
    private String permissionDes;
    /**
     * 弹出的第二个Dialog文字
     */
    private String missPermissionDes;
    /**
     * 是否显示权限提示
     */
    private boolean isShowPermission = false;
    private boolean isShowPermissionSettingDialog = false;
    private PermissionCallback permissionCallback;

    public interface PermissionCallback {
        void hasPermission();

        void noPermission(String code);
    }

    public static PermissionsHepler getInstance(Activity pAct) {
        if (sHepler == null) {
            sHepler = new PermissionsHepler();
        }
        sHepler.mActivity = pAct;
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
        performCodeWithPermission(permissionDes, callback, false, true, permissions);
    }

    /**
     * Android M运行时权限请求封装
     *
     * @param permissionDes 权限描述
     * @param callback      请求权限回调
     * @param isShowDialog  是否显示对话框
     * @param permissions   请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, PermissionCallback callback, boolean isShowDialog, boolean isShowPermissionSettingDialog, @NonNull String... permissions) {
        isShowPermission = true;
        this.isShowPermissionSettingDialog = isShowPermissionSettingDialog;
        this.permissionDes = getActivity().getResources().getString(R.string.permission_tips);
        this.permissionDes = String.format(this.permissionDes, permissionDes);
        this.missPermissionDes = getActivity().getResources().getString(R.string.miss_permission_tips);
        this.missPermissionDes = String.format(this.missPermissionDes, mActivity.getResources().getString(R.string.app_name), permissionDes);
        if (permissions.length == 0) return;
//        this.permissionrequestCode = requestCode;
        this.permissionCallback = callback;

        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)/* || checkPermissionGranted(permissions)*/) {
            if (permissionCallback != null) {
                permissionCallback.hasPermission();
                permissionCallback = null;
            }
        } else {
            //permission has not been granted.
            requestPermission(isShowDialog, permissionRequestCode, permissions);
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
            }
        }
        return flag;
    }

    private void requestPermission(boolean isShowDialog, final int requestCode, final String[] permissions) {
        if (checkPermissionGranted(permissions)) {
            if (permissionCallback != null) {
                permissionCallback.hasPermission();
                permissionCallback = null;
            }
        } else {
            if (isShowDialog) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.permission_title).setMessage(permissionDes)
                        .setPositiveButton(R.string.action_keepon, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (permissionCallback != null) {
                            permissionCallback.noPermission("cancel");
                        }
                        isShowPermission = false;
                        permissionCallback = null;
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
            }
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
                    permissionCallback.noPermission("refuse");
//                    permissionCallback = null;
                }
                if (isShowPermissionSettingDialog) {
                    new AlertDialog.Builder(getActivity()).setTitle(R.string.permission_title).setMessage(missPermissionDes)
                            .setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                                    mActivity.startActivity(intent);
                                    if (permissionCallback != null) {
                                        permissionCallback.noPermission("setting");
                                    }
                                    isShowPermission = false;
                                    permissionCallback = null;
                                }
                            }).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (permissionCallback != null) {
                                permissionCallback.noPermission("cancel");
                            }
                            isShowPermission = false;
                            permissionCallback = null;
                        }
                    }).show();
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

    public static String getVersion() {
        return "1.1.1";
    }

    public static String getGitHubLink() {
        return "https://github.com/SwordJian/PermissionsHelper";
    }

}
