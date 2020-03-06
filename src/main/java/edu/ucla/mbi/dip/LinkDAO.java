package edu.ucla.mbi.dip;

/*==============================================================================
 * $HeadURL:: https://imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-legacy/src/main/j#$
 * $Id:: LinkDAO.java 2616 2012-08-07 05:17:42Z lukasz                         $
 * Version: $Rev:: 2616                                                        $
 *==============================================================================
 *
 * LinkDAO:
 *
 *=========================================================================== */

import edu.ucla.mbi.orm.*;
import org.hibernate.*;

import java.net.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

 
public class LinkDAO extends AbstractDAO{
    
    public void create( Link link )
	throws DAOException {
	saveOrUpdate( link );
    }

    public void saveOrUpdate( Link link ) 
	throws DAOException {
	super.saveOrUpdate( link );
    }

    public Link find( Long id ) 
	throws DAOException {
	return (Link) find( Link.class, id );
    }

    public Link find( long id ) 
	throws DAOException {
	return (Link) find( Link.class, new Long( id ) );
    }
    
    public List<Link> findById( long id )
        throws DAOException {
        Link link = (Link) find( Link.class, new Long( id ) );
        
	List<Link> res = new ArrayList<Link>();
	if( link!=null ){
	    res.add( link );
	}
	return res;
    }

    //--------------------------------------------------------------------------
    
    public Link findByMinId()
        throws DAOException {

	List objects = null;

        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();

	try {
            Query query = session
                .createQuery("from Link dl where" +
                             " dl.id= (select min(l.id) from Link l)");
            objects = query.list();
            tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	}
        
	if( objects!=null && objects.size()==1 ){
	    return (Link) objects.get(0);
	} else{
	    throw new DAOException("No MinId Link found");
	}
    }

    //--------------------------------------------------------------------------

    public Link findByMaxId()
        throws DAOException {

	List objects = null;
        
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
	try {
            Query query = session
                .createQuery( "from Link dl where" +
                              " dl.id= (select max(l.id) from Link l)" );
            objects = query.list();
            tx.commit();
	} catch (HibernateException e) {
	    handleException(e);
	}
        
	if( objects!=null && objects.size()==1 ){
	    return (Link) objects.get(0);
	} else{
	    throw new DAOException("No MaxId Link found");
	}
    }

    //--------------------------------------------------------------------------
    
    public List<Link> findByPubMedId( String pmid )
        throws DAOException {
	
	List<Link> objects = null;

        Log log = LogFactory.getLog( this.getClass() );
        
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
	try {
	    Query q1 = session
                .createQuery( "from Link as l"+
                              " where l.evidence.dataSrc.pmid= :id" );
            
	    q1.setParameter( "id", pmid );
	    objects = q1.list();
	    tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	}
	log.debug( "LinkDAO: "+objects.size() );
        
	if( objects!=null && objects.size()>0 ){
	    return (List<Link>) objects;
	} else{
	    return new ArrayList<Link>();
	}
    }

    //--------------------------------------------------------------------------
    
    public List<Link> findByImexId( String id ){
	
	// ImexId format: IM-1342342
	//---------------------------
         
        if( id.startsWith("IM-") ){
	    id=id.replaceFirst( "IM-", "" );
	    try{
	    	return findByImexId( new Long( id ) );
	    } catch( Exception e ){}
	}
	return new ArrayList<Link>();   
    }
    
    public List<Link> findByImexId( long id ){
	return findByImexId( new Long( id ) );
    }
    
    public List<Link> findByImexId( Long id )
        throws DAOException {
	
	List objects = null;

        Log log = LogFactory.getLog( this.getClass() );
        
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();

	try {
            Query query1  = session
                .createQuery( "from ImexSRec as imx" +
                              " where imx.imexId= :id" );
	    query1.setParameter( "id", id );
	    List tObj = query1.list();
	    
	    if(tObj!=null && tObj.size()>0){
		ImexSRec imx=(ImexSRec)tObj.get(0);

                log.debug( "ImexId: " + imx.getImexId() );
                
		Query query2
		    = session.createQuery("from Link as l " +
                                          " where l.evidence.id= :id" );
		
		query2.setParameter( "id", imx.getEvidId() );
		objects = query2.list();
	    }
	    tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	}
        
	if( objects!=null && objects.size()>0 ){
	    return  (List<Link>) objects;
	} else{
	    throw new DAOException("No matching Link found");
	}
    }

    //--------------------------------------------------------------------------

    public List<Link> findByImex254( String id )
        throws DAOException {
	
	List objects = null;

        Log log = LogFactory.getLog( this.getClass() );

        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
	try {
            Query query1 = session
                .createQuery( "from ImexSRec as imx" +
                              " where imx.imex254= :id" );
	    query1.setParameter( "id", id );
	    List tObj = query1.list();
	    
	    if( tObj!=null && tObj.size()>0 ){
		ImexSRec imx=(ImexSRec) tObj.get(0);
                
		log.debug( "ImexId: " + imx.getImexId() );
                
		Query query2 = session
                    .createQuery( "from Link as l " +
                                  " where l.evidence.id= :id" );
		
		query2.setParameter( "id", imx.getEvidId() );
		objects = query2.list();
	    }
	    tx.commit();
	} catch( HibernateException e ) {
	    handleException(e);
	}

	if(objects!=null && objects.size()>0){
	    return (List<Link>) objects;
	} else{
	    throw new DAOException("No matching Link found");
	}
    }
    
    //--------------------------------------------------------------------------

    public  List<Link> findRange( long fr, long to )
        throws DAOException {
	
        List objects = null;
        
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        try {
            Query query = session
                .createQuery( "from Link dl" +
                              " where dl.id>= :fr and dl.id <= :to" );
	    
            query.setParameter( "fr", fr );
            query.setParameter( "to", to );
            objects = query.list();
            tx.commit();
        } catch (HibernateException e) {
            handleException(e);
        }
        
        if( objects != null ){
            List<Link> res = new ArrayList<Link>();

            for( Iterator i=objects.iterator(); i.hasNext(); ){
                Link ll = (Link) i.next();


                Log log = LogFactory.getLog( this.getClass() );
                log.debug( "LinkDAO: ac="+ ll.getAccession());

                if( LinkDAO.getPublic( ll ) ){
                    res.add( ll );
                }
            }
            
            if( res.size()>0 ){
                Log log = LogFactory.getLog( this.getClass() );
                log.debug( "LinkDAO: range("+fr+"->"+to+"): " + res.size() );
                return res;
            }
        } 
        
        return new ArrayList<Link>();            
    }

    //--------------------------------------------------------------------------

    public List<Link> findByNodeIdSet( Set ndId, boolean exact ){
	if(exact){
	    return findByNodeIdList( new ArrayList( ndId ));
	} else {
	    return findByNodeIdListMatch( new ArrayList( ndId ) );
	}
    }
    
    public List<Link> findByNodeIdList( List ndId, boolean exact ){
	if(exact){
	    return findByNodeIdList( new ArrayList( ndId ) );
        } else {
            return findByNodeIdListMatch( new ArrayList( ndId ) );
	}
    }
    
    public List<Link> findByNodeIdSet( Set ndId ){
	return findByNodeIdList(new ArrayList( ndId ) );
    }
    

    //--------------------------------------------------------------------------
    // finds any interaction with ineractor list
    //    same or superset of ndId list

    public List<Link> findByNodeIdListMatch( List ndId ) 
	throws DAOException {

        Log log = LogFactory.getLog( this.getClass() );
        log.debug("LinkDAO: findByNodeIdList");
	
	List<Link> objects = null;
        
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();

	try {

	    String q1 ="select l from Link as l "+
		"inner join l.lnode as lnA "+
		"where "+
		" lnA.node.id = :nA";
	    
	    String q2 ="select l from Link as l "+
		"inner join l.lnode as lnA "+
		"inner join l.lnode as lnB "+
		"where "+
		" lnA.node.id = :nA and"+
		" lnB.node.id = :nB";

	    String q3 ="select l from Link as l "+
		"inner join l.lnode as lnA "+
		"inner join l.lnode as lnB "+
		"inner join l.lnode as lnC "+
		"where "+
		" lnA.node.id = :nA and"+
		" lnB.node.id = :nB and"+
		" lnC.node.id = :nC";

	    String q4 ="select l from Link as l "+
		"inner join l.lnode as lnA "+
		"inner join l.lnode as lnB "+
		"inner join l.lnode as lnC "+
		"inner join l.lnode as lnD "+
		"where "+
		" lnA.node.id = :nA and"+
		" lnB.node.id = :nB and"+
		" lnC.node.id = :nC and"+
		" lnD.node.id = :nD";
	    
	    if( ndId==null ){
                tx.commit();
                return new ArrayList<Link>();
	    }
	    
	    int listSize = ndId.size();
	    
	    if( listSize<1 ){
                tx.commit();
                return new ArrayList<Link>();
	    }

	    Query query = null;
	    
	    if( listSize<5 ){
                
		log.debug("LinkDAO:  simple query: listsize->"+listSize);

		// simple query
		
		switch( listSize ){
		case 1:
		    query = session.createQuery( q1 );
		    query.setParameter( "nA", (Long)ndId.get(0) );
		    break;

		case 2:
		    query = session.createQuery( q2 );
		    query.setParameter( "nA", (Long) ndId.get(0) );
		    query.setParameter( "nB", (Long) ndId.get(1) );
		    break;
		case 3:

		    query = session.createQuery( q3 );
		    query.setParameter( "nA", (Long) ndId.get(0) );
		    query.setParameter( "nB", (Long) ndId.get(1) );
		    query.setParameter( "nC", (Long) ndId.get(2) );
		    break;
		case 4:

		    query = session.createQuery( q4 );
		    query.setParameter( "nA", (Long) ndId.get(0) );
		    query.setParameter( "nB", (Long) ndId.get(1) );
		    query.setParameter( "nC", (Long) ndId.get(2) );
		    query.setParameter( "nD", (Long) ndId.get(3) );
		    break;
		}
	    
		objects = (List<Link>) query.list();
		tx.commit();
		
	    } else {

		log.debug( "LinkDAO:  complex query: listsize->"+listSize);

		int cSz = listSize;
		int cBc = 4;
		int cLs = -1;
		int off = 0;
		List cObj = null;
		int maxCnt = 1;
                
		int lcnt = listSize/cBc+1; 

		while( cBc>0 && cLs!=0 && maxCnt<=lcnt ){
		    log.debug( "LinkDAO:  "+
                              "pass("+maxCnt+"); "+
                              "off("+off+"); "+
                              "result size("+cLs+")");
                    
		    Query pq = session.createQuery(q4);
		    
		    pq.setParameter( "nA", (Long) ndId.get(off+0) );
		    pq.setParameter( "nB", (Long) ndId.get(off+1) );
		    pq.setParameter( "nC", (Long) ndId.get(off+2) );
		    pq.setParameter( "nD", (Long) ndId.get(off+3) );


		    log.debug("LinkDAO:  query for-> " + listSize
                             +": "+ndId.get(off+0)
                             +": "+ndId.get(off+1)
                             +": "+ndId.get(off+2)
                             +": "+ndId.get(off+3)
                             );

		    List pObj = pq.list();

		    if( pObj==null ){
			log.debug( "LinkDAO:  res size -> null)" );
		    } else {
			log.debug( "LinkDAO:  res size -> " + pObj.size() + ")" );
		    }
					
		    if( cObj==null ){ // first pass
			cObj = pObj;
		    } else {
			List tmpObj = new ArrayList<Link>();
			
			if( cObj!=null && pObj!=null &&
                            cObj.size()>0 && pObj.size()>0 ){
			    for( Iterator ip = pObj.iterator(); ip.hasNext(); ){

				Link pl= (Link) ip.next();

				for( Iterator ic = cObj.iterator(); ic.hasNext(); ){
				    Link cl = (Link) ic.next();
				    
				    if( cl.getId()==pl.getId() ){
					tmpObj.add( cl );
					ic.remove();
					break;
				    }
				}
				if( cObj.size()==0 ){
				    break;
				}
			    }	 
			}
   			cObj = tmpObj;
		    }

		    off += cBc;
		    if( listSize-off<4 ){
			off = listSize-cBc;
		    }
		    
		    if( cObj==null ){
			cLs = 0;
		    }else {
			cLs = cObj.size();
		    }
		    maxCnt++;
		}
		
		objects = cObj;
		tx.commit();
	    }
	} catch (HibernateException e) {
	    handleException(e);
	}
        
	if( objects!=null && objects.size()>0 ){ 
	    return objects;
	}else { 
	    return new ArrayList<Link>();
	}
    }

    //--------------------------------------------------------------------------
    // finds interaction with interactor
    //      list match  and given size 

    public List<Link> findByNodeIdListMatch( List ndId, long ncnt ) 
	throws DAOException {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug("LinkDAO: findByNodeIdList");
	
	List<Link> objects = null;

        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();

	try {
            String q1 = "select l from Link as l "+
		"inner join l.lnode as lnA "+
		"where l.nodeCount = :cnt and"+
		" lnA.node.id = :nA";
	    
	    String q2 = "select l from Link as l "+
		"inner join l.lnode as lnA "+
		"inner join l.lnode as lnB "+
		"where l.nodeCount = :cnt and"+
		" lnA.node.id = :nA and"+
		" lnB.node.id = :nB";
            
	    String q3 = "select l from Link as l "+
		"inner join l.lnode as lnA "+
		"inner join l.lnode as lnB "+
		"inner join l.lnode as lnC "+
		"where l.nodeCount = :cnt and"+
		" lnA.node.id = :nA and"+
		" lnB.node.id = :nB and"+
		" lnC.node.id = :nC";

	    String q4 = "select l from Link as l "+
		"inner join l.lnode as lnA "+
		"inner join l.lnode as lnB "+
		"inner join l.lnode as lnC "+
		"inner join l.lnode as lnD "+
		"where l.nodeCount = :cnt and"+
		" lnA.node.id = :nA and"+
		" lnB.node.id = :nB and"+
		" lnC.node.id = :nC and"+
		" lnD.node.id = :nD";
	    
	    if( ndId==null ){
                tx.commit();
		return new ArrayList<Link>();
	    }
	    
	    int listSize=ndId.size();
	    
	    if( listSize<1 || listSize>ncnt ){
                tx.commit();
                return new ArrayList<Link>();
	    }

	    Query query = null;
	    
	    if(listSize<5){

		log.debug("LinkDAO:  simple query: listsize->"+listSize);

		// simple query
		
		switch(listSize){
		case 1:
		    query = session.createQuery(q1);
		    query.setParameter("cnt", new Long(ncnt));
		    query.setParameter("nA", (Long)ndId.get(0));
		    break;

		case 2:
		    query = session.createQuery(q2);
		    query.setParameter("cnt", new Long(ncnt));
		    query.setParameter("nA", (Long)ndId.get(0));
		    query.setParameter("nB", (Long)ndId.get(1));
		    break;
		case 3:

		    query = session.createQuery(q3);
		    query.setParameter("cnt", new Long(ncnt));
		    query.setParameter("nA", (Long)ndId.get(0));
		    query.setParameter("nB", (Long)ndId.get(1));
		    query.setParameter("nC", (Long)ndId.get(2));
		    break;
		case 4:

		    query = session.createQuery(q4);
		    query.setParameter("cnt", new Long(ncnt));
		    query.setParameter("nA", (Long)ndId.get(0));
		    query.setParameter("nB", (Long)ndId.get(1));
		    query.setParameter("nC", (Long)ndId.get(2));
		    query.setParameter("nD", (Long)ndId.get(3));
		    break;
		}
	    
		objects = (List<Link>)query.list();
		tx.commit();
		
	    } else {

		log.debug("LinkDAO:  complex query: listsize->"+listSize);
                
		int cSz=listSize;
		int cBc=4;
		int cLs=-1;
		int off=0;
		List cObj = null;
		int maxCnt=1;

		int lcnt=listSize/cBc+1; 

		while(cBc>0 && cLs!=0 && maxCnt<=lcnt){
		    log.debug("LinkDAO:  "+
				"pass("+maxCnt+"); "+
				"off("+off+"); "+
				"result size("+cLs+")");

		    Query pq = session.createQuery(q4);
		    
		    pq.setParameter("cnt", new Long(ncnt));
		    pq.setParameter("nA", (Long)ndId.get(off+0));
		    pq.setParameter("nB", (Long)ndId.get(off+1));
		    pq.setParameter("nC", (Long)ndId.get(off+2));
		    pq.setParameter("nD", (Long)ndId.get(off+3));


		    log.debug("LinkDAO:  query for-> "+listSize
				+": "+ndId.get(off+0)
				+": "+ndId.get(off+1)
				+": "+ndId.get(off+2)
				+": "+ndId.get(off+3)
				);

		    List<Link> pObj = (List<Link>)pq.list();

		    if(pObj==null){
			log.debug("LinkDAO:  res size -> null)");
		    } else {
			log.debug("LinkDAO:  res size -> "+pObj.size()+")");
		    }
					
		    if(cObj==null){ // first pass
			cObj=pObj;
		    } else {

			List<Link> tmpObj = new ArrayList<Link>();
			
			if(cObj!=null && pObj!=null &&
			   cObj.size()>0 && pObj.size()>0){
			    for(Iterator ip=pObj.iterator();ip.hasNext();){

				Link pl= (Link)ip.next();

				for(Iterator ic=cObj.iterator();ic.hasNext();){
				    Link cl=(Link)ic.next();
				    
				    if(cl.getId()==pl.getId()){
					tmpObj.add(cl);
					ic.remove();
					break;
				    }
				}
				if(cObj.size()==0){
				    break;
				}
			    }	 
			}
   			cObj=tmpObj;
		    }

		    off+=cBc;
		    if(listSize-off<4){
			off=listSize-cBc;
		    }
		    
		    if(cObj==null){
			cLs=0;
		    }else {
			cLs=cObj.size();
		    }
		    maxCnt++;
		}
		
		objects=cObj;
		tx.commit();
	    }
	} catch( HibernateException e ) {
	    handleException(e);
	}
       
	if( objects!=null && objects.size()>0 ){ 
	    return objects;
	}else { 
	    return new ArrayList<Link>();
	}
    }

    //--------------------------------------------------------------------------
    // finds interaction with at least one interactor 
    //    in list and a given size 
    
    public List<Link> findByNodeIdInList( List ndId, long ncnt ) 
	throws DAOException {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug("LinkDAO: findByNodeIdList");
	
	List<Link> objects = null;

        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();

	try {
            String q1 = "select l from Link as l "+
		"inner join l.lnode as lnA "+
		"where l.nodeCount = :cnt and"+
		" lnA.node.id in ( :nlA )";
	    
	    if( ndId == null ){
                tx.commit();
		return new ArrayList<Link>();
	    }
	    
	    int listSize=ndId.size();
	    
	    if( listSize<1 ){
                tx.commit();
                return new ArrayList<Link>();
	    }

	    Query query = session.createQuery( q1 );
            query.setParameter( "cnt", new Long(ncnt) );
            query.setParameterList( "nlA", ndId );
            
            objects = (List<Link>)query.list();
            tx.commit();
		
        } catch( HibernateException e ) {
	    handleException(e);
	}
        
	if( objects!=null && objects.size()>0 ){ 
	    return objects;
	}else { 
	    return new ArrayList<Link>();
	}
    }

   
    //--------------------------------------------------------------------------
    // finds interaction with at least M interactors 
    //    in list and a given N size  (N=0 any size)
    
    public List<Link> findByNodeIdInList( List ndId, long mcnt, long ncnt ) 
	throws DAOException {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug("LinkDAO: findByNodeIdList mcnt=" + mcnt + " ncnt=" + ncnt);
	
	List<Link> objects = null;

        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
        String q1 = "select l from Link as l ";

        for( long im =0; im < mcnt ; im++ ){
            q1 = q1 + " inner join l.lnode as ln" + im + " ";
        }
        
        if( ncnt > 0 ){
            q1 +=  "where l.nodeCount = :cnt ";
        } else {
            q1 += "where 1=1 ";
        }
        for( long im =0; im < mcnt ; im++ ){
            q1 += " and ln" + im + ".node.id in ( :nl" + im +")";
            if( im > 0 ){
                q1 += " and ln" + im + ".node.id > ln" + (im-1) + ".node.id"; 
            }
        }

        log.debug( "ListSize=" + ndId.size());
        log.debug( "LinkDAO: q1=" + q1 );
        
	try {
	    
	    if( ndId == null || (ncnt > 0 && mcnt > ncnt) ){
                log.debug("A");
                tx.commit();
		return new ArrayList<Link>();
	    }
	    
	    int listSize=ndId.size();
	    
            Query query = session.createQuery( q1 );
            
            for( long im =0; im < mcnt ; im++ ){
                query.setParameterList( "nl"+im , ndId );
            }

            if( ncnt > 0 ){
                query.setParameter( "cnt", new Long( ncnt ) );
            }
            
            objects = (List<Link>) query.list();
            tx.commit();
		
        } catch( HibernateException e ) {
	    handleException(e);
	}
        
	if( objects!=null && objects.size()>0 ){ 
            for( Iterator<Link> il = ((List<Link>)objects).iterator();
                 il.hasNext(); ){
                log.debug("LID: "+ il.next().getId());
            }
            return objects;
	}else { 
	    return new ArrayList<Link>();
	}
    }

    //--------------------------------------------------------------------------
    // finds interaction with at least one interactor 
    //    in list
    
    public List<Link> findByNodeIdInList( List ndId ) 
	throws DAOException {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.debug("LinkDAO: findByNodeIdList");
	
	List<Link> objects = null;

        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();

	try {
            String q1 = "select l from Link as l "+
		"inner join l.lnode as lnA "+
		"where lnA.node.id in ( :nlA )";
	    
	    if( ndId == null ){
                tx.commit();
                return new ArrayList<Link>();
	    }
	    
	    int listSize=ndId.size();
	    
	    if( listSize < 1  ){
                tx.commit();
                return new ArrayList<Link>();
	    }

	    Query query = session.createQuery( q1 );
            query.setParameterList( "nlA", ndId );
            
            objects = (List<Link>)query.list();
            tx.commit();
		
        } catch( HibernateException e ) {
	    handleException(e);
	}
        
	if( objects!=null && objects.size()>0 ){ 
	    return objects;
	}else { 
	    return new ArrayList<Link>();
	}
    }

    //--------------------------------------------------------------------------
    // finds interaction with exact interactor
    //      list match
    
    public List<Link> findByNodeIdList( List ndId ) 
	throws DAOException {
        Log log = LogFactory.getLog( this.getClass() );
	
	log.debug( "LinkDAO: findByNodeIdList" );
	
	List<Link> objects = null;

        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
	try {

	    String q1 = "select l from Link as l "+
		"inner join l.lnode as lnA "+
		"where l.nodeCount = :cnt and"+
		" lnA.node.id = :nA";
	    
	    String q2 = "select l from Link as l "+
		"inner join l.lnode as lnA "+
		"inner join l.lnode as lnB "+
		"where l.nodeCount = :cnt and"+
		" lnA.node.id = :nA and"+
		" lnB.node.id = :nB";

	    String q3 = "select l from Link as l "+
		"inner join l.lnode as lnA "+
		"inner join l.lnode as lnB "+
		"inner join l.lnode as lnC "+
		"where l.nodeCount = :cnt and"+
		" lnA.node.id = :nA and"+
		" lnB.node.id = :nB and"+
		" lnC.node.id = :nC";

	    String q4 = "select l from Link as l "+
		"inner join l.lnode as lnA "+
		"inner join l.lnode as lnB "+
		"inner join l.lnode as lnC "+
		"inner join l.lnode as lnD "+
		"where l.nodeCount = :cnt and"+
		" lnA.node.id = :nA and"+
		" lnB.node.id = :nB and"+
		" lnC.node.id = :nC and"+
		" lnD.node.id = :nD";
	    
            
	    if( ndId==null ){
                tx.commit();
                return new ArrayList<Link>();
	    }
	    
	    int listSize = ndId.size();
	    
	    if(listSize<1){
                tx.commit();
		return new ArrayList<Link>();
	    }

	    Query query = null;
	    
	    if( listSize<5 ){

		log.debug("LinkDAO:  simple query: listsize->"+listSize);

		// simple query
		
		switch( listSize ){
		case 1:
		    query = session.createQuery(q1);
		    query.setParameter("cnt", new Long(1));
		    query.setParameter("nA", (Long)ndId.get(0));
		    break;

		case 2:
		    query = session.createQuery(q2);
		    query.setParameter("cnt", new Integer(2));
		    query.setParameter("nA", (Long)ndId.get(0));
		    query.setParameter("nB", (Long)ndId.get(1));
		    break;
		case 3:

		    query = session.createQuery(q3);
		    query.setParameter("cnt", new Integer(3));
		    query.setParameter("nA", (Long)ndId.get(0));
		    query.setParameter("nB", (Long)ndId.get(1));
		    query.setParameter("nC", (Long)ndId.get(2));
		    break;
		case 4:

		    query = session.createQuery(q4);
		    query.setParameter("cnt", new Integer(4));
		    query.setParameter("nA", (Long)ndId.get(0));
		    query.setParameter("nB", (Long)ndId.get(1));
		    query.setParameter("nC", (Long)ndId.get(2));
		    query.setParameter("nD", (Long)ndId.get(3));
		    break;
		}
	    
		objects = (List<Link>)query.list();
		tx.commit();
            } else {

		log.debug("LinkDAO:  complex query: listsize->"+listSize);

		int cSz=listSize;
		int cBc=4;
		int cLs=-1;
		int off=0;
		List cObj = null;
		int maxCnt=1;

		int lcnt=listSize/cBc+1; 

		while( cBc>0 && cLs!=0 && maxCnt<=lcnt ){
		    log.debug("LinkDAO:  "+
                             "pass("+maxCnt+"); "+
                             "off("+off+"); "+
                             "result size("+cLs+")");

		    Query pq = session.createQuery(q4);
		    
		    pq.setParameter("cnt", new Integer(listSize));
		    pq.setParameter("nA", (Long)ndId.get(off+0));
		    pq.setParameter("nB", (Long)ndId.get(off+1));
		    pq.setParameter("nC", (Long)ndId.get(off+2));
		    pq.setParameter("nD", (Long)ndId.get(off+3));
                    
		    log.debug( "LinkDAO:  query for-> "+listSize
                              +": "+ndId.get(off+0)
                              +": "+ndId.get(off+1)
                              +": "+ndId.get(off+2)
                              +": "+ndId.get(off+3)
                              );
                    
		    List<Link> pObj = (List<Link>)pq.list();

		    if( pObj==null ){
			log.debug( "LinkDAO:  res size -> null)");
		    } else {
			log.debug( "LinkDAO:  res size -> "+pObj.size()+")");
		    }
					
		    if( cObj==null ){ // first pass
			cObj = pObj;
		    } else {
                        
			List tmpObj = new ArrayList();
			
			if( cObj!=null && pObj!=null &&
                            cObj.size()>0 && pObj.size()>0 ){
			    for( Iterator ip=pObj.iterator(); ip.hasNext(); ){
                                
				Link pl = (Link)ip.next();
                                
				for( Iterator ic=cObj.iterator(); ic.hasNext(); ){
				    Link cl = (Link)ic.next();
				    
				    if( cl.getId()==pl.getId() ){
					tmpObj.add( cl );
					ic.remove();
					break;
				    }
				}
				if( cObj.size()==0 ){
				    break;
				}
			    }	 
			}
   			cObj = tmpObj;
		    }

		    off += cBc;
		    if( listSize-off<4 ){
			off = listSize-cBc;
		    }
		    
		    if( cObj==null ){
			cLs = 0;
		    }else {
			cLs = cObj.size();
		    }
		    maxCnt++;
		}
		
		objects=cObj;
		tx.commit();
	    }
	} catch( HibernateException e ) {
	    handleException(e);
	}
        
	if( objects!=null && objects.size()>0 ){ 
	    return objects;
	} else { 
	    return new ArrayList<Link>();
	}
    }

    
    /*

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
	    //HibernateUtil.closeSession();
	}

	if(objects!=null){ 
	    return objects;
	}else { 
	    return new EmptyList();
	}
    }

    */

    /**  
	 returns one of:  
            assembly/physical/direct/covalent/functional/genetic
	 as inferred from the evidence available at the query time
    **/

    public List<CvType> getLinkType( Link ln )
	throws DAOException {
	
	List<CvType> ret = new ArrayList<CvType>();
        Log log = LogFactory.getLog( this.getClass() );
        log.debug( "LinkDAO: getLinkType -> "+ln.getAccession());
	log.debug( "LinkDAO: getLinkType(ev.size) -> "+ln.getEvidence().size());
        
	for( Iterator i = ln.getEvidence().iterator(); i.hasNext(); ){
	    Evidence e = (Evidence) i.next();
	    log.debug( "LinkDAO: getLinkType(ev.id) -> "+e.getId());
	    log.debug( "LinkDAO: getLinkType(ev.it) -> "+e.getCvtDetType());
	}
	
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        
	try {
            
            //Query query_evd
            //    = session.createQuery("select e.cvtInfType"+
	    //		      " from Evidence as e"+
	    //		      " where e. = :ln");
	    //query_evd.setParameter("ln", ln.getId());
	    //List evdl = query_evd.list();

	    //log.debug("LinkDAO: evdl="+evdl.size());

            Query query_inf = session
                .createQuery( "select i.cvtInfType.cvId" +
                              " from Inference i" +
                              " where i.link = :ln" );
            query_inf.setParameter( "ln", ln );
            List infl = query_inf.list();
	    log.debug( "LinkDAO: infll=" + infl.size() );
	    tx.commit();
            
	    boolean as=false;
	    boolean ph=false;
	    boolean dr=false;
	    boolean cv=false;
	    boolean gn=false;

	    if( ln.getEvidence()!=null && ln.getEvidence().size()>0 ){
		for( Iterator evdi = ln.getEvidence().iterator(); evdi.hasNext(); ){
		    Evidence cev = (Evidence)evdi.next();
		    String cac = cev.getCvtDetType().getCvId();
		    as = as || (cac.equals("dip:0191") || cac.equals("MI:0914"));
		    ph = ph || (cac.equals("MI:0218")  || cac.equals("MI:0915"));  
		    dr = dr || isDirect(cac);
		    cv = cv || isCovalent(cac);
		    gn = gn || isGenetic(cac);
		}
	    }

	    if( infl!=null && infl.size()>0 ){
		for( Iterator infi = infl.iterator(); infi.hasNext(); ){
		    String cac = (String)infi.next();
		    as = as || (cac.equals("dip:0191") || cac.equals("MI:0000"));
		    ph = ph || (cac.equals("MI:0218")  || cac.equals("MI:0915"));  
		    dr = dr || isDirect(cac);
		    cv = cv || isCovalent(cac);
		    gn = gn || isGenetic(cac);
		}
	    }
	    
	    if( gn ){
		// set genetic
		CvType t = new CvType( "psi-mi", "MI:0208", "genetic" );
                ret.add(t);
	    }
	    
	    if( cv ){
		// set covalent
		CvType t = new CvType( "psi-mi", "MI:0195", "covalent" );
		ret.add( t );
		return ret;
	    }

	    if( dr ){

		// set direct
		CvType t = new CvType( "psi-mi", "MI:0407", "direct" );
		ret.add( t );
		return ret;
	    }

	    if( ph ){
		// set physical
		
		CvType t = new CvType( "psi-mi", "MI:0915", "physical association" );
		ret.add( t );
		return ret;
	    }

	    if( as ){
                // set assembly
		CvType t = new CvType( "dip", "dip:0914", "association" );
		ret.add( t );
		return ret;
	    }


        } catch( HibernateException e ) {
            handleException(e);
        }
	return ret;

    }
   
    public List<Link> findAll() throws DAOException {
	return (List<Link>) findAll( Link.class, "ikey" );
    }

    public List<Link> findAll( String field ) throws DAOException {
	return (List<Link>) findAll( Link.class, field );
    }

    //--------------------------------------------------------------------------

    private boolean isDirect( String ac ){
	
	String[] acl = {"MI:0407","MI:0414"};
	
	for( int i=0; i<acl.length; i++ ){
            if( ac.equals(acl[i]) ){
                return true;
            }
        }
	return false;
    };

    //--------------------------------------------------------------------------

    private boolean isCovalent( String ac ){
	
	String[] acl = {"MI:0195","MI:0408","MI:0556"};
	
	for( int i=0; i<acl.length; i++ ){
            if( ac.equals(acl[i]) ){
                return true;
            }
        }
	return false;
    };
    
    //--------------------------------------------------------------------------

    private boolean isGenetic( String ac ){
	
	String[] acl = {"MI:0208","MI:0794","MI:0795","MI:0796","MI:0797",
			"MI:0798","MI:0799","MI:0800","MI:0801","MI:0802"};
	
	for( int i=0; i<acl.length; i++ ){
	    if( ac.equals(acl[i]) ){
		return true;
	    }
	}
	return false;
    };

    //--------------------------------------------------------------------------
    
    public static boolean getPublic( Link res ){

        int evc=0;

        Log log = LogFactory.getLog( LinkDAO.class );

        if( res.getEvidence()!=null ){
            
            Set evs = new HashSet();
            for(Iterator i=res.getEvidence().iterator(); i.hasNext(); ){
                Evidence ev = (Evidence) i.next();
                
                log.debug( "ev.getDataSrc().isPublic()=" 
                           + ev.getDataSrc().isPublic());

                if( ev.getStatus() == 1 
                    && ev.getDataSrc().isPublic() ){
                    evs.add( ev );
                    evc++;
                }
            }
            res.setEvidence( evs );
        }

        if( res.getInference() != null ){
            
            Set ins = new  HashSet();
            for( Iterator i=res.getInference().iterator(); i.hasNext(); ){
                Inference in = (Inference)i.next();

                log.debug( "in.getGrounds().getDataSrc().isPublic()=" 
                           + in.getGrounds().getDataSrc().isPublic());

                if( in.getGrounds().getStatus() == 1
                    && in.getGrounds().getDataSrc().isPublic() ){
                    ins.add( in );
                    evc++;
                }
            }
            res.setInference( ins );
        }

        if( evc > 0 ){
            return true;
        } else {
            return false;
        }
    }
}
