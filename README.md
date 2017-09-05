# PermissionsHelper
封装Android 6.0以上的权限管理方法

参考了https://github.com/didikee/PermissionsHelper 封装成自己喜欢的调用方式

导入方式：
Step 1. Add the JitPack repository to your build file

gradle

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.SwordJian:PermissionsHelper:1.0.3'
	}
  
  其他导入请参考https://jitpack.io
  
  使用方法：
  1. 在Activity 里@Override onRequestPermissionsResult ，然后调用`PermissionsHepler.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults)`<br>
  
  2. 在需要询问权限的方法前调用`PermissionsHepler.getInstance(this).performCodeWithPermission()`， 就会弹出询问打开权限的对话框。
  `performCodeWithPermission` 方法的第一个参数是权限的名字，用来显示的。第二个参数是`PermissionCallback`， 用来回调权限是否打开。第三个参数是权限，从`Manifest.permission`里面获取。<br>
  
  例如：
  ```
   PermissionsHepler.getInstance(this).performCodeWithPermission("相机", new PermissionsHepler.PermissionCallback() {
                @Override
                public void hasPermission() {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                }

                @Override
                public void noPermission(String code) {

                }
            }, Manifest.permission.CAMERA);
```
