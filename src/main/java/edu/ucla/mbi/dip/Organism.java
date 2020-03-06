package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class Organism{
    
    private long   id     = -1;
    private long   taxId  = -1;
    private String name   = "";
    private String cname  = "";

    

    // setters
    //--------
    
    private Organism setId(long id){
	this.id=id;
	return this;
    }

    public Organism setTaxId(long taxId){
	this.taxId=taxId;
	return this;
    }

    public Organism setCommonName(String cname){
	this.cname=cname;
	return this;
    }

    public Organism setName(String name){
	this.name=name;
	return this;
    }


    // getters
    //--------

    public long getId(){
       	return this.id;
    }
    
    public long getTaxId(){
       	return this.taxId;
    }

    public String getName(){
       	return this.name;
    }

    public String getCommonName(){
       	return this.cname;
    }
}
