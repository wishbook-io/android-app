package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.commonmodels.ProductItem;
import com.wishbook.catalog.home.models.ProductObj;

/**
 * Created by root on 30/1/17.
 */
public class Response_SelectionBrandwise
{
    private String id ;

    private Catalogs[] catalogs;

    private String brand_name;

    public String getId  ()
    {
        return id ;
    }

    public void setId  (String id )
    {
        this.id  = id ;
    }

    public Catalogs[] getCatalogs ()
    {
        return catalogs;
    }

    public void setCatalogs (Catalogs[] catalogs)
    {
        this.catalogs = catalogs;
    }

    public String getBrand_name ()
    {
        return brand_name;
    }

    public void setBrand_name (String brand_name)
    {
        this.brand_name = brand_name;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id  = "+id +", catalogs = "+catalogs+", brand_name = "+brand_name+"]";
    }

    public class Catalogs
    {
        private String id;

        private String catalog_name;

        private ProductsSelection[] products;

        private String total_products;

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getCatalog_name ()
        {
            return catalog_name;
        }

        public void setCatalog_name (String catalog_name)
        {
            this.catalog_name = catalog_name;
        }

        public ProductsSelection[] getProducts ()
        {
            return products;
        }

        public void setProducts (ProductsSelection[] products)
        {
            this.products = products;
        }

        public String getTotal_products ()
        {
            return total_products;
        }

        public void setTotal_products (String total_products)
        {
            this.total_products = total_products;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [id = "+id+", catalog_name = "+catalog_name+", products = "+products+", total_products = "+total_products+"]";
        }
    }

}
