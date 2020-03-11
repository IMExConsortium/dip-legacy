package edu.ucla.mbi.dip;

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

public class LNodeDAO extends AbstractDAO{
    
    public void create( LNode lnode )
        throws DAOException {
        saveOrUpdate( lnode );
    }
    
    public void saveOrUpdate( LNode lnode ) 
        throws DAOException {
        super.saveOrUpdate( lnode );
    }
    
    public LNode find( Long id ) 
        throws DAOException {
        return (LNode) find( LNode.class, id );
    }

    public LNode find( long id ) 
        throws DAOException {
        return (LNode) find( LNode.class, new Long( id ) );
    }
    
    public List findById( long id )
        throws DAOException {
        LNode  lnode = (LNode) find( LNode.class, new Long( id ) );
        
        if( lnode!=null ){
            List res = new ArrayList();
            res.add( lnode );
            return res;
        } else {
            return new EmptyList();
        }
    }
    
    /*
    public List findByRefSeq( String rs ) 
      throws DAOException {
    
      List objects = null;
      try {
	    startOperation();
        Query query
		= session.createQuery("from Node j where j.refSeq = :rs");
	    query.setParameter("rs", rs);
	    objects = query.list();
	    tx.commit();
        } catch (HibernateException e) {
	    handleException(e);
	} finally {
	    HibernateUtil.closeSession();
	}

	if(objects!=null){ 
	    return objects;
	}else { 
	    return new EmptyList();
	}
    }

    public List findByUniProt(String up) 
	throws DAOException {

	List objects = null;
	try {
	    startOperation();
            Query query
		= session.createQuery("from Node j where j.uniProt = :up");
	    query.setParameter("up", up);
	    objects = query.list();
	    tx.commit();
	} catch (HibernateException e) {
	    handleException(e);
	} finally {
	    HibernateUtil.closeSession();
	}

	if(objects!=null){ 
	    return objects;
	}else { 
	    return new EmptyList();
	}
    }

    */
    
    public List findAll() throws DAOException {
        return findAll( LNode.class, "ikey" );
    }
    
    public List findAll( String field ) throws DAOException {
        return findAll( LNode.class, field );
    }
    
}



