package com.wishbook.catalog.home.catalog.add;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.home.Fragment_RecievedCatalogs;
import com.wishbook.catalog.home.Fragment_ShareStatus;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.Fragment_Brands;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.Fragment_MySelections;

/**
 * Created by root on 21/9/16.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {



    SparseArray<GATrackedFragment> registeredFragments = new SparseArray<GATrackedFragment>();

    /**
     * Create pager adapter
     *
     * @param fm
     */
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public GATrackedFragment getItem(int position) {
        final GATrackedFragment result;
        switch (position) {
            case 0:
                // First Fragment of First Tab
                result = new Fragment_Brands();
                break;
            case 1:
                // First Fragment of Second Tab
                result = new Fragment_RecievedCatalogs();
                break;
            case 2:
                // First Fragment of Third Tab
                result = new Fragment_Catalogs(CatalogHolder.MYCATALOGS);
                break;
            case 3:
                // First Fragment of Third Tab
                result = new Fragment_MySelections();
                break;
            case 4:
                // First Fragment of Third Tab
                result = new Fragment_ShareStatus();
                break;
            default:
                result = null;
                break;
        }

        return result;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        switch (position) {
            case 0:
                return "Brands";
            case 1:
                return "Received Catalog";
            case 2:
                return "MyCatalog";
            case 3:
                return "MySelection";
            case 4:
                return "SharedByMe";
            default:
                return null;
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
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        GATrackedFragment fragment = (GATrackedFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    /**
     * Remove the saved reference from our Map on the Fragment destroy
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }


    /**
     * Get the Fragment by position
     *
     * @param position tab position of the fragment
     * @return
     */
    public GATrackedFragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
