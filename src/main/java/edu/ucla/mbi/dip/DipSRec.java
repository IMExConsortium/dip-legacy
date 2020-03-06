package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class DipSRec{
    
    private long     id      = -1;
    private String   mif     = "";
    private Evidence evid    = null;
    

    // setters
    //--------
    
    private DipSRec setId(long id){
	this.id=id;
	return this;
    }

    public DipSRec setMif(String mif){
	this.mif=mif;
	return this;
    }

    public DipSRec setEvidence(Evidence evid){
	this.evid=evid;
	return this;
    }


    // getters
    //--------

    public long getId(){
       	return this.id;
    }

    public String getMif(){
       	return this.mif;
    }
    
    public Evidence getEvidence(){
       	return this.evid;
    }
    
}
