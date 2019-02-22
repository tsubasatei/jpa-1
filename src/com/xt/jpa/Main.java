package com.xt.jpa;

import com.xt.jpa.helloworld.Customer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xt
 * @date 2019/2/15 - 11:50
 * @description
 */
public class Main {
    public static void main(String[] args) {
        // 1. 创建 EntityManagerFactory
        String persistenceUnitName = "jpa-1";
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.show_sql", true);
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory(persistenceUnitName);
//                Persistence.createEntityManagerFactory(persistenceUnitName, properties);
        
        // 2. 创建 EntityManager 类似于 Hibernate 的 SessionFactory
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        // 3. 开启事务
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        // 4. 进行持久化操作
        Customer customer = new Customer();
        customer.setLastName("tei");
        customer.setEmail("tei@163.com");
        customer.setAge(30);
        customer.setCreatedTime(new Date());
        customer.setBirth(new Date());
        customer.setTime(new Date());

        entityManager.persist(customer);

        // 5. 提交事务
        transaction.commit();

        // 6. 关闭 EntityManager
        entityManager.close();

        // 7. 关闭 EntityManagerFactory
        entityManagerFactory.close();
    }
}
