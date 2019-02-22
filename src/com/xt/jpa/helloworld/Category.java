package com.xt.jpa.helloworld;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xt
 * @date 2019/2/16 - 15:05
 * @description
 */
@Table(name = "JPA_CATEGORY")
@Entity
public class Category {

    private Integer id;
    private String categoryName;
    private Set<Item> items = new HashSet<>();

    @GeneratedValue
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "category_name")
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @ManyToMany(mappedBy = "categories")
    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }
}
