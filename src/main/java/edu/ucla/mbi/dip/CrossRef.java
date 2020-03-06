package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public abstract class CrossRef{
    
    private long   id     = -1;
    private String acc    = "";
    private CvTerm srcDb  = null;
    private CvTerm refTp  = null;

    // setters
    //--------
    
    public CrossRef setId(long id){
	this.id=id;
	return this;
    }

    public CrossRef setAcc(String acc){
	this.acc=acc;
	return this;
    }


    public CrossRef setSrcDb(CvTerm cvt){
	this.srcDb=cvt;
	return this;
    }

    public CrossRef setRefType(CvTerm cvt){
	this.refTp=cvt;
	return this;
    }

    // getters
    //--------

    public long getId(){
       	return this.id;
    }

    public String getAcc(){
       	return this.acc;
    }

    public CvTerm getSrcDb(){
       	return this.srcDb;
    }

    public CvTerm getRefType(){
       	return this.refTp;
    }

}
