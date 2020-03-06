package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class LNode{
    
    private long    id      = -1;
    private String  label   = "";
    private String  name    = "";
    
    private DipNode node = null;
    private Link    link = null;
    

    // setters
    //--------
    
    private LNode setId(long id){
	this.id=id;
	return this;
    }

    public LNode setLabel(String label){
	this.label=label;
	return this;
    }

    public LNode setName(String name){
	this.name=name;
	return this;
    }

    public LNode setNode(DipNode node){
	this.node=node;
	return this;
    }

    public LNode setLink(Link link){
	this.link=link;
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


    public DipNode getNode(){
        return this.node;
    }

    public Link getLink(){
        return this.link;
    }
    

    // delegates
    //----------

    public CvType getCvType(){
	if(node!=null){
	    return node.getCvType();
	} else {
	    return null;
	}
    }

    public String getRefSeq(){
	if(node!=null){
	    return node.getRefSeq();
	} else {
	    return "";
	}
    }

    public String getUniProt(){
	if(node!=null){
	    return node.getUniProt();
	} else {
	    return "";
	}
    }
    
    public Organism getOrganism(){

	if(node!=null){
	    return node.getOrganism();
	} else {
	    return null;
	}
    }
    

    public String getAccession(){
	if(node!=null){
	    return node.getAccession();
	} else {
	    return "";
	}
    }

    public long getTaxId(){
	if(node!=null){
	    return node.getTaxId();
	} else {
	    return -1;
	}
    }

    public String getType(){
	if(node!=null){
	    return node.getType();
	} else {
	    return "undef";
	}
    }
}
