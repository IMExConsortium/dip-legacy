package edu.ucla.mbi.dip;

import java.net.*;
import java.util.*;
 
import org.hibernate.*;
import org.hibernate.cfg.*;

public class Link{
    
    private long id = -1;
    private String label = "";
    private String name = "";

    private CvType cvt = null;
    private CvType qual = null;

    private CvTerm cvIntType = null;
    private CvTerm cvIntCoreType = null;

    private Set<LNode> lnode = null;
    private int nodeCount = 0;

    private Set evid = null;
    private Set infer = null;

    private Set xrefs = null;

    private Set imexId = null;

    // setters
    //--------
    
    public Link setId(long id){
	this.id=id;
	return this;
    }

    public Link setLabel(String label){
	this.label=label;
	return this;
    }

    public Link setName(String name){
	this.name=name;
	return this;
    }
    
    public Link setCvType(CvType cvt){
	this.cvt=cvt;
	return this;
    }


    public Link setCvIntType( CvTerm cvt ){
        this.cvIntType=cvt;
    return this;
    }

    public Link setCvIntCoreType( CvTerm cvt ){
        this.cvIntCoreType=cvt;
        return this;
    }


    public Link setQuality(CvType qual){
	this.qual=qual;
	return this;
    }

    public Link setLnode(Set<LNode> lnode){
        this.lnode=lnode;
        return this;
    }

    public Link setNodeCount(int nc){
        this.nodeCount=nc;
        return this;
    }

    public Link setEvidence(Set evid){
        this.evid=evid;
        return this;
    }

    public Link setInference( Set infer ){
        this.infer=infer;
        return this;
    }

    public Link setXrefs(Set xrefs){
        this.xrefs=xrefs;
        return this;
    }

    public Link setImexId(Set imex){
        this.imexId=imex;
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

    public CvType getQuality(){
       	return this.qual;
    }

    public CvTerm getCvIntType(){
       	return this.cvIntType;
    }

    public CvTerm getCvIntCoreType(){
       	return this.cvIntType;
    }
    
    public Set<LNode> getLnode(){
        return this.lnode;
    }

    public Set getImexId(){
        return this.imexId;
    }

    public int getNodeCount(){
        return this.nodeCount;
    }

    // synonym of the above
    
    public int getDNC(){
        return this.nodeCount;
    }

    
    public Set getEvidence(){
        return this.evid;
    }

    public Set getInference(){
        return this.infer;
    }

    public Set getXrefs(){
        return this.xrefs;
    }

    //--------------------------------------------------------------------------    

    public int getEvidenceCount(){
        if( evid != null ){
            return evid.size();
        }
        return 0;
     }
    
    public int getImexEvidenceCount(){
        int iec = 0; 
        if( evid != null ){
            for( Iterator i = evid.iterator(); i.hasNext(); ){

                String imx254 = ((Evidence) i.next()).getImex254();
                if( imx254 != null &&  imx254.length() > 0 ){
                    iec++;
                }
            }            
        }
        return iec;
    }

    public int getHtpEvidenceCount(){

        if( evid == null ) return 0;

        int hec = 0; 
        for( Iterator i = evid.iterator(); i.hasNext(); ){
            CvType evidScale = ((Evidence) i.next()).getEvidScale();
            if( evidScale != null 
                && evidScale.getAc() != null  
                && evidScale.getAc().equals("dip:0005") ){
                hec++;
            }
        }            
        return hec;
    }
    
    public int getInferenceCount(){
        
        if( infer == null ) return 0;
        return infer.size();
    }
    
    public int getHtpInferenceCount(){
        
        if( infer == null ) return 0;

        int hic = 0;
        for( Iterator i = infer.iterator(); i.hasNext(); ){
    
            Inference ii = (Inference) i.next();
            if( ii.getGrounds() != null ){
                CvType evidScale = ii.getGrounds().getEvidScale();
                if( evidScale != null
                    && evidScale.getAc() != null  
                    && evidScale.getAc().equals("dip:0005") ){
                    hic++;                
                }
            }
        }
        return hic;
    }

    
    //--------------------------------------------------------------------------    
    
    public String getAccession(){
        return "DIP-"+id+cvt.getPostFix();
    }

    public String getType(){
        return cvt.getName();
    }
    
    // comparisions
    //-------------
    
    public boolean gt(Link l){

        if(l!=null){
            return this.id>l.id ? true : false;
        } else {
            return true;
        }
    }

    public boolean ge(Link l){

        if(l!=null){
            return this.id>=l.id ? true : false;
        } else {
            return true;
        }
    }

    public boolean lt(Link l){

        if(l!=null){
            return this.id<l.id ? true : false;
        } else {
            return true;
        }
    }

    public boolean le(Link l){

        if(l!=null){
            return this.id<=l.id ? true : false;
        } else {
            return true;
        }
    }

    // identity
    //---------

    public boolean equals( Object obj ){
        if( obj.getClass() != this.getClass() ) return false;
        
        return this.getId() == ((Link)obj).getId(); 
    }

    // hash code
    //----------

    public int hashCode(){
        return (new Long( this.getId())).hashCode();
    }
}
