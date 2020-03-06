package edu.ucla.mbi.dip;

/*==============================================================================
 * $HeadURL:: https://imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-legacy/src/main/j#$
 * $Id:: OrganismDAO.java 2616 2012-08-07 05:17:42Z lukasz                     $
 * Version: $Rev:: 2616                                                        $
 *==============================================================================
 *
 * OrganismDAO:
 *
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

public class OrganismDAO extends AbstractDAO{
    
    public void create( Organism org )
	throws DAOException {
	saveOrUpdate( org );
    }

    public void saveOrUpdate( Organism org ) 
	throws DAOException {
	super.saveOrUpdate( org );
    }

    public Organism find( Long id ) 
	throws DAOException {
	return (Organism) find( Organism.class, id );
    }

    public Organism find( long id ) 
	throws DAOException {
	return (Organism) find( Organism.class, new Long( id ) );
    }
    
    public List findById( long id )
        throws DAOException {
        Organism org = (Organism) find( Organism.class, new Long( id ) );
        
	if( org!=null ){
	    List res = new ArrayList();
	    res.add( org );
	    return res;
	} else {
	    return new EmptyList();
	}
    }
    
    public List findByTaxId( String txid ) 
	throws DAOException {
        
	List objects = null;

        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();

	try {
            long ltx = Long.parseLong( txid );
            
            Query query = session
                .createQuery( "from Organism o where o.taxId = :tx" );
	    query.setParameter( "tx", ltx );
	    objects = query.list();
	    tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	}
        
	if( objects!=null ){ 
	    return objects;
	}else { 
	    return new EmptyList();
	}
    }
    
    public List findAll() throws DAOException {
	return findAll( Organism.class, "okey" );
    }

    public List findAll( String field ) throws DAOException {
	return findAll( Organism.class, field );
    }
    
}



