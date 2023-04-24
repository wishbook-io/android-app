
package com.wishbook.catalog.commonmodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CategoryTree {

    private Integer id;
    private List<ChildCategory> child_category = new ArrayList<ChildCategory>();
    private String category_name;
    private Integer parent_category;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public CategoryTree(Integer id, String category_name) {
        this.id = id;
        this.category_name = category_name;
    }

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The child_category
     */
    public List<ChildCategory> getchild_category() {
        return child_category;
    }

    /**
     * 
     * @param child_category
     *     The child_category
     */
    public void setchild_category(List<ChildCategory> child_category) {
        this.child_category = child_category;
    }

    /**
     * 
     * @return
     *     The category_name
     */
    public String getcategory_name() {
        return category_name;
    }

    /**
     * 
     * @param category_name
     *     The category_name
     */
    public void setcategory_name(String category_name) {
        this.category_name = category_name;
    }

    /**
     * 
     * @return
     *     The parent_category
     */
    public Integer getparent_category() {
        return parent_category;
    }

    /**
     * 
     * @param parent_category
     *     The parent_category
     */
    public void setparent_category(Integer parent_category) {
        this.parent_category = parent_category;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
