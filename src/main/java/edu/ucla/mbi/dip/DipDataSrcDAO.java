package edu.ucla.mbi.dip;

/*==============================================================================
 * $HeadURL:: https://imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-legacy/src/main/j#$
 * $Id:: DipDataSrcDAO.java 2189 2012-04-21 17:59:45Z lukasz                   $
 * Version: $Rev:: 2189                                                        $
 *==============================================================================
 *
 * DipDataSrcDAO:
 *
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

 
public class DipDataSrcDAO extends AbstractDAO{
    
    public void create( DipDataSrc dsrc )
	throws DAOException {
	saveOrUpdate( dsrc );
    }

    public void saveOrUpdate( DipDataSrc dsrc ) 
	throws DAOException {
	super.saveOrUpdate( dsrc );
    }

    public void delete( DipDataSrc dsrc ) 
	throws DAOException {
	super.delete( dsrc );
    }

    public DipDataSrc find( Long id ) 
	throws DAOException {
	return (DipDataSrc) find( DipDataSrc.class, id );
    }

    public DipDataSrc find( long id ) 
	throws DAOException {
	return (DipDataSrc) find( DipDataSrc.class, new Long( id ) );
    }

    //--------------------------------------------------------------------------
    
    public  DipDataSrc findByMinId()
        throws DAOException {

        List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query = session
                .createQuery( "from DipDataSrc dn where" +
                              " dn.id= (select min(n.id) from DipDataSrc n)" );
            objects = query.list();
            tx.commit();
        } catch( HibernateException e ) {
            handleException(e);
        } finally {
            session.close();
        }

        if( objects!=null && objects.size()==1 ){
            return (DipDataSrc) objects.get(0);
        } else{
            throw new DAOException("No MinId DipDataSrc found");
        }
    }

    //--------------------------------------------------------------------------
    
    public  DipDataSrc findByMaxId()
        throws DAOException {

        List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query = session
                .createQuery( "from DipDataSrc dn where"+
                              " dn.id= (select max(n.id) from DipDataSrc n)" );
            objects = query.list();
            tx.commit();
        } catch( HibernateException e ) {
            handleException(e);
        } finally {
            session.close();
        }

        if( objects!=null && objects.size()==1 ){
            return (DipDataSrc) objects.get(0);
        } else{
            throw new DAOException("No MaxId DipDataSrc found");
        }
    }

    //--------------------------------------------------------------------------

    public  List<DipDataSrc> findRange( long fr, long to )
        throws DAOException {

        List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query = session
                .createQuery( "from DipDataSrc dn where"+
                              " dn.id>= :fr and dn.id <= :to" );

            query.setParameter( "fr", fr );
            query.setParameter( "to", to );
            objects = query.list();
            tx.commit();
        } catch( HibernateException e ) {
            handleException(e);
        } finally {
            session.close();
        }

        List<DipDataSrc> res = new ArrayList<DipDataSrc>();

        if( objects != null ){
            for( Iterator i=objects.iterator(); i.hasNext(); ){
                DipDataSrc ds = (DipDataSrc) i.next();
                res.add( ds );
            }
        }

        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "DipDataSrcDAO: range("+fr+"->"+to+"): " + res.size() );
        return res;
    }

    //--------------------------------------------------------------------------
    
    public DipDataSrc findByPmid( String pmid ) 
	throws DAOException {

	List objects = null;

        Session session =
            HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

	try {
            Query query = session
                .createQuery( "select distinct ds from DipDataSrc ds" + 
                              " where ds.pmid = :pmid" );
	    query.setParameter( "pmid", pmid );
	    objects = query.list();
	    tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	} finally {
            session.close();
	}

	if( objects!=null && objects.size()>0 ){ 
	    return (DipDataSrc) objects.get(0);
	}else { 
	    return null;
	}
    }
    
    public List findAll() throws DAOException {
	return findAll( DipDataSrc.class, "skey" );
    }

    public List findAll( String field ) throws DAOException {
	return findAll( DipDataSrc.class, field );
    }
    
}



