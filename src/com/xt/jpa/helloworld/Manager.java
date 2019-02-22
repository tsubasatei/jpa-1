package com.xt.jpa.helloworld;

import javax.persistence.*;

/**
 * @author xt
 * @date 2019/2/16 - 13:57
 * @description 经理
 */
@Table(name="JPA_MANAGER")
@Entity
public class Manager {
    private Integer id;
    private String mgrName;
    private Department department;

    @GeneratedValue
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="mgr_name")
    public String getMgrName() {
        return mgrName;
    }

    public void setMgrName(String mgrName) {
        this.mgrName = mgrName;
    }

    /**
     * 对于不维护关联关系，没有外键的一方，使用 @OneToOne 来进行映射，建议设置 mappedBy=true
     * @return
     */
    @OneToOne(mappedBy = "mgr")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
