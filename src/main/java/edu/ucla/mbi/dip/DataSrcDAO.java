package edu.ucla.mbi.dip;

/*==============================================================================
 * $HeadURL:: https://imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-legacy/src/main/j#$
 * $Id:: DataSrcDAO.java 2189 2012-04-21 17:59:45Z lukasz                      $
 * Version: $Rev:: 2189                                                        $
 *==============================================================================
 *
 * DataSrcDAO:
 *
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

 
public class DataSrcDAO extends AbstractDAO{
    
    public void create( DataSrc dsrc )
	throws DAOException {
	saveOrUpdate(dsrc);
    }

    public void saveOrUpdate( DataSrc dsrc ) 
	throws DAOException {
	super.saveOrUpdate(dsrc);
    }

    public DataSrc find( Long id ) 
	throws DAOException {
	return (DataSrc) find( DataSrc.class, id );
    }

    public DataSrc find(long id) 
	throws DAOException {
	return (DataSrc) find( DataSrc.class, new Long( id ) );
    }

    public List findByPmid( String pmid ) 
	throws DAOException {
        
	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

	try {
            Query query = session
                .createQuery( "select distinct ds.id" +
                              " from DataSrc ds where ds.pmid = :pmid" );
	    query.setParameter("pmid", pmid);
	   
	    objects= query.list();
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
	return findAll(DataSrc.class,"skey");
    }

    public List findAll(String field) throws DAOException {
	return findAll(DataSrc.class, field);
    }
    
}



