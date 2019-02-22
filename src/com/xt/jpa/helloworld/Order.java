package com.xt.jpa.helloworld;

import javax.persistence.*;

/**
 * @author xt
 * @date 2019/2/16 - 9:29
 * @description 订单类
 */
@Table(name = "JPA_ORDERS")
@Entity
public class Order {
    private Integer id;
    private String orderName;
    private Customer customer;

    @GeneratedValue
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="order_name")
    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    /**
     * 映射单向 n-1 的关联关系
     * 使用 @ManyToOne 来映射多对一的关联关系
     * 使用 @JoinColumn 来映射外键
     * 可使用 @ManyToOne 的 fetch 属性来修改默认的关联属性的加载策略
     * @return
     */
    @JoinColumn(name = "customer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
