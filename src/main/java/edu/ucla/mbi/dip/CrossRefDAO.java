package edu.ucla.mbi.dip;

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 
public class CrossRefDAO 
    extends AbstractDAO{
    
    public void create( CrossRef xref )
        throws DAOException {
	saveOrUpdate( xref );
    }
    
    public void saveOrUpdate( CrossRef xref ) 
        throws DAOException {
        super.saveOrUpdate( xref );
    }
    
    public CrossRef find( Long id ) 
        throws DAOException {
        return (CrossRef) find( CrossRef.class, id );
    }
    
    public CrossRef find( long id ) 
        throws DAOException {
        return (CrossRef) find( CrossRef.class, new Long(id) );
    }
    
    public List findById( long id )
        throws DAOException {
        CrossRef xref = (CrossRef) find( CrossRef.class, new Long(id) );
        
        if(xref!=null){
            List res = new ArrayList();
            res.add(xref);
            return res;
        } else {
            return new EmptyList();
        }
    }
    
    public List getCrossRef(DipNode nd)
        throws DAOException{
        
        List objects=null;
        
        Session session = HibernateUtil.getCurrentSession();                
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            
            Query query
                = session.createQuery("select ncr from NodeCrossRef ncr"+
                                      " where ncr.target= :nd");
            query.setParameter("nd", nd);
            objects = query.list();
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        }  
        
        if(objects!=null){
            return objects;
        }
	
        return new EmptyList();
    }

    public List getCrossRef( DipNode nd, CvTerm xreftype )
        throws DAOException{
        
        List objects=null;
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            //startOperation();
            
            Query query
                = session.createQuery("select ncr from NodeCrossRef ncr"+
                                      " where ncr.target= :nd"+
                                      " and ncr.refType= :tp");
            query.setParameter("nd", nd);
            query.setParameter("tp", xreftype);
            objects = query.list();
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        }

        if(objects!=null){
            return objects;
        }
        
        return new EmptyList();
    }
    
    public List findAll() throws DAOException {
        return findAll(CrossRef.class,"id");
    }
    
    public List findAll(String field) throws DAOException {
        return findAll(CrossRef.class, field);
    }    
}



