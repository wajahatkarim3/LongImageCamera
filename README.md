LongImageCamera
============
[ ![Download](https://api.bintray.com/packages/wajahatkarim3/LongImageCamera/com.wajahatkarim3.longimagecamera/images/download.svg) ](https://bintray.com/wajahatkarim3/LongImageCamera/com.wajahatkarim3.longimagecamera/_latestVersion) [![API](https://img.shields.io/badge/API-15%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=15)
 
A camera view to capture long image merged from small captured images as it is in [Shoparoo](https://play.google.com/store/apps/details?id=infoscout.shoparoo&hl=en) app! This library uses [CameraView](https://github.com/google/cameraview) from Google as the backbone of camera functionality and adds auto-support of creating long or wide images from multiple images. This library includes:
* Built-in Camera UI and activity
* Built-in Image Viewer with pinch-fling gestures for zoom/move/rotate etc.
* TouchImageView with gestures support for zoom/move/rotate etc.
* Built-in permission handling for Camera and Storage
* Horizontal or Vertical image merging

![](https://github.com/wajahatkarim3/LongImageCamera/blob/master/Art/demo.gif)

Demo
====
Install [Demo](https://github.com/wajahatkarim3/LongImageCamera/releases/download/1.0.1/LongImageCamera-v1.0.0.apk) app or APK from [Releases](https://github.com/wajahatkarim3/LongImageCamera/releases) on your device and try to capture a long image!

Changelog
=========
Changes exist in the [releases](https://github.com/wajahatkarim3/LongImageCamera/releases) tab.

Installation
============
Add this in your app's build.gradle file:
```groovy
dependencies {
  compile 'com.wajahatkarim3.LongImageCamera:LongImageCamera:1.0.1'
}
```
Or add LongImageCamera as a new dependency inside your pom.xml
```xml
<dependency>
  <groupId>com.wajahatkarim3.LongImageCamera</groupId>
  <artifactId>LongImageCamera</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```
Usage
=====
Launch Camera Activity
---
Start the camera from any ```Activity``` with this code:
```java
// Launches camera in Vertical Merge Mode (Captured image will be long)
LongImageCameraActivity.launch( myActivity );

// Launches Camea in Horizontal Merge Mode (Captured image will be wide)
LongImageCameraActivity.launch( myActivity, LongImageCameraActivity.ImageMergeMode.HORIZONTAL );
```
and then you will get the result image in ```onActivityResult()``` method like this:
```java
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == LongImageCameraActivity.LONG_IMAGE_RESULT_CODE && data != null)
        {
            String imageFileName = data.getStringExtra(LongImageCameraActivity.IMAGE_PATH_KEY);
            Log.e(TAG, "onActivityResult: " + imageFileName );
        }
    }
```
Launch Image Viewer Activity
---
This library comes with a built-in image viewer including support of pinch/fling gestures for zoom/move/roate etc. You can launch image viewer with this code:
```java
Intent ii = new Intent(myActivity, PreviewLongImageActivity.class);
ii.putExtra("imageName", myImagePath);
startActivity(ii);
```
Using TouchImageView in your layouts
---
You can use customized ```ImageView``` with support of gestures for zoom/move/rotate etc like this:
```xml
<com.wajahatkarim3.longimagecamera.TouchImageView
        android:id="@+id/imageView1"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
# Libs used in this Library
* CameraView (https://github.com/google/cameraview) by Google
* TouchImageView (https://github.com/MikeOrtiz/TouchImageView)

Developed By
============
```
Wajahat Karim
```
- Website (http://wajahatkarim.com)
- Twitter (http://twitter.com/wajahatkarim)
- Medium (http://www.medium.com/@wajahatkarim3)
- LinkedIn (http://www.linkedin.com/in/wajahatkarim)

# How to Contribute
1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create new Pull Request

# License

    Copyright 2017 Wajahat Karim

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
