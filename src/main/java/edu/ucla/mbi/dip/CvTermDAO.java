package edu.ucla.mbi.dip;

/*==============================================================================
 * $HeadURL:: https://imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-legacy/src/main/j#$
 * $Id:: CvTermDAO.java 2616 2012-08-07 05:17:42Z lukasz                       $
 * Version: $Rev:: 2616                                                        $
 *==============================================================================
 *
 * NodeDAO:
 *
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;
 
public class CvTermDAO extends AbstractDAO{
    
    public void create( CvTerm cvt )
	throws DAOException {
	saveOrUpdate(cvt);
    }

    public void saveOrUpdate( CvTerm cvt ) 
	throws DAOException {
	super.saveOrUpdate(cvt);
    }

    public CvTerm find( Long id ) 
	throws DAOException {
	return (CvTerm) find(CvType.class, id);
    }

    public CvTerm find( long id ) 
	throws DAOException {
	return (CvTerm) find(CvTerm.class, new Long(id));
    }
    
    public List findById( long id )
        throws DAOException {
        CvTerm cvt = (CvTerm) find(CvTerm.class, new Long(id));

	if(cvt!=null){
	    List res = new ArrayList();
	    res.add(cvt);
	    return res;
	} else {
	    return new EmptyList();
	}
    }


    public CvTerm find( String cvid ) 
	throws DAOException {

	List objects = null;
        
        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
	
        try {
            Query query = session
                .createQuery( "from CvTerm cvt where cvt.cvId = :cvid" );
	    query.setParameter("cvid",cvid);
	    objects = query.list();
	    tx.commit();
	} catch (HibernateException e) {
	    handleException(e);
	}finally{
            session.close();
	}
	if(objects!=null && objects.size()==1){ 
	    return (CvTerm) objects.get(0);
	}else { 
	    return null;
	}
    }

    //--------------------------------------------------------------------------
    
    public List findByName(String name) 
	throws DAOException {

	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
	try {
            Query query = session
                .createQuery( "from CvTerm cvt where cvt.name = :cvt" );
	    query.setParameter( "cvt", name );
	    objects = query.list();
	    tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	}finally{
            session.close();
	}

	if( objects!=null ){ 
	    return objects;
	}else{ 
	    return new EmptyList();
	}
    }

    
    public List findAll() throws DAOException {
	return findAll(CvTerm.class,"id");
    }

    public List findAll(String field) throws DAOException {
	return findAll(CvTerm.class, field);
    }
    
}



