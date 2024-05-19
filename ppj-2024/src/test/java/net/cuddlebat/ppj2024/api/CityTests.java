package net.cuddlebat.ppj2024.api;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.cuddlebat.ppj2024.Ppj2024Application;
import net.cuddlebat.ppj2024.repositories.CityRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Ppj2024Application.class)
@ActiveProfiles({"TEST"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CityTests
{
	@Autowired
	private CityRepository cr;
	
	@Before
    public void init()
	{
        cr.deleteAll();
    }
	
}
