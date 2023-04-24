package com.wishbook.catalog.home.catalog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.SearchActivity;
import com.wishbook.catalog.home.cart.Fragment_MyCart;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CatalogHolderVersion2 extends GATrackedFragment {
    private View view;

    public static Toolbar toolbar;

    static FloatingActionButton add_catalog;
    @BindView(R.id.catalog_holder_container)
    FrameLayout catalog_holder_container;

    int catalogWishList = 0;

    Menu menu;

    CatalogTypeChangeListener CatalogTypeChangeListener;
    String layout_type;

    /**
     * Layout type 1 == GRID , Layout type == LINK
     *
     * @param savedInstanceState
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.catalog_holder_version2, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        if (UserInfo.getInstance(getActivity()).getDefaultProductView().equalsIgnoreCase(Constants.PRODUCT_VIEW_GRID)) {
            layout_type = Constants.PRODUCT_VIEW_GRID;
            getActivity().invalidateOptionsMenu();
        } else {
            layout_type = Constants.PRODUCT_VIEW_LIST;
            getActivity().invalidateOptionsMenu();
        }
        setupToolbar();
        add_catalog = view.findViewById(R.id.add_catalog);
        if (Activity_Home.pref.getString("groupstatus", "0").equals("2")) {
            add_catalog.setVisibility(View.GONE);
        } else if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            add_catalog.setVisibility(View.GONE);
        }
        showPublicCatalogFragment();
        initListener();
        return view;
    }


    public void setupToolbar() {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Browse");
        toolbar.inflateMenu(R.menu.menu_catalog_holder_version2);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d("TAG", "onMenuItemClick: ");
                switch (item.getItemId()) {
                    case R.id.action_search:
                        WishbookEvent wishbookEvent = new WishbookEvent();
                        wishbookEvent.setEvent_category(WishbookEvent.PRODUCT_EVENTS_CATEGORY);
                        wishbookEvent.setEvent_names("Products_Search_screen");
                        HashMap<String, String> prop = new HashMap<>();
                        prop.put("source", "product tab");
                        wishbookEvent.setEvent_properties(prop);
                        new WishbookTracker(getActivity(), wishbookEvent);

                     /*   String catalog_type = catalog_type_spinner.getSelectedItem().toString();
                        Intent intent = new Intent(getContext(), SearchActivity.class);
                        if (catalog_type.equalsIgnoreCase("Non-Catalog")) {
                            intent.putExtra("catalog_type", Constants.CATALOG_TYPE_NON);
                        } else if (catalog_type.equalsIgnoreCase("Screens")) {
                            intent.putExtra("catalog_type", Constants.CATALOG_TYPE_SCREEN);
                        } else {
                            intent.putExtra("catalog_type", Constants.CATALOG_TYPE_CAT);
                        }
                        startActivity(intent);*/
                        break;

                    case R.id.action_view_type:
                        if (layout_type == Constants.PRODUCT_VIEW_LIST) {
                            layout_type = Constants.PRODUCT_VIEW_GRID;
                            UserInfo.getInstance(getActivity()).setDefaultProductView(Constants.PRODUCT_VIEW_GRID);
                        } else if (layout_type == Constants.PRODUCT_VIEW_GRID) {
                            layout_type = Constants.PRODUCT_VIEW_LIST;
                            UserInfo.getInstance(getActivity()).setDefaultProductView(Constants.PRODUCT_VIEW_LIST);
                        }

                        if (CatalogTypeChangeListener != null) {
                            CatalogTypeChangeListener.changeViewType(layout_type);
                        }
                        getActivity().invalidateOptionsMenu();
                        break;
                    case R.id.action_wishlist:
                        Log.d("TAG", "onMenuItemClick: WishList Clik");
                        Application_Singleton.CONTAINER_TITLE = "My Wishlist";
                        Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
                        getActivity().startActivity(new Intent(getContext(), OpenContainer.class));
                        //StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                        break;
                    case R.id.action_cart:
                        try {
                            SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                            int cart_count = preferences.getInt("cartcount", 0);
                            Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                        break;
                }
                return false;
            }
        });
    }

    public void initListener() {
        add_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UserInfo.getInstance(getActivity()).isCompanyProfileSet()
                        && UserInfo.getInstance(getActivity()).getCompanyname().isEmpty()) {
                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put("type", "profile_update");
                    new DeepLinkFunction(param, getActivity());
                    return;
                }
                Intent intent = new Intent(getActivity(), Activity_AddCatalog.class);
                intent.putExtra("catalog_type", getSpinnerValue());
                startActivity(intent);
                WishbookTracker.sendScreenEvents(getActivity(), WishbookEvent.SELLER_EVENTS_CATEGORY, "CatalogItem_Add_screen", "product page", null);

            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (layout_type == Constants.PRODUCT_VIEW_LIST) {
            menu.findItem(R.id.action_view_type).setIcon(getResources().getDrawable(R.drawable.ic_view_grid_black_24dp));
        } else if (layout_type == Constants.PRODUCT_VIEW_GRID) {
            menu.findItem(R.id.action_view_type).setIcon(getResources().getDrawable(R.drawable.ic_view_list_black_24dp));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_catalog_holder_version2, menu);
        this.menu = menu;
        cartToolbarUpdate();
        wishListToolbarUpdate();
        isSavedFilterApplied();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //String catalog_type = catalog_type_spinner.getSelectedItem().toString();
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.action_wishlist:
                Log.d("TAG", "onMenuItemClick: WishList Clik");
                Application_Singleton.CONTAINER_TITLE = "My Wishlist";
                Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
                getActivity().startActivity(new Intent(getContext(), OpenContainer.class));
                break;
            case R.id.action_cart:
                try {
                    SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                    int cart_count = preferences.getInt("cartcount", 0);
                    Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                break;

            case R.id.action_saved_filter:

                break;
        }
        return true;
    }

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
        FloatingActionButton add_float = CatalogHolderVersion2.add_catalog;
        try {
            Activity_Home.pref = StaticFunctions.getAppSharedPreferences(context);
            if (Activity_Home.pref != null) {
                if (Activity_Home.pref.getString("groupstatus", "0").equals("2") || UserInfo.getInstance(context).getCompanyType().equals("buyer")) {
                    add_float = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (floatingActionButton != null) {
            final FloatingActionButton finalAdd_float = add_float;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && floatingActionButton.getVisibility() == View.VISIBLE) {
                        floatingActionButton.setVisibility(View.GONE);
                        if (finalAdd_float != null)
                            finalAdd_float.setVisibility(View.GONE);
                    } else if (dy < 0 && floatingActionButton.getVisibility() != View.VISIBLE) {
                        floatingActionButton.show();
                        if (finalAdd_float != null)
                            finalAdd_float.setVisibility(View.VISIBLE);
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
            }
            cartToolbarUpdate();
            wishListToolbarUpdate();
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

    public void showPublicCatalogFragment() {
        Fragment_BrowseProduct fragment_browseCatalogs = new Fragment_BrowseProduct();
        Bundle bundle = new Bundle();
        if (getArguments() != null && getArguments().getString("focus_position") != null) {
            bundle.putInt(Application_Singleton.adapterFocusPosition, Application_Singleton.Non_CATALOG_POSITION);
        }
        fragment_browseCatalogs.setArguments(bundle);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.catalog_holder_container, fragment_browseCatalogs).commit();
        Application_Singleton.Non_CATALOG_POSITION = 0;
    }


    public void cartToolbarUpdate() {
        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            try {
                SharedPreferences preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                int cartcount = preferences.getInt("cartcount", 0);
                if (cartcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(getActivity(), menu.findItem(R.id.action_cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(getActivity(), menu.findItem(R.id.action_cart), getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        } else {
            menu.findItem(R.id.action_cart).setVisible(false);
        }
    }

    private void wishListToolbarUpdate() {
        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            MenuItem wishlist_item = menu.findItem(R.id.action_wishlist);
            if (wishlist_item != null) {
                wishlist_item.setVisible(true);
            }
            int wishcount = UserInfo.getInstance(getActivity()).getWishlistCount();
            if (wishcount == 0) {
                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                ActionItemBadge.update(getActivity(), wishlist_item, getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), badgeStyleTransparent, "");
            } else {
                ActionItemBadge.update(getActivity(), wishlist_item, getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), ActionItemBadge.BadgeStyles.RED, wishcount);
            }
        } else {
            MenuItem wishlist_item = menu.findItem(R.id.action_wishlist);
            if (wishlist_item != null) {
                wishlist_item.setVisible(true);
            }
            wishlist_item.setVisible(false);
        }
    }

    private void isSavedFilterApplied() {
        if (!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            MenuItem wishlist_item = menu.findItem(R.id.action_saved_filter);
            if (wishlist_item != null) {
                wishlist_item.setVisible(true);
            }
            int wishcount = UserInfo.getInstance(getActivity()).getWishlistCount();
            if (wishcount == 0) {
                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                ActionItemBadge.update(getActivity(), wishlist_item, getResources().getDrawable(R.drawable.ic_save_filter_24dp), badgeStyleTransparent, "");
            } else {
                ActionItemBadge.update(getActivity(), wishlist_item, getResources().getDrawable(R.drawable.ic_save_filter_24dp), ActionItemBadge.BadgeStyles.RED, wishcount);
            }
        } else {
            MenuItem wishlist_item = menu.findItem(R.id.action_saved_filter);
            if (wishlist_item != null) {
                wishlist_item.setVisible(true);
            }
            wishlist_item.setVisible(false);
        }
    }

    public interface CatalogTypeChangeListener {
        void changeCatalog();

        void changeViewType(String layout_type);
    }

    public void setCatalogTypeChangeListener(CatalogTypeChangeListener CatalogTypeChangeListener) {
        this.CatalogTypeChangeListener = CatalogTypeChangeListener;
    }

    public static String getSpinnerValue() {

        return "catalog";

    }

    public static void setCatalog_type_spinner(String catalog_type) {

    }


}
