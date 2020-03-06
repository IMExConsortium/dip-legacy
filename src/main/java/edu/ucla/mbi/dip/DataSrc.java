package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class DataSrc{
    
    private long   id      = -1;
    private String label   = "";
    private String name    = "";
    private CvType cvt     = null;

    private String   pmid = "";
    private int   imexSrc =  0;
    
    private boolean pub;
    
    // setters
    //--------
    
    private DataSrc setId(long id){
	this.id=id;
	return this;
    }

    public DataSrc setImexSrc(int src){
	this.imexSrc=src;
	return this;
    }

    public DataSrc setLabel(String label){
    	this.label=label;
    	return this;
    }

    public DataSrc setName(String name){
	this.name=name;
	return this;
    }

    public DataSrc setCvType(CvType cvt){
	this.cvt=cvt;
	return this;
    }

    public DataSrc setPmid(String pmid){
	this.pmid=pmid;
	return this;
    }

    public DataSrc setPublic( boolean pub ){
        this.pub = pub;
        return this;
    }

    // getters
    //--------

    public long getId(){
       	return this.id;
    }

    public String getLabel(){
	return this.label;
    }
    
    public String getName(){
       	return this.name;
    }

    public CvType getCvType(){
       	return this.cvt;
    }

    public String getPmid(){
       	return this.pmid;
    }

    public int getImexSrc(){
       	return this.imexSrc;
    }
    

    public boolean getPublic(){
        return this.pub;
    }

    public boolean isPublic(){
        return this.pub;
    }

    //---------------------

    public String getAccession(){
        return "DIP-"+id+cvt.getPostFix();
    }

    public String getType(){
	if(cvt!=null){
	    return cvt.getName();
	} else {
	    return "undef";
	}
    }

    public boolean equals(Object obj){
	System.out.println("EQ:"+obj);
	if(obj.getClass().equals(this.getClass())){
	    if(id==((DataSrc)obj).getId()){
		return true;
	    }
	}
	return false;
    }

}
