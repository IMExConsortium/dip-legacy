package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class CvRelation{
    
    private long id      = -1;
    private String name  = "";
    private String definition  = null;

    // setters
    //--------    
    private CvRelation setId(long id){
	this.id=id;
	return this;
    }

    public CvRelation setName(String name){
	this.name=name;
	return this;
    }

    public CvRelation setDefinition(String definition){
	this.definition=definition;
	return this;
    }

    // getters
    //--------
    public long getId(){
       	return this.id;
    }

    public String getName(){
       	return this.name;
    }

    public String getDefinition(){
       	return this.definition;
    }
}
