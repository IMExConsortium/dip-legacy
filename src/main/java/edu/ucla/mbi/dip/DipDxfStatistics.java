package edu.ucla.mbi.dip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.dip.*;
import edu.ucla.mbi.dxf14.*;
import edu.ucla.mbi.orm.*;


public class DipDxfStatistics{

    private static Map<String,String> orgrp;

    static{
        orgrp = new HashMap<String,String>();
        orgrp.put( "4932","4932");
        orgrp.put( "7227","7227");
        orgrp.put( "562","562:2489:83333:83334:168807:199310:217992:331111:574521" );
        orgrp.put( "6239","6239");
        orgrp.put( "9606","9606" );
        orgrp.put( "85962", "210:85962:85963:512562:563041" );
        orgrp.put( "10090","10090" );
        orgrp.put( "10116", "10116" );
        orgrp.put( "9913", "9913" );
        orgrp.put( "3702", "3702" );
    }
    
    static NodeDAO nDAO = null;
    static LNodeDAO lnDAO = null;
    static OrganismDAO oDAO = null;
    static CvtDAO cvtDAO = null;
    static CvTermDAO cvTermDAO = null;
    static LinkDAO lDAO = null;
    static EvidDAO xDAO = null;
    static InferDAO infDAO = null;
    static DataSrcDAO sDAO = null;
    static ImexSRecDAO imexDAO = null;
    static DipSRecDAO dipSRecDAO = null;

    static DipDataSrcDAO ddsDAO = null;
    static CountsDAO dcDAO = null;
    static CountTableDAO ctDAO = null;

    static edu.ucla.mbi.dxf14.ObjectFactory dxfFactory
	= new edu.ucla.mbi.dxf14.ObjectFactory();

    //--------------------------------------------------------------------------
        
    public static edu.ucla.mbi.dxf14.DatasetType 
	getCounts( String ns, String  ac, String detail ) {
	
	Log log = LogFactory.getLog( DipDxfStatistics.class );
        log.info( "DipDxfStat: getCounts called" );
	log.info( "DipDxfStat:  ac=\""+ac+"\"  (ns=\""+ns+"\")");
	
       	// create empty result dataset
	//----------------------------
	
	DatasetType res = dxfFactory.createDatasetType();
	int _id = 1;
	
	ac=ac.replaceAll("\\s","");
	
	DxfLevel mode = DxfLevel.fromString( detail );
	List dipEvidList = null;

	if( dcDAO == null ) {
	    log.debug( "DipSOAP:   Creating CountsDAO..." );
	    dcDAO = new CountsDAO();
	    log.debug( "DipSOAP:   ...done" );
	}
        
        if( ctDAO == null ) {
	    log.debug( "DipSOAP:   Creating CountTableDAO..." );
	    ctDAO = new CountTableDAO();
	    log.debug( "DipSOAP:   ...done" );
	}

        if( oDAO == null ) {
	    log.debug( "DipSOAP:   Creating OrganismDAO..." );
	    oDAO = new OrganismDAO();
	    log.debug( "DipSOAP:   ...done" );
	}
        
	if( ns.equals("psi-mi") && ac.equals("MI:0465") ) { // DIP globals stats
            
            List<Counts> cl = (List<Counts>) ctDAO.findByTaxid( "" );
            
            //List<Counts> cl = (List<Counts>) dcDAO.findByTaxgrp( "" );

            if( cl != null ) {
                for( Iterator<Counts> ci = cl.iterator(); ci.hasNext(); ) {
                    Counts cc = ci.next();
                    log.info( "DipDxfStat: counts=" + cc.toString() );
                }
                res.getNode().add( DipDxfUtil.dip2dxf( cl, null, _id++, mode ) );
            }
            
	    log.info( "DipDxfStat: getCounts: DONE(global)\n" );
	    return res;	    
	} 

        //----------------------------------------------------------------------

	if( ns.equals( "taxid" ) ) { // taxon stat
            
            if( ac == null ||  ac.replaceAll("[^0-9]","").length() == 0 ){
                log.info( "DipDxfStat: getCounts: DONE(missing taxon)\n" );
                return res;
            } 

            if( orgrp.get( ac ) == null ){
                log.info( "DipDxfStat: getCounts: DONE(no data)\n" );
                return res;
            }

            List<Counts> cl = (List<Counts>) ctDAO.findByTaxid( orgrp.get( ac ) );

            if( cl != null ) {
                for( Iterator<Counts> ci = cl.iterator(); ci.hasNext(); ) {
                    Counts cc = ci.next();
                    log.info( "DipDxfStat: counts=" + cc.toString() );
                }


                List orgList = oDAO.findByTaxId( ac.replaceAll( "[^0-9]", "" ) );
                
                res.getNode().add( DipDxfUtil.dip2dxf( cl, 
                                                       (Organism) orgList.get(0), 
                                                       _id++, mode ) );
            }

            log.info( "DipDxfStat: getCounts: DONE(species)\n" );
            return res;
        
        

            /*
	    long lAcc = Long.valueOf( ac.replaceAll("[^0-9]","") );
	    List<Counts> cl = dcDAO.findByTaxid( lAcc );
	    
	    if( oDAO == null ) {
		log.debug( "DipSOAP:   Creating OrganismDAO..." );
		oDAO = new OrganismDAO();
		log.debug( "DipSOAP:   ...done" );
	    }
	    
	    List orgList = oDAO.findByTaxId( ac.replaceAll( "[^0-9]", "" ) );
	    
	    if( cl != null ) {
		res.getNode().add( DipDxfUtil.dip2dxf( cl, 
                                                       (Organism) orgList.get(0),
                                                       _id++, mode ) );
	    }
	    log.info( "DipDxfStat: getCounts: DONE(species)\n" );
            return res;
            */
	}

        //----------------------------------------------------------------------

	if( ns.equals("dip") && ac.endsWith("N") ) {
	    
	    if( nDAO == null ) {
                log.debug( "DipSOAP:   Creating NodeDAO..." );
                nDAO = new NodeDAO();
                log.debug( "DipSOAP:   ...done" );
            }
	    
	    DipNode nd = 
		nDAO.find( Long.valueOf( ac.replaceAll( "[^0-9]", "") ) );
	    
	    CountsFull cf = dcDAO.getNodeCounts( nd );
	    res.getNode().add( DipDxfUtil.dip2dxf( cf, nd, _id++, mode) );

	    log.info("DipDxfStat: getCounts: DONE(node)\n");
            return res;
	}
        
        //----------------------------------------------------------------------

	if( ns.equals("dip") && ac.endsWith("E") ) {
	    
	    if( lDAO==null ) {
                log.debug( "DipSOAP:   Creating LinkDAO..." );
                lDAO=new LinkDAO();
                log.debug( "DipSOAP:   ...done" );
            }
	    
	    Link ln = 
		lDAO.find( Long.valueOf(ac.replaceAll( "[^0-9]","") ) );
	    
	    CountsFull cf = dcDAO.getLinkCounts( ln );
	    res.getNode().add( DipDxfUtil.dip2dxf( cf, ln, _id++, mode ) );

	    log.info( "DipDxfStat: getCounts: DONE(link)\n" );
            return res;
	}
	
	log.info( "DipDxfStat: getCounts: DONE(empty)\n" );
	return res; 
    }

}
