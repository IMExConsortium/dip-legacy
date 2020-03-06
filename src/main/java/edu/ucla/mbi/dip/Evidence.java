package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class Evidence{
    
    private long   id       = -1;
    private String label    = "";
    private String name     = "";

    private long status = 0;

    private CvType evidType =new CvType();

    private CvType evidSrc  = new CvType();

    private CvType evidFor  = new CvType();

    private Link   link = null;

    private DataSrc dts = null;

    private Long imexId = null;
    private String imex254 = null;
    private String mif254 = null;


    // experiment scale:
    //    small-scale/high-throughput

    private CvType expScale = new CvType();


    // experiment scope:  
    //    directed/screen

    private CvType expScope = new CvType();


    // detection method

    private CvType detMeth = new CvType();

    private CvTerm cvtDetMeth = new CvTerm();


    // detected type

    private CvType detType = new CvType();

    private CvTerm cvtDetType = new CvTerm();


    // experiment scale

    private CvTerm cvtExpScale = new CvTerm();


    // detected part list

    private Set    lnode  = null;


    // distinct node count

    private int    nodeCount  = 0;


    // detected part list status:
    //    complete/ucomplete
    
    private CvType detPartStatus = new CvType();


    // attributes 

    private Set attrList = null;



    // setters
    //--------

    private Evidence setStatus( long status ) {
        this.status = status;
        return this;
    }

    
    private Evidence setId(long id){
	this.id=id;
	return this;
    }

    public Evidence setLabel(String label){
	this.label=label;
	return this;
    }

    public Evidence setName(String name){
	this.name=name;
	return this;
    }
    
    public Evidence setSource(CvType src){
	this.evidSrc=src;
	return this;
    }


    public Evidence setEvidFor(CvType evFor){
	this.evidFor=evFor;
	return this;
    }

    public Evidence setLink(Link link){
	this.link=link;
	return this;
    }

    public Evidence setImexId(Long imexId){
        this.imexId=imexId;
        return this;
    }

    public Evidence setImexId(long imexId){
        this.imexId=new Long(imexId);
        return this;
    }

    public Evidence setImex254(String imexId){
        this.imex254=imexId;
        return this;
    }

    public Evidence setMif254(String mif){
        this.mif254=mif;
        return this;
    }
    
    public void setDataSrc(DataSrc dts){
	this.dts=dts;
    }

    public Evidence setEvidType(CvType evType){
	this.evidType=evType;
	return this;
    }
    
    public Evidence setEvidScope(CvType evScope){
	this.expScope=evScope;
	return this;
    }

    public Evidence setEvidScale(CvType evScale){
	this.expScale=evScale;
	return this;
    }

    public Evidence setDetMethod(CvType evMeth){
	this.detMeth=evMeth;
	return this;
    }


    public Evidence setCvtDetMethod(CvTerm cvt){
	this.cvtDetMeth=cvt;
	return this;
    }

    public Evidence setDetType(CvType detType){
	this.detType=detType;
	return this;
    }

    public Evidence setCvtDetType(CvTerm cvt){
	this.cvtDetType=cvt;
	return this;
    }

    public Evidence setCvtExpScale(CvTerm cvt){
	this.cvtExpScale=cvt;
	return this;
    }


    public Evidence setDetPartStatus(CvType partStat){
	this.detPartStatus=partStat;
	return this;
    }



    // getters
    //--------

    public long getId(){
       	return this.id;
    }

    public long getStatus() {
       	return this.status;
    }

    public String getLabel(){
       	return this.label;
    }

    public String getName(){
       	return this.name;
    }

    public CvType getSource(){
       	return this.evidSrc;
    }

    public CvType getEvidFor(){
       	return this.evidFor;
    }

    public Link getLink(){
       	return this.link;
    }

    public String  getImex254(){
        return this.imex254 == null ? "" : this.imex254;
    }

    public String  getMif254(){
        return this.mif254;
    }

    public Long  getImexId(){
        return this.imexId;
    }

    public DataSrc getDataSrc(){
       	return this.dts;
    }

    public CvType getEvidType(){
       	return this.evidType;
    }

    public CvType getEvidScope(){
       	return this.expScope;
    }

    public CvType getEvidScale(){
       	return this.expScale;
    }

    public CvType getDetMethod(){
       	return this.detMeth;
    }

    public CvTerm getCvtDetMethod(){
       	return this.cvtDetMeth;
    }

    public CvType getDetType(){
       	return this.detType;
    }

    public CvTerm getCvtDetType(){
       	return this.cvtDetType;
    }

    public CvTerm getCvtExpScale(){
       	return this.cvtExpScale;
    }

    public CvType getDetPartStatus(){
       	return this.detPartStatus;
    }

        
    //---------------------
    
    public String getAccession(){
        return "DIP-"+id+evidType.getPostFix();
    }

    public String getType(){
        return evidType.getName();
    }

 
}
