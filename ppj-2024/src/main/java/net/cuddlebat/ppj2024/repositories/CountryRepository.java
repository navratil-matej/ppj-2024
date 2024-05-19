package net.cuddlebat.ppj2024.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import net.cuddlebat.ppj2024.orm.Country;

@RepositoryRestResource(collectionResourceRel="Country", path="Country")
public interface CountryRepository extends CrudRepository<Country, Integer>
{
	public Optional<Country> findByCode(String code);
}
