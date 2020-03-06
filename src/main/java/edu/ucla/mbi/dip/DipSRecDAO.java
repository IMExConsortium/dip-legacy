package edu.ucla.mbi.dip;

/*==============================================================================
 * $HeadURL:: https://imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-legacy/src/main/j#$
 * $Id:: DipSRecDAO.java 2189 2012-04-21 17:59:45Z lukasz                      $
 * Version: $Rev:: 2189                                                        $
 *==============================================================================
 *
 * DipSRecDAO:
 *
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

public class DipSRecDAO extends AbstractDAO{
    
    public void create( DipSRec isr )
	throws DAOException {
	saveOrUpdate( isr );
    }
    
    public void saveOrUpdate( DipSRec isr ) 
	throws DAOException {
	super.saveOrUpdate( isr );
    }

    public DipSRec find( Long id ) 
	throws DAOException {
	return (DipSRec) find( DipSRec.class, id );
    }

    public DipSRec find( long id ) 
	throws DAOException {
	return (DipSRec) find( DipSRec.class, new Long( id ) );
    }

    public List findById( long id )
        throws DAOException {
        DipSRec isr = (DipSRec) find( DipSRec.class, new Long( id ) );

	if( isr!=null ){
	    List res = new ArrayList();
	    res.add( isr );
	    return res;
	} else {
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
                .createQuery( "from DipSRec isr where isr.evidence.id = :eid");
	    query.setParameter("eid", eid);
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
	return findAll( DipSRec.class, "id" );
    }

    public List findAll( String field ) throws DAOException {
	return findAll( DipSRec.class, field );
    }
    
}



