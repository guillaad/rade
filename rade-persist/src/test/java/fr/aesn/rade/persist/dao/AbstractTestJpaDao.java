/*  This file is part of the Rade project (https://github.com/mgimpel/rade).
 *  Copyright (C) 2018 Marc Gimpel
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
/* $Id$ */
package fr.aesn.rade.persist.dao;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Abstract JUnit Test Class for JPA DAO.
 * Does all the necessary setup code (Derby in-memory database with Hibernate
 * JPA Entity and Transaction Managers).
 * 
 * @author Marc Gimpel (mgimpel@gmail.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public abstract class AbstractTestJpaDao {
  /** Static Spring Configuration. */
  @Configuration
  @EnableJpaRepositories(basePackages = "fr.aesn.rade.persist.dao")
  protected static class Config {
    @Bean
    protected DataSource dataSource() {
      DriverManagerDataSource ds = new DriverManagerDataSource();
      ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
      ds.setUrl("jdbc:derby:memory:testdb");
      ds.setUsername("sa");
      ds.setPassword("");
      return ds;
    }
    @Bean
    protected EntityManagerFactory entityManagerFactory() {
      LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
      entityManagerFactoryBean.setDataSource(dataSource());
      entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
      entityManagerFactoryBean.setPackagesToScan("fr.aesn.rade.persist.model");
      Properties jpaProperties = new Properties();
      jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.DerbyTenSevenDialect");
      jpaProperties.put("hibernate.hbm2ddl.auto", "update");
      jpaProperties.put("hibernate.show_sql", "false");
      jpaProperties.put("hibernate.enable_lazy_load_no_trans", "true");
      //jpaProperties.put("hibernate.format_sql", "create-drop");
      entityManagerFactoryBean.setJpaProperties(jpaProperties);
      entityManagerFactoryBean.afterPropertiesSet();
      return entityManagerFactoryBean.getObject();
    }
    @Bean
    protected EntityManager entityManager() {
      return entityManagerFactory().createEntityManager();
    }
    @Bean
    protected JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
      JpaTransactionManager transactionManager = new JpaTransactionManager();
      transactionManager.setEntityManagerFactory(entityManagerFactory);
      return transactionManager;
    }
  }
  /** In Memory Derby Database Instance. */
  protected static EmbeddedDatabase db;

  /**
   * Set up the Test Environment.
   */
  @BeforeClass
  public static void setUpClass() {
    // create temporary database for Hibernate
    db = new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.DERBY)
        .setScriptEncoding("UTF-8")
        .setName("testdb")
        .addScript("db/sql/create-tables.sql")
        .build();
  }

  /**
   * Close the Test Environment cleanly.
   */
  @AfterClass
  public static void tearDownClass() {
    db.shutdown();
  }
}
