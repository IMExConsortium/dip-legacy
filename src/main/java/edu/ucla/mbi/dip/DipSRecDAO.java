package edu.ucla.mbi.dip;

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

public class DipSRecDAO extends AbstractDAO{
    
    public void create( DipSRec isr )
        throws DAOException {
        saveOrUpdate( isr );
    }
    
    public void saveOrUpdate( DipSRec isr ) 
        throws DAOException {
        super.saveOrUpdate( isr );
    }
    
    public DipSRec find( Long id ) 
        throws DAOException {
        return (DipSRec) find( DipSRec.class, id );
    }
    
    public DipSRec find( long id ) 
        throws DAOException {
        return (DipSRec) find( DipSRec.class, new Long( id ) );
    }
    
    public List findById( long id )
        throws DAOException {
        DipSRec isr = (DipSRec) find( DipSRec.class, new Long( id ) );
        
        if( isr!=null ){
            List res = new ArrayList();
            res.add( isr );
            return res;
        } else {
            return new EmptyList();
        }
    }
    
    //--------------------------------------------------------------------------
    
    public List findByEvidId( long eid ) 
        throws DAOException {
        
        List objects = null;

        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query = session
                .createQuery( "from DipSRec isr where isr.evidence.id = :eid");
            query.setParameter("eid", eid);
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
        return findAll( DipSRec.class, "id" );
    }
    
    public List findAll( String field ) throws DAOException {
        return findAll( DipSRec.class, field );
    }
}



