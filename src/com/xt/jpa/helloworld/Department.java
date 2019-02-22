package com.xt.jpa.helloworld;

import javax.persistence.*;

/**
 * @author xt
 * @date 2019/2/16 - 13:58
 * @description 部门
 */
@Table(name = "JPA_departments")
@Entity
public class Department {

    private Integer id;
    private String deptName;
    private Manager mgr;

    @GeneratedValue
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="dept_name")
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * 使用 @OneToOne 来映射 1-1 关联关系。
     * 若需要在当前数据表中添加主键则需要使用 @JoinColumn 来进行映射。
     * 注意：1-1 关联关系，所以需要添加 unique=true
     * @return
     */
    @JoinColumn(name="mgr_id", unique = true)
    @OneToOne(fetch = FetchType.LAZY)
    public Manager getMgr() {
        return mgr;
    }

    public void setMgr(Manager mgr) {
        this.mgr = mgr;
    }
}
