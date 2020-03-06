package edu.ucla.mbi.dip;


/*==============================================================================
 * $HeadURL:: https://imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-legacy/src/main/j#$
 * $Id:: InferDAO.java 2616 2012-08-07 05:17:42Z lukasz                        $
 * Version: $Rev:: 2616                                                        $
 *==============================================================================
 *
 * InferDAO:
 *
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

public class InferDAO extends AbstractDAO{
    
    public void create( Inference inference )
	throws DAOException {
	saveOrUpdate( inference );
    }
    
    public void saveOrUpdate( Inference inference ) 
	throws DAOException {
	super.saveOrUpdate( inference );
    }

    public Inference find( Long id ) 
	throws DAOException {
	return (Inference) find( Inference.class, id );
    }

    public Inference find( long id ) 
	throws DAOException {
	return (Inference) find( Inference.class, new Long( id ) );
    }

    public List findById( long id )
        throws DAOException {
        Inference  inf = (Inference) find( Inference.class, new Long( id ) );

	if( inf!=null ){
	    List res = new ArrayList();
	    res.add( inf );
	    return res;
	} else {
	    return new EmptyList();
	}
    }
    
    public List findByLinkId( long id )
        throws DAOException {
	
	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
	try {
            Query query = session
                .createQuery( "from Inference inf" +
                              " where inf.link.id = :id" );
	    
	    query.setParameter("id", id);
	    
	    objects = query.list();
            tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	} finally {
            session.close();
	}

	if(objects!=null){
	    return objects;
	} else{
	    return new EmptyList();
	}
    }


    public List findByGroundsId( long id )
        throws DAOException {
	
	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

	try {
	    Query query = session
                .createQuery( "from Inference inf"+
                              " where inf.grounds.id = :id");
	    
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
	} else{
	    return new EmptyList();
	}
    }
    
    public List findAll() throws DAOException {
	return findAll( Inference.class, "iik" );
    }

    public List findAll( String field ) throws DAOException {
	return findAll( Inference.class, field );
    }
    
}



