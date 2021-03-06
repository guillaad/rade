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
package fr.aesn.rade.service;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import fr.aesn.rade.persist.dao.DepartementJpaDao;
import fr.aesn.rade.persist.model.Departement;
import fr.aesn.rade.service.impl.DepartementServiceImpl;

/**
 * JUnit Test for DelegationService.
 * 
 * @author Marc Gimpel (mgimpel@gmail.com)
 */
public class TestDepartementService
  extends AbstractTestService {
  /** DAO for the Service to be tested. */
  @Autowired
  private DepartementJpaDao jpaDao;
  /** Service  to be tested. */
  private DepartementService service;

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
        .addScript("db/sql/insert-TypeEntiteAdmin.sql")
        .addScript("db/sql/insert-TypeNomClair.sql")
        .addScript("db/sql/insert-StatutModification.sql")
        .addScript("db/sql/insert-TypeGenealogieEntiteAdmin.sql")
        .addScript("db/sql/insert-Audit.sql")
        .addScript("db/sql/insert-Region.sql")
        .addScript("db/sql/insert-RegionGenealogie.sql")
        .addScript("db/sql/insert-Departement.sql")
        .addScript("db/sql/insert-DepartementGenealogie.sql")
        .build();
  }

  /**
   * Set up the Test Environment.
   */
  @Before
  public void setUp() {
    service = new DepartementServiceImpl();
    ((DepartementServiceImpl)service).setDepartementJpaDao(jpaDao);
  }

  /**
   * Test getting a the list of all Departements.
   */
  @Test
  public void testGettingDepartementList() {
    List<Departement> list = service.getAllDepartement();
    assertNotNull("DepartementService returned a null list", list);
    assertEquals(173, list.size());
    for (Departement dept : list) {
      assertNotNull("Hibernate returned a List but an Entity is null",
                    dept);
    }
  }

  /**
   * Test getting a the list of all Departements.
   * @throws ParseException failed to parse date.
   */
  @Test
  public void testGettingDepartementById() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Departement dept = service.getDepartementById(197);
    assertNotNull("Hibernate didn't return a Departement", dept);
    assertEquals("Hibernate returned a Departement, but the Id doesn't match",
                 197, dept.getId().intValue());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 sdf.parse("1999-01-01"), dept.getDebutValidite());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 null, dept.getFinValidite());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 null, dept.getArticleEnrichi());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "GUADELOUPE", dept.getNomMajuscule());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "Guadeloupe", dept.getNomEnrichi());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "", dept.getCommentaire());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "3", dept.getTypeNomClair().getCode());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "DEP", dept.getTypeEntiteAdmin().getCode());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 1, dept.getAudit().getId().intValue());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "971", dept.getCodeInsee());
    assertEquals("vdept returned a Departement, but a field doesn't match",
                 "97105", dept.getChefLieu());
    assertEquals("vdept returned a Departement, but a field doesn't match",
                 "01", dept.getRegion());
    assertNull(service.getDepartementById(0));
    assertNull(service.getDepartementById(-1));
    assertNull(service.getDepartementById(1000));

  }

  /**
   * Test getting a the list of all Departements.
   * @throws ParseException failed to parse date.
   */
  @Test
  public void testGettingDepartementByCode() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    List<Departement> list = service.getDepartementByCode("971");
    assertNotNull("DepartementService returned a null list", list);
    assertEquals(1, list.size());
    Departement dept = list.get(0);
    assertNotNull("Hibernate didn't return a Departement", dept);
    assertEquals("Hibernate returned a Departement, but the Id doesn't match",
                 197, dept.getId().intValue());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 sdf.parse("1999-01-01"), dept.getDebutValidite());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 null, dept.getFinValidite());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 null, dept.getArticleEnrichi());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "GUADELOUPE", dept.getNomMajuscule());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "Guadeloupe", dept.getNomEnrichi());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "", dept.getCommentaire());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "3", dept.getTypeNomClair().getCode());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "DEP", dept.getTypeEntiteAdmin().getCode());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 1, dept.getAudit().getId().intValue());
    assertEquals("Hibernate returned a Departement, but a field doesn't match",
                 "971", dept.getCodeInsee());
    assertEquals("vdept returned a Departement, but a field doesn't match",
                 "97105", dept.getChefLieu());
    assertEquals("vdept returned a Departement, but a field doesn't match",
                 "01", dept.getRegion());
  }

  /**
   * Test getting a the list of all Departements for a given Region.
   * @throws ParseException failed to parse date.
   */
  @Test
  public void testGettingDepartementForRegion() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    List<Departement> list;
    // Toute la France
    list = service.getDepartementForRegion(null, null);
    assertEquals(101, list.size());
    list = service.getDepartementForRegion(null, sdf.parse("2018-01-01"));
    assertEquals(101, list.size());
    list = service.getDepartementForRegion(null, sdf.parse("2008-01-01"));
    assertEquals(100, list.size());
    list = service.getDepartementForRegion(null, sdf.parse("1998-01-01"));
    assertEquals(0, list.size());
    // Grand Est - Alsace-Champagne-Ardenne-Lorraine
    list = service.getDepartementForRegion("44", null);
    assertEquals(10, list.size());
    list = service.getDepartementForRegion("44", sdf.parse("2018-01-01"));
    assertEquals(10, list.size());
    list = service.getDepartementForRegion("44", sdf.parse("2016-01-01"));
    assertEquals(10, list.size());
    list = service.getDepartementForRegion("44", sdf.parse("2015-01-01"));
    assertEquals(0, list.size());
    // Alsace et Lorraine
    list = service.getDepartementForRegion("41", null);
    assertEquals(0, list.size());
    list = service.getDepartementForRegion("42", null);
    assertEquals(0, list.size());
    list = service.getDepartementForRegion("41", sdf.parse("2015-01-01"));
    assertEquals(4, list.size());
    list = service.getDepartementForRegion("42", sdf.parse("2015-01-01"));
    assertEquals(2, list.size());
    list = service.getDepartementForRegion("41", sdf.parse("1999-01-01"));
    assertEquals(4, list.size());
    list = service.getDepartementForRegion("42", sdf.parse("1999-01-01"));
    assertEquals(2, list.size());
    list = service.getDepartementForRegion("41", sdf.parse("1998-01-01"));
    assertEquals(0, list.size());
    list = service.getDepartementForRegion("42", sdf.parse("1998-01-01"));
    assertEquals(0, list.size());
  }
}
