package edu.ucla.mbi.dip;

/* =============================================================================
 * $Id:: DipDAO.java 2189 2012-04-21 17:59:45Z lukasz                          $
 *  Version: $Rev:: 2189                                                       $
 *=============================================================================$
 *                                                                             $
 *  DipDAO - static Dip DAO                                                    $
 *                                                                             $
 *    NOTES:                                                                   $
 *                                                                             $
 *=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.dip.*;
import edu.ucla.mbi.dxf14.*;
import edu.ucla.mbi.orm.*;

//import javax.xml.parsers.*;

public class DipDAO{

    static NodeDAO nDAO = null;
    public static NodeDAO node(){
	if(nDAO==null){
	    Log log = LogFactory.getLog(DipDAO.class);
	    log.debug("DipDAO:   Creating NodeDAO...");
	    nDAO=new NodeDAO();
	    log.debug("DipDAO:   ...done");
	}
	return nDAO;
    }

    static LNodeDAO lnDAO = null;
        public static LNodeDAO lnode(){
	if(lnDAO==null){
	    Log log = LogFactory.getLog(DipDAO.class);
	    log.debug("DipDAO:   Creating LNodeDAO...");
	    lnDAO=new  LNodeDAO();
	    log.debug("DipDAO:   ...done");
	}
	return lnDAO;
    }

    static OrganismDAO oDAO = null;
    public static OrganismDAO organism(){
	if(oDAO==null){
	    Log log = LogFactory.getLog(DipDAO.class);
	    log.debug("DipDAO:   Creating OrganismDAO...");
	    oDAO=new OrganismDAO();
	    log.debug("DipDAO:   ...done");
	}
	return oDAO;
    }

    static CvtDAO cvtDAO = null;
    public static CvtDAO cvt(){
	if(cvtDAO==null){
	    Log log = LogFactory.getLog(DipDAO.class);
	    log.debug("DipDAO:   Creating CvtDAO...");
	    cvtDAO=new CvtDAO();
	    log.debug("DipDAO:   ...done");
	}
	return cvtDAO;
    }

    static CvTermDAO cvTermDAO = null;
    public static CvTermDAO cvterm(){
	if(cvTermDAO==null){
	    Log log = LogFactory.getLog(DipDAO.class);
	    log.debug("DipDAO:   Creating CvTermDAO...");
	    cvTermDAO=new CvTermDAO();
	    log.debug("DipDAO:   ...done");
	}
	return cvTermDAO;
    }

    static LinkDAO lDAO = null;
    public static LinkDAO link(){
	if(lDAO==null){
	    Log log = LogFactory.getLog(DipDAO.class);
	    log.debug("DipDAO:   Creating LinkDAO...");
	    lDAO=new LinkDAO();
	    log.debug("DipDAO:   ...done");
	}
	return lDAO;
    }

    static EvidDAO xDAO = null;
    public static EvidDAO evidence(){
	if(xDAO==null){
	    Log log = LogFactory.getLog(DipDAO.class);
	    log.debug("DipDAO:   Creating EvidDAO...");
	    xDAO=new EvidDAO();
	    log.debug("DipDAO:   ...done");
	}
	return xDAO;
    }

    static InferDAO  infDAO = null;
    public static InferDAO inference(){
        if(infDAO==null){
            Log log = LogFactory.getLog(DipDAO.class);
            log.debug("DipDAO:   Creating InferDAO...");
            infDAO=new InferDAO();
            log.debug("DipDAO:   ...done");
        }
        return infDAO;
    }

    static DataSrcDAO  sDAO = null;
    public static DataSrcDAO datasrc(){
        if(sDAO==null){
            Log log = LogFactory.getLog(DipDAO.class);
            log.debug("DipDAO:   Creating DataSrcDAO...");
            sDAO=new DataSrcDAO();
            log.debug("DipDAO:   ...done");
        }
        return sDAO;
    }

    static ImexSRecDAO imexDAO = null;
    public static ImexSRecDAO imexsrec(){
        if(imexDAO==null){
            Log log = LogFactory.getLog(DipDAO.class);
            log.debug("DipDAO:   Creating ImexSRecDAO...");
            imexDAO=new ImexSRecDAO();
            log.debug("DipDAO:   ...done");
        }
        return imexDAO;
    }

    static DipSRecDAO  dipSRecDAO = null;
    public static DipSRecDAO dipsrec(){
        if(dipSRecDAO==null){
            Log log = LogFactory.getLog(DipDAO.class);
            log.debug("DipDAO:   Creating DipSRecDAO...");
            dipSRecDAO=new DipSRecDAO();
            log.debug("DipDAO:   ...done");
        }
        return dipSRecDAO;
    }

    static DipDataSrcDAO  ddsDAO = null;
    public static DipDataSrcDAO dipdatasrc(){
        if(ddsDAO==null){
            Log log = LogFactory.getLog(DipDAO.class);
            log.debug("DipDAO:   Creating DipDataSrcDAO...");
            ddsDAO=new DipDataSrcDAO();
            log.debug("DipDAO:   ...done");
        }
        return ddsDAO;
    }
}

