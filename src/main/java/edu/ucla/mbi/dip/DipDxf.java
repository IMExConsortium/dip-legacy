package edu.ucla.mbi.dip;

/* =============================================================================
 * $Id:: DipDxf.java 2191 2012-04-24 00:47:31Z lukasz                          $
 * Version: $Rev:: 2191                                                        $
 *==============================================================================
 *
 *  DipDxf - retrieves DIP records an presents them in DXF1.4 format
 *
 *    NOTES:   should return ttl info ? 
 *
 *============================================================================*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.dip.*;
import edu.ucla.mbi.dxf14.*;
import edu.ucla.mbi.orm.*;

//import javax.xml.parsers.*;

public class DipDxf{

    static NodeDAO nDAO = null;
    static LNodeDAO lnDAO = null;
    static OrganismDAO oDAO = null;
    static CvtDAO cvtDAO    = null;
    static CvTermDAO cvTermDAO = null;
    static LinkDAO lDAO = null;
    static EvidDAO xDAO = null;
    static InferDAO infDAO = null;
    static DataSrcDAO sDAO = null;
    static ImexSRecDAO imexDAO = null;
    static DipSRecDAO dipSRecDAO = null;

    static DipDataSrcDAO  ddsDAO = null;

    static edu.ucla.mbi.dxf14.ObjectFactory dxfFactory
	= new edu.ucla.mbi.dxf14.ObjectFactory();
    
    public static edu.ucla.mbi.dxf14.DatasetType 
	transform(edu.ucla.mbi.dxf14.DatasetType indoc, 
		  String expand, String detail){
	
        return DipDxfTransform.transform(indoc,expand,detail);
    }

    
    public static edu.ucla.mbi.dxf14.DatasetType 
	getCounts(String ns, String ac, String detail){
	
	Log log = LogFactory.getLog(DipDxf.class);
	log.info("DipDxf: getCounts called");
        return DipDxfStatistics.getCounts(ns,ac,detail);
    }

    //--------------------------------------------------------------------------    

    public static String getImexSRec(String ns, String ac){
	
	Log log = LogFactory.getLog(DipDxf.class);
        log.debug("DipDxf: getImex called");
	log.debug("DipDxf:    node (ns):"+ns+" (ac):"+ac);
	
	String sRes =null;
	String mif = "<entrySet/>\n";	
	
	if( ac.length()>0 && (ns.equals("dip") || ns.equals("imex")) ){
	    
	    log.debug("DipSOAP:    ns/ac debug complete: searching");
	    ImexSRec isr=null;
            
	    if(ns.equals("dip")){
		int xid=-1;
		try{
		    ac = ac.replaceAll("[^0-9]","");
		    xid = Integer.parseInt(ac);
		} catch( NumberFormatException ne ){
		    log.debug( ne, ne );		
		}
			    
		if( xid>0 ){
		    if(imexDAO==null){
			log.debug("DipSOAP:   Creating ImexSRecDAO...");
			imexDAO=new ImexSRecDAO();
		    }
		
		    if( imexDAO!=null &&ns.equals("dip") ){
		    
			// search by evidence id
                        //----------------------
                        
			log.debug("DipSOAP:   evid: "+xid);
			List lIsr= imexDAO.findByEvidId(xid);
			log.debug("DipSOAP:   hits: "+lIsr.size());
		    
			if(lIsr.size()>0){
			    isr=(ImexSRec) lIsr.get(0);
			}
		    }
		}
	    }
	    
	    if( ns.equals("imex") ){
		if( imexDAO!=null ){
		    
		    // search by imex id
		    //------------------
		    log.debug("DipSOAP:   imexid: "+ac);
		    List lIsr= imexDAO.findByImex254(ac);
		    log.debug("DipSOAP:   hits: "+lIsr.size());
		    
		    if(lIsr.size()>0){
			isr=(ImexSRec)lIsr.get(0);
		    }
		}
	    }
	    
	    if(isr!=null){
		mif=isr.getMif254();
	    }	    
	    log.debug("DipSOAP:    ------------------------ "); 
	}
	log.debug("DipDxf: DONE:getImex");
	return mif;
    }    
    
    //--------------------------------------------------------------------------

    public static edu.ucla.mbi.dxf14.DatasetType  getSourceBounds(){
	
	Log log = LogFactory.getLog(DipDxf.class);
        log.debug("DipDxf: getSourceBounds called");
	

	DipDataSrc ddsMin = null;
	DipDataSrc ddsMax = null;

	// search for min id node
        //-----------------------
 
	if( ddsDAO==null ){
	    log.debug("DipDxf:   Creating DipDataSrcDAO...");
	    ddsDAO = new DipDataSrcDAO();
	    log.debug( "DipDxf:   ...done" );
	}
        
        ddsMin = ddsDAO.findByMinId();
	ddsMax = ddsDAO.findByMaxId();

	DatasetType res = dxfFactory.createDatasetType();
	
	if( ddsMin!=null && ddsMax!=null ){
	    res.getNode().add( DipDxfDataSrcUtil
                               .dip2dxf( ddsMin,1,DxfLevel.SHRT ) );
	    res.getNode().add( DipDxfDataSrcUtil
                               .dip2dxf( ddsMax,2,DxfLevel.SHRT ) );
	}
	return res;
    }

    //--------------------------------------------------------------------------

    public static edu.ucla.mbi.dxf14.DatasetType 
        getSourceRange( long beg, long end, String detail ){
	
	Log log = LogFactory.getLog(DipDxf.class);
        log.debug( "DipDxf: getNodeRange called" );
	
	DxfLevel mode = DxfLevel.fromString( detail );
        
	log.debug( "DipDxf:  Sources Requested: from:"+beg+" to:" + end );

	// create empty result dataset
	//----------------------------

	DatasetType res = dxfFactory.createDatasetType();
	
	if( beg<=end ){
	    
	    // get node range
	    //---------------

	    if( ddsDAO==null ){
		log.debug( "DipDxf:   Creating DipDataSrcDAO...");
		ddsDAO = new DipDataSrcDAO();
		log.debug( "DipDxf:   ...done");
	    }

	    List nlRange = ddsDAO.findRange(beg,end);		
				
	    if( nlRange !=null && nlRange.size()>0 ){
		    
		// create dxfnode from Link object
		//--------------------------------
		    
		int _id=1;
		
		for(Iterator i=nlRange.iterator(); i.hasNext();){
			
		    DipDataSrc dipNode= (edu.ucla.mbi.dip.DipDataSrc) i.next();
		    
		    // add link to results
		    //--------------------
		    res.getNode().add( DipDxfDataSrcUtil
                                       .dip2dxf( dipNode, _id++, mode ) );
		}
	    }
	}    
	log.debug("DipSOAP: getNodeRange: DONE\n");
	return  res; 
    }

    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.DatasetType 
        getSource( String ns, String ac, String detail ){

	Log log = LogFactory.getLog( DipDxf.class );
        log.debug( "DipDxf: getEvidence called" );
	
	ac=ac.replaceAll( "\\s", "" );
	
	log.debug( "DipDxf:  Requested: ac=\""+ac+"\"  (ns=\""+ns+"\")");
	
	DxfLevel mode = DxfLevel.fromString(detail);

	DipDataSrc dds=null;
	
	long lAcc=Long.valueOf(ac.replaceAll("[^0-9]",""));
        
	if( ns.equals("dip") ){
	
	    int  sInd=(ac.toUpperCase()).lastIndexOf("S");

	    if( lAcc>0 && sInd==ac.length()-1 ){
                
		// search by DIP source accession (S)
		//-----------------------------------
		
		log.debug("DipDxf:  Getting DIP Source: "+
			  "ac=\""+ac+"\"  (id=\""+lAcc+"\")");

		if( sDAO==null ){
		    log.debug("DipDxf:   Creating DipDataSrcDAO...");
		    ddsDAO=new DipDataSrcDAO();
		    log.debug("DipDxf:   ...done");
		}

		try{
		    dds = ddsDAO.find(lAcc);
		} catch( DAOException de ){
		}

		log.debug( "DipDxf:  DipDataSrcDAO  called..." );
	    }
	} else {
	    if( ns.equals("pubmed") && lAcc>0 ){
		
		// search by PUBMED
		//-----------------
		
  		log.debug("DipDxf:  Getting DIP Source: "+
			  "PubMed="+lAcc);
		
		if(ddsDAO==null){
		    log.debug("DipDxf:   Creating DipDataSrcDAO...");
		    ddsDAO=new  DipDataSrcDAO();
		    log.debug("DipDxf:   ...done");
		}
		try{
		    dds=ddsDAO.findByPmid(ac);
		} catch(DAOException de){
                }
		
		log.debug("DipDxf:  DipDataSrcDAO  called...");
	    }
	}
		
       	// create empty result dataset
	//----------------------------
	
	DatasetType res = dxfFactory.createDatasetType();

	if(dds!=null){
	    log.debug("DipDxf:    found source: "+ dds.getAccession());
	    log.debug("DipDxf:    building response...");
	    
	
	    // create dxfnode from DipDataSrc object
	    //--------------------------------------
	
	    int _id=1;
	    res.getNode().add( DipDxfDataSrcUtil
                               .dip2dxf( dds,_id++, mode ) );
	}	
	log.debug("DipDxf: getSource: DONE\n");
	return res; 
    }

    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.DatasetType
	getEvidence( String ns, String ac, String match, String detail ){

	Log log = LogFactory.getLog(DipDxf.class);
        log.debug("DipSOAP: getEvidence called");
	
	ac=ac.replaceAll("\\s","");
	
	log.debug("DipSOAP:  Requested: ac=\""+ac+"\"  (ns=\""+ns+"\")");

	DxfLevel mode=DxfLevel.fromString(detail);
	List dipEvidList=null;
	
	if( ns.equals("dip") ){
	    int xInd=(ac.toUpperCase()).lastIndexOf("X");
	    int eInd=(ac.toUpperCase()).lastIndexOf("E");
	    int lAcc=Integer.valueOf(ac.replaceAll("[^0-9]",""));
	    
	    
	    if(lAcc>0 && xInd==ac.length()-1){
  
		// search by DIP evidence accession (X)
		//-------------------------------------
		
		log.debug("DipSOAP:  Getting DIP Evidence: "+
			  "ac=\""+ac+"\"  (id=\""+lAcc+"\")");

		if(xDAO==null){
		    log.debug("DipSOAP:   Creating EvidenceDAO...");
		    xDAO=new EvidDAO();
		    log.debug("DipSOAP:   ...done");
		}
		try{
		    dipEvidList= xDAO.findById(lAcc);
		} catch(DAOException de){
                }
		

		if(dipEvidList!=null && dipEvidList.size()>0){
		    log.debug("DipSOAP:    link: "+
			      ((Evidence)dipEvidList.get(0))
                              .getLink().getAccession());
		} 
	    }
	    
	    if( lAcc>0 && eInd==ac.length()-1 ){
                
		// search by DIP link accession (E)
		//---------------------------------
		
  		log.debug("DipSOAP:  Getting DIP Evidence: "+
			  "ac=\""+ac+"\"  (id=\""+lAcc+"\")");
		
		if(xDAO==null){
		    log.debug("DipSOAP:   Creating EvidenceDAO...");
		    xDAO=new EvidDAO();
		    log.debug("DipSOAP:   ...done");
		}
		dipEvidList= xDAO.findByLinkId(lAcc);

		if(dipEvidList!=null && dipEvidList.size()>0){
		    log.debug("DipSOAP:    link: "
			      + ((Evidence)dipEvidList.get(0))
                              .getLink().getAccession());
		    log.debug( "DipSOAP:    evid count: " 
                               + dipEvidList.size() );
		} 
	    }
	    
	} else {
 	    log.debug("DipSOAP:  Getting DIP Evidence:  called...");
	}
	log.debug("DipSOAP:   got it... building response...");
	
       	// create empty result dataset
	//----------------------------
	
	DatasetType res = dxfFactory.createDatasetType();
	
	// create dxfnode from Link object
	//--------------------------------
	
	int _id=1;
	
	for( Iterator i=dipEvidList.iterator(); i.hasNext(); ){
            
	    Evidence ev= (edu.ucla.mbi.dip.Evidence) i.next();
	    log.debug("DipSOAP:   imexId: "+ev.getImex254());
	    // add link to results
	    //--------------------
	    res.getNode().add( DipDxfEvidUtil.dip2dxf( ev, _id++, mode ));
	}
	log.debug("DipSOAP: getgetEvidenc: DONE\n");
	return res; 
    }

    //--------------------------------------------------------------------------

    public static edu.ucla.mbi.dxf14.DatasetType getLinkBounds(){
	
	Log log = LogFactory.getLog(DipDxf.class);
        log.info("DipDxf: getLinkBounds called");
	
        Link dipLinkMin = null;
        Link dipLinkMax = null;

        // search for min id node
        //-----------------------

        if(nDAO==null){
            log.debug("DipSOAP:   Creating LinkDAO...");
            lDAO=new LinkDAO();
            log.debug("DipSOAP:   ...done");
        }

        dipLinkMin = lDAO.findByMinId();
        dipLinkMax = lDAO.findByMaxId();

	DatasetType res = dxfFactory.createDatasetType();

        if(dipLinkMin!=null &&
           dipLinkMax!=null){
	    res.getNode().add( DipDxfUtil
                               .dip2dxf( dipLinkMin, 1, DxfLevel.SHRT));
	    res.getNode().add( DipDxfUtil
                               .dip2dxf( dipLinkMax, 1, DxfLevel.SHRT));
	}
	return res;  //DipDxfUtil.dts2doc(res);
    }

    //--------------------------------------------------------------------------

    public static edu.ucla.mbi.dxf14.DatasetType
        getLinkRange( long beg, long end, String detail ){
	
	Log log = LogFactory.getLog(DipDxf.class);
        log.debug("DipDxf: getLinkRange called");
	
	DxfLevel mode=DxfLevel.fromString(detail);
	log.debug( "DipDxf:  Links Requested: from:" + beg + " to:" + end );
	
	if( beg<=end ){
		
	    // get node range
	    //---------------

	    if( lDAO==null ){
		log.debug( "DipDxf:   Creating LinkDAO..." );
		lDAO = new LinkDAO();
		log.debug( "DipDxf:   ...done" );
	    }
		
	    List llRange = lDAO.findRange(beg,end);		
	    
	    // create empty result dataset
	    //----------------------------
		
	    DatasetType res = dxfFactory.createDatasetType();
	    
	    if( llRange !=null && llRange.size()>0 ){
		
		// create dxfnode from Link object
		//--------------------------------
		
		int _id=1;
		
		for(Iterator i=llRange.iterator(); i.hasNext();){
		    
		    Link link= (edu.ucla.mbi.dip.Link) i.next();
		    
		    if( link.getEvidence()!=null 
                        && link.getEvidence().size()>0 ){
			
			// add link to results
			//--------------------
			res.getNode().add( DipDxfUtil.dip2dxf( link, 
                                                               _id++, mode) );
		    }
		}
	    }
	    log.debug("DipDxf: getLinkRange: DONE\n");
	    return res; 
            //(new edu.ucla.mbi.dxf14.ObjectFactory()).createDatasetTypeType();    
	}
	log.debug("DipDxf: getLinkRange: DONE\n");
	return (new edu.ucla.mbi.dxf14.ObjectFactory()).createDatasetType();
    }

    //--------------------------------------------------------------------------

    public static edu.ucla.mbi.dxf14.DatasetType
	getLink( String ns, String ac, String match, String detail ){
	
	Log log = LogFactory.getLog(DipDxf.class);
	log.debug( "DipDxf: getLink called" );
	
	int _id=1;
	
	ac = ac.replaceAll("\\s","");
	log.debug( "DipSOAP:  Link Requested: ac=\"" + ac 
                   + "\"  (ns=\""+ns+"\") dt="+detail);
	
	DxfLevel mode=DxfLevel.fromString(detail);
	
	edu.ucla.mbi.dip.Link dipLink = null;
	
	// search by DIP accession
	//------------------------	
	if(ns.equals("dip")){
	    int lInd=(ac.toUpperCase()).lastIndexOf("E");
	    int dAcc=Integer.valueOf(ac.replaceAll("[^0-9]",""));

	    if(dAcc>0 && lInd==ac.length()-1){
  
		log.debug("DipSOAP:  Getting DIP Link: "+
			  "ac=\""+ac+"\"  (id=\""+dAcc+"\")");

		if(lDAO==null){
		    log.debug("DipSOAP:   Creating LinkDAO...");
		    lDAO=new LinkDAO();
		    log.debug("DipSOAP:   ...done");
		}

		try{
		    dipLink = lDAO.find(dAcc);
		} catch(DAOException de){
		}
		
		if(dipLink!=null){

		    if(infDAO==null){
			log.debug("DipSOAP:   Creating InferDAO...");
			infDAO=new InferDAO();
			log.debug("DipSOAP:   ...done");
		    }

		    
		    List infs = infDAO.findByLinkId(dipLink.getId());
		    log.debug("DipSOAP: inf size:"+infs.size());
		    if(infs.size()>0){
			Set infSet = new HashSet( infs );
			dipLink.setInference( infSet );
		    }
		}
	    } 
	} 
      
	log.debug("DipDxf:   got it... building response...");
	
	// create empty result dataset
	//----------------------------	
	
	DatasetType res = dxfFactory.createDatasetType();
	
        // add dxf node to results
	//------------------------	
	//res.getNode().add(DipDxfUtil.dip2dxf(dipLink,_id++,mode));

	if(dipLink!=null){

	    //    DxfLevel mode=DxfLevel.fromString(detail);
	    

	    // get link type 
	    //--------------
	    
	    List<CvType> ltl = lDAO.getLinkType(dipLink);
	    
	    // add link to the result dataset
	    //-------------------------------
	    
	    res.getNode()
                .add( DipDxfUtil.dip2dxf( dipLink, ltl, _id++, mode ));
	    
	    if( mode.compareTo(DxfLevel.FULL)>=0 ){
		
		DatasetType cntFull = 
		    DipDxfStatistics.getCounts("dip",
					       dipLink.getAccession(),
					       detail);
		
		for( Iterator<NodeType> in=cntFull.getNode().iterator();
                     in.hasNext(); ){
		    res.getNode().add(in.next());
		}
	    }
        }
	
	log.debug("DipDxf: DONE:getLink\n");
	return res;
    }

    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.DatasetType getNodeBounds(){
	
	Log log = LogFactory.getLog(DipDxf.class);
        log.debug("DipDxf: getNodeBounds called");
	
	DipNode dipNodeMin = null;
	DipNode dipNodeMax = null;
        
	// search for min id node
        //-----------------------
 
	if(nDAO==null){
	    log.debug("DipSOAP:   Creating NodeDAO...");
	    nDAO=new NodeDAO();
	    log.debug("DipSOAP:   ...done");
	}
        
        dipNodeMin = nDAO.findByMinId();
	dipNodeMax = nDAO.findByMaxId();

	DatasetType res = dxfFactory.createDatasetType();
	
	if(dipNodeMin!=null && 
	   dipNodeMax!=null){
	    res.getNode()
                .add( DipDxfUtil.dip2dxf( dipNodeMin, 1, DxfLevel.SHRT ) );
	    res.getNode()
                .add( DipDxfUtil.dip2dxf( dipNodeMax, 1, DxfLevel.SHRT ) );
	}
	return res;
    }

    //--------------------------------------------------------------------------

    public static edu.ucla.mbi.dxf14.DatasetType
	getNodeRange( long beg, long end, String detail ){
	
	Log log = LogFactory.getLog(DipDxf.class);
        log.debug("DipDxf: getNodeRange called");
	
	DxfLevel mode=DxfLevel.fromString(detail);
	log.debug("DipDxf:  Nodes Requested: from:"+beg+" to:"+end);

	// create empty result dataset
	//----------------------------
        
	DatasetType res = dxfFactory.createDatasetType();
	
	if(beg<=end){
	    
	    // get node range
	    //---------------

	    if(nDAO==null){
		log.debug("DipSOAP:   Creating NodeDAO...");
		nDAO=new NodeDAO();
		log.debug("DipSOAP:   ...done");
	    }

	    List nlRange = nDAO.findRange(beg,end);		
				
	    if(nlRange !=null && nlRange.size()>0){
		    
		// create dxfnode from Link object
		//--------------------------------
		    
		int _id=1;
		
		for(Iterator i=nlRange.iterator(); i.hasNext();){
			
		    DipNode dipNode= (edu.ucla.mbi.dip.DipNode) i.next();
		    
		    // add link to results
		    //--------------------
		    res.getNode().add(DipDxfUtil.dip2dxf(dipNode,_id++,mode));
		}
	    }
	}    
	log.debug("DipSOAP: getNodeRange: DONE\n");
	return  res; 
    }

    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.DatasetType
	matchNode( edu.ucla.mbi.dxf14.DatasetType inset, 
                   String match, String detail ){
	
	Log log = LogFactory.getLog(DipDxf.class);
	log.debug("DipDxf: matchNode called");

	DxfLevel mode=DxfLevel.fromString(detail);	
	log.debug("DipDxf:    node number:  "+inset.getNode().size());
	
	int cn=0;
	
	DatasetType res = dxfFactory.createDatasetType();
	
	int _id=0;
	
	// go over nodes
	//--------------

	for( Iterator i=inset.getNode().iterator();i.hasNext();){
	    
	    NodeType dxfNode= (NodeType)i.next();
	    
	    log.debug("DipSOAP:    ------------------------ "); 
	    log.debug("DipSOAP:    node["+cn+"]");
	    log.debug("DipSOAP:    node(label): "+dxfNode.getLabel()); 
	    
	    cn++;
	    
	    // test if protein 
	    //----------------

	    if( (dxfNode.getType().getNs().equals("psi-mi") &&
                 dxfNode.getType().getAc().equals("MI:0326") )
                ||
                (dxfNode.getType().getNs().equals("dxf") &&
                 dxfNode.getType().getAc().equals("dxf:0003") ) ){
		
		String ndLabel=dxfNode.getLabel();
		String ndName =dxfNode.getName();
				
		log.debug("DipSOAP:    node(name): "
			  +dxfNode.getName()); 
		log.debug("DipSOAP:    node(type): "
			  +dxfNode.getType().getAc()); 
		log.debug("DipSOAP:    node(ns)  : "+dxfNode.getNs()); 
		log.debug("DipSOAP:    node(ac)  : "+dxfNode.getAc()); 
		
		// protein:
		//  - get UniProt/RefSeq/taxid/sequence
		//-------------------------------------

		String ndTxId="";
		String ndUnPr="";
		String ndRfSq="";
		String ndSeq ="";
		String ndDip ="";

                if( dxfNode.getNs() != null && dxfNode.getAc() != null
                    && dxfNode.getNs().equals("dip") ){ 
                    ndDip = dxfNode.getAc();
                }
                
		if(dxfNode.getXrefList()!=null){

		    log.debug("DipSOAP:    node:xreflist");
		    for(Iterator j=dxfNode.getXrefList().getXref().iterator();
			j.hasNext();){

			XrefType dxfXref= (XrefType)j.next();
			    
			if(dxfXref.getTypeNs().equals("dxf") &&
			   dxfXref.getTypeAc().equals("dxf:0007") &&
			   dxfXref.getNs().equals("TaxId")){
			       
			    ndTxId=dxfXref.getAc();
			} else {
			    if(dxfXref.getTypeNs().equals("dxf") &&
			       dxfXref.getTypeAc().equals("dxf:0006")){
				if(dxfXref.getNs().toLowerCase().equals("uniprot")){
				    ndUnPr=dxfXref.getAc();
				    }
				if(dxfXref.getNs().toLowerCase().equals("refseq")){
				    ndRfSq=dxfXref.getAc();
				}
				if(dxfXref.getNs().equals("dip")){
				    ndDip=dxfXref.getAc();
				}
			    }

			    if(dxfXref.getTypeNs().equals("dxf") &&
			       dxfXref.getTypeAc().equals("dxf:0018")){
				if(dxfXref.getNs().toLowerCase().equals("uniprot")){
				    ndUnPr=dxfXref.getAc();
				}
				if(dxfXref.getNs().toLowerCase().equals("refseq")){
				    ndRfSq=dxfXref.getAc();
				}
				if(dxfXref.getNs().equals("dip")){
				    ndDip=dxfXref.getAc();
				}
			    }
			}
		    }
		}
		    
		if(dxfNode.getAttrList()!=null){

		    log.debug("DipSOAP:    node:attrlist");
		    if(dxfNode.getAttrList()!=null){

			log.debug("DipSOAP:    node:xattrlist");
			for( Iterator j 
                                 = dxfNode.getAttrList().getAttr().iterator();
			    j.hasNext();){
			    
			    AttrType dxfAttr= (AttrType)j.next();
			    
			    if(dxfAttr.getAc().equals("dip:0008")){
				ndSeq=dxfAttr.getValue().getValue();
			    }
			}
		    }
		}
                
		log.debug( "DipSOAP:    node(TxId): >"+ndTxId+"<");
		log.debug( "DipSOAP:    node(UnPr): >"+ndUnPr+"<");
		log.debug( "DipSOAP:    node(RfSq): >"+ndRfSq+"<");
		log.debug( "DipSOAP:    node(Dip): >"+ndDip+"<");
		log.debug( "DipSOAP:    node(seq) : >"+ndSeq+"<");
		    
		// -test if name/(UniProt/RefSeq/sequence)/taxid if not empty
		//-----------------------------------------------------------

		if(ndTxId.length()>0 || ndUnPr.length()>0  ||   
		   ndRfSq.length()>0 || ndSeq.length()>0){
	      
		    if(nDAO==null){
			log.debug("DipSOAP:   Creating NodeDAO...");
			nDAO=new NodeDAO();
			log.debug("DipSOAP:   ...done");
		    }

		    if(oDAO==null){
			log.debug("DipSOAP:   Creating OrganismDAO...");
			oDAO=new OrganismDAO();
			log.debug("DipSOAP:   ...done");
		    }

		    if(cvtDAO==null){
			log.debug("DipSOAP:   Creating CvtDAO...");
			cvtDAO=new CvtDAO();
			log.debug("DipSOAP:   ...done");
		    }
					   	       	
		    //  find UniProt
		    //--------------

		    List upTst = new ArrayList();
		    if(ndUnPr.length()>0){
			log.debug("DipSOAP:     findByUniProt : >"+ndUnPr+"<");
			upTst = nDAO.findByUniProt(ndUnPr);
		    }
			
		    //  find RefSeq
		    //-------------
		    
		    List rsTst = new ArrayList();
		    if(ndRfSq.length()>0){
			log.debug("DipSOAP:     findByRefSeq : >"+ndRfSq+"<");
			rsTst = nDAO.findByRefSeq(ndRfSq);
		    }

		    //  find Dip
		    //-----------
		    
		    List dpTst = new ArrayList();
		    if(ndDip.length()>0){

			String dipId=ndDip.replaceAll("\\D","");

			if(dipId.length()>0){
			    log.debug("DipSOAP:     findById : >"+dipId+"<");
			    try{
				dpTst = nDAO.findById(Long.valueOf(dipId));
			    } catch(NumberFormatException nfe){
			    }
			}
		    }

		    // find sequence
                    //--------------
		    
		    List seqTst = new ArrayList();
		    if(ndSeq.length()>0){
			log.debug("DipSOAP:     findBySequence : >"+ndSeq+"<");
			seqTst = nDAO.findBySequence(ndSeq);
		    }
		    
		    //List upTst = nDAO.findByUniProt(ndUnPr);
		    //List rsTst = nDAO.findByRefSeq(ndRfSq);
		    
		    log.debug("DipSOAP:       found" 
                              + " :dp: " + dpTst.size() 
                              + " :up: " + upTst.size() 
                              + " :rs: "+ rsTst.size()
                              + " :sq: " + seqTst.size());
                    
		    if(upTst.size()>0 ||
		       rsTst.size()>0 ||
		       dpTst.size()>0 ||
		       seqTst.size()>0){
			
			// sequence match only
			//--------------------

			if(seqTst.size()>0 
			   && ndUnPr.equals("") 
			   && ndRfSq.equals("")
			   && ndDip.equals("")
			   ){
			    
			    log.debug("DipSOAP:       adding by sequence match");
			    
			    for(Iterator iNd=seqTst.iterator();iNd.hasNext();){

				DipNode dipNode = (DipNode)iNd.next();
				
				// add node to the result dataset
				//-------------------------------
				res.getNode()
                                    .add( DipDxfUtil.dip2dxf( dipNode,
                                                              _id++, 
                                                              DxfLevel.SHRT ) );
			    }
			}

			// UniProt match only
                        //--------------------

			if(upTst.size()>0 
			   && ndSeq.equals("") 
			   && ndRfSq.equals("")
			   && ndDip.equals("")
			   ){
			    
			    log.debug("DipSOAP:       adding by UniProt match");

			    for(Iterator iNd=upTst.iterator();iNd.hasNext();){

				DipNode dipNode = (DipNode)iNd.next();
				
				// add node to the result dataset
				//-------------------------------
				res.getNode()
                                    .add(DipDxfUtil.dip2dxf( dipNode,
                                                             _id++,
                                                             DxfLevel.SHRT ) );
			    }
			}
			
			// RefSeq match only
                        //------------------
			
			if(rsTst.size()>0 
			   && ndUnPr.equals("") 
			   && ndDip.equals("")
			   && ndSeq.equals("")
			   ){
			    
			    log.debug("DipSOAP:       adding by RefSeq match");

			    for(Iterator iNd=rsTst.iterator();iNd.hasNext();){

				DipNode dipNode = (DipNode)iNd.next();
			    
				// add node to the result dataset
				//-------------------------------
				res.getNode()
                                    .add( DipDxfUtil.dip2dxf( dipNode,
                                                              _id++, mode ) );
			    }
			}
			
			// Dip match only
                        //---------------
			
			if(dpTst.size()>0 
			   && ndRfSq.equals("") 
			   && ndUnPr.equals("") 
			   && ndSeq.equals("")){
			    
			    log.debug("DipSOAP:       adding by Dip match");
			    
			    for(Iterator iNd=dpTst.iterator();iNd.hasNext();){
				
                                DipNode dipNode = (DipNode)iNd.next();
				
				// add node to the result dataset
				//-------------------------------
				res.getNode()
                                    .add(DipDxfUtil.dip2dxf( dipNode,
                                                             _id++,
                                                             DxfLevel.SHRT ) );
			    }
			}
		    } 
		}
	    }
	}
        
	log.debug("DipDxf:    ------------------------ ");     
	log.debug("DipDxf: DONE:matchNode\n");
	return res; 
    }

    //--------------------------------------------------------------------------	

    public static edu.ucla.mbi.dxf14.DatasetType 
	getNode( String ns, String ac, String match, String detail ){
	
	Log log = LogFactory.getLog(DipDxf.class);
	log.debug("DipDxf: getNode called......");
	
	log.debug( "DipDxf:  Node Requested: ac=\"" + ac 
                   + "\"  (ns=\""+ns+"\") detail->" + detail );

	ac=ac.replaceAll("\\s","");
	
	// search for node
	//----------------
	DipNode dipNode = null;
	
	// by DIP accession
	//-----------------
	if( dipNode==null && ns.equals("dip") && ac.length()>0 ){
	    int nInd=(ac.toUpperCase()).lastIndexOf("N");
	    long dAcc=Long.valueOf(ac.replaceAll("[^0-9]",""));
	    
	    if(dAcc>0 && nInd==ac.length()-1){
  
		log.debug("DipDxf:  Getting DIP Node: ac=\""
                          + ac + "\"  (id=\"" + dAcc + "\")");
		
		if( nDAO==null ){
		    log.debug( "DipDxf:   Creating NodeDAO...");
		    nDAO=new NodeDAO();
		    log.debug( "DipDxf:   ...done");
		}
		try{
		    dipNode=nDAO.find(dAcc);
		} catch( DAOException de ){
		}
		
		log.debug( "DipDxf: dipNode " + dipNode );
	    } 
	}

	// by RefSeq
	//-----------     
	if( dipNode==null && ns.toLowerCase().equals("refseq") ){
            
	    log.debug("DipSOAP:  Getting DIP Node: RefSeq=\""+ac+"\"");

	    if(nDAO==null){
		log.debug("DipSOAP:   Creating NodeDAO...");
		nDAO=new NodeDAO();
		log.debug("DipSOAP:   ...done");
	    }

	    List ndl= nDAO.findByRefSeq(ac);

	    if(ndl.size()>0){
		dipNode= (DipNode) ndl.get(0);
	    }
	}

	// by other xref
	//--------------
  
	if(dipNode==null){
     
	    log.debug("DipSOAP:  Getting DIP Node: "+ns+"=\""+ac+"\"");

	    if(nDAO==null){
		log.debug("DipSOAP:   Creating NodeDAO...");
		nDAO=new NodeDAO();
		log.debug("DipSOAP:   ...done");
	    }

	    List ndl= nDAO.findByXref(ns,ac);

	    if(ndl.size()>0){
		dipNode= (DipNode) ndl.get(0);
	    }
	}

	// by UniProt
	//-----------
 	if(dipNode==null && ns.toLowerCase().equals("uniprot")){

	    log.debug("DipSOAP:  Getting DIP Node: UniProt=\""+ac+"\"");
     
            if(nDAO==null){
                log.debug("DipSOAP:   Creating NodeDAO...");
                nDAO=new NodeDAO();
                log.debug("DipSOAP:   ...done");
            }

            List ndl= nDAO.findByUniProt(ac);
	    if(ndl.size()>0){
                dipNode= (DipNode) ndl.get(0);
            }
	}
	
	/*

	// by sequence
	//------------
 	if(dipNode==null && ns.toLowerCase().equals("uniprot")){

	    logger.debug("DipSOAP:  Getting DIP Node: UniProt=\""+ac+"\"");
     
            if(nDAO==null){
                logger.debug("DipSOAP:   Creating NodeDAO...");
                nDAO=new NodeDAO();
                logger.debug("DipSOAP:   ...done");
            }

            List ndl= nDAO.findByUniProt(ac);
	    if(ndl.size()>0){
                dipNode= (DipNode) ndl.get(0);
            }
	}
    
	*/
	
	// create empty result dataset
	//---------------------------- 
	
	DatasetType res = dxfFactory.createDatasetType();
	
	if(dipNode!=null){
	    DxfLevel mode=DxfLevel.fromString(detail);
    
	    // add node to the result dataset
	    //-------------------------------
	    
	    res.getNode().add(DipDxfUtil.dip2dxf(dipNode,1L,mode));

	    if(mode.compareTo(DxfLevel.FULL)>=0){

		DatasetType cntFull = 
		    DipDxfStatistics.getCounts("dip",
					       dipNode.getAccession(),
					       detail);
		
		for( Iterator<NodeType> in = cntFull.getNode().iterator();
                     in.hasNext(); ){
		    res.getNode().add( in.next() );
		}
	    }
	}
	return res;
    }
    
    //--------------------------------------------------------------------------

    public static edu.ucla.mbi.dxf14.DatasetType 
	getNode(String sequence, String match, String detail){
	
	Log log = LogFactory.getLog(DipDxf.class);
	log.debug("DipDxf: getNode (by sequence) called...");
	
	// search for node
	//----------------
	DipNode dipNode = null;
	
	// by sequence
	//------------
	if(sequence!=null && sequence.length()>0){
	    
	    log.debug( "DipSOAP:  Getting DIP Node: sequence=\"" 
                       + sequence + "\"" );
	    
            if( nDAO==null ){
		log.debug( "DipSOAP:   Creating NodeDAO..." );
                nDAO = new NodeDAO();
		log.debug( "DipSOAP:   ...done" );
	    }

	    List ndl= nDAO.findBySequence( sequence );
	    if(ndl.size()>0){
		dipNode= (DipNode) ndl.get(0);
	    }
	}
	
	// create empty result dataset
	//---------------------------- 
	
	DatasetType res = dxfFactory.createDatasetType();
	
	if(dipNode!=null){
	    DxfLevel mode=DxfLevel.fromString(detail);
    
	    // add node to the result dataset
	    //-------------------------------
	    
	    res.getNode().add(DipDxfUtil.dip2dxf(dipNode,1L,mode));

	    if(mode.compareTo(DxfLevel.FULL)>=0){

		DatasetType cntFull = 
		    DipDxfStatistics.getCounts("dip",
					       dipNode.getAccession(),
					       detail);
                
		for( Iterator<NodeType> in=cntFull.getNode().iterator();
                     in.hasNext(); ){
		    res.getNode().add(in.next());
		}
	    }
	}
	return res;
    }

    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.DatasetType
	getLinksByNodeSet( edu.ucla.mbi.dxf14.DatasetType inset, 
                           String match, String detail ){
	
	Log log = LogFactory.getLog( DipDxf.class );
	log.debug( "DipDxf:  LinksByNodeSet Requested:" );
	
	//int mode=0;
	//if(detail.equals("stub")){ mode=0; }
	//if(detail.equals("base")){ mode=1; }

	List ndList= new ArrayList();
	      
	edu.ucla.mbi.dip.Link dipLink = null;
        
        List dxfNodes= inset.getNode(); // ((DatasetType)jaxbNodes.getValue()).getNode(); 
            
        for( Iterator<NodeType> i = inset.getNode().iterator();
             i.hasNext(); ){
		
            NodeType iN=(NodeType)i.next();
            
            String ns = iN.getNs();
            String ac = iN.getAc();
            
            if( ns.equals("dip") ){
                int nInd=(ac.toUpperCase()).lastIndexOf("N");
		    int dAcc=Integer.valueOf(ac.replaceAll("[^0-9]",""));
                    
		    if(dAcc>0 && nInd==ac.length()-1){
			log.debug("DipSOAP:    NodeList: DIP Node: ac=\""+ac+
				  "\"  (id=\""+dAcc+"\")");			
			ndList.add( new Long(dAcc) );
		    }
            }
        }	
        
	if( lDAO==null ){
	    log.debug( "DipSOAP:   Creating LinkDAO..." );
	    lDAO=new LinkDAO();
	    log.debug("DipSOAP:   ...done");
	}
        
	List ndl = lDAO.findByNodeIdList(ndList);
	    
	log.debug("DipSOAP:   got it... building response...");
	
	// create empty result dataset
	//----------------------------
	
	DatasetType res = dxfFactory.createDatasetType();
	
	// create dxfnode from Link object
	//--------------------------------
	
	int _id=1;
	
	if(ndl.size()>0){
	    
	    DxfLevel mode=DxfLevel.fromString(detail);
	    
	    for(Iterator i=ndl.iterator(); i.hasNext();){
		
		dipLink= (edu.ucla.mbi.dip.Link) i.next();
		
		// add link to results
		//--------------------
		res.getNode().add(DipDxfUtil.dip2dxf(dipLink,_id++,mode));
	    }
	}
	return res; // DipDxfUtil.dts2doc(res);
	//} else{
	//    log.debug("DipSOAP: getLinksByNodeSet: DONE\n");
	//    return null;
	//}
    }
}
