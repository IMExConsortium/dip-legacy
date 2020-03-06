package edu.ucla.mbi.dip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.compass.core.*;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;

//import edu.ucla.mbi.service.*;
import edu.ucla.mbi.dxf14.*;
import edu.ucla.mbi.orm.*;

public class DipIndexAgent implements Runnable {
        
    private Compass compass;
    private int limit;
    
    public DipIndexAgent( Compass compass, int limit ) {
        this.compass = compass;
	this.limit = limit;   
    }
    
    public void run() {

        Log log = LogFactory.getLog( this.getClass() );
        log.info( "DipIndexAgent: index run starting..." );
        
        if ( compass == null ) { return; }
        
        // regenerate indices
        //-------------------

        compass.getSearchEngineIndexManager().deleteIndex();
        compass.getSearchEngineIndexManager().createIndex();

        // add nodes
        //----------

        log.info("DipIndexContext: indexing nodes");

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
            
            log.info( "DipContext: minlink" + minLink);
            log.info( "DipContext: maxlink" + maxLink);

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
            
            for( long i = minDds.getId(); i<=maxDds.getId(); i++ ) {
		
                DataSrc     ds = null;
                DipDataSrc dds = null;
                
                try {
                    ds = dsDAO.find( i );
                    dds = ddsDAO.find( i );
                } catch( DAOException e ) {
                    log.info( "DipContext:" +
                              " ObjectNotFoundException id=" + i );
                } catch( Exception ox){
                    log.info( "DipContext:" +
                              "*ObjectNotFoundException id=" + i );
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
        
        log.info("DipIndexAgent: done...");
    }
}
