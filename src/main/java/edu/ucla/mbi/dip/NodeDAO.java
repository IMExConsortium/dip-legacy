package edu.ucla.mbi.dip;

import edu.ucla.mbi.orm.*;

import org.hibernate.*;

import java.net.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class NodeDAO extends AbstractDAO {
    
    public void create( DipNode node ) throws DAOException {
        saveOrUpdate( node );
    }
    
    public void saveOrUpdate( DipNode node ) throws DAOException {
        super.saveOrUpdate( node );
    }
    
    public DipNode find( Long id ) throws DAOException {
        return (DipNode) find( DipNode.class, id );
    }
    
    public DipNode find( long id ) throws DAOException {
        return (DipNode) find( DipNode.class, new Long( id ) );
    }
    
    public List findById( long id ) throws DAOException {
        DipNode  node = (DipNode) find( DipNode.class, new Long( id ) );
        
        if( node!=null ){
            List res = new ArrayList();
            res.add( node );
            return res;
        } else {
            return new EmptyList();
        }
    }
    
    //--------------------------------------------------------------------------
    
    public List findBySequence( String seq ) throws DAOException {
        
        List objects = null;
        
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try{
            Query query = session
                .createQuery( "from DipNode j where j.sequence = :seq" );
            query.setParameter( "seq", seq );
            objects = query.list();
            tx.commit();
        }catch( HibernateException e ){
            handleException(e);
        }
        
        if( objects!=null ){ 
            return objects;
        }else{ 
            return new EmptyList();
        }
    }
    
    public List findByRefSeq( String rs ) throws DAOException {
        
        List objects = null;
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try{
            Query query = session
                .createQuery( "from DipNode j where j.refSeq = :rs" );
            query.setParameter( "rs", rs );
            objects = query.list();
            tx.commit();
        }catch( HibernateException e ) {
            handleException(e);
        }
        
        if( objects!=null ){ 
            return objects;
        }else{ 
            return new EmptyList();
        }
    }
    
    
    //--------------------------------------------------------------------------
    
    public List findByXref( String ns, String acc ) throws DAOException {
        
        List objects = null;
        
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query = session
                .createQuery( "select distinct ncr.target" +
                              " from NodeCrossRef ncr "+
                              " where ncr.acc = :ac and ncr.srcDb.name= :ns" );
            query.setParameter( "ac", acc );
            query.setParameter( "ns", ns );
            objects = query.list();
            tx.commit();
        } catch( HibernateException e ) {
            handleException(e);
        }
        
        if( objects!=null ){ 
            return objects;
        }else{ 
            return new EmptyList();
        }
    }
    
    //--------------------------------------------------------------------------
    
    public List findByUniProt( String up ) throws DAOException {
        
        List objects = null;
        
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query = session
                .createQuery( "from DipNode j where j.uniProt = :up" );
            query.setParameter( "up", up );
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
    
    //--------------------------------------------------------------------------
    
    public  DipNode findByMinId() throws DAOException {
        
        List objects = null;
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            
            Query query = session
                .createQuery( "from DipNode dn where"+
                              " dn.id= (select min(n.id) from DipNode n)" );
            objects = query.list();
            tx.commit();
        } catch(HibernateException e ) {
            handleException(e);
        }
        if( objects!=null && objects.size()==1 ){
            return (DipNode) objects.get(0);
        } else{
            throw new DAOException("No MinId Node found");
        }
    }
    
    //--------------------------------------------------------------------------
    
    public  DipNode findByMaxId() throws DAOException {
        
        List objects = null;
        
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query = session
                .createQuery( "from DipNode dn where"+
                              " dn.id= (select max(n.id) from DipNode n)" );
            objects = query.list();
            tx.commit();
        } catch( HibernateException e ){
            handleException(e);
        }
        
        if( objects!=null && objects.size()==1 ){
            return (DipNode) objects.get(0);
        } else{
            throw new DAOException( "No MaxId Node found" );
        }
    }
    
    //--------------------------------------------------------------------------
    
    public  List<DipNode> findRange( long fr, long to ) throws DAOException {
        
        List objects = null;
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query = session
                .createQuery( "from DipNode dn"+
                              " where dn.id>= :fr and dn.id <= :to" );
            
            query.setParameter( "fr", fr );
            query.setParameter( "to", to );
            objects = query.list();
            tx.commit();
        } catch( HibernateException e ) {
            handleException(e);
        }
        
        List<DipNode> res = new ArrayList<DipNode>();
        
        if( objects != null ){   
            for( Iterator i=objects.iterator(); i.hasNext(); ){
                DipNode dn = (DipNode) i.next();
                res.add( dn );
            }
        }
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "NodeDAO: range("+fr+"->"+to+"): " + res.size() );
        return res;
    }
    
    //--------------------------------------------------------------------------
    
    public List findAll() throws DAOException {
        return findAll( DipNode.class, "pkey" );
    }
    
    public List findAll( String field ) throws DAOException {
        return findAll( DipNode.class, field );
    }  
}



