package com.xt.jpa.test;

import com.xt.jpa.helloworld.*;
import org.hibernate.annotations.QueryHints;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author xt
 * @date 2019/2/15 - 15:29
 * @description
 */
public class JPATest {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction transaction;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa-1");
        entityManager = entityManagerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();
    }

    @After
    public void destroy() {
        transaction.commit();
        entityManager.close();
        entityManagerFactory.close();
    }

    /**
     * 可以使用 JPQL 完成 UPDATE 和 DELETE 操作。
     */
    @Test
    public void testJpqlExecute() {
        String jpql = "update Customer c set c.lastName=?1 where c.id=?2";
        Query query = entityManager.createQuery(jpql).setParameter(1, "lhxt").setParameter(2, 12);
        query.executeUpdate();
    }

    /**
     * 使用 jpql 内建的函数
     */
    @Test
    public void testJpqlFunction() {
        String jpql = "select upper(c.email) from Customer c";
        List<String> emails = entityManager.createQuery(jpql).getResultList();
        System.out.println(emails);
    }

    @Test
    public void testSubQuery() {
        // 查询所有 Customer 的 lastName为xt3 的Order
        String jpql = "select o from Order o where o.customer = (select c from Customer c where c.lastName=?)";
        List<Order> list = entityManager.createQuery(jpql).setParameter(1, "xt3").getResultList();
        System.out.println(list.size());
    }

    /**
     * JPQL 的关联查询统 HQL 的关联查询
     */
    @Test
    public void testLeftOuterJoin() {
        String jpql = "from Customer c left outer join fetch c.orders where c.id=?";
        Query query = entityManager.createQuery(jpql).setParameter(1, 12);
        Customer customer = (Customer) query.getSingleResult();
        System.out.println(customer.getLastName());
        System.out.println(customer.getOrders().size());

//        List<Object[]> list = entityManager.createQuery(jpql).setParameter(1, 12).getResultList();
//        System.out.println(list);
    }

    /**
     * 查询 order 数量大于2的那些customer
     */
    @Test
    public void testGroupBy() {
        String jpql = "select o.customer from Order o group by o.customer having count(o.id)>=2";
        Query query = entityManager.createQuery(jpql);
        List<Customer> customers = query.getResultList();
        System.out.println(customers);
    }

    @Test
    public void testOrderBy() {
        String jpql = "from Customer where age > ? order by age desc ";
        Query query = entityManager.createQuery(jpql).setHint(QueryHints.CACHEABLE, true);

        // 占位符的索引是从 1 开始
        query.setParameter(1, 1);
        List<Customer> customers = query.getResultList();
        System.out.println(customers.size());
    }

    /**
     * 使用 hibernate 的查询缓存。
     */
    @Test
    public void testQueryCache() {
        String jpql = "from Customer where age > ?";
        Query query = entityManager.createQuery(jpql).setHint(QueryHints.CACHEABLE, true);

        // 占位符的索引是从 1 开始
        query.setParameter(1, 1);
        List<Customer> customers = query.getResultList();
        System.out.println(customers.size());

        query = entityManager.createQuery(jpql).setHint(QueryHints.CACHEABLE, true);

        // 占位符的索引是从 1 开始
        query.setParameter(1, 1);
        customers = query.getResultList();
        System.out.println(customers.size());
    }

    /**
     * createNativeQuery 适用于本地 SQL
     */
    @Test
    public void testNativeQuery() {
        String sql = "select age from jpa_customers where id=?";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, 2);
        Object result = query.getSingleResult();
        System.out.println(result);
    }

    /**
     * createNamedQuery 适用于在实体类前使用 @NamedQuery 标记的查询语句
     */
    @Test
    public void testNamedQuery() {
        Query query = entityManager.createNamedQuery("testNamedQuery").setParameter(1, 2);
        Customer customer = (Customer) query.getSingleResult();
        System.out.println(customer);
    }

    /**
     * 默认情况下，若只查询部分属性，则将返回 Object[] 类型的结果，或者 Object[] 类型的 List。
     * 也可以在实体类中创建对应的构造器，然后再 JPQL 语句中利用对应的构造器返回实体类对象。
     */
    @Test
    public void testPartlyProperties() {
        String jpql = "SELECT new Customer(c.lastName, c.age) FROM Customer c where c.id>?";
        List result = entityManager.createQuery(jpql).setParameter(1, 10).getResultList();
        System.out.println(result);
    }

    @Test
    public void testHelloJPQL() {
        String jpql = "from Customer where age > ?";
        Query query = entityManager.createQuery(jpql);

        // 占位符的索引是从 1 开始
        query.setParameter(1, 1);
        List<Customer> customers = query.getResultList();
        System.out.println(customers.size());
    }

    @Test
    public void testSecondeLevelCache() {
        Customer customer1 = entityManager.find(Customer.class, 2);
        transaction.commit();
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();

        Customer customer2 = entityManager.find(Customer.class, 2);

    }

    // 多对多关联关系
    /**
     * 对于关联的集合对象，默认使用懒加载的策略。
     * 使用维护关联关系的一方获取，还是使用不维护关联关系的一方获取，SQL 语句相同。
     */
    @Test
    public void testManyToManyFind() {
//        Item item = entityManager.find(Item.class,1);
//        System.out.println(item.getItemName());

//        System.out.println(item.getCategories().size());
        Category category = entityManager.find(Category.class, 1);
        System.out.println(category.getCategoryName());
        System.out.println(category.getItems().size());
    }

    /**
     * 多对多的保存
     */
    @Test
    public void testManyToManyPersist() {
        Item i1 = new Item();
        i1.setItemName("i-3");

        Item i2 = new Item();
        i2.setItemName("i-4");

        Category c1 = new Category();
        c1.setCategoryName("c-3");

        Category c2 =  new Category();
        c2.setCategoryName("c-4");

        // 设置关联关系
        i1.getCategories().add(c1);
        i1.getCategories().add(c2);
        i2.getCategories().add(c1);
        i2.getCategories().add(c2);

        c1.getItems().add(i1);
        c1.getItems().add(i2);
        c2.getItems().add(i1);
        c2.getItems().add(i2);

        // 执行保存
        entityManager.persist(c1);
        entityManager.persist(c2);
        entityManager.persist(i1);
        entityManager.persist(i2);
    }

    // 双向一对一关联关系
    /**
     * 1. 默认情况下，若获取不维护关联关系的一方，则也会通过左外连接获取其关联的对象。
     * 可以通过 @OneToOne 的 fetch 属性来修改加载策略，但依然会再发送 SQL 语句来初始化其关联的对象
     * 这说明在不维护关联关系的一方，不建议修改 fetch属性
     */
    @Test
    public void testOneToOneFind2() {
        Manager manager = entityManager.find(Manager.class, 1);
        System.out.println(manager.getMgrName());

        System.out.println(manager.getDepartment().getClass().getName());
    }

    /**
     * 1. 默认情况下，若获取维护关联关系的一方，则会通过左外连接获取其关联的对象。
     * 但可以通过 @OneToOne 的 fetch 属性来修改加载策略。
     */
    @Test
    public void testOneToOneFind1() {
        Department department = entityManager.find(Department.class, 1);
        System.out.println(department.getDeptName());
        System.out.println(department.getMgr().getClass().getName());

    }

    /**
     * 双向 1-1 的关联关系，建议先保存不维护关联关系的一方，即没有外键的一方，这样不会多出 update 语句。
     */
    @Test
    public void testOneToOnePersist() {
        Department department = new Department();
        department.setDeptName("D-BB");

        Manager manager = new Manager();
        manager.setMgrName("M-BB");

        // 设置关联关系
        department.setMgr(manager);
        manager.setDepartment(department);

        // 执行保存操作
        entityManager.persist(department);
        entityManager.persist(manager);
    }

    // 一对多关联关系
    @Test
    public void testOneToManyUpdate() {
        Customer customer = entityManager.find(Customer.class, 10);

        customer.getOrders().iterator().next().setOrderName("XXX");
    }

    /**
     * 默认情况下，若删除 1 的一端，则会先把关联的 n 的一端的外键置空，然后进行删除。
     * 可以通过 @OneToMany 的 cascade 属性来修改默认的删除策略。
     */
    @Test
    public void testOneToManyRemove() {
        Customer customer = entityManager.find(Customer.class, 8);
        entityManager.remove(customer);
    }

    /**
     * 默认对关联的多的一方使用懒加载的加载策略
     * 可以使用 @OneToMany 的 fetch 属性来修改默认的加载策略
     */
    @Test
    public void testOneToManyFind() {
        Customer customer = entityManager.find(Customer.class, 9);
        System.out.println(customer.getLastName());

        System.out.println(customer.getOrders().size());
    }

    /**
     * 若是双向 1-n 的关联关系，执行保存时
     * 若先保存 n 的一端，再保存 1 的一端，默认情况下，会多出 n 条 update 语句。
     * 若先保存 1 的一端，则会多出 n 条 update 语句。
     * 在进行双向 1-n 关联关系是，建议使用 n 的一方来维护关联关系，而 1 的一方不维护关联关系，这样会有效的减少 SQL 语句。
     * 注意：若在 1 的一端的 @OneToMany 中使用 mappedBy 属性，则 @OneToMany 端就不能再使用 @JoinColumn 属性了。
     *
     * 单向 1-n 关联关系执行保存时，一定会多出 update 语句。
     * 因为 n 的一端在插入时不会同时插入外键列。
     */
    @Test
    public void testOneToManyPersist() {
        Customer customer = new Customer();
        customer.setLastName("xt3");
        customer.setEmail("xt3@163.com");
        customer.setAge(30);
        customer.setBirth(new Date());
        customer.setTime(new Date());
        customer.setCreatedTime(new Date());

        Order order1 = new Order();
        order1.setOrderName("O-H-1");

        Order order2 = new Order();
        order2.setOrderName("O-H-2");

        // 建立关联关系
        customer.getOrders().add(order1);
        customer.getOrders().add(order2);

        order1.setCustomer(customer);
        order2.setCustomer(customer);

        // 执行保存操作
        entityManager.persist(customer);
        entityManager.persist(order1);
        entityManager.persist(order2);
    }

    // 多对一关联关系
    /*@Test
    public void testManyToOneUpdate() {
        Order order = entityManager.find(Order.class, 2);
        order.getCustomer().setLastName("XXX");
    }
    *//**
     * 不能直接删除 1 的一端，因为有外键约束。
     *//*
    @Test
    public void testManyToOneRemove() {
//        Order order = entityManager.find(Order.class, 1);
//        entityManager.remove(order);

        Customer customer = entityManager.find(Customer.class, 7);
        entityManager.remove(customer);
    }
    *//**
     * 默认情况下，使用左外连接的方式来获取 n 的一端的对象和其关联的 1 的一端的对象。
     * 可使用 @ManyToOne 的 fetch 属性来修改默认的关联属性的加载策略
     *//*
    @Test
    public void testManyToOneFind() {
        Order order = entityManager.find(Order.class, 1);
        System.out.println(order.getOrderName());
        System.out.println(order.getCustomer().getLastName());
    }
    *//**
     * 保存多对一时，建议先保存 1 的一端，后保存 n 的一端，
     * 这样不会多出额外的 update 操作
     *//*
    @Test
    public void testManyToOnePersist() {
        Customer customer = new Customer();
        customer.setLastName("xt2");
        customer.setEmail("xt2@163.com");
        customer.setAge(30);
        customer.setBirth(new Date());
        customer.setTime(new Date());
        customer.setCreatedTime(new Date());

        Order order1 = new Order();
        order1.setOrderName("O-T-1");

        Order order2 = new Order();
        order2.setOrderName("O-T-2");

        // 设置关联关系
        order1.setCustomer(customer);
        order2.setCustomer(customer);

        // 执行保存操作
        entityManager.persist(customer);
        entityManager.persist(order1);
        entityManager.persist(order2);

    }*/

    /**
     * 同 hibernate 中 Session 的 refresh 方法。
     * 用数据库实体记录的值更新实体对象的状态，即更新实例的属性值。
     */
    @Test
    public void testRefresh() {
        Customer customer = entityManager.find(Customer.class, 2);
        customer = entityManager.find(Customer.class, 2);

        entityManager.refresh(customer);
    }

    /**
     * 同 hibernate 中 Session 的 flush 方法。
     * 同步持久上下文环境，即将持久上下文环境的所有未保存实体的状态信息保存到数据库中。
     */
    @Test
    public void testFlush() {
        Customer customer = entityManager.find(Customer.class, 2);
        System.out.println(customer);

        customer.setLastName("lh");

        entityManager.flush();
    }
    /**
     * 若传入的是一个游离对象，即传入的对象有OID
     * 1. 若在 EntityManager 缓存中有对应的对象
     * 2. JPA 会把游离对象的属性复制到查询到 EntityManager 缓存中的对象中。
     * 3. EntityManager 缓存中的对象执行 update 操作
     */
    @Test
    public void testMerger4() {
        Customer customer = new Customer();
        customer.setLastName("sanae");
        customer.setEmail("sanae@163.com");
        customer.setAge(29);
        customer.setBirth(new Date());
        customer.setTime(new Date());
        customer.setCreatedTime(new Date());
        customer.setId(6);

        Customer customer2 = entityManager.find(customer.getClass(), 6);

        entityManager.merge(customer);
        System.out.println(customer == customer2); //false
    }

    /**
     * 若传入的是一个游离对象，即传入的对象有OID
     * 1. 若在 EntityManager 缓存中没有该对象
     * 2. 若在数据库中也有对应的记录
     * 3. JPA 会查询对应的记录，然后返回该记录对应的对象，再然后会把游离对象的属性复制到查询到的对象中。
     * 4. 对查询到的对象执行 update 操作
     */
    @Test
    public void testMerger3() {
        Customer customer = new Customer();
        customer.setLastName("xt");
        customer.setEmail("xt@163.com");
        customer.setAge(30);
        customer.setBirth(new Date());
        customer.setTime(new Date());
        customer.setCreatedTime(new Date());
        customer.setId(6);

        Customer customer2 = entityManager.merge(customer);

        System.out.println(customer == customer2); //false
    }

    /**
     * 若传入的是一个游离对象，即传入的对象有OID
     * 1. 若在 EntityManager 缓存中没有该对象
     * 2. 若在数据库中也没有对应的记录
     * 3. JPA 会创建一个新的对象，然后把当前游离对象的属性复制到新创建的对象中
     * 4. 对新创建的对象执行 insert 操作
     */
    @Test
    public void testMerger2() {
        Customer customer = new Customer();
        customer.setLastName("sanae");
        customer.setEmail("sanae@163.com");
        customer.setAge(28);
        customer.setBirth(new Date());
        customer.setTime(new Date());
        customer.setCreatedTime(new Date());
        customer.setId(100);

        Customer customer2 = entityManager.merge(customer);

        System.out.println("customer#id: " + customer.getId());
        System.out.println("customer2#id: " + customer2.getId());
    }

    /**
     * 总的来说：类似于 hibernate 中 Session 的 saveOrUpdate 方法。
     *
     * 1. 若传入的是一个临时对象
     * 会创建一个新的对象，把临时对象的属性复制到新的对象中，然后对新的对象执行持久化操作
     * 所以，新的对象中有id，但以前的临时对象中没有id。
     */
    @Test
    public void testMerge1() {
        Customer customer = new Customer();
        customer.setLastName("tsubasa");
        customer.setEmail("tsubasa@163.com");
        customer.setAge(28);
        customer.setBirth(new Date());
        customer.setTime(new Date());
        customer.setCreatedTime(new Date());

        Customer customer2 = entityManager.merge(customer);

        System.out.println("customer#id: " + customer.getId());
        System.out.println("customer2#id: " + customer2.getId());
    }

    // 类似于 hibernate 中 Session 的 delete 方法。 把对象对应的记录从数据库中移除。
    // 但注意：该方法只能移除 持久化对象。 而 hibernate 的 delete 方法实际上还可以移除 游离对象。
    @Test
    public void testRemove() {
//        Customer customer = new Customer();
//        customer.setId(1);

        Customer customer = entityManager.find(Customer.class, 1);
        entityManager.remove(customer);
    }

    // 类似于 hibernate 中 Session 的 save 方法。 使对象由临时状态变为持久化状态。
    // 和 hibernate 的save 方法的不同之处：若对象有id，则不能执行 insert 操作，而会抛出异常。
    @Test
    public void testPersist() {
        Customer customer = new Customer();
        customer.setLastName("hiroi");
        customer.setEmail("hiroi@163.com");
        customer.setAge(26);
        customer.setBirth(new Date());
        customer.setTime(new Date());
        customer.setCreatedTime(new Date());
//        customer.setId(100);

        entityManager.persist(customer);
    }

    // 类似于 hibernate 中 Session 的 load 方法。
    @Test
    public void testGetReference() {
        Customer customer = entityManager.getReference(Customer.class, 3);
        System.out.println(customer.getClass().getName());

        System.out.println("--------------------------");
//        transaction.commit();
//        entityManager.close();

        System.out.println(customer.toString());
    }

    // 类似于 hibernate 中 Session 的 get 方法。
    @Test
    public void testFind() {
        Customer customer = entityManager.find(Customer.class, 1);
        System.out.println("--------------------------");

        System.out.println(customer.toString());
    }
}
