package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class DipNode{
    
    private long   id      = -1;
    private String label   = "";
    private String name    = "";
    private CvType cvt     = null;

    private String  uniProt = "";
    private String  refSeq = "";
    private String  entrezGene = "";
    private Organism org    = null;

    private String  sequence = "";    

    private Set xrefs = null;

    // setters
    //--------
    
    public DipNode setId(long id){
	this.id=id;
	return this;
    }

    public DipNode setLabel(String label){
	this.label=label;
	return this;
    }

    public DipNode setName(String name){
	this.name=name;
	return this;
    }

    public DipNode setCvType(CvType cvt){
	this.cvt=cvt;
	return this;
    }

    
    public DipNode setRefSeq(String rs){
	this.refSeq=rs;
	return this;
    }

    public DipNode setUniProt(String up){
	this.uniProt=up;
	return this;
    }

    public DipNode setEntrezGene(String eg){
	this.entrezGene=eg;
	return this;
    }

    public DipNode setOrganism(Organism org){
	this.org=org;
	return this;
    }

    public DipNode setSequence(String seq){
	this.sequence=seq;
	return this;
    }

    public DipNode setXrefs(Set xrefs){
	this.xrefs=xrefs;
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

    public String getEntrezGene(){
       	return this.entrezGene;
    }
    
    public String getUniProt(){
	return this.uniProt;
    }

    public String getSequence(){
	return this.sequence;
    }
    
    public Organism getOrganism(){
       	return this.org;
    }

    public Set getXrefs(){
       	return this.xrefs;
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


    // equality
    //---------
    
    public boolean equals(Object obj){
        if(obj.getClass().equals(this.getClass())){
            if(id==((DipNode)obj).getId()){
                return true;
            }
        }
        return false;
    }
    
    // hash code
    //----------

    public int hashCode(){
        return (new Long( this.getId())).hashCode();
    }


    // comparisions
    //-------------
    
    public boolean gt(DipNode dn){

	if(dn!=null){
	    return this.id>dn.id ? true : false;
	} else {
	    return true;
	}
    }

    public boolean ge(DipNode dn){

	if(dn!=null){
	    return this.id>=dn.id ? true : false;
	} else {
	    return true;
	}
    }

    public boolean lt(DipNode dn){

	if(dn!=null){
	    return this.id<dn.id ? true : false;
	} else {
	    return true;
	}
    }
    
    public boolean le(DipNode dn){

	if(dn!=null){
	    return this.id<=dn.id ? true : false;
	} else {
	    return true;
	}
    }
}
