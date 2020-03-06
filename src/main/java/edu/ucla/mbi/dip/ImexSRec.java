package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class ImexSRec{
    
    private long     id       = -1;
    private String   mif      = "";
    private Long     imexid   = new Long(0);
    private Long     evid     = new Long(0);
    private int      imex_dts = 1;

    private String mif254 ="";
    private String imex254 ="";
    
    
    //private Evidence evid = null;
    //private Set      evid = null;

    // setters
    //--------
    
    private ImexSRec setId( long id ) {
	this.id = id;
	return this;
    }

    public ImexSRec setDts( int id ) {
	this.imex_dts = id;
	return this;
    }

    public ImexSRec setMif( String mif ) {
	this.mif = mif;
	return this;
    }

    public ImexSRec setImexId( Long imexid ) {
	this.imexid = imexid;
	return this;
    }

    public ImexSRec setMif254( String mif254 ) {
	this.mif254 = mif254;
	return this;
    }

    public ImexSRec setImex254( String imexid ) {
	this.imex254 = imexid;
	return this;
    }

    /*

    public ImexSRec setEvidence(Evidence evid){
	this.evid=evid;
	return this;
    }

    */

    public ImexSRec setEvidId( Long evid ) {
	this.evid = evid;
	return this;
    }

    public ImexSRec setEvidId( long evid ) {
	this.evid = new Long( evid );
	return this;
    }


    // getters
    //--------

    public long getId() {
       	return this.id;
    }

    public int getDts() {
       	return this.imex_dts;
    }

    public String getMif() {
       	return this.mif;
    }
    
    public Long getImexId() {
       	return this.imexid;
    }

    public String getMif254(){
       	return this.mif254;
    }
    
    public String getImex254() {
       	return this.imex254;
    }

    /*
    public Evidence getEvidence(){
       	return this.evid;
    }
    */

    public Long getEvidId() {
       	return this.evid;
    }
}
