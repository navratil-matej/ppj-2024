package net.cuddlebat.ppj2024.orm;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table
public class Country
{
	@Id @GeneratedValue @Column
	@JsonIgnore
	private int idCountry;
	@Column
	private String name;
	@Column
	private String code;
	
	public Country() {}

	public Country(String name, String code)
	{
		super();
		this.name = name;
		this.code = code;
	}

	public int getIdCountry()
	{
		return idCountry;
	}

	public void setIdCountry(int idCountry)
	{
		this.idCountry = idCountry;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}
}
