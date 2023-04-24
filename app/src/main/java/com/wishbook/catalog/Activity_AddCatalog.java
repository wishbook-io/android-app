package com.wishbook.catalog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.catalog.add.Fragment_AddCatalog_Version2;
import com.wishbook.catalog.home.catalog.add.Fragment_AddCatalog_Version_Step2;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_AddCatalog extends AppCompatActivity {

   /* @BindView(R.id.toolbar)
    Toolbar toolbar;*/

    @BindView(R.id.content_main)
    FrameLayout content_main;

    FragmentTransaction fragmentTransaction;

    private Activity_AddCatalog.OnBackPressedListener onBackPressedListener;
    private Activity_AddCatalog.NavigateListener navigateListener;

    // ##### Variable Initialize Start ########


    public int add_step;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_catalog_version2);
        ButterKnife.bind(this);
        add_step = 1;
        updateUI(false);
    }


    protected void setupToolbar() {
      /*  toolbar.setBackgroundColor(getResources().getColor(R.color.color_primary));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setTitle(Application_Singleton.CONTAINER_TITLE);
        toolbar.setTitleTextColor(Color.WHITE);
        Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_step = add_step - 1;
                onBackPressed();
            }
        });*/
    }


    protected void setStepOne() {
       // toolbar.setTitle("Upload Products");
        Bundle bundle = new Bundle();
        Fragment_AddCatalog_Version2 step_one_fragment = new Fragment_AddCatalog_Version2();
        bundle.putBoolean("isEditMode",getIntent().getBooleanExtra("isEditMode",false));
        if(getIntent()!=null && getIntent().getStringExtra("catalog_type")!=null) {
            bundle.putString("catalog_type",getIntent().getStringExtra("catalog_type"));
        }
        if(getIntent()!=null && getIntent().getStringExtra("catalog_id")!=null) {
            bundle.putString("catalog_id",getIntent().getStringExtra("catalog_id"));
        }

        if(getIntent()!=null && getIntent().getStringExtra("product_id")!=null) {
            bundle.putString("product_id",getIntent().getStringExtra("product_id"));
        }

        step_one_fragment.setArguments(bundle);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .add(R.id.content_main, step_one_fragment);
        fragmentTransaction.addToBackStack("STEP1");
        fragmentTransaction.commit();
        Log.e("TAG", "setStepOne: "+getSupportFragmentManager().getBackStackEntryCount());
    }

    protected void setStepTwo() {
       // toolbar.setTitle("Upload Products");
        Fragment_AddCatalog_Version_Step2 step_two_fragment = new Fragment_AddCatalog_Version_Step2();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .add(R.id.content_main, step_two_fragment);
        fragmentTransaction.addToBackStack("STEPTWO");
        fragmentTransaction.commit();

        Log.e("TAG", "setStepTwo: "+getSupportFragmentManager().getBackStackEntryCount());
    }


    public void updateUI(boolean isFromBack) {
        if (add_step == 1) {
            Log.e("TAG", "updateUI: "+getSupportFragmentManager().getBackStackEntryCount());
            if(isFromBack){
                Log.e("TAG", "updateUI: From Back "+getSupportFragmentManager().getBackStackEntryCount());
                getSupportFragmentManager().popBackStackImmediate();
            } else {
                setStepOne();
            }
        } else if (add_step == 2) {
            setStepTwo();
        }
    }


   /* @Override
    public void onBackPressed() {
        Log.e("TAG", "onBackPressed: Count=== "+add_step);
        if (getSupportFragmentManager().getBackStackEntryCount() > 1){
            getSupportFragmentManager().popBackStackImmediate();
            getSupportFragmentManager().beginTransaction().commit();
        } else {
            finish(); // Closes app
        }
    }*/

    /**
     * Pops the topmost fragment from the stack.
     * The lowest fragment can't be popped, it can only be replaced.
     *
     * @return false if the stack can't pop or true if a top fragment has been popped.
     */
    public boolean pop() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            return false;
        getSupportFragmentManager().popBackStackImmediate();
        return true;
    }

    public interface OnBackPressedListener {
        public void doBack();
    }

    public void setOnBackPressedListener(Activity_AddCatalog.OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (this.onBackPressedListener != null) {
            onBackPressedListener.doBack();
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Fragment_CatalogsGallery.BECOME_SELLER_REQUEST && resultCode == Activity.RESULT_OK) {
            Toast.makeText(Activity_AddCatalog.this, "You are succesfully seller of this catalog", Toast.LENGTH_LONG).show();
            successDialog(false, Activity_AddCatalog.this, null);
        }
    }

    public void successDialog(final boolean isVisible, final Activity context, final Response_catalog response_catalog) {
        final MaterialDialog materialDialog1 = new MaterialDialog.Builder(context)
                .customView(R.layout.succes_seller_dialog, true)
                .build();
        materialDialog1.getCustomView().findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog1.dismiss();
            }
        });
        materialDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        materialDialog1.getWindow().setDimAmount(0.8f);
        materialDialog1.setCanceledOnTouchOutside(true);
        materialDialog1.show();
        materialDialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(navigateListener!=null){
                    navigateListener.navigateDetail();
                }
            }

        });
    }

    public interface NavigateListener {

        void navigateDetail();
    }

    public void setNavigateListner(NavigateListener navigateListner){
        this.navigateListener = navigateListner;
    }
}
