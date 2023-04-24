/*
 * Copyright 2014, Yang Chang Geun.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.wishbook.catalog.Utils.imagecropper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.orders.ActivityOrderHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotoTaker extends Activity {

    private static final int INTENT_SELECT_PHOTO = 104;
    private final String TEMP_PREFIX = "tmp_";
    private final int IMAGE_CAPTURE = 101;
    private final int CROP_IMAGE = 102;
    private final int PICK_FROM_FILE = 103;

    private boolean return_data = true;
    private boolean scale = true;
    private boolean faceDetection = true;

    //	private String mOutput;
    private String mTemp;
    private File mDirectory;
    private OnNotFoundCropIntentListener mNotFoundCropIntentListener;
    private Uri mCropUri;
    private String filename;
    private boolean isCoverImage;
    boolean isWithoutCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setOutput(Environment.getExternalStorageDirectory().getPath() + "/touchschool/", "touchschool_photo_preview.jpg");
            String mode = getIntent().getStringExtra("mode");
            isCoverImage = getIntent().getBooleanExtra("isCoverImage", false);
            filename = getIntent().getStringExtra("filename");
            isWithoutCrop = getIntent().getBooleanExtra("withoutcrop",false);
            switch (mode) {
                case "camera":
                    doImageCapture();
                    break;
                case "picture":
                    doPickImage();
                    break;
                default:
                    finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOutput(String path, String name) {
        mDirectory = createDirectory(path);
        //		mOutput = name;
        mTemp = TEMP_PREFIX.concat(name);
    }

    public void setOutput(String name) {
        mTemp = TEMP_PREFIX.concat(name);
    }

    public void setNotFoundCropIntentListener(
            OnNotFoundCropIntentListener listener) {
        mNotFoundCropIntentListener = listener;
    }

    private MediaUriFinder.MediaScannedListener mScanner = new MediaUriFinder.MediaScannedListener() {

        @Override
        public void OnScanned(Uri uri) {
            /*
             * Start Crop Activity with URI that we get once scanned if not
             * found Support Crop Activity then run OnNotFoundCropIntent()
             */
            if (isWithoutCrop) {
                Log.e("TAG", "OnScanned: isWithoutCrop" );
                File mDirectory = createDirectory(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
                File output = getFile(mDirectory, filename);
                Bitmap bitmap = getBitmap(uri);
                boolean writed = writeBitmapToFile(bitmap, output);
                Intent intent = new Intent(PhotoTaker.this,ActivityOrderHolder.class);

                if (writed) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        intent.setData(FileProvider.getUriForFile(PhotoTaker.this, getApplicationContext().getPackageName() + ".provider", output));
                    } else {
                        intent.setData(Uri.fromFile(output));
                    }
                }


                intent.putExtra("real_path",output.getPath());
                setResult(Application_Singleton.CAMERA_IMAGE_RESPONSE_CODE, intent);
                Log.e("TAG", "OnScanned: isWithoutCrop 1" );
                finish();
            } else {
                if (!doCropImage(uri, filename) && mNotFoundCropIntentListener != null)
                    mNotFoundCropIntentListener.OnNotFoundCropIntent(
                            mDirectory.getAbsolutePath(), mCropUri);
            }

        }
    };

    private boolean writeBitmapToFile(Bitmap bitmap, File file) {
        try {
            if(bitmap != null) {
                FileOutputStream fops = new FileOutputStream(file);
                bitmap.compress(CompressFormat.JPEG, 100, fops);
                fops.flush();
                fops.close();
                fops = null;
                return true;
            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Bitmap getBitmap(Uri uri) {
        //		Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            Log.v("test", "uri.getPath() : "+uri.getPath());
            final int IMAGE_MAX_SIZE = 2048;

            in = getContentResolver().openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;
        } catch (FileNotFoundException e) {
            Log.e("Cropper", "file " + uri.toString() + " not found");
        } catch (IOException e) {
            Log.e("Cropper", "file " + uri.getPath() + " not io");
        } catch (Exception e) {
            Log.e("Cropper", "file " + uri.getPath() + " not io");
        }
        return null;
    }

    @Override
    protected void onPause() {
        if (dlg != null) dlg.hide();
        super.onPause();
    }

    private ProgressDialog dlg = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case IMAGE_CAPTURE:
                        dlg = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
                        dlg.setMessage("Please wait..");
                        dlg.show();
                        File tempFile = getFile(mDirectory, mTemp);
                        MediaUriFinder.create(this, tempFile.getAbsolutePath(), mScanner);
                        break;
                    case PICK_FROM_FILE:
                        Uri dataUri = data.getData();
                        if (dataUri != null) {
                            if (dataUri.getScheme().trim().equalsIgnoreCase("content"))
                                doCropImage(data.getData(), filename);
                                // if Scheme URI is File then scan for content then Crop it!
                            else if (dataUri.getScheme().trim().equalsIgnoreCase("file"))
                                MediaUriFinder.create(this, dataUri.getPath(), mScanner);
                        }
                        break;
                    case CROP_IMAGE:
                        getFile(mDirectory, mTemp).delete();
                        PhotoTakerUtils.setImageUri(data.getData());
                        finish();
                        break;
                }// end Switch case
            } else {
                finish();
                getFile(mDirectory, mTemp).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean doCropImage(Uri uri, String filename) {

        try {

            if (uri.getScheme().trim().equalsIgnoreCase("file")) {
                return false;
            }

            mCropUri = uri;

            Intent intent = new Intent(this, PhotoCropActivity.class);

            intent.setType("image/*");
            intent.setData(uri);
            intent.putExtra("filename", filename);
            intent.putExtra("noFaceDetection", !faceDetection);
            intent.putExtra("scale", scale);
            if (isCoverImage) {
                intent.putExtra("isCoverImage", isCoverImage);
            }
            intent.putExtra("return-data", return_data);
            startActivityForResult(intent, CROP_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause)
    {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null)
        {
            boolean moveToFirst = cursor.moveToFirst();
            if(moveToFirst)
            {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if( uri==MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Images.Media.DATA;
                }else if( uri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Audio.Media.DATA;
                }else if( uri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }

    public void doImageCapture() {
        try {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                String url = String.valueOf(System.currentTimeMillis()) + ".jpg";
                setOutput(url);
            }

            File temp = getFile(mDirectory, mTemp);

            // Take Image for Camera and write to tempfile
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("outputFormat", CompressFormat.JPEG.toString());
            intent.putExtra("return-data", true);

            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(PhotoTaker.this, getApplicationContext().getPackageName() + ".provider", temp);
            } else {
                uri = Uri.fromFile(temp);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(intent, IMAGE_CAPTURE);

            return;
        } catch (ActivityNotFoundException anfe) {
            anfe.printStackTrace();
            return;
        }
    }

    public void doPickImage() {
        try {
		/*	Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
			intent.setType("image*//*");
			intent.putExtra("return-data", return_data);
			startActivityForResult(intent, PICK_FROM_FILE);*/
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_FROM_FILE);
            return;
        } catch (ActivityNotFoundException anfe) {
            anfe.printStackTrace();
            return;
        }
    }

    private File createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private File getFile(File dir, String name) {
        File output = new File(dir, name);
        if (!output.exists()) {
            try {
                output.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    @Override
    protected void finalize() throws Throwable {
        mCropUri = null;
        mNotFoundCropIntentListener = null;
        super.finalize();
    }

    public interface OnNotFoundCropIntentListener {
        void OnNotFoundCropIntent(String path, Uri uri);
    }

}
