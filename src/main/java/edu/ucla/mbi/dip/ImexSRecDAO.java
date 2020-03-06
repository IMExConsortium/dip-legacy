package edu.ucla.mbi.dip;

/*==============================================================================
 * $HeadURL:: https://imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-legacy/src/main/j#$
 * $Id:: ImexSRecDAO.java 2616 2012-08-07 05:17:42Z lukasz                     $
 * Version: $Rev:: 2616                                                        $
 *==============================================================================
 *
 * ImexSRecDAO:
 *
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

public class ImexSRecDAO extends AbstractDAO{
    
    public void create( ImexSRec isr )
	throws DAOException {
	saveOrUpdate( isr );
    }

    public void saveOrUpdate( ImexSRec isr ) 
	throws DAOException {
	super.saveOrUpdate( isr );
    }

    public void delete( ImexSRec isr ) 
	throws DAOException {
	super.delete( isr );
    }

    public ImexSRec find( Long id ) 
	throws DAOException {
	return (ImexSRec) find( ImexSRec.class, id );
    }

    public ImexSRec find( long id ) 
	throws DAOException {
	return (ImexSRec) find( ImexSRec.class, new Long( id ) );
    }

    public List findById( long id )
        throws DAOException {
        ImexSRec isr = (ImexSRec) find( ImexSRec.class, new Long( id ) );

	if( isr!=null ){
	    List res = new ArrayList();
	    res.add( isr );
	    return res;
	} else {
	    return new EmptyList();
	}
    }
    
    //--------------------------------------------------------------------------

    public List findByImex254( String id ) 
	throws DAOException {

	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

	try {
            Query query = session
                .createQuery( "from ImexSRec isr where isr.imex254 = :id" );
	    query.setParameter( "id", id );
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

    //--------------------------------------------------------------------------

    public List findByImexId( long iid ) 
	throws DAOException {

	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

	try {
            Query query = session
                .createQuery( "from ImexSRec isr where isr.imexId = :iid" );
	    query.setParameter("iid", iid);
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
    
    //--------------------------------------------------------------------------
    
    public List findByEvidId( long eid ) 
	throws DAOException {

	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
	try {
            Query query = session
                .createQuery( "from ImexSRec isr where isr.evidId = :eid" );
	    query.setParameter( "eid", eid );
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
	return findAll( ImexSRec.class, "id" );
    }

    public List findAll( String field ) throws DAOException {
	return findAll( ImexSRec.class, field );
    }
    
}



