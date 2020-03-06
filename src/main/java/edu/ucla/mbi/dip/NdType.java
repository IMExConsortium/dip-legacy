package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class NdType{
    
    private long   id       = -1;
    private String postfix  = "";
    private String name     = "";

    // setters
    //--------
    
    private NdType setId(long id){
	this.id=id;
	return this;
    }

    public NdType setPostFix(String pFix){
	this.postfix=pFix;
	return this;
    }

    public NdType setName(String name){
	this.name=name;
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

}
