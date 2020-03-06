package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class Node{
    
    private long   id      = -1;
    private String label   = "";
    private String name    = "";
    private CvType cvt     = null;

    private String  uniProt = "";
    private String  refSeq  = "";
    private Organism org    = null;
    

    // setters
    //--------
    
    public Node setId(long id){
	this.id=id;
	return this;
    }

    public Node setLabel(String label){
	this.label=label;
	return this;
    }

    public Node setName(String name){
	this.name=name;
	return this;
    }

    public Node setCvType(CvType cvt){
	this.cvt=cvt;
	return this;
    }

    
    public Node setRefSeq(String rs){
	this.refSeq=rs;
	return this;
    }

    public Node setUniProt(String up){
	this.uniProt=up;
	return this;
    }

    public Node setOrganism(Organism org){
	this.org=org;
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

    public String getRefSeq(){
       	return this.refSeq;
    }
    
    public String getUniProt(){
	return this.uniProt;
    }
    
    public Organism getOrganism(){
       	return this.org;
    }


    //---------------------

    public String getAccession(){
        return "DIP-"+id+cvt.getPostFix();
    }

    public long getTaxId(){
	if(org!=null){
	    return org.getTaxId();
	} else {
	    return -1;
	}
    }

    public String getType(){
	if(cvt!=null){
	    return cvt.getName();
	} else {
	    return "undef";
	}
    }
}
