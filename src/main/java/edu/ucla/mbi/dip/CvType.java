package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class CvType{
    
    private long   id       = -1;

    private String ns       = "dxf";
    private String ac       = "";
    private String name     = "";

    private String postfix  = "";

    public CvType(){};
    
    public CvType(String ns, String ac,
		  String name){
	this.ns=ns;
	this.ac=ac;
	this.name=name;
    }
    
    // setters
    //--------
    
    private CvType setId(long id){
	this.id=id;
	return this;
    }

    public CvType setName(String name){
	this.name=name;
	return this;
    }

    public CvType setNs(String ns){
	this.ns=ns;
	return this;
    }
    public CvType setAc(String ac){
	this.ac=ac;
	return this;
    }

    public CvType setPostFix(String pFix){
	this.postfix=pFix;
	return this;
    }

    // getters
    //--------

    public long getId(){
       	return this.id;
    }

    public String getPostFix(){
       	return this.postfix;
    }

    public String getName(){
       	return this.name;
    }

    public String getNs(){
       	return this.ns;
    }

    public String getAc(){
       	return this.ac;
    }

}
