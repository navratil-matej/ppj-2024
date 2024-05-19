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
public class CityTests
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
        
        cr.save(new City("London", gb, 10, 10));
        cr.save(new City("Prague", cz, 10, 20));        
        cr.save(new City("Same Name", gb, 20, 10));
        cr.save(new City("Same Name", cz, 20, 20));
    }
	
	@Test
	public void searchControllerTest()
	{
		var gbAll = cr.findByCountry("GB");
		assertEquals("findByCountry(GB) should contain two elements", 2, gbAll.size());
		var czSn = cr.findByNameAndCountry("Same Name", "CZ");
		var gbSn = cr.findByNameAndCountry("Same Name", "GB");
		assertNotEquals("findByNameAndCountry should distinguish same name cities", czSn.get().getIdCity(), gbSn.get().getIdCity());
	}
	
	@Test
	public void entityControllerTest()
	{
        var cz = sr.findByCode("CZ").orElseThrow();
        var id = cr.save(new City("Liberec", cz, 10, 10)).getIdCity();
        var lbc = cr.findById(id);
        assertTrue("save and findById should return a city", lbc.isPresent());
        assertEquals("save and findById should return the correct city", "Liberec", lbc.orElseThrow().getName());
        cr.deleteById(id);
        lbc = cr.findById(id);
        assertFalse("deleteById and findById should return empty", lbc.isPresent());
	}
}
