package com.wishbook.catalog.home.catalog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.Fragment_RecievedCatalogs;
import com.wishbook.catalog.home.Fragment_ShareStatus;
import com.wishbook.catalog.home.catalog.add.Fragment_AddCatalog;
import com.wishbook.catalog.home.inventory.barcode.SimpleScannerActivity;

/**
 * Created by root on 9/9/16.
 */
public class CatalogHolder extends GATrackedFragment {
    private View v;
    private TabLayout tabs;

    // public static CustomViewPager viewPager;
    public static Toolbar toolbar;


    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    public static String MYCATALOGS = "mycatalogs";
    public static String MYRECEIVED = "myreceived";
    public static String PUBLIC = "public";
    static FloatingActionButton add_catalog;
    int catalogWishList = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.catalog_holder, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle("Catalogs");
        tabs = (TabLayout) v.findViewById(R.id.tabs);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        add_catalog = v.findViewById(R.id.add_catalog);
        int NUM_ITEMS;
        if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("2")) {
            NUM_ITEMS = 2;
        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            NUM_ITEMS = 3;
        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            NUM_ITEMS = 4;
        } else {
            NUM_ITEMS = 4;
        }


        adapter = new ViewPagerAdapter(getChildFragmentManager(), NUM_ITEMS);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);


        if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("2")) {
            if (Application_Singleton.deep_link_filter != null) {
                if (Application_Singleton.deep_link_filter.get("view_type").equals("mycatalogs")) {
                    viewPager.setCurrentItem(1);
                } else if (Application_Singleton.deep_link_filter.get("view_type").equals("myreceived")) {
                    viewPager.setCurrentItem(0);
                }
            } else {
                viewPager.setCurrentItem(1);
            }
        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            if (Application_Singleton.deep_link_filter != null) {
                if (Application_Singleton.deep_link_filter.get("view_type").equals("mycatalogs")) {
                    viewPager.setCurrentItem(1);
                }
            } else {
                viewPager.setCurrentItem(0);
            }

        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            if (Application_Singleton.deep_link_filter != null) {
                if (Application_Singleton.deep_link_filter.get("view_type").equals("public")) {
                    viewPager.setCurrentItem(0);
                } else if (Application_Singleton.deep_link_filter.get("view_type").equals("myreceived")) {
                    viewPager.setCurrentItem(2);
                } else if (Application_Singleton.deep_link_filter.get("view_type").equals("wishlist")) {
                    viewPager.setCurrentItem(3);
                }
            } else {
                viewPager.setCurrentItem(0);
            }
        } else {
            if (Application_Singleton.deep_link_filter != null) {
                if (Application_Singleton.deep_link_filter.containsKey("view_type")) {
                    if (Application_Singleton.deep_link_filter.get("view_type").equals("public")) {
                        viewPager.setCurrentItem(0);
                    } else if (Application_Singleton.deep_link_filter.get("view_type").equals("myreceived")) {
                        viewPager.setCurrentItem(2);
                    } else if (Application_Singleton.deep_link_filter.get("view_type").equals("wishlist")) {
                        viewPager.setCurrentItem(3);
                    }
                } else {
                    viewPager.setCurrentItem(0);
                }
            } else {
                viewPager.setCurrentItem(0);
            }


        }


        //for banner redirect
        if (Application_Singleton.selectedCatalogInnerSubTab != 2) {
            viewPager.setCurrentItem(Application_Singleton.selectedCatalogInnerSubTab);
        }


        // indicator.setViewPager(pager);
        // setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);

        toolbar.inflateMenu(R.menu.menu_add_catalog);


        if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
            toolbar.getMenu().getItem(0).setVisible(false);
            toolbar.getMenu().getItem(1).setVisible(false);
            add_catalog.setVisibility(View.GONE);
        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            toolbar.getMenu().getItem(1).setVisible(false);
            add_catalog.setVisibility(View.GONE);
        } else {
            //setting tutorial
            //StaticFunctions.checkAndShowTutorial(getContext(), "add_catalog_tut_shown", toolbar.getChildAt(1).findViewById(R.id.menu_add), getResources().getString(R.string.add_catalog_tutorial_text), "bottom");
        }
        add_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.CONTAINER_TITLE = "Add Catalog";
                Application_Singleton.CONTAINERFRAG = new Fragment_AddCatalog();
                startActivityForResult(new Intent(getActivity(),OpenContainer.class),Application_Singleton.ADD_CATALOG_REQUEST);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (UserInfo.getInstance(getActivity()).isGuest()) {
                    StaticFunctions.ShowRegisterDialog(getActivity(),"Product Page");
                    return false;
                }

                switch (item.getItemId()) {
                    case R.id.menu_add:
                        Application_Singleton.CONTAINER_TITLE = "Add Catalog";
                        Application_Singleton.CONTAINERFRAG = new Fragment_AddCatalog();
                        startActivityForResult(new Intent(getActivity(),OpenContainer.class),Application_Singleton.ADD_CATALOG_REQUEST);
                        break;
                    case R.id.menu_scan_qr:
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CAMERA}, 1);
                        } else {
                            Intent i = new Intent(getActivity(), SimpleScannerActivity.class);
                            startActivityForResult(i, 1);
                        }
                        break;
                }
                return false;
            }
        });

        //Catalog Tutorial

        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            if (!Application_Singleton.tutorial_pref.getBoolean("catalog_tutorial", false)) {
               /* Intent intent = new Intent(getActivity(), IntroActivity.class);
                ArrayList<AppIntroModel> models = new ArrayList<>();
                models.add(new AppIntroModel(getString(R.string.intro_catalog_title), getString(R.string.intro_catalog_desc), R.drawable.intro_catalog_new));
                intent.putParcelableArrayListExtra("list", models);
                startActivity(intent);
                Application_Singleton.tutorial_pref.edit().putBoolean("catalog_tutorial", true).apply();*/
            }
        }


        return v;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS;
        SparseArray<GATrackedFragment> registeredFragments = new SparseArray<GATrackedFragment>();

        public ViewPagerAdapter(FragmentManager fragmentManager, int NUM_ITEMS) {
            super(fragmentManager);
            this.NUM_ITEMS = NUM_ITEMS;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public GATrackedFragment getItem(int position) {
            if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("2")) {
                GATrackedFragment result = new Fragment_RecievedCatalogs();
                switch (position) {
                    case 0:
                        // First Fragment of First Tab
                        result = new Fragment_RecievedCatalogs();
                        break;
                    case 1:
                        // First Fragment of Third Tab
                        result = new Fragment_Catalogs(CatalogHolder.MYCATALOGS);
                        break;
                    /* case 2:
                        // First Fragment of Third Tab
                        result = new Fragment_MySelections();
                        break;
                   case 3:
                        // First Fragment of Third Tab
                        result = new Fragment_MySelections();
                        break;*/
                }
                return result;
            } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                GATrackedFragment result = new Fragment_RecievedCatalogs();
                switch (position) {
                    case 0:
                        // First Fragment of First Tab
                        result = new Fragment_BrowseCatalogs();
                        break;

                    case 1:
                        // First Fragment of First Tab
                        // result = new Fragment_Brands();
                        result = new Fragment_Catalogs(CatalogHolder.MYCATALOGS);
                        break;
                    case 2:
                        // First Fragment of Third Tab

                        result = new Fragment_ShareStatus();
                        break;
                   /* case 2:
                        // First Fragment of Third Tab
                        result = new Fragment_MySelections();
                        break;
                    case 3:
                        // First Fragment of Third Tab

                        break;*/

                }

                return result;
            } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                GATrackedFragment result = new Fragment_RecievedCatalogs();
                switch (position) {
                    case 0:
                        // First Fragment of First Tab
                        result = new Fragment_BrowseCatalogs();
                        break;
                    case 1:
                        // First Fragment of First Tab
                        result = new Fragment_Brands();
                        break;
                    case 2:
                        // First Fragment of Second Tab
                        result = new Fragment_RecievedCatalogs();
                        break;
                    case 3:
                        // First Fragment of Third Tab
                        result = new Fragment_WishList();
                        break;

                }

                return result;
            } else {
                GATrackedFragment result = new Fragment_Catalogs();
                switch (position) {

                    case 0:
                        Fragment_BrowseCatalogs fragment_browseCatalogs = new Fragment_BrowseCatalogs();
                        // First Fragment of First Tab
                        result = fragment_browseCatalogs;
                        break;
                    case 1:
                        // First Fragment of First Tab
                        result = new Fragment_Brands();
                        break;
                    case 2:
                        // First Fragment of Second Tab
                        result = new Fragment_RecievedCatalogs();
                        break;
                    case 3:
                        // First Fragment of Third Tab
                        result = new Fragment_WishList();
                        break;
                 /*   case 4:
                        // First Fragment of Third Tab
                        result = new Fragment_MySelections();
                        break;
                    case 5:
                        // First Fragment of Third Tab
                        result = new Fragment_ShareStatus();
                        break;*/

                }

                return result;

            }
        }

        /**
         * On each Fragment instantiation we are saving the reference of that Fragment in a Map
         * It will help us to retrieve the Fragment by position
         *
         * @param container
         * @param position
         * @return
         */
       /* @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }*/

        /**
         * Remove the saved reference from our Map on the Fragment destroy
         *
         * @param container
         * @param position
         * @param object
         */
      /*  @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }
*/

        /**
         * Get the Fragment by position
         *
         * @param position tab position of the fragment
         * @return
         */
        public GATrackedFragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


        @Override
        public CharSequence getPageTitle(final int position) {
            if (UserInfo.getInstance(getActivity()).getGroupstatus().equals("2")) {
                switch (position) {
                    case 0:
                        return "Received";
                    case 1:
                        return "MyCatalog";
                   /* case 2:
                        return "MySelection";
                    case 3:
                        return "MySelection";*/
                    default:
                        return "Received";
                }
            } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                switch (position) {
                    case 0:
                        return "Public";
                    case 1:
                        return "MyCatalog";
                    case 2:
                        return "SharedByMe";
                    /*case 2:
                        return "MySelection";
                    case 3:*/

                    default:
                        return "MyCatalog";
                }
            } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                switch (position) {
                    case 0:
                        return "Public";
                    case 1:
                        return "Brands";
                    case 2:
                        return "Received";
                    case 3:
                        return "Wishlist";
                 /*   case 3:
                        return "MySelection";*/
                    default:
                        return "Received";
                }
            } else {
                switch (position) {

                    case 0:
                        return "Public";
                    case 1:
                        return "Brands";
                    case 2:
                        return "Received";
                    case 3:
                        return "Wishlist";
                   /* case 4:
                        return "MySelection";
                    case 5:
                        return "SharedByMe";*/
                    default:
                        return "Received";
                }

            }
        }


    }
  /*
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new  Fragment_Brands(), "Brand");
        adapter.addFragment(new Fragment_RecievedCatalogs(), "Received Catalog");
        adapter.addFragment(new Fragment_Catalogs(), "MyCatalog");
        adapter.addFragment(new Fragment_MySelections(), "MySelection");
        adapter.addFragment(new Fragment_ShareStatus(), "SharedByMe");
        viewPager.setAdapter(adapter);
    }
  class ViewPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }*/

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter =new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String url = data.getStringExtra("content");
                Uri intentUri = Uri.parse(url);
                if (intentUri != null) {
                    String catalog = intentUri.getPath().replaceAll("\\D+", "");
                    String supplier = intentUri.getQueryParameter("supplier");
                    StaticFunctions.catalogQR(catalog, (AppCompatActivity) getActivity(), supplier);
                }
            }
        }
    }

    public static void toggleFloating(RecyclerView recyclerView, Context context) {
        final FloatingActionButton floatingActionButton = Activity_Home.support_chat_fab;
        FloatingActionButton add_float = CatalogHolder.add_catalog;
        try {
            Activity_Home.pref = StaticFunctions.getAppSharedPreferences(context);
            if (Activity_Home.pref != null) {
                if (Activity_Home.pref.getString("groupstatus", "0").equals("2") || UserInfo.getInstance(context).getCompanyType().equals("buyer")) {
                    add_float = null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (floatingActionButton != null) {
            final FloatingActionButton finalAdd_float = add_float;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && floatingActionButton.getVisibility() == View.VISIBLE) {
                        floatingActionButton.hide();
                        if (finalAdd_float != null)
                            finalAdd_float.hide();
                    } else if (dy < 0 && floatingActionButton.getVisibility() != View.VISIBLE) {
                        floatingActionButton.show();
                        if (finalAdd_float != null)
                            finalAdd_float.show();
                    }
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
                // wishlist count
                int wishlistCount = UserInfo.getInstance(getActivity()).getWishlistCount();
                if (catalogWishList == wishlistCount) {
                    // not update wishlist
                    Fragment_WishList.isRequiredUpdateData = false;
                } else {
                    // update data
                    Fragment_WishList.isRequiredUpdateData = true;
                }
                catalogWishList = wishlistCount;
                tabs.getTabAt(3).setText("WishList" + "(" + wishlistCount + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        final FloatingActionButton floatingActionButton = Activity_Home.support_chat_fab;
        if (floatingActionButton != null) {
            floatingActionButton.show();
        }
    }
}
