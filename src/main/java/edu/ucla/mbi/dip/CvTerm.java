package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class CvTerm{
    
    private long   id       = -1;

    private String cvid = "";
    private String name = "";
    private String def  = "";
    private boolean depreciated = false;


    private Set parents = new HashSet();

    public CvTerm(){};
    
    public CvTerm(String cvid,String name){
	this.cvid=cvid;
	this.name=name;
    }

    // setters
    //--------    
    private CvTerm setId(long id){
	this.id=id;
	return this;
    }

    public CvTerm setCvId(String cvid){
	this.cvid=cvid;
	return this;
    }

    public CvTerm setName(String name){
	this.name=name;
	return this;
    }
    public CvTerm setDefinition(String def){
	this.def=def;
	return this;
    }

    public CvTerm setParents(Set parents){
	this.parents=parents;
	return this;
    }

    public CvTerm setDepreciated(boolean depr){
	this.depreciated=depr;
	return this;
    }

   
    // getters
    //--------
    public long getId(){
       	return this.id;
    }

    public String getCvId(){
       	return this.cvid;
    }

    public String getCvNs(){
       	if( cvid != null && cvid.startsWith("dip")){
            return "dip";
        }
        if( cvid != null && cvid.startsWith("dxf")){
            return "dxf";
        }
        if( cvid != null && cvid.startsWith("MI:")){
            return "psi-mi";
        }
        return "";
    }

    public String getName(){
       	return this.name;
    }

    public String getDefinition(){
       	return this.def;
    }

    public Set getParents(){
        return this.parents;
    }

    public boolean getDepreciated(){
        return this.depreciated;
    }

    public boolean isDepreciated(){
        return this.depreciated;
    }

    public String toString(){
        
        return getCvNs() + ": " + getCvId() + "(" + getName() + ")";

    }

}
