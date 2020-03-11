package edu.ucla.mbi.dip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;

import javax.naming.*;

import org.compass.core.*;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;

import edu.ucla.mbi.dip.*;
import edu.ucla.mbi.orm.*;

public class DipContext {
    
    boolean reload = false;
    
    public void setReload( boolean reload ) {
        this.reload = reload;
    }
    
    public boolean getReload() {
        return reload;
    }

    //---------------------------------------------------------------------
    
    static boolean indexReport = false;
    
    public void setIndexReport( boolean indexReport ) {
        this.indexReport = indexReport;
    }
    
    public static boolean getIndexReport() {
        return indexReport;
    }

    //---------------------------------------------------------------------

    int reloadLimit = 0;
    
    public void setReloadLimit( int limit ) {
        this.reloadLimit = limit;
    }
    
    public int getReloadLimit() {
        return reloadLimit;
    }
    
    //---------------------------------------------------------------------
    
    private static final Properties config = new Properties();
    
    public static String getProperty( String key ) {
        return config.getProperty( key );
    }

    //---------------------------------------------------------------------
    
    private static final Map<String,Transformer> 
        transformer = new HashMap<String,Transformer>();
    
    public static Transformer getTransformer( String key ) {
        return transformer.get(key);
    }
    
    //---------------------------------------------------------------------
    
    private static Compass compass;
    
    public static Compass getCompass(){
        return compass;
    }
    
    public void initialize() {
        
        Log log = LogFactory.getLog( this.getClass() );
        log.info( "DipContext: initialize" );

        try{
            
            // initialize compass
            //-------------------
            
            CompassConfiguration cconf =
                CompassConfigurationFactory.newConfiguration();
            cconf.configure();
            
            compass = cconf.buildCompass();
            
            if ( reload ) {
                
                int limit = reloadLimit;
                log.info( "DipContext: limit=" + limit*100 );
                log.info( "DipContext: starting DipIndexAgent" );
                
                new Thread( new DipIndexAgent( compass, limit ) ).start();
            }

            
            
        /*
		// regenerate indices
		//-------------------
                
		compass.getSearchEngineIndexManager().deleteIndex();
		compass.getSearchEngineIndexManager().createIndex();
        
		// add nodes
		//----------

		log.info("DipContext: indexing nodes");
		
		CompassSession loadsession = compass.openSession();

		try {                    
		    NodeDAO nDAO = new NodeDAO();
		    DipNode minNode = nDAO.findByMinId();
		    DipNode maxNode = nDAO.findByMaxId();
                    
		    log.info( "DipContext:  node range:" + 
                              " min=" + minNode.getAccession() + 
                              " max=" + maxNode.getAccession() );
		    
		    LinkDAO lDAO = new LinkDAO();
		    EvidDAO xDAO = new EvidDAO();

		    Link minLink = lDAO.findByMinId();
                    Link maxLink = lDAO.findByMaxId();
                    
		    log.info( "DipContext:  link range:" + 
                              " min=" + minLink.getAccession() + 
                              " max=" + maxLink.getAccession() );
		    DataSrcDAO dsDAO     = new DataSrcDAO();
		    DipDataSrcDAO ddsDAO = new DipDataSrcDAO();

		    DipDataSrc  minDds = ddsDAO.findByMinId();
		    DipDataSrc  maxDds = ddsDAO.findByMaxId();
                    
		    log.info( "DipContext:  dds range: " +
                              " min=" + minDds.getAccession() + 
                              " max="+maxDds.getAccession() );

                    for( long i= minDds.getId(); i<=maxDds.getId(); i++ ) {
			
			DataSrc     ds = null;
			DipDataSrc dds = null;

			try{
			    ds = dsDAO.find( i );
			    dds = ddsDAO.find( i );
			} catch( DAOException e ) {
			    log.info( "DipContext:" +
                                      " ObjectNotFoundException id=" + i );
			}

			if( ds != null && dds != null ) {
			    
			    List xl = xDAO.findByDataSrc( ds );
			    
			    if( xl.size()>0){

				Set xs = new HashSet();
				xs.addAll( xl );
				dds.setEvidence( xs );
                                
				CompassTransaction ltx = 
                                    loadsession.beginTransaction();
				loadsession.save( dds );
				ltx.commit();
			    }
			}
                        
			if( i%100 == 0 ) {
			    log.info( "DipContext:  processed " + 
                                      i + " sources" );
			    if( limit > 0 && i >= limit*100 ) {
				i = maxDds.getId() + 1;
			    }
			}
		    }
		} catch( Exception e ) {
		    e.printStackTrace();
		}
		loadsession.close();
	    }
	    */
            
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        /*    
        if ( !config.getProperty( "qtest", "" ).equals( "" ) ) {
            
	    try {
                
		NodeDAO nDAO = new NodeDAO();
                
		// query test
		//-----------
		
		log.info("DipContext: query test");

		CompassSession searchsession = compass.openSession();
		CompassTransaction stx = searchsession.beginTransaction();
		
		CompassHits hits = 
                    searchsession.find( config.getProperty( "qtest", 
                                                            "name:antigen" ) );
		log.info( "Found [" + hits.getLength() + "] " + 
                          " hits for "+
                          config.getProperty( "qtest", "name:antigen" ) + 
                          " query");
		log.info( "==============================================");
		for (int i = 0; i < hits.getLength(); i++) {
		    print(log,hits, i,nDAO);
		}
		
		hits.close();
		
		stx.commit();
		searchsession.close();
	    
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
        */
    }
    
    public void cleanup() {

        Log log = LogFactory.getLog( this.getClass() );
        log.info("Cleanup:");
        compass.close();
        log.info("Cleanup: DONE");
    }

    //---------------------------------------------------------------------

    public static void print( Log log, CompassHits hits, int hitNumber, 
                              NodeDAO nDAO ) {
        Object value = hits.data(hitNumber);
        Resource resource = hits.resource(hitNumber);
        if( resource.getAlias().equals( "node" ) ) {
             
            log.info( "ALIAS [" + resource.getAlias() + "] ID [" + 
                      resource.getId() + "] SCORE ["
                      + hits.score(hitNumber) + "]" );
            try {
                long id = Long.parseLong( resource.getId() );
                DipNode cnd = nDAO.find( id );
                log.info("   NAME[" + cnd.getName()+ "]");
            } catch( NumberFormatException nex ) {
                
            }	    
        } else {
            log.info( "ALIAS [" + resource.getAlias() + "] ID [" + 
                      resource.getId() + "] SCORE ["
                      + hits.score(hitNumber) + "]" );
        }
    }




    public static Map toMap(Context ctx) throws NamingException {

        Log log = LogFactory.getLog( DipContext.class  );
        
        String namespace = ctx instanceof InitialContext ? ctx.getNameInNamespace() : "";
        HashMap<String, Object> map = new HashMap<String, Object>();
        log.info("> Listing namespace: " + namespace);
        NamingEnumeration<NameClassPair> list = ctx.list(namespace);
        while (list.hasMoreElements()) {
            NameClassPair next = list.next();
            String name = next.getName();
            String jndiPath = namespace + name;
            Object lookup;
            try {
                log.info("> Looking up name: " + jndiPath);
                Object tmp = ctx.lookup(jndiPath);
                if (tmp instanceof Context) {
                    lookup = toMap((Context) tmp);
                } else {
                    lookup = tmp.toString();
                }
            } catch (Throwable t) {
                lookup = t.getMessage();
            }
            map.put(name, lookup);
            
        }
        return map;
    }
}
