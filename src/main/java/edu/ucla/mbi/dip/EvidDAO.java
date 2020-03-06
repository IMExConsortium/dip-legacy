package edu.ucla.mbi.dip;

/*==============================================================================
 * $HeadURL:: https://imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-legacy/src/main/j#$
 * $Id:: EvidDAO.java 2616 2012-08-07 05:17:42Z lukasz                         $
 * Version: $Rev:: 2616                                                        $
 *==============================================================================
 *
 * EvidDAO:
 *
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

 
public class EvidDAO extends AbstractDAO{
    
    public void create( Evidence evid )
	throws DAOException {
	saveOrUpdate( evid );
    }
    
    public void saveOrUpdate( Evidence evid ) 
	throws DAOException {
	super.saveOrUpdate( evid );
    }

    public void delete( Evidence evid ) 
	throws DAOException {
	super.delete( evid );
    }

    public Evidence find( Long id ) 
	throws DAOException {
	return (Evidence) find( Evidence.class, id );
    }

    public Evidence find( long id ) 
	throws DAOException {
	return (Evidence) find( Evidence.class, new Long( id ) );
    }

    public List findById( long id )
        throws DAOException {
        Evidence  evid = (Evidence) find( Evidence.class, new Long( id ) );
        
	if( evid!=null ){
	    List res = new ArrayList();
	    res.add( evid );
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
                .createQuery( "from Evidence ev" +
                              " where ev.link.id = :id" );
	    
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

    public List findByImexId( long id ){
	return findByImexId( new Long( id ) );
    }

    //--------------------------------------------------------------------------
    
    public List findByImexId( Long id )
        throws DAOException {
	
	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
	try {
            
	    Query query1 = session
                .createQuery( "from ImexSRec isr" +
                              " where isr.imexId = :id order by isr.id desc" );	    
	    query1.setParameter( "id", id );

	    List tObj = query1.list();
	    
	    if( tObj!=null && tObj.size()>0 ){
		
                
		Query query2 = session
                    .createQuery( "from Evidence ev" +
                                  " where ev.evidId = :id" );
	    
		query2.setParameter( "id", 
                                     ((ImexSRec)tObj.get(0)).getEvidId() );
                objects = query2.list();

	    }
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

    //--------------------------------------------------------------------------

    public List findByImex254( String id )
        throws DAOException {
	
	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
	try {

	    Query query1 = session
                .createQuery( "from ImexSRec isr where "+
                              " isr.imex254 = :id order by isr.id desc");	    
	    query1.setParameter("id", id);

	    List tObj = query1.list();
	    
	    if(tObj!=null && tObj.size()>0){
		
	       
		Query query2
		    = session.createQuery( "from Evidence ev" +
                                           " where ev.evidId = :id" );
	    
		query2.setParameter( "id", 
                                     ((ImexSRec)tObj.get(0)).getEvidId() );
	    
		objects = query2.list();

	    }
            tx.commit();
	} catch (HibernateException e) {
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

    //--------------------------------------------------------------------------

    public List findByDataSrc( DataSrc dsrc )
        throws DAOException {
	
	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
	try {
            Query query1 = session
                .createQuery( "from Evidence ev" +
                              " where ev.dataSrc = :dsrc order by ev.id asc" );	    
	    query1.setParameter( "dsrc", dsrc );
            
	    objects = query1.list();
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
	return findAll( Evidence.class, "evkey" );
    }

    public List findAll( String field ) throws DAOException {
	return findAll( Evidence.class, field );
    }
    
}



