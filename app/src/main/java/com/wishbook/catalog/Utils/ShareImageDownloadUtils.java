package com.wishbook.catalog.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSources;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.home.catalog.Fragment_ResellerMultipleProductShare;
import com.wishbook.catalog.home.catalog.ResellerCatalogShareBottomSheet;
import com.wishbook.catalog.home.catalog.details.DialogViewTaxShipping;
import com.wishbook.catalog.home.models.ProductObj;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ShareImageDownloadUtils {


    Context mContext;
    static ImageView imge_step_one, imge_step_two;
    static TextView txt_whatsapp_share_title;
    static ProgressBar progressBar;
    public static int whatsapp_count;
    static AlertDialog whatsapp_dialog;
    public static StaticFunctions.SHARETYPE last_share_type;

    public static DownloadImageTaskCompleteListener downloadImageTaskCompleteListener;

    public ShareImageDownloadUtils(Context mContext) {
        this.mContext = mContext;
    }

    public static void shareProducts(AppCompatActivity mContext, Fragment resultContext,
                                     ProductObj[] productObjs, StaticFunctions.SHARETYPE sharetype,
                                     String title, String catalogname,
                                     boolean isRequiredWatermark ,boolean isFromReseller) throws IOException {
        new DownloadImageAsynchTask(mContext, resultContext, productObjs, sharetype, title, catalogname, isRequiredWatermark,isFromReseller).execute();
    }

    private static class DownloadImageAsynchTask extends AsyncTask<Void, Integer, ArrayList<Uri>> {

        AppCompatActivity mContext;
        Fragment resultContext;

        ProductObj[] productObjs;
        StaticFunctions.SHARETYPE sharetype;
        String title;
        String catalogname;
        MaterialDialog dialogMaterial;
        boolean isRequiredWatermark;
        boolean isFromResellerShare;
        private ProgressDialog dialog;

        public DownloadImageAsynchTask(AppCompatActivity mContext, Fragment resultContext, ProductObj[] productObjs,
                                       StaticFunctions.SHARETYPE sharetype, String title, String catalogname,
                                       boolean isRequiredWatermark, boolean isFromResellerShare) {
            this.mContext = mContext;
            this.productObjs = productObjs;
            this.sharetype = sharetype;
            this.title = title;
            this.catalogname = catalogname;
            this.isRequiredWatermark = isRequiredWatermark;
            this.resultContext = resultContext;
            this.isFromResellerShare = isFromResellerShare;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mContext!=null) {
                if (isFromResellerShare && (sharetype == StaticFunctions.SHARETYPE.WHATSAPP || sharetype == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS)) {
                    showShareStatusDialog(mContext);
                } else {
                    dialogMaterial = new MaterialDialog.Builder(mContext).progress(false, 100, false).title("Downloading Images ..").show();
                    dialogMaterial.setCancelable(false);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int percentage = (values[0] * 100) / values[1];
            Log.e("TAG", "onProgressUpdate: ====>" + percentage);
            if(mContext !=null) {
                if (isFromResellerShare && (sharetype == StaticFunctions.SHARETYPE.WHATSAPP || sharetype == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS)) {
                    progressBar.setProgress(percentage);
                } else {
                    dialogMaterial.setProgress(percentage);
                }
            }

        }

        @Override
        protected ArrayList<Uri> doInBackground(Void... params) {
            ArrayList<ProductObj> list = new ArrayList<ProductObj>(Arrays.asList(productObjs));
            ArrayList<Uri> uris = new ArrayList<Uri>();
            ArrayList<File> filesToShare = new ArrayList<File>();

            if (list != null) {
                int position = 1;
                for (ProductObj productObj : list) {
                   /* Uri uri = Uri.parse(productObj.getImage().getThumbnail_medium());
                    File temp_file = getImageFromFresco(mContext,uri);
                    if(temp_file!=null) {
                        try {
                            Log.e("TAG", "doInBackground: File Not Null");
                            if(mContext!=null) {
                                File to = new File(mContext.getFilesDir(), generateUniqueFileName()+".png");
                                StaticFunctions.copy(temp_file,to);
                                filesToShare.add(to);
                                publishProgress(position, list.size());
                                position++;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.i("TAG", "doInBackground: File Is Null");
                    }*/
                    try {
                        File cachedImage = ImageLoader.getInstance().getDiscCache().get(productObj.getImage().getThumbnail_medium());
                        if (cachedImage != null && cachedImage.exists()) { // if image was cached by UIL
                            try {
                                filesToShare.add(cachedImage);
                                Log.i("Exist", cachedImage.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (StaticFunctions.isOnline(mContext)) {
                                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                                        .cacheOnDisc(true).build();
                                ImageLoader.getInstance().loadImageSync(productObj.getImage().getThumbnail_medium(), options);
                                File downloadedImage = ImageLoader.getInstance().getDiscCache().get(productObj.getImage().getThumbnail_medium());
                                filesToShare.add(downloadedImage);
                                publishProgress(position, list.size());
                                position++;

                            }

                        }
                    } catch (IllegalStateException e) {
                        // Handle for init not done
                        Application_Singleton.initImageLoader(mContext);
                    }


                }
            }
            int counter = 0;
            for (File file : filesToShare) {
                // if(isRequiredWatermark) {
                addProductIDWaterMark(file.getAbsolutePath(), productObjs[counter].getId());
                counter = counter + 1;
                Log.i("TAG", "doInBackground: WaterMark Counter"+counter );
                // }
                if (Build.VERSION.SDK_INT >= 24) {
                    uris.add(FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file));
                } else {
                    uris.add(Uri.fromFile(file));
                }
            }

            if (sharetype == StaticFunctions.SHARETYPE.GALLERY) {
                File f1 = new File(Environment.getExternalStorageDirectory() + "/" + "Wishbook", catalogname);
                if (!f1.exists()) {
                    f1.mkdirs();
                }
                Log.i("TAG", "doInBackground: Gallery File Size"+filesToShare.size() );
                for (int i = 0; i < filesToShare.size(); i++) {
                    String filename = "";
                    if (productObjs[i].getSku() != null && !productObjs[i].getSku().isEmpty()) {
                        filename = productObjs[i].getSku() + ".png";
                    } else {
                        filename = i + ".png";
                    }
                    File to = new File(Environment.getExternalStorageDirectory() + "/" + "Wishbook", catalogname + "/" + filename);
                    boolean isRename = false;
                    try {
                        StaticFunctions.copy(filesToShare.get(i),to);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                   /* if(isRename) {
                        Log.i("TAG", "To Path===>"+to.getAbsolutePath()+"doInBackground: Rename Success======>" +filesToShare.get(i).getAbsolutePath());
                    } else {
                        Log.i("TAG", "doInBackground: Rename Failed======>" +filesToShare.get(i).getAbsolutePath());
                    }*/
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(to);
                            mediaScanIntent.setData(contentUri);
                            mContext.sendBroadcast(mediaScanIntent);
                        } else {
                            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return uris;
        }

        @Override
        protected void onPostExecute(ArrayList<Uri> uris) {
            super.onPostExecute(uris);
            if(mContext!=null &&  !mContext.isFinishing()) {
                if (dialogMaterial != null && dialogMaterial.isShowing() && !mContext.isFinishing()) {
                    dialogMaterial.dismiss();
                }
                if (isFromResellerShare && sharetype == StaticFunctions.SHARETYPE.WHATSAPP || sharetype == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS) {
                    shareStatusDialogUpdate(100, true);
                }

                if (sharetype == StaticFunctions.SHARETYPE.GALLERY) {
                    Toast.makeText(mContext, "Successfully saved to Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    last_share_type = sharetype;
                    openGenericShareIntent(mContext, resultContext, uris, title, sharetype);
                }
            }
        }

    }

    private static void addProductIDWaterMark(String filepath, String productId) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inMutable = true;
            Bitmap result = BitmapFactory.decodeFile(filepath, bmOptions);

            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(result, 0, 0, null);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setAlpha(60);
            paint.setTextSize(25f);
            paint.setAntiAlias(true);

            String str = "P-" + productId;
            Paint.FontMetrics fm = new Paint.FontMetrics();
            paint.getFontMetrics(fm);

            int x_point = 50;
            int y_point = 60;

            int margin = 10;
            canvas.drawRect(x_point - margin, y_point + fm.top - margin,
                    x_point + paint.measureText(str) + margin, y_point + fm.bottom
                            + margin, paint);
            paint.setAlpha(100);
            paint.setColor(Color.WHITE);

            canvas.drawText(str, x_point, y_point, paint);


            // canvas.drawText("P-"+productId, 0, 50, paint);
            try {
                OutputStream fOut = null;
                File file = new File(filepath);
                fOut = new FileOutputStream(file);
                result.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static File changeExtension(File file, String extension) {
        String filename = file.getName();

        if (filename.contains(".")) {
            Log.i("TAG", "changeExtension: contains ." );
            filename = filename.substring(0, filename.lastIndexOf('.'));
        }
        filename += "." + extension;
        Log.i("TAG", "changeExtension:====> "+filename );

        file.renameTo(new File(file.getAbsolutePath(), filename));
        return file;
    }

    public static void openGenericShareIntent(final AppCompatActivity activity, Fragment resultContext, ArrayList<Uri> uris, String sharetext,
                                              StaticFunctions.SHARETYPE sharetype) {

        Intent shareIntent = new Intent();
        if (uris.size() > 1) {
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        } else {
            shareIntent.setAction(Intent.ACTION_SEND);
            if (uris.size() > 0)
                shareIntent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (sharetype == StaticFunctions.SHARETYPE.FACEBOOK) {
            boolean isFbinstalled = StaticFunctions.isPackageInstalled("com.facebook.katana", activity.getPackageManager());
            boolean isFbliteInstalled = StaticFunctions.isPackageInstalled("com.facebook.lite", activity.getPackageManager());
            if (isFbinstalled || isFbliteInstalled) {
                if (isFbinstalled) {
                    shareIntent.setPackage("com.facebook.katana");
                } else if (isFbliteInstalled) {
                    shareIntent.setPackage("com.facebook.lite");
                }
            } else {
                Toast.makeText(getApplicationContext(), "Facebook is not installed on your phone", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP) {
            boolean installed = StaticFunctions.appInstalledOrNot("com.whatsapp", activity);
            if (installed) {
                shareIntent.setPackage("com.whatsapp");
            } else {
                Toast.makeText(getApplicationContext(), "Whatsapp is not installed on your phone", Toast.LENGTH_LONG).show();
                return;
            }

        }
        if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS) {
            boolean installed = StaticFunctions.appInstalledOrNot("com.whatsapp.w4b", activity);
            if (installed) {
                shareIntent.setPackage("com.whatsapp.w4b");
            } else {
                Toast.makeText(getApplicationContext(), "Whatsapp Business is not installed on your phone", Toast.LENGTH_LONG).show();
                return;
            }
        }

        try {
            if(resultContext!=null && resultContext.isAdded() && !resultContext.isDetached()) {
                if (resultContext instanceof ResellerCatalogShareBottomSheet) {
                    ((ResellerCatalogShareBottomSheet) resultContext).startActivityForResult(shareIntent, 5001);
                } else if (resultContext instanceof Fragment_ResellerMultipleProductShare) {
                    ((Fragment_ResellerMultipleProductShare) resultContext).startActivityForResult(shareIntent, 5001);
                } else if (resultContext instanceof DialogViewTaxShipping) {
                    ((DialogViewTaxShipping) resultContext).startActivityForResult(shareIntent, 5001);
                } else {
                    activity.startActivityForResult(shareIntent, 5001);
                }
            } else {
                activity.startActivityForResult(shareIntent, 5001);
            }
            if(downloadImageTaskCompleteListener!=null) {
                downloadImageTaskCompleteListener.onDownloadImageSuccess();
            }
        } catch (android.content.ActivityNotFoundException ex) {
            // ex.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Share Images..", Toast.LENGTH_LONG).show();
    }


    public static void showShareStatusDialog(Activity activity) {
        whatsapp_count = 1;
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        View viewInflated = LayoutInflater.from(activity).inflate(R.layout.dialog_whatsapp_share_steps, null, false);
        alert.setView(viewInflated);
        alert.setCancelable(true);
        whatsapp_dialog = alert.create();
        imge_step_one = viewInflated.findViewById(R.id.img_step_one);
        imge_step_two = viewInflated.findViewById(R.id.img_step_two);
        txt_whatsapp_share_title = viewInflated.findViewById(R.id.txt_whatsapp_share_title);
        progressBar = viewInflated.findViewById(R.id.progress_bar);
        txt_whatsapp_share_title.setText("Sharing Whatsapp (Step 1 of 2)");
        shareStatusDialogUpdate(0, false);
        whatsapp_dialog.show();
    }

    public static void setStepTwoCounter(final Context mContext, final String share_text) {
        final int count_to = 2000;
        txt_whatsapp_share_title.setText("Sharing Whatsapp (Step 2 of 2)");
        CountDownTimer countDownTimer = new CountDownTimer(count_to, 10) {
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) ((count_to - millisUntilFinished) / 10));
            }

            public void onFinish() {
                shareStatusDialogUpdate(100, true);
                shareProductDescription(mContext, share_text, last_share_type);
            }

        }.start();
    }

    public static void shareStatusDialogUpdate(int progress, boolean isFinished) {
        if(whatsapp_dialog!=null) {
            progressBar.setProgress(progress);
            if (isFinished) {
                try {
                    if (whatsapp_count == 1) {
                        imge_step_one.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.circle_selected));
                    }
                    if (whatsapp_count == 2) {
                        imge_step_two.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.circle_selected));
                        whatsapp_dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                whatsapp_count += 1;
            }
        }
    }

    public static void shareProductDescription(Context mContext, String share_text, StaticFunctions.SHARETYPE sharetype) {
        if(mContext!=null) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Product Details");
                intent.putExtra(Intent.EXTRA_TEXT, share_text);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP) {
                    boolean installed = StaticFunctions.appInstalledOrNot("com.whatsapp", mContext);
                    if (installed) {
                        intent.setPackage("com.whatsapp");
                    } else {
                        Toast.makeText(mContext, "Whatsapp is not installed on your phone", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS) {
                    boolean installed = StaticFunctions.appInstalledOrNot("com.whatsapp.w4b", mContext);
                    if (installed) {
                        intent.setPackage("com.whatsapp.w4b");
                    } else {
                        Toast.makeText(mContext, "Whatsapp Business is not installed on your phone", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Toast.makeText(mContext, "Share Description", Toast.LENGTH_LONG).show();
                last_share_type = null;
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static File getImageFromFresco(Context context, Uri uri) {
        if(context!=null) {
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                    .build();
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            DataSource<CloseableReference<CloseableImage>>
                    dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
            try {
                CloseableReference<CloseableImage> result = DataSources.waitForFinalResult(dataSource);
                if (result != null) {
                    BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource( imagePipeline.getCacheKey(imageRequest,context));
                    File file=((FileBinaryResource)resource).getFile();
                    return file;
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            } finally {
                dataSource.close();
            }
        }

        return null;
    }

    public static String generateUniqueFileName() {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toGMTString();
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        String rndchars = RandomStringUtils.randomAlphanumeric(16);
        filename = rndchars + "_" + datetime + "_" + millis;
        return filename;
    }

    public interface DownloadImageTaskCompleteListener {
        void onDownloadImageSuccess();

        void onDownloadImageFailure();
    }


    public void setDownloadImageTaskCompleteListener(DownloadImageTaskCompleteListener downloadImageTaskCompleteListener) {
        this.downloadImageTaskCompleteListener = downloadImageTaskCompleteListener;
    }
}
