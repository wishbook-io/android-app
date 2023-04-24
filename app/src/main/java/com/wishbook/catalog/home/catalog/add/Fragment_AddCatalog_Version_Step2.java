package com.wishbook.catalog.home.catalog.add;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.imagecropper.PhotoTakerUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_AddCatalog_Version_Step2 extends GATrackedFragment {

    @BindView(R.id.edit_product_selection)
    EditText edit_product_selection;

    @BindView(R.id.check_same_price)
    CheckBox check_same_price;

    @BindView(R.id.img_cover)
    ImageView img_cover;

    @BindView(R.id.btn_upload_cover)
    TextView btn_upload_cover;

    @BindView(R.id.radio_full_catalog)
    RadioButton radio_full_catalog;

    @BindView(R.id.radio_single_piece_catalog)
    RadioButton radio_single_piece_catalog;


    /// ###### Variable Initialize Start  ############
    private Bitmap bitmapImage = null;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.activity_add_catalog_step_2, ga_container, true);
        ButterKnife.bind(this, v);
        initListner();
        return v;
    }


    public void initListner() {
        btn_upload_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotoUploadWidget();
            }
        });
        img_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoUploadWidget();
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 900) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openPhotoUploadWidget();
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }


    private void openPhotoUploadWidget() {
        String[] permissions = {
                "android.permission.CAMERA",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 900);
        } else {
            PhotoTakerUtils.openTaker(getActivity(), "catalogpreview.jpg", true, new PhotoTakerUtils.OnCropFinishListener() {
                @Override
                public void OnCropFinsh(Bitmap bitmap) {
                    img_cover.setImageBitmap(bitmap);
                    if (bitmap != null) {
                        String decoded = scanQRImage(bitmap);
                        if (decoded != null) {
                            Toast.makeText(getActivity(), decoded, Toast.LENGTH_LONG).show();
                        }
                    }

                    bitmapImage = bitmap;

                    File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
                    File output = new File(mDirectory, "catalogpreview.jpg");
                    Log.v("selected image", output.getAbsolutePath());
                    if (output.exists()) {
                        Log.v("selected image exits", output.getAbsolutePath());
                    }
                    btn_upload_cover.setText("Change Cover Photo");
                }
            });
        }

    }

    public String scanQRImage(Bitmap bMap) {
        String contents = null;

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
        } catch (Exception e) {
            Log.e("QrTest", "Error decoding barcode", e);
        }
        return contents;
    }


}
