package edu.ucla.mbi.dip;

/* =============================================================================
 * $Id:: CvtDAO.java 2616 2012-08-07 05:17:42Z lukasz                          $
 * Version: $Rev:: 2616                                                        $
 *==============================================================================
 *                                                                             $
 *  CvtDAO -                                                                   $
 *                                                                             $
 *    NOTES:                                                                   $
 *                                                                             $
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

public class CvtDAO extends AbstractDAO{
    
    public void create( CvType cvt )
	throws DAOException {
	saveOrUpdate(cvt);
    }

    public void saveOrUpdate( CvType cvt ) 
	throws DAOException {
	super.saveOrUpdate(cvt);
    }

    public CvType find( Long id ) 
	throws DAOException {
	return (CvType) find(CvType.class, id);
    }

    public CvType find(long id) 
	throws DAOException {
	return (CvType) find(CvType.class, new Long(id));
    }
    
    public List findById(long id)
        throws DAOException {
        CvType cvt = (CvType) find(CvType.class, new Long(id));

	if(cvt!=null){
	    List res = new ArrayList();
	    res.add(cvt);
	    return res;
	} else {
	    return new EmptyList();
	}
    }
    
    public List findByName(String name) 
	throws DAOException {

	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

	try {
            Query query = session
                .createQuery( "from CvType cvt where cvt.name = :cvt" );
	    query.setParameter( "cvt", name );
	    objects = query.list();
	    tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	}finally{
	    session.close();
	}

	if(objects!=null){ 
	    return objects;
	}else { 
	    return new EmptyList();
	}
    }

    
    public List findAll() throws DAOException {
	return findAll(CvType.class,"id");
    }

    public List findAll(String field) throws DAOException {
	return findAll(CvType.class, field);
    }
    
}



