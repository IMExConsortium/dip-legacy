package edu.ucla.mbi.dip;

/* =============================================================================
 * $Id:: CountsDAO.java 2616 2012-08-07 05:17:42Z lukasz                       $
 * Version: $Rev:: 2616                                                        $
 *==============================================================================
 *                                                                             $
 *  CountsDAO - access to txstat (cumulative counts) table                     $
 *                                                                             $
 *    NOTES:                                                                   $
 *                                                                             $
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

public class CountsDAO extends AbstractDAO{
    
    public void create( Counts cnt ) throws DAOException {
	saveOrUpdate( cnt );
    }

    public void saveOrUpdate( Counts cnt ) throws DAOException {
	super.saveOrUpdate( cnt );
    }
    
    public Counts find( Long id ) throws DAOException {
	return (Counts) find( Counts.class, id );
    }
    
    public Counts find( long id ) throws DAOException {
	return (Counts) find( Counts.class, new Long( id ) );
    }
    
    public List findById( long id ) throws DAOException {
        Counts cnt = (Counts) find( Counts.class, new Long( id ) );

	if( cnt != null ) {
	    List res = new ArrayList();
	    res.add( cnt );
	    return res;
	} else {
	    return new EmptyList();
	}
    }

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
    
    public List findByTaxid( long txid ) throws DAOException {
        
	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

	try {
            Query query = session
                .createQuery( "from Counts c where c.taxid = :tx" );
	    query.setParameter( "tx", txid );
	    objects = query.list();
	    tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	} finally {
	    session.close();
	}
        
	if( objects!=null ){ 
	    return objects;
	}else { 
	    return new EmptyList();
	}
    }
    
    public List findAll() throws DAOException {
	return findAll( Counts.class, "interactions" );
    }

    public List findAll( String field ) throws DAOException {
	return findAll( Counts.class, field );
    }
    
}



