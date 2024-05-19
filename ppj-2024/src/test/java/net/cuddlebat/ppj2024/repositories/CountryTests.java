package net.cuddlebat.ppj2024.repositories;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import net.cuddlebat.ppj2024.Ppj2024Application;
import net.cuddlebat.ppj2024.orm.City;
import net.cuddlebat.ppj2024.orm.Country;
import net.cuddlebat.ppj2024.repositories.CityRepository;
import net.cuddlebat.ppj2024.repositories.CountryRepository;
import net.cuddlebat.ppj2024.repositories.MeasurementRepository;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Ppj2024Application.class)
@ActiveProfiles({"TEST"})
@TestPropertySource(locations = "classpath:application-test.properties")
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CountryTests
{
	@Autowired
	private MeasurementRepository mr;
	@Autowired
	private CityRepository cr;
	@Autowired
	private CountryRepository sr;
	
	@Before
    public void init()
	{
        mr.deleteAll();
        cr.deleteAll();
		sr.deleteAll();
        
        var gb = sr.save(new Country("England", "GB"));
        var cz = sr.save(new Country("Czechia", "CZ"));
    }
	
	@Test
	public void searchControllerTest()
	{
		var gb = sr.findByCode("GB");
		assertTrue("findByCode(GB) should return something", gb.isPresent());
		assertEquals("findByCode(GB) should return England", "England", gb.orElseThrow().getName());
	}
	
	@Test
	public void entityControllerTest()
	{
        var id = sr.save(new Country("Germany", "D")).getIdCountry();
        var de = sr.findById(id);
        assertTrue("save and findById should return a country", de.isPresent());
        assertEquals("save and findById should return the correct country", "Germany", de.orElseThrow().getName());
        sr.deleteById(id);
        de = sr.findById(id);
        assertFalse("deleteById and findById should return empty", de.isPresent());
	}
}
