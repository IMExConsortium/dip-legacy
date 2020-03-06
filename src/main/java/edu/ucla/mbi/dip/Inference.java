package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class Inference{
    
    private long   id       = -1;

    private CvType    evidFor = new CvType();
    private Link      link    = null;
    private Evidence  grounds = null;

    private CvTerm cvtDetType = null;
    private CvTerm cvtInfType = null;

    // attributes 

    private Set attrList = null;

    // setters
    //--------
    
    private Inference setId(long id){
	this.id=id;
	return this;
    }

    public Inference setEvidFor(CvType evFor){
	this.evidFor=evFor;
	return this;
    }

    public Inference setLink(Link link){
	this.link=link;
	return this;
    }

    public Inference setCvtDetType(CvTerm cvt){
	this.cvtDetType=cvt;
	return this;
    }

    public Inference setCvtInfType(CvTerm cvt){
	this.cvtInfType=cvt;
	return this;
    }
    
    public Inference setGrounds(Evidence grounds){
	this.grounds=grounds;
	return this;
    }
    
    // getters
    //--------

    public long getId(){
       	return this.id;
    }

    public CvType getEvidFor(){
       	return this.evidFor;
    }

    public Link getLink(){
       	return this.link;
    }

    public CvTerm getCvtDetType(){
       	return this.cvtDetType;
    }

    public CvTerm getCvtInfType(){
       	return this.cvtDetType;
    }

    public Evidence getGrounds(){
       	return this.grounds;
    }

}
