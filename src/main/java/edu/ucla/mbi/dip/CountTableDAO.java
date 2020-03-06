package edu.ucla.mbi.dip;

/* =============================================================================
 * $Id:: CountTableDAO.java 1278 2010-10-22 00:21:18Z lukasz                   $
 * Version: $Rev:: 1278                                                        $
 *==============================================================================
 *                                                                             $
 *  CountsDAO - access to txstat (cumulative counts) table                     $
 *                                                                             $
 *    NOTES:                                                                   $
 *                                                                             $
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.*;
import java.util.*;

public class CountTableDAO extends AbstractDAO{
    
    public void create( CountTable cnt ) throws DAOException {
	saveOrUpdate( cnt );
    }

    public void saveOrUpdate( CountTable cnt ) throws DAOException {
	super.saveOrUpdate( cnt );
    }
    
    public List findAll() throws DAOException {
	return findAll( CountTable.class, "interactionCountAll" );
    }
    
    public List findAll( String field ) throws DAOException {
	return findAll( CountTable.class, field );
    }
    
    public CountTable find( Long id ) throws DAOException {
	return (CountTable) find( CountTable.class, id );
    }

    public CountTable find( long id ) throws DAOException {
	return (CountTable) find( CountTable.class, new Long( id ) );
    }
    
    public List findById( long id ) throws DAOException {
        CountTable cnt = (CountTable) find( CountTable.class, new Long( id ) );
        
	if( cnt != null ) {
	    List res = new ArrayList();
	    res.add( cnt );
	    return res;
	} else {
	    return new EmptyList();
	}
    }
    
    //--------------------------------------------------------------------------    
    
    private static InferDAO iDAO = null;
    
    public CountsFull getNodeCounts( DipNode nd ) {

	CountsFull res = new CountsFull();

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {  
       	    
	    // interactions-binary
	    //--------------------

            Query query_ib = session
                .createQuery( "select count (distinct l) " +
                              " from Link as l inner join l.lnode as lnA "+
                              " where l.nodeCount<3 and lnA.node = :nA");
            query_ib.setParameter( "nA", nd );
            List ib = query_ib.list();


	    // interactions-multi
	    //-------------------

            Query query_im = session
                .createQuery( "select count (distinct l) " +
                              " from Link as l inner join l.lnode as lnA "+
                              " where l.nodeCount>2 and lnA.node = :nA");
            query_im.setParameter( "nA", nd );
            List im = query_im.list();

	    // partners-binary
	    //----------------
            
            Query query_pb = session
                .createQuery("select count (distinct lnB.node.id) " +
                             " from Link as l"+
                             "  inner join l.lnode as lnA " +
                             "  inner join l.lnode as lnB"+
                             " where l.nodeCount=2 and lnA.node = :nA");
            query_pb.setParameter( "nA", nd );
            List pb = query_pb.list();

	    // partners-complex
	    //-----------------

            Query query_pm = session
                .createQuery( "select count (distinct lnB.node.id) " +
                              " from Link as l"+
                              " inner join l.lnode as lnA " +
                              " inner join l.lnode as lnB"+
                              " where l.nodeCount>2 and lnA.node = :nA");
            query_pm.setParameter( "nA", nd );
            List pm = query_pm.list();
	    

	    if( ib!=null && ib.size()==1 ) {
		res.getDetail().put( "interaction-binary-count",
                                     (Integer)ib.get(0) );
	    } else {
		res.getDetail().put( "interaction-binary-count",0 );
	    }
	    
	    if( im!=null && im.size()==1 ) {
		res.getDetail().put( "interaction-multi-count",
                                     (Integer)im.get(0) );
	    } else{
		res.getDetail().put( "interaction-multi-count", 0 );
	    }

	    if( pb!=null && pb.size()==1 ) {
		res.getDetail().put( "partner-binary-count",
                                     (Integer)pb.get(0) );
	    } else{
		res.getDetail().put( "partner-binary-count", 0 );
	    }

	    if( pm!=null && pm.size()==1 ) {
		res.getDetail().put( "partner-multi-count",
                                     (Integer)pm.get(0) );
	    } else{
		res.getDetail().put( "partner-multi-count", 0 );
	    }
	    
            tx.commit();
        } catch( HibernateException e ) {
            handleException(e);
        } finally {
            session.close();
        }
	return res;
    }

    //--------------------------------------------------------------------------    

    public CountsFull getLinkCounts( Link ln ) {
        
	CountsFull res= new CountsFull();
              
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
	
	    int ec=0;
	    int eic=0;
	    
	    if( ln.getEvidence() != null ) {
		ec = ln.getEvidence().size();
	    } else {
		
		// evidence-count
		//---------------
                
		Query query_ea = session
                    .createQuery( "select count (distinct e)"+
                                  " from Evidence as e"+
                                  " where e.link = :lA");
		query_ea.setParameter( "lA", ln );
		List ea = query_ea.list();

		if( ea!=null && ea.size()==1 ) {
		    ec= (Integer) ea.get(0);
		}
	    }

	    // evidence-imex-count
	    //--------------------
            
            Query query_ei
                = session.createQuery( "select count (distinct e.imexId)"+
                                       " from Evidence as e"+
                                       " where e.imexId > 0 and e.link = :lA");
            query_ei.setParameter( "lA", ln );
            List ei = query_ei.list();
            
	    if( ei!=null && ei.size()==1 ) {
		eic = (Integer)ei.get(0);
	    }
            
	    // get inferences - all
	    //---------------------
	    
	    if( iDAO==null ) {
		iDAO = new InferDAO();
	    }
	    
	    List il = iDAO.findByLinkId( ln.getId() );
            
	    if( il!=null ) {
		ec += il.size();
                
		for( Iterator ii = il.iterator(); ii.hasNext(); ) {
		    Inference ci = (Inference) ii.next();
		    
		    // imex
		    //-----

		    if( ci.getGrounds().getImexId() != null ) {
			eic++;
		    }
		}
	    }

	    res.getDetail().put( "evidence-count", ec );
	    res.getDetail().put( "evidence-imex-count", eic );
            
        } catch( HibernateException e ) {
            handleException(e);
        } finally {
            session.close();
        }

	return res;
    }

    //--------------------------------------------------------------------------    
    
    public List findByTaxid( long txid ) throws DAOException {
        return this.findByTaxid( String.valueOf( txid ) );
    }
    
    public List findByTaxid( String taxgrp ) throws DAOException {

        Log log = LogFactory.getLog( CountTableDAO.class );
        log.info( "findByTaxid(String): " + taxgrp );
	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

	try {

            //------------------------------------------------------------------
            // dip (legacy+imex) public
            //-------------------------
            
            Query queryAll = session
                .createQuery( "select c from CountTable as c where" +
                              " c.dtsgrp = :ds and c.taxgrp = :tx and" +
                              " c.curgrp = :cs and c.infgrp = :ii");
	    queryAll.setParameter( "ds", "0:1" );
            queryAll.setParameter( "tx", taxgrp );
            queryAll.setParameter( "cs", "1" );
            queryAll.setParameter( "ii", "E:A" );
            List objectsAll = queryAll.list();

            log.info( "objAll: " + objectsAll);
            if( objectsAll != null ){
                log.info( "objAll: " + objectsAll.size());
            }
            
            //------------------------------------------------------------------
            // dip (imex only) public
            //-----------------------
            
            Query queryImex = session
                .createQuery( "select c from CountTable as c where" +
                              " c.dtsgrp = :ds and c.taxgrp = :tx and" +
                              " c.curgrp = :cs and c.infgrp = :ii");
	    queryImex.setParameter( "ds", "1" );
            queryImex.setParameter( "tx", taxgrp );
            queryImex.setParameter( "cs", "1" );
            queryImex.setParameter( "ii", "E:A" );
	    List objectsImex = queryImex.list();

            log.info( "objImx: " + objectsImex);
            if( objectsImex != null ){
                log.info( "objImx: " + objectsImex.size());
            }

            //------------------------------------------------------------------
            // dip (author inferences) publc
            //------------------------------

             Query queryAuth = session
                 .createQuery( "select c from CountTable as c where" +
                               " c.dtsgrp = :ds and c.taxgrp = :tx and" +
                               " c.curgrp = :cs and c.infgrp = :ii");
             queryAuth.setParameter( "ds", "0:1" );
             queryAuth.setParameter( "tx", taxgrp );
             queryAuth.setParameter( "cs", "1" );
             queryAuth.setParameter( "ii", "P:E" );
             List objectsAuth = queryAuth.list();

             log.info( "objAuth: " + objectsAuth);
             if( objectsAuth != null ){
                 log.info( "objAuth: " + objectsAuth.size());
             }

            //------------------------------------------------------------------
             
            Counts cnt = new Counts();
            cnt.setProcessingStatusId( 1 );
            cnt.setImexSrcId( 1 );
            
            if( objectsAll != null && objectsAll.size()==1 ){
                CountTable c = (CountTable) objectsAll.get( 0 );
                
                log.info( "objAll: " + objectsAll.size());

                cnt.setProteinCount( c.getProteinCountAll() );
                cnt.setInteractionCount( c.getInteractionCountAll() );
                cnt.setEvidenceCount( c.getEvidenceCountEvd() );
                cnt.setImexEvidenceCount( 0 );
                cnt.setAuthorInferenceCount( 0 );
                cnt.setAutoInferenceCount(  c.getEvidenceCountInf());
                cnt.setSourceCount( c.getSourceCountAll());
                cnt.setImexSourceCount( 0 );
                cnt.setSpeciesCount( c.getTaxonCountAll() );
            }

            if( objectsImex != null && objectsImex.size()==1 ){
                CountTable c = (CountTable) objectsImex.get( 0 );
                cnt.setImexEvidenceCount( c.getEvidenceCountEvd() );
                cnt.setImexSourceCount( c.getSourceCountEvd() );
            }
            
            if( objectsAuth != null && objectsAuth.size()==1 ){
                CountTable c = (CountTable) objectsAuth.get( 0 );
                cnt.setAuthorInferenceCount( c.getEvidenceCountEvd() );
            }
            
            objects = new ArrayList<Counts>();
            objects.add( cnt );

	    tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	} finally {
	    session.close();
	}
        
	if( objects != null ){ 
	    return objects;
	}else { 
	    return new EmptyList();
	}
    }
    
}
