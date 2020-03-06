package edu.ucla.mbi.dip;

/* =============================================================================
 * $Id:: CountTable.java 1276 2010-10-20 23:10:41Z lukasz                      $
 * Version: $Rev:: 1276                                                        $
 *==============================================================================
 *                                                                             $
 * CountTable.java - internal representation of artifact count                 $
 *                                                                             $
 *=========================================================================== */

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class CountTable {
    
    private long  id  = -1;

    private String dtsgrp = ""; // datasource (DIP, IntAct, etc...)
    private String taxgrp = ""; // taxons
    private String curgrp = ""; // curation status 
    private String infgrp = ""; // inference type (Auth:eXper,Evid ?) 

    private long  taxonAll;
    private long  taxonEvd;
    private long  taxonInf;

    private long  proteinAll;
    private long  proteinEvd;
    private long  proteinInf;

    private long  interactionAll;
    private long  interactionEvd;
    private long  interactionInf;

    private long  evidenceAll;
    private long  evidenceEvd;
    private long  evidenceInf;

    private long  sourceAll;
    private long  sourceEvd;
    private long  sourceInf;

    private String  intEvidAll = "";
    private String  intEvidEvd = "";
    private String  intEvidInf = "";

   
    //--------------------------------------------------------------------------
    // setters
    //--------
    
    private CountTable setId( long id ) {
	this.id = id;
	return this;
    }

    //--------------------------------------------------------------------------
    
    private CountTable setDtsgrp( String dtsgrp ) {
	this.dtsgrp = dtsgrp;
	return this;
    }

    private CountTable setTaxgrp( String taxgrp ) {
	this.taxgrp = taxgrp;
	return this;
    }
    
    private CountTable setCurgrp( String curgrp ) {
	this.curgrp = curgrp;
	return this;
    }
    
    private CountTable setInfgrp( String infgrp ) {
	this.infgrp = infgrp;
	return this;
    }
    
    //--------------------------------------------------------------------------
    
    public CountTable setTaxonCountAll( long count ) {
	this.taxonAll = count;
	return this;
    }

    public CountTable setTaxonCountEvd( long count ) {
	this.taxonEvd = count;
	return this;
    }
    
    public CountTable setTaxonCountInf( long count ) {
	this.taxonInf = count;
	return this;
    }

    //--------------------------------------------------------------------------
    
    public CountTable setProteinCountAll( long count ) {
	this.proteinAll = count;
	return this;
    }

    public CountTable setProteinCountEvd( long count ) {
	this.proteinEvd = count;
	return this;
    }

    public CountTable setProteinCountInf( long count ) {
	this.proteinInf = count;
	return this;
    }

    //--------------------------------------------------------------------------

    public CountTable setInteractionCountAll( long count ) {
	this.interactionAll = count;
	return this;
    }
    
    public CountTable setInteractionCountEvd( long count ) {
	this.interactionEvd = count;
	return this;
    }

    public CountTable setInteractionCountInf( long count ) {
	this.interactionInf = count;
	return this;
    }

    //--------------------------------------------------------------------------

    public CountTable setEvidenceCountAll( long count ) {
	this.evidenceAll = count;
	return this;
    }

    public CountTable setEvidenceCountEvd( long count ) {
	this.evidenceEvd = count;
	return this;
    }

    public CountTable setEvidenceCountInf( long count ) {
	this.evidenceInf = count;
	return this;
    }
    
    //--------------------------------------------------------------------------

    public CountTable setSourceCountAll( long count ) {
	this.sourceAll = count;
	return this;
    }

    public CountTable setSourceCountEvd( long count ) {
	this.sourceEvd = count;
	return this;
    }
    
    public CountTable setSourceCountInf( long count ) {
	this.sourceInf = count;
	return this;
    }

    //--------------------------------------------------------------------------

    public CountTable setIntEvidCountAll( String count ) {
	this.intEvidAll = count;
	return this;
    }

    public CountTable setIntEvidCountEvd( String count ) {
	this.intEvidEvd = count;
	return this;
    }
    
    public CountTable setIntEvidCountInf( String count ) {
	this.intEvidInf = count;
	return this;
    }
    
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // getters
    //--------

    public long getId(){
       	return this.id;
    }
    
    //--------------------------------------------------------------------------

    public String  getDtsgrp(){
       	return this.dtsgrp;
    }
    
    public String  getTaxgrp(){
       	return this.taxgrp;
    }

    public String  getCurgrp(){
       	return this.curgrp;
    }

    public String  getInfgrp(){
       	return this.infgrp;
    }
    
    //--------------------------------------------------------------------------
    
    public long getTaxonCountAll(){
       	return this.taxonAll;
    }

    public long getTaxonCountEvd(){
       	return this.taxonEvd;
    }

    public long getTaxonCountInf(){
       	return this.taxonInf;
    }

    //--------------------------------------------------------------------------
    
    public long getProteinCountAll(){
       	return this.proteinAll;
    }

    public long getProteinCountEvd(){
       	return this.proteinEvd;
    }

    public long getProteinCountInf(){
       	return this.proteinInf;
    }

    //--------------------------------------------------------------------------

    public long getInteractionCountAll(){
       	return this.interactionAll;
    }
    
    public long getInteractionCountEvd(){
       	return this.interactionEvd;
    }
    
    public long getInteractionCountInf(){
       	return this.interactionInf;
    }

    //--------------------------------------------------------------------------

    public long getEvidenceCountAll(){
       	return this.evidenceAll;
    }

    public long getEvidenceCountEvd(){
       	return this.evidenceEvd;
    }

    public long getEvidenceCountInf(){
       	return this.evidenceInf;
    }
    
    //--------------------------------------------------------------------------
    
    public long getSourceCountAll(){
       	return this.sourceAll;
    }

    public long getSourceCountEvd(){
       	return this.sourceEvd;
    }
    
    public long getSourceCountInf(){
       	return this.sourceInf;
    }

    //--------------------------------------------------------------------------

    public String getIntEvidCountAll(){
       	return this.intEvidAll;
    }

    public String getIntEvidCountEvd(){
       	return this.intEvidEvd;
    }
    
    public String getIntEvidCountInf(){
       	return this.intEvidInf;
    }
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
    public String toString() {
	return "(ID=" + id + ": TAXGRP=" + taxgrp +
	    " : ND=" + proteinAll +
	    " ED=" + interactionAll +
            " EV=" + evidenceAll +
	    " SR=" + sourceAll + ")";
    }
}
