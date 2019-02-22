package com.xt.jpa.helloworld;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xt
 * @date 2019/2/15 - 11:38
 * @description
 */

/**
 * @Entity 指出该 Java 类为实体类，将映射到指定的数据库表。
 * @Table  当实体类与其映射的数据库表名不同名时使用，name 用于指明数据库的表名
 */
@NamedQuery(name = "testNamedQuery", query = "select c from Customer c where c.id = ?")
@Cacheable(true)
@Table(name = "JPA_CUSTOMERS")
@Entity
public class Customer {
    private Integer id;
    private String lastName;
    private String email;
    private int age;
    private Date createdTime;
    private Date birth;
    private Date time;

    private Set<Order> orders = new HashSet<>();

    public Customer() {
    }

    public Customer(String lastName, int age) {
        this.lastName = lastName;
        this.age = age;
    }

    /**
     * 映射单向 1-n 的关联关系
     * 使用 @OneToMany 来映射 1-n 的关联关系
     * 使用 @JoinColumn 来映射外键列的名称
     * 可以使用 @OneToMany 的 fetch 属性来修改默认的加载策略
     * 可以通过 @OneToMany 的 cascade 属性来修改默认的删除策略。
     * 注意：若在 1 的一端的 @OneToMany 中使用 mappedBy 属性，则 @OneToMany 端就不能再使用 @JoinColumn 属性了。
     * @return
     */
//    @JoinColumn(name="customer_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE}, mappedBy = "customer")
    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    /**
     * @Id 用于声明一个实体类的属性映射为数据库的主键列。
     * @GeneratedValue  用于标注主键的生成策略，通过 strategy 属性指定。默认情况下，JPA 自动选择一个最适合底层数据库的主键生成策略。
     *                  AUTO： JPA自动选择合适的策略，是默认选项
     *
     * 用 table 来生成主键: 将当前主键的值单独保存到一个数据库的表中，主键的值每次都是从指定的表中查询来获得
     *    name 属性表示该表主键生成策略的名称，它被引用在@GeneratedValue中设置的“generator”值中。
     *    table 属性表示表生成策略所持久化的表名。
     *    catalog 属性和 schema 具体指定表所在的目录名或是数据库名。
     *    pkColumnName 属性的值表示在持久化表中，该主键生成策略所对应键值的名称。
     *    pkColumnValue 属性的值表示在持久化表中，该生成策略所对应的主键。
     *    valueColumnName 属性的值表示在持久化表中，该主键当前所生成的值，它的值将会随着每次创建累加。
     *    initialValue表示主键初识值，默认为0。
     *    allocationSize表示每次主键值增加的大小，例如设置成1，则表示每次创建新记录后自动加1，默认为50。
     *
     * @return
     */
    /*@TableGenerator(name = "ID_GENERATOR",
            table = "jpa_id_generators",
            pkColumnName = "PK_NAME",
            pkColumnValue = "CUSTOMER_ID",
            valueColumnName = "PK_VALUE",
            allocationSize = 100
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ID_GENERATOR")*/
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @Column 当实体的属性与其映射的数据库表的列不同名时需要使用。
     *         常用属性是 name，用于设置映射数据库表的列名
     * @return
     */
    @Column(name="LAST_NAME", length = 50, nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @Basic 对于没有任何标注的 getXxxx() 方法,默认即为@Basic
     * @return
     */
    @Basic
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @Temporal注解 调整Date精度
     * @return
     */
    @Column(name="created_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Temporal(TemporalType.DATE)
    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    @Temporal(TemporalType.TIME)
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * @Transient 表示该属性并非一个到数据库表的字段的映射,ORM框架将忽略该属性
     * @return
     */
    @Transient
    public String getInfo() {
        return "lastName: " + lastName + ", email: " + email;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", createdTime=" + createdTime +
                ", birth=" + birth +
                ", time=" + time +
                '}';
    }
}
