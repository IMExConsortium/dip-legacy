package edu.ucla.mbi.dip;

/* =============================================================================
 * $Id:: Counts.java 844 2010-01-14 03:27:42Z lukasz                           $
 * Version: $Rev:: 844                                                         $
 *==============================================================================
 *                                                                             $
 * Counts.java - cumulative artifact counts                                    $
 *                                                                             $
 *=========================================================================== */

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class Counts {
    
    private long  id  = -1;
    private long  taxid;
    private long  imexSrcId;
    private long  procStatId;
    private long  protein;
    private long  interaction;
    private long  evidence;
    private long  imexEvidence;
    private long  authorInference;
    private long  automatedInference;
    private long  source;
    private long  imexSource;
    private long  species;

    //--------------------------------------------------------------------------
    // setters
    //--------
    
    private Counts setId( long id ) {
	this.id = id;
	return this;
    }

    public Counts setTaxid( long taxid ) {
	this.taxid = taxid;
	return this;
    }

    public Counts setImexSrcId( long id ) {
	this.imexSrcId = id;
	return this;
    }

    public Counts setProcessingStatusId( long id ) {
	this.procStatId = id;
	return this;
    }

    public Counts setProteinCount( long count ) {
	this.protein = count;
	return this;
    }
    public Counts setInteractionCount( long count ) {
	this.interaction = count;
	return this;
    }

    public Counts setEvidenceCount( long count ) {
	this.evidence = count;
	return this;
    }

    public Counts setImexEvidenceCount( long count ) {
	this.imexEvidence = count;
	return this;
    }

    public Counts setAuthorInferenceCount( long count ) {
	this.authorInference = count;
	return this;
    }

    public Counts setAutoInferenceCount( long count ) {
	this.automatedInference = count;
	return this;
    }

    public Counts setSourceCount( long count ) {
	this.source = count;
	return this;
    }

    public Counts setImexSourceCount( long count ) {
	this.imexSource = count;
	return this;
    }

    public Counts setSpeciesCount( long count ) {
	this.species = count;
	return this;
    }

    //--------------------------------------------------------------------------
    // getters
    //--------

    public long getId(){
       	return this.id;
    }
    
    public long getTaxid(){
       	return this.taxid;
    }

    public long getImexSrcId() {
        return this.imexSrcId;
    }

    public long getProcessingStatusId() {
        return this.procStatId;
    }
    
    public long getProteinCount(){
       	return this.protein;
    }

    public long getInteractionCount(){
       	return this.interaction;
    }

    public long getEvidenceCount(){
       	return this.evidence;
    }

    public long getImexEvidenceCount(){
       	return this.imexEvidence;
    }

    public long getAuthorInferenceCount() {
        return this.authorInference;
    }

    public long getAutoInferenceCount() {
        return this.automatedInference;
    }

    public long getSourceCount(){
       	return this.source;
    }

    public long getImexSourceCount(){
       	return this.imexSource;
    }

    public long getSpeciesCount() {
       	return this.species;
    }


    //--------------------------------------------------------------------------

    public String toString() {
	return "(ID=" + id + ": TXID=" + taxid +  
            " ISID=" + imexSrcId + " PSID=" + procStatId +
	    " : ND="+protein+
	    " ED=" + interaction+
            " EV=" + evidence+
            " IMEX EV=" + imexEvidence+
	    " SR=" + source+
	    " IMEX SR=" + imexSource+
	    " SP="+species+")";
    }
}
