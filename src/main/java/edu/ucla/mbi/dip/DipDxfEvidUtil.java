package edu.ucla.mbi.dip;

/* =============================================================================
 # $Id:: DipDxfEvidUtil.java 1884 2011-10-07 23:46:37Z lukasz                  $
 # Version: $Rev:: 1884                                                        $
 #==============================================================================
 #                                                                             $
 #  DipDxfEvidUtil - dip objects->DXF1.4 conversion:                           $
 #                       Evidence & Inference                                  $
 #                                                                             $
 #    NOTES:                                                                   $
 #                                                                             $
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;
import java.util.regex.PatternSyntaxException;
    
import javax.xml.transform.stream.*;
import javax.xml.bind.*;

import edu.ucla.mbi.dip.*;
import edu.ucla.mbi.dxf14.*;

import org.hupo.psi.mi.mif.*;

public class DipDxfEvidUtil{
    
    static edu.ucla.mbi.dxf14.ObjectFactory 
	dof = new edu.ucla.mbi.dxf14.ObjectFactory();


    //--------------------------------------------------------------------------
    // Evidence
    //---------
    
    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf( edu.ucla.mbi.dip.Evidence dipEvid, 
                 long id, DxfLevel mode ){
        
        CvTerm evCvtDetTpe = dipEvid.getCvtDetType();
        CvType evDetType = dipEvid.getDetType();

	return DipDxfEvidUtil.dip2dxf( dipEvid, evCvtDetTpe, evDetType, 
                                       id, mode, true );  // link 
    }
    
    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf( edu.ucla.mbi.dip.Evidence dipEvid, 
                 long id, DxfLevel mode, boolean linkDef ){
        
        CvTerm evCvtDetTpe = dipEvid.getCvtDetType();
        CvType evDetType = dipEvid.getDetType();

	return DipDxfEvidUtil.dip2dxf( dipEvid, evCvtDetTpe, evDetType, 
                                       id, mode, linkDef );  // link 
    }
    
    //--------------------------------------------------------------------------`
    
    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf( edu.ucla.mbi.dip.Evidence dipEvid,
                 CvTerm evCvtDetTpe, CvType evDetType,
                 long id, DxfLevel mode, boolean linkDef ){

	Log log = LogFactory.getLog( DipDxfEvidUtil.class );
	
	edu.ucla.mbi.dxf14.NodeType dxfNode = null;
	
	if( dipEvid!=null && dipEvid.getSource()!=null && 
            !dipEvid.getSource().getAc().replaceAll("\\s","").equals("null") ){     
            
	    // create new DXF node & node type elements
	    //-----------------------------------------
		    
	    dxfNode = dof.createNodeType();

	    // set node type 
	    //--------------
	    
	    edu.ucla.mbi.dxf14.TypeDefType 
		dxfType = dof.createTypeDefType();
	    
	    CvType evidType = dipEvid.getEvidType();
	    //CvType detMth  = dipEvid.getDetMethod();
	    
	    dxfType.setNs( evidType.getNs() );
	    dxfType.setAc( evidType.getAc() );
	    dxfType.setName( evidType.getName() );
	    
	    dxfNode.setId( id );
	    dxfNode.setNs( "dip" );
	    dxfNode.setAc( dipEvid.getAccession() );
	    
	    dxfNode.setType( dxfType );
	    
	    log.debug( "DipDxfEvidUtil:   label=" + dipEvid.getLabel() );
	    log.debug( "DipDxfEvidUtil:   name =" + dipEvid.getName() );
	    
	    // set label
	    //----------
	    
	    dxfNode.setLabel( dipEvid.getAccession() );
	    
	    // create attribute list
	    //----------------------
            
	    dxfNode.setAttrList( dof.createNodeTypeAttrList() );
	    
	    log.debug( "DipDxfEvidUtil:   adding attributes" );
	    
	    // add attributes:		
	    //----------------
	    
	    //  - detected interaction type
	    //-----------------------------
	    
	    //CvTerm evCvtDetTpe = dipEvid.getCvtDetType();
	    
	    if( evCvtDetTpe!=null &&
                evCvtDetTpe.getCvId()!=null &&
                evCvtDetTpe.getName()!=null ){
		
		// 'new' style
		
		AttrType dtpeAtt =
		    DipDxfUtil.createAttr( "dip", "dip:0001",
                                           "link-type",
                                           "psi-mi",
                                           evCvtDetTpe.getCvId(),
                                           evCvtDetTpe.getName() );
		dxfNode.getAttrList().getAttr().add( dtpeAtt );
	    } else {
		
		//CvType evDetType = dipEvid.getDetType();
		
		if( evDetType!=null &&
                    evDetType.getNs()!=null &&
                    evDetType.getAc()!=null &&
                    evDetType.getName()!=null){
		    AttrType dtpAtt = DipDxfUtil
                        .createAttr( "dip", "dip:0001", "link-type",
                                     evDetType.getNs(), evDetType.getAc(), 
                                     evDetType.getName() );
		    dxfNode.getAttrList().getAttr().add( dtpAtt );
		}
	    }

	    //  -  interaction detection method
	    //---------------------------------
	    
	    CvTerm evCvtDetMth = dipEvid.getCvtDetMethod();
	    
	    if( evCvtDetMth!=null &&
                evCvtDetMth.getCvId()!=null &&
                evCvtDetMth.getName()!=null ){
		
		// 'new' style
                
		AttrType dmthAtt = 
		    DipDxfUtil.createAttr( "psi-mi", "MI:0001", 
                                           "interaction detection method",
                                           "psi-mi",
                                           evCvtDetMth.getCvId(), 
                                           evCvtDetMth.getName());
		dxfNode.getAttrList().getAttr().add( dmthAtt );
	    } else {
		
		CvType evDetMth = dipEvid.getDetMethod();
		
		if( evDetMth!=null &&
                    evDetMth.getAc()!=null &&
                    evDetMth.getName()!=null ){
		    
		    // 'old' style
		    
		    AttrType dmthAtt =
			DipDxfUtil.createAttr( "psi-mi", "MI:0001",
                                               "interaction detection method",
                                               evDetMth.getNs(),
                                               evDetMth.getAc(),
                                               evDetMth.getName() );
		    dxfNode.getAttrList().getAttr().add( dmthAtt );
		}
	    }
	    
	    //  -  experiment scale (small-scale/high-throughput)
	    //---------------------------------------------------
	    
	    CvTerm evCvtExpScale = dipEvid.getCvtExpScale();
	    
	    if( evCvtExpScale!=null &&
                evCvtExpScale.getCvId()!=null &&
                evCvtExpScale.getName()!=null ){
		
		// 'new' style
		
		AttrType esclAtt =
		    DipDxfUtil.createAttr( "dip", "dip:0003",
                                           "experiment-scale",
                                           "dip",
                                           evCvtExpScale.getCvId(),
                                           evCvtExpScale.getName() );
		dxfNode.getAttrList().getAttr().add( esclAtt );
	    } else {
		
		CvType evScale = dipEvid.getEvidScale();
		
		if( evScale!=null &&
                    evScale.getNs()!=null &&
                    evScale.getAc()!=null &&
                    evScale.getName()!=null ){
		   
                    String ns = "dip";
		    String ac = "dip:0005";
		    String nm = "high-throughput";
		    
		    if( evScale.getName().equals("1") ){
			ac = "dip:0002";
			nm = "small-scale";
		    }
		    
		    AttrType scaleAtt = 
			DipDxfUtil.createAttr( "dip", "dip:0003", 
                                               "experiment-scale",
                                               ns, ac, nm );
		    dxfNode.getAttrList().getAttr().add( scaleAtt );
		}
	    }
	    
	    //  -  experiment scope (directed/screen)
            
	    CvType evScope = dipEvid.getEvidScope();
	    if( evScope!=null && 1==0 ){
		AttrType scopeAtt =
		    DipDxfUtil.createAttr( "dip", "dip:0007", 
                                           "experiment-scope",
                                           evScope.getAc(), evScope.getAc(),
                                           evScope.getName() );
		dxfNode.getAttrList().getAttr().add( scopeAtt );
	    }

	    //  -  author-year
	    //----------------
	    	    
	    String ay = dipEvid.getDataSrc().getLabel();
	
	    AttrType ayAtt 
		= DipDxfUtil.createAttr( "dip", "dip:0027",
                                         "author-year", ay);
	    dxfNode.getAttrList().getAttr().add( ayAtt );
	    
	    //  -  participant list status (complete/uncomplete)
	    
	    CvType evPStat = dipEvid.getDetPartStatus();
	    if( evPStat!=null &&
                evPStat.getNs()!=null &&
                evPStat.getAc()!=null &&
                evPStat.getName()!=null ){
		
		String ns = "dip";
		String ac = "dip:0302";
		String nm = "complete";
		
		if( evPStat.getName().equals("1") ){
		    
		    AttrType pstatAtt =
			DipDxfUtil.createAttr( "dip", "dip:0006", 
                                               "part-list-status",
                                               ns, ac, nm );
		    dxfNode.getAttrList().getAttr().add( pstatAtt );
		}
	    }
	    
	    log.debug( "DipDxfUtil:    attributes: DONE..." );
	    
	    // create xref list
	    //-----------------
	    
	    dxfNode.setXrefList( dof.createNodeTypeXrefList() );
	    
	    log.debug( "DipDxfEvidUtil:   adding xrefs" );
	    
	    // adding xrefs:
	    //--------------
	    
	    //  - evidence for (link)
	    //-----------------------
	   	    
	    CvType ev4 = dipEvid.getEvidFor();
	    
	    log.debug( "DipDxfEvidUtil:    evid for xref" );
	    
	    if( ev4 != null ) { 
		    
		edu.ucla.mbi.dxf14.XrefType ev4Xref = 
		    DipDxfUtil.createXref( ev4.getNs(),
                                           ev4.getAc(),
                                           "dxf", "dxf:0013", "supports" );
		dxfNode.getXrefList().getXref().add( ev4Xref );
                
                if ( linkDef ){
                    if( mode.compareTo(DxfLevel.DEEP)>=0){
                        ev4Xref.setNode( DipDxfUtil.dip2dxf( dipEvid.getLink(),
                                                             id++,DxfLevel.SHRT ) );
                    }
                }
	    }  
	    
	    //  - evidence source (pmid)
	    //--------------------------
	    
	    CvType evSrc = dipEvid.getSource();
	    
	    log.debug( "DipDxfEvidUtil:    evid src xref" );
	    
	    if( evSrc!=null 
                && !evSrc.getAc().replaceAll("\\s","").equals("null") ){
		
                edu.ucla.mbi.dxf14.XrefType srcXref = 
		    DipDxfUtil.createXref( evSrc.getNs(),
                                           evSrc.getAc().replaceAll("\\s",""),
                                           "dxf", "dxf:0014", "described-by" );
		dxfNode.getXrefList().getXref().add( srcXref );
	    }
	    
	    //  - evidence source (imex)
	    //--------------------------
	    
	    String evImex = dipEvid.getImex254();

            String ics = "none"; // imex curation status
            

	    log.debug( "DipDxfUtil:    evid imex xref" );
	    
	    if( evImex!=null ){
		edu.ucla.mbi.dxf14.XrefType srcXref =
		    DipDxfUtil.createXref( "imex", evImex,
                                           "dxf", "dxf:0009", "identical-to" );
		dxfNode.getXrefList().getXref().add( srcXref );

                if( evImex.equals( "N/A" ) ){
                    ics = "preliminary";
                } else {
                    ics = "final";
                }
	    }	    

	    //  - evidence source (dip)
            //--------------------------
	    
	    String dsac=  dipEvid.getDataSrc().getAccession();
	    if( dsac != null ){
		edu.ucla.mbi.dxf14.XrefType dascXref =
                    DipDxfUtil.createXref( "dip",
                                           dsac,
                                           "dxf", "dxf:0014", "described-by" );
                dxfNode.getXrefList().getXref().add( dascXref );
            }

            
            // - imex curation status
            //-----------------------
            
            AttrType icsAtt
                = DipDxfUtil.createAttr( "psi-mi", "MI:0959",
                                         "imex curation", ics );
            dxfNode.getAttrList().getAttr().add( icsAtt );
            
            
            // - published-by/curated-by
            //-------------
            
            int imexSrcInt = dipEvid.getDataSrc().getImexSrc();
            
            log.debug( "DipDxfEvidUtil: imexSrcInt=" + imexSrcInt  );

            if( imexSrcInt >= 0 ){
                edu.ucla.mbi.dxf14.XrefType pbx = DipDxfDataSrcUtil
                    .createPublishedByXref( imexSrcInt );

                edu.ucla.mbi.dxf14.XrefType cbx = DipDxfDataSrcUtil
                    .createCuratedByXref( imexSrcInt );

                if( pbx != null && cbx != null) {
                    dxfNode.getXrefList().getXref().add( pbx );
                    dxfNode.getXrefList().getXref().add( cbx );
                }
            }
            
            
            // - mif254-derived info
            //----------------------

            if( mode.compareTo( DxfLevel.DEEP ) >= 0 
                && dipEvid.getMif254() != null ){
                dxfNode.setPartList( dof.createNodeTypePartList() );
                
                DipDxfEvidUtil.mif2dxf( dxfNode.getPartList().getPart(),
                                        dipEvid.getMif254() );
                
            } 

            
	    log.debug( "DipDxfEvidUtil:     xrefs: DONE" );
	}
	return dxfNode;
    }

    //--------------------------------------------------------------------------
    // Inference
    //----------

    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf( edu.ucla.mbi.dip.Inference dipInfer, 
                 long id, DxfLevel mode ){
	

        Evidence dipEvid = dipInfer.getGrounds();

        CvTerm evCvtDetTpe = dipInfer.getCvtDetType();
        CvType evDetType = dipEvid.getDetType();
        
       	//return DipDxfEvidUtil.dip2dxf( dipInfer, id, mode, true );  // 
        
        return DipDxfEvidUtil.dip2dxf( dipEvid,evCvtDetTpe, evDetType, 
                                       id, mode, true );  // 
    }
    
    //--------------------------------------------------------------------------

    public static edu.ucla.mbi.dxf14.NodeType
        dip2dxf( edu.ucla.mbi.dip.Inference dipInfer,
                 long id, DxfLevel mode, boolean linkDef ){
        
        Evidence dipEvid = dipInfer.getGrounds();
        
        CvTerm evCvtDetTpe = dipInfer.getCvtDetType();
        CvType evDetType = dipEvid.getDetType();
        
        return DipDxfEvidUtil.dip2dxf( dipEvid, evCvtDetTpe, evDetType,
                                       id, mode, linkDef );  // link
    }


    //---------------------------------------------------------------------------
    // retired ?

    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxfOld( edu.ucla.mbi.dip.Inference dipInfer, 
                    long id, DxfLevel mode, boolean linkDef ){
	
	Log log = LogFactory.getLog(DipDxfUtil.class);
	
	Evidence dipEvid=dipInfer.getGrounds();
	
	edu.ucla.mbi.dxf14.NodeType dxfNode = null;
	
	if(dipEvid!=null && dipEvid.getSource()!=null && 
           !dipEvid.getSource().getAc().replaceAll("\\s","").equals("null")){     
	    
	    // create new DXF node & node type elements
	    //-----------------------------------------
	    
	    dxfNode = dof.createNodeType();

	    // set node type 
	    //--------------
	    
	    edu.ucla.mbi.dxf14.TypeDefType 
		dxfType= dof.createTypeDefType();
	    
	    CvType evidType = dipEvid.getEvidType();
	    //CvType detMth   = dipEvid.getDetMethod();
	    
	    dxfType.setNs(evidType.getNs());
	    dxfType.setAc(evidType.getAc());
	    dxfType.setName(evidType.getName());
	    
	    dxfNode.setId(id);
	    dxfNode.setNs("dip");
	    dxfNode.setAc(dipEvid.getAccession());
	    
	    dxfNode.setType(dxfType);
	    
	    log.debug("DipDxfUtil:   label="+dipEvid.getLabel());
	    log.debug("DipDxfUtil:   name ="+dipEvid.getName());
	    
	    // set label
	    //----------
	    
	    dxfNode.setLabel(dipEvid.getAccession());
	    
	    // create attribute list
	    //----------------------
            
	    dxfNode.setAttrList(dof.createNodeTypeAttrList());
	    
	    log.debug("DipDxfUtil:   adding attributes");
	    
	    // add attributes:		
	    //----------------
	    
	    //  - detected interaction type
	    //-----------------------------
	    
	    CvTerm evCvtDetTpe = dipInfer.getCvtDetType();
	    
	    if(evCvtDetTpe!=null &&
	       evCvtDetTpe.getCvId()!=null &&
	       evCvtDetTpe.getName()!=null){
		
		// 'new' style
		
		AttrType dtpeAtt =
		    DipDxfUtil.createAttr("dip", "dip:0001",
					  "link-type",
					  "psi-mi",    // NOTE: ns fix me !!!
					  evCvtDetTpe.getCvId(),
					  evCvtDetTpe.getName());
		dxfNode.getAttrList().getAttr().add(dtpeAtt);
	    } else {
	       
		CvType evDetType = dipEvid.getDetType();
		
		if(evDetType!=null &&
		   evDetType.getNs()!=null &&
		   evDetType.getAc()!=null &&
		   evDetType.getName()!=null){
		    AttrType dtpAtt =  
			DipDxfUtil.createAttr("dip", "dip:0001", 
					      "link-type",
					      evDetType.getNs(),evDetType.getAc(), 
					      evDetType.getName());
		    dxfNode.getAttrList().getAttr().add(dtpAtt);
		}
	    }
	    
	    //  -  interaction detection method
	    //---------------------------------
	    
	    CvTerm evCvtDetMth = dipEvid.getCvtDetMethod();
	    
	    if(evCvtDetMth!=null &&
	       evCvtDetMth.getCvId()!=null &&
	       evCvtDetMth.getName()!=null){
		
		// 'new' style
		
		AttrType dmthAtt = 
		    DipDxfUtil.createAttr("psi-mi", "MI:0001", 
					  "interaction detection method",
					  "psi-mi",
					  evCvtDetMth.getCvId(), 
					  evCvtDetMth.getName());
		dxfNode.getAttrList().getAttr().add(dmthAtt);
	    } else {
		
		CvType evDetMth = dipEvid.getDetMethod();
		
		if(evDetMth!=null &&
		   evDetMth.getAc()!=null &&
		   evDetMth.getName()!=null){
		    
		    // 'old' style
		    
		    AttrType dmthAtt =
			DipDxfUtil.createAttr("psi-mi", "MI:0001",
					      "interaction detection method",
					      evDetMth.getNs(),
					      evDetMth.getAc(),
					      evDetMth.getName());
		    dxfNode.getAttrList().getAttr().add(dmthAtt);
		}
	    }
	    
	    //  -  experiment scale (small-scale/high-throughput)
	    //---------------------------------------------------
	    
	    CvTerm evCvtExpScale = dipEvid.getCvtExpScale();
	    
	    if(evCvtExpScale!=null &&
	       evCvtExpScale.getCvId()!=null &&
	       evCvtExpScale.getName()!=null){
		
		// 'new' style
		
		AttrType esclAtt =
		    DipDxfUtil.createAttr("dip", "dip:0003",
					  "experiment-scale",
					  "dip",
					  evCvtExpScale.getCvId(),
					  evCvtExpScale.getName());
		dxfNode.getAttrList().getAttr().add(esclAtt);
	    } else {
		
		CvType evScale = dipEvid.getEvidScale();
		
		if(evScale!=null &&
		   evScale.getNs()!=null &&
		   evScale.getAc()!=null &&
		   evScale.getName()!=null){
		    String ns = "dip";
		    String ac = "dip:0005";
		    String nm = "high-throughput";
		    
		    if(evScale.getName().equals("1")){
			ac="dcv:0002";
			nm="small-scale";
		    }
		    
		    AttrType scaleAtt = 
			DipDxfUtil.createAttr("dip",  "dip:0003", 
					      "experiment-scale",
					      ns,ac,nm);
		    dxfNode.getAttrList().getAttr().add(scaleAtt);
		}
	    }
	    
	    //  -  experiment scope (directed/screen)
	    
	    CvType evScope = dipEvid.getEvidScope();
	    if(evScope!=null && 1==0){
		AttrType scopeAtt =
		    DipDxfUtil.createAttr("dip",   "dip:0007", 
					  "experiment-scope",
					  evScope.getAc(), evScope.getAc(),
					  evScope.getName());
		dxfNode.getAttrList().getAttr().add(scopeAtt);
	    }
	    
	    //  -  participant list status (complete/uncomplete)
	    
	    CvType evPStat = dipEvid.getDetPartStatus();
	    if(evPStat!=null &&
	       evPStat.getNs()!=null &&
	       evPStat.getAc()!=null &&
	       evPStat.getName()!=null){
		
		String ns = "dip";
		String ac = "dip:0302";
		String nm = "complete";
		
		if(evPStat.getName().equals("1")){
		    
		    AttrType pstatAtt =
			DipDxfUtil.createAttr("dip","dip:0006", 
					      "part-list-status",
					      ns,ac,nm);
		    dxfNode.getAttrList().getAttr().add(pstatAtt);
		}
	    }
	    
	    log.debug("DipDxfUtil:    attributes: DONE...");
	    
	    // create xref list
	    //-----------------
	    
	    dxfNode.setXrefList(dof.createNodeTypeXrefList());
	    
	    log.debug("DipDxfUtil:   adding xrefs");
	    
	    // adding xrefs:
	    //--------------
	    
	    //  - evidence for (link)
	    //-----------------------
	    
	    CvType ev4 = dipEvid.getEvidFor();
	    
	    log.debug("DipDxfUtil:    evid for xref");
	    
	    if(ev4!=null && linkDef){
		
		edu.ucla.mbi.dxf14.XrefType ev4Xref = 
		    DipDxfUtil.createXref(ev4.getNs(),
					  ev4.getAc(),
					  "dxf", "dxf:0021", "evidence-for");
		dxfNode.getXrefList().getXref().add(ev4Xref);
		
		if(mode.compareTo(DxfLevel.DEEP)>=0){
		    ev4Xref.setNode(DipDxfUtil.dip2dxf(dipEvid.getLink(),
						       id++,DxfLevel.SHRT));
		}
	    }
	    
	    //  - evidence source (pmid)
	    //--------------------------
	    
	    CvType evSrc = dipEvid.getSource();
	    
	    log.debug("DipDxfUtil:    evid src xref");
	    
	    if(evSrc!=null && !evSrc.getAc().replaceAll("\\s","").equals("null")){
		
		edu.ucla.mbi.dxf14.XrefType srcXref = 
		    DipDxfUtil.createXref(evSrc.getNs(),
					  evSrc.getAc().replaceAll("\\s",""),
					  "dxf", "dxf:0014", "described-by");
		dxfNode.getXrefList().getXref().add(srcXref);
	    }
	    
	    //  - evidence source (imex)
	    //--------------------------
	    
	    String evImex = dipEvid.getImex254();
	    log.debug( " DipDxfUtil: evImex=" + evImex );


	    log.debug("DipDxfUtil:    evid imex xref");
	    
	    if( evImex!=null){
		
		edu.ucla.mbi.dxf14.XrefType srcXref =
		    DipDxfUtil.createXref("imex",
					  evImex,
					  "dxf", "dxf:0021", "evidence-for");
		dxfNode.getXrefList().getXref().add(srcXref);
	    }

	    log.debug("DipDxfUtil:     xrefs: DONE");   
	}
	return dxfNode;
    }

    //--------------------------------------------------------------------------
    // parse & process mif254 file 
    //----------------------------

    private static void mif2dxf( List<NodeType.PartList.Part> part,
                                 String mif254 ){

        Log log = LogFactory.getLog( DipDxfEvidUtil.class );
        
        // parse mif
        //----------

        EntrySetType est = null;

        try{ 
            JAXBContext jc = JAXBContext.newInstance( "org.hupo.psi.mi.mif" );
            Unmarshaller u = jc.createUnmarshaller(); 
            JAXBElement<EntrySetType> jest =  
                u.unmarshal( new StreamSource( new StringReader(  mif254 ) ),
                             EntrySetType.class );
            est = jest.getValue();
            
        } catch( JAXBException jbx ){
            log.info( "JAXBException" );
        }

        if ( est == null ) {
            return;
        }
        
        log.debug( "EST: " + est.getLevel()
                  + "." + est.getVersion()
                  + "." + est.getMinorVersion() );
       
        if( est.getEntry().size() != 1 ) return; // should be only one ? 
        EntrySetType.Entry.InteractionList intlist = 
            est.getEntry().get(0).getInteractionList();
        
        if( intlist != null 
            && intlist.getInteraction().size() != 1 ) return; // should be only one ?

        // interaction-evidence pair
        //--------------------------
        
        InteractionElementType i11 = intlist.getInteraction().get( 0 );
                               
        // process participants
        //---------------------

        if( i11.getParticipantList() == null ) return; // should be non-empty
        
        List<ParticipantType> plist =
            i11.getParticipantList().getParticipant();
            
        long id = 0;

        for( Iterator<ParticipantType> pi = plist.iterator(); pi.hasNext(); ) {

            ParticipantType p11 = pi.next();

            if( p11.getNames() != null ) {
                log.debug(" PSL: " + p11.getNames().getShortLabel() );
            } 

            NodeType.PartList.Part partType = dof.createNodeTypePartListPart();
            part.add( partType );

            //<part id="3" name="{A}">
            // <type ac="dxf:0045" name="evidence-node" ns="dxf"/>
            //
            // <node ac="DIP-9121NP" id="2" ns="dip">
            //  <type ac="dxf:0003" name="protein" ns="dxf"/>
            //  <label/>
            //  <name>Adenine phosphoribosyltransferase (APRT)</name>
            //
            //  <xrefList>
            //     <xref ac="562" ns="TaxId" type="produced-by" typeAc="dxf:0007" typeNs="dxf">
            //        <node ac="562" id="123" ns="TaxId">
            //           <type ac="dxf:0017" name="organism" ns="dxf"/>
            //           <label>Escherichia coli</label>
            //           <name/>
            //        </node>
            //     </xref>
            //
            //     <xref ac="P07672" ns="UniProt" type="instance-of" typeAc="dxf:0006" typeNs="dxf"/>
            //     <xref ac="NP_415002" ns="RefSeq" type="instance-of" typeAc="dxf:0006" typeNs="dxf"/>
            //  </xrefList>
            // </node>
            //</part>

            // set id/type
            //------------

            id = id + 1;

            partType.setId( id );
            
            TypeDefType tdt = dof.createTypeDefType();
            tdt.setNs( "dxf" );
            tdt.setAc( "dxf:0045" );
            tdt.setName( "evidence-node" );
            partType.setType( tdt);
            
            // build interactor
            //-----------------

            InteractorElementType i10 = p11.getInteractor();
            
            // partType: add interactor
            //-------------------------
            
            NodeType i10node = DipDxfEvidUtil.mif2dxf( i10, id );
            partType.setNode( i10node );
           
            // partType: set name
            //-------------------
            
            String ptn = i10node.getLabel();
            if( ptn == null ) {
                ptn = "{" + id + "}";
            } else {
                ptn += "{" + id + "}";
            }
            partType.setName( ptn );
            
            // experimental role
            //------------------
            
            if ( p11.getExperimentalRoleList() != null ) {
                
                List<ParticipantType.ExperimentalRoleList
                    .ExperimentalRole> pimList = p11
                    .getExperimentalRoleList()
                    .getExperimentalRole();

                for( Iterator<ParticipantType
                         .ExperimentalRoleList
                         .ExperimentalRole> 
                         pimi = pimList.iterator(); 
                     pimi.hasNext(); ){
                    
                    org.hupo.psi.mi.mif.CvType pim = 
                        (org.hupo.psi.mi.mif.CvType) pimi.next();
                    
                    log.debug( "  EROLE: db=" + pim.getXref().getPrimaryRef().getDb()
                              + " dbac=" + pim.getXref().getPrimaryRef().getDbAc()
                              + " id=" + pim.getXref().getPrimaryRef().getId()
                              + " name=" + pim.getNames().getFullName() );


                    String name = pim.getNames().getShortLabel();
                    if( name == null || name.equals("") ){
                        name = pim.getNames().getFullName();
                    }
                    
                    // partType: add attribute
                    //------------------------
                    
                    PartType.AttrList atl =  partType.getAttrList();
                    if( atl == null ) {
                        partType.setAttrList( dof.createPartTypeAttrList() );
                        atl =  partType.getAttrList();
                    }

                    //<attr ac="MI:0495" name="experimental role" ns="psi-mi">
                    //  <value ac="MI:0914" ns="psi-mi">association</value>
                    //</attr>
                    
                    AttrType att = DipDxfUtil
                        .createAttr( "psi-mi", "MI:0495", "experimental role",
                                     pim.getXref().getPrimaryRef().getDb(),
                                     pim.getXref().getPrimaryRef().getId(), 
                                     name );
                    
                    atl.getAttr().add( att );
                }
            }

            // biological role
            //----------------
            
            if ( p11.getBiologicalRole() != null ) {
                
                //<biologicalRole>
                // <names>
                //  <shortLabel>unspecified role</shortLabel>
                // </names>
                // <xref>
                //  <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0499" 
                //              refType="identity" refTypeAc="MI:0356"/>
                // </xref>
                //</biologicalRole>

                org.hupo.psi.mi.mif.CvType bioRole =  p11.getBiologicalRole();
                
                log.debug( "  BROLE: db=" + bioRole.getXref().getPrimaryRef().getDb() 
                          + " dbac=" + bioRole.getXref().getPrimaryRef().getDbAc()
                          + " id=" + bioRole.getXref().getPrimaryRef().getId() 
                          + " name=" + bioRole.getNames().getShortLabel() );
                
                
                String name = bioRole.getNames().getShortLabel();
                if( name == null || name.equals("") ){
                    name = bioRole.getNames().getFullName();
                }


                // partType: add attribute
                //------------------------
                    
                PartType.AttrList atl =  partType.getAttrList();
                if( atl == null ) {
                    partType.setAttrList( dof.createPartTypeAttrList() );
                    atl =  partType.getAttrList();
                }
                

                //<attr ac="MI:0495" name="experimental role" ns="psi-mi">
                //  <value ac="MI:0914" ns="psi-mi">association</value>
                //</attr>

                AttrType att = DipDxfUtil
                    .createAttr( "psi-mi", "MI:0500", "biological role",
                                 bioRole.getXref().getPrimaryRef().getDb(),
                                 bioRole.getXref().getPrimaryRef().getId(),
                                 name );
                
                atl.getAttr().add( att );                
            }

            // identification method role
            //---------------------------
            
            if ( p11.getParticipantIdentificationMethodList() != null ) {
                
                List<ParticipantType.ParticipantIdentificationMethodList
                    .ParticipantIdentificationMethod> pimList = p11
                    .getParticipantIdentificationMethodList()
                    .getParticipantIdentificationMethod();

                for( Iterator<ParticipantType
                         .ParticipantIdentificationMethodList
                         .ParticipantIdentificationMethod> 
                         pimi = pimList.iterator(); 
                     pimi.hasNext(); ){
                    
                    org.hupo.psi.mi.mif.CvType pim = 
                        (org.hupo.psi.mi.mif.CvType) pimi.next();
                    
                    log.debug( "  PIM: db=" + pim.getXref().getPrimaryRef().getDb()
                              + " dbac=" + pim.getXref().getPrimaryRef().getDbAc()
                              + " id=" + pim.getXref().getPrimaryRef().getId()
                              + " name=" + pim.getNames().getFullName() );
                    

                    // get non-empty name
                    //-------------------

                    String name = pim.getNames().getShortLabel();
                    if( name == null || name.equals("") ){
                        name = pim.getNames().getFullName();
                    }

                    // partType: add attribute
                    //------------------------

                    
                    PartType.AttrList atl =  partType.getAttrList();
                    if( atl == null ) {
                        partType.setAttrList( dof.createPartTypeAttrList() );
                        atl =  partType.getAttrList();
                    }

                    //<attr ac="MI:0495" name="experimental role" ns="psi-mi">
                    //  <value ac="MI:0914" ns="psi-mi">association</value>
                    //</attr>

                    AttrType att = DipDxfUtil
                        .createAttr( "psi-mi", "MI:0002", 
                                     "participant identification method",
                                     pim.getXref().getPrimaryRef().getDb(),
                                     pim.getXref().getPrimaryRef().getId(),
                                     name);
                    
                    atl.getAttr().add( att );
                }                
            } else { // global method

                log.debug(" global: " );
                

                ExperimentType exp = null;

                if( i11.getExperimentList() != null 
                    && i11.getExperimentList()
                    .getExperimentRefOrExperimentDescription() != null ){
                    
                    List i11l = i11.getExperimentList()
                        .getExperimentRefOrExperimentDescription();


                    log.debug( " global: i11l.size=" + i11l.size() );
                    log.debug( " global:  class=" + i11l.get( 0 ).getClass() );

                    if( i11l.size() == 1 &&
                        i11l.get( 0 ) instanceof ExperimentType ) {
                        
                        exp = (ExperimentType) i11l.get( 0 );
                    }
                }

                log.debug(" global: EXP=" + exp );

                if( exp != null ){
                    org.hupo.psi.mi.mif.CvType pim 
                        = exp.getParticipantIdentificationMethod();

                    if( pim != null ){

                        // get non-empty name
                        //-------------------
                        
                        String name = pim.getNames().getShortLabel();
                        if( name == null || name.equals("") ){
                            name = pim.getNames().getFullName();
                        }

                        // partType: add attribute
                        //------------------------

                        PartType.AttrList atl =  partType.getAttrList();
                        if( atl == null ) {
                            partType.setAttrList( dof.createPartTypeAttrList() );
                            atl =  partType.getAttrList();
                        }

                        //<attr ac="MI:0495" name="experimental role" ns="psi-mi">
                        //  <value ac="MI:0914" ns="psi-mi">association</value>
                        //</attr>

                        AttrType att = DipDxfUtil
                            .createAttr( "psi-mi", "MI:0002",
                                         "participant identification method",
                                         pim.getXref().getPrimaryRef().getDb(),
                                         pim.getXref().getPrimaryRef().getId(),
                                         name);
                        
                        atl.getAttr().add( att );
                        
                    }                    
                }
            }

            // host organism(s)
            //-----------------

            if ( p11.getHostOrganismList() != null ) {

                List<ParticipantType.HostOrganismList.HostOrganism> hoList 
                    = p11.getHostOrganismList().getHostOrganism();

                for( Iterator<ParticipantType.HostOrganismList.HostOrganism>
                         hoi = hoList.iterator();
                     hoi.hasNext(); ){
                    
                    BioSourceType ho = hoi.next();
                    int taxid = ho.getNcbiTaxId();
                    
                    edu.ucla.mbi.dxf14.XrefType nXref = DipDxfUtil
                        .createXref("ncbitaxid", String.valueOf( taxid ),
                                    "dxf", "dxf:0007", "produced-by");
                    
                    

                    PartType.XrefList xrl = partType.getXrefList();
                    if( xrl == null ) {
                        partType.setXrefList( dof.createPartTypeXrefList() );
                        xrl =  partType.getXrefList();
                    }
                    xrl.getXref().add( nXref );
                }
            }
        }
    }


    //--------------------------------------------------------------------------

    private static edu.ucla.mbi.dxf14.NodeType 
        mif2dxf( InteractorElementType i10, long id ){
        
        Log log = LogFactory.getLog( DipDxfEvidUtil.class );

        edu.ucla.mbi.dxf14.NodeType dxfNode = dof.createNodeType();
        
        //<interactor id="107">
        // <names>
        //  <shortLabel>DCP1b</shortLabel>
        //  <fullName>mRNA-decapping enzyme 1B</fullName>
        // </names>
        // <xref>
        //  <primaryRef db="uniprot knowledge base" dbAc="MI:0486" id="Q8IZD4" 
        //              refType="identity" refTypeAc="MI:0356" version="68"/>
        //  <secondaryRef db="refseq" dbAc="MI:0481" id="NP_689853" 
        //                refType="identity" refTypeAc="MI:0356" version="3"/>
        //  <secondaryRef db="dip" dbAc="MI:0465" id="DIP-31289N" 
        //                refType="identity" refTypeAc="MI:0356"/>
        // </xref>
        // <interactorType>
        //  <names>
        //   <fullName>protein</fullName>
        //  </names>
        //  <xref>
        //   <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0326" 
        //               refType="identity" refTypeAc="MI:0356"/>
        //  </xref>
        // </interactorType>
        // <organism ncbiTaxId="9606">
        //  <names>
        //   <fullName>Homo sapiens</fullName>
        //  </names>
        // </organism>
        //</interactor>

        // convert into: 

        //<node ac="DIP-65756NP" id="2" ns="dip">
        // <type ac="0003" name="protein" ns="dxf"/>
        // <label>mt-TyrAA</label>
        // <name>Tyrosyl-tRNA</name>
        // <xrefList>
        //  <xref ac="9606" ns="ncbitaxid" 
        //        type="produced-by" typeAc="0007" typeNs="dxf"/>
        //  <xref ac="Q9Y2Z4" ns="UniProt" 
        //        type="instance-of" typeAc="0006" typeNs="dxf"/>
        //  <xref ac="NP_001035526.1" ns="RefSeq" 
        //        type="instance-of" typeAc="0006" typeNs="dxf"/>
        // </xrefList>
        //</node>
        
        //----------------------------------------------------------------------
        // set node id
        //------------

        dxfNode.setId( id );
        
        //----------------------------------------------------------------------
        // set node type
        //--------------
        
        edu.ucla.mbi.dxf14.TypeDefType dxfType = dof.createTypeDefType();
       
        dxfType.setNs( i10.getInteractorType().getXref()
                       .getPrimaryRef().getDb() );
        dxfType.setAc( i10.getInteractorType().getXref()
                       .getPrimaryRef().getId() );
        dxfType.setName( i10.getInteractorType().getNames()
                         .getFullName() );
        
        dxfNode.setType( dxfType );

        //----------------------------------------------------------------------
        // set label/name
        //---------------

        if( i10.getNames().getShortLabel() != null 
            && i10.getNames().getShortLabel().length() > 0 ) {
            dxfNode.setLabel( i10.getNames().getShortLabel() );
        }
        
        if( i10.getNames().getFullName() != null 
            && i10.getNames().getFullName().length() > 0 ) {
            dxfNode.setName( i10.getNames().getFullName() );
        }
        
        //----------------------------------------------------------------------
        // create xref list
        //-----------------
        
        dxfNode.setXrefList( dof.createNodeTypeXrefList() );
        

        //----------------------------------------------------------------------
        // add xrefs: taxon/organism 
        //--------------------------
        
        edu.ucla.mbi.dxf14.XrefType oXref = DipDxfUtil
            .createXref( "ncbitaxid",
                         String.valueOf( i10.getOrganism().getNcbiTaxId() ),
                         "dxf", "dxf:0007", "produced-by" );
        
        dxfNode.getXrefList().getXref().add(oXref);
        

        //----------------------------------------------------------------------
        // go over xrefs - add as needed
        //------------------------------

        List<DbReferenceType> xref = new ArrayList<DbReferenceType>();

        if( i10.getXref() != null ) {
            xref.add( i10.getXref().getPrimaryRef() );
            if( i10.getXref().getSecondaryRef() != null ) {
                xref.addAll( i10.getXref().getSecondaryRef() );
            }
        }
        
        for( Iterator<DbReferenceType> xi = xref.iterator(); xi.hasNext(); ) {
            DbReferenceType xr = xi.next();

            //------------------------------------------------------------------
            // DIP accession

            if( xr.getDbAc() != null && xr.getRefTypeAc() != null 
                && xr.getDbAc().equals( "MI:0465" ) 
                && xr.getRefTypeAc().equals( "MI:0356" ) ){

                //<secondaryRef db="dip" dbAc="MI:0465" id="DIP-31289N" 
                //              refType="identity" refTypeAc="MI:0356"/>

                dxfNode.setNs( "dip" );
                dxfNode.setAc( xr.getId() );
                continue;
            }


            //------------------------------------------------------------------
            // uniprot accession

            if( xr.getDbAc() != null && xr.getDbAc().equals( "MI:0486" ) ){
                
                //<secondaryRef db="uniprot knowledge base" dbAc="MI:0486" 
                //              id="Q8IZD4" refType="identity" 
                //              refTypeAc="MI:0356" version="68"/>

                //<xref ac="Q9Y2Z4" ns="UniProt" type="instance-of" 
                //      typeAc="0006" typeNs="dxf"/>

                String ac = xr.getId();
                if( xr.getVersion() != null ) {
                    String ver = xr.getVersion().replaceAll( "\\D", "" );
                    ac = ac + "." + ver;
                }

                if( xr.getRefTypeAc() == null 
                    || xr.getRefTypeAc().equals( "MI:0356" ) ){
                    
                    edu.ucla.mbi.dxf14.XrefType nXref =
                        DipDxfUtil.createXref( "UniProt", ac,
                                               "dxf", "dxf:0006", "instance-of" );
                    
                    dxfNode.getXrefList().getXref().add( nXref );
                    continue;
                }
            }

            

            //------------------------------------------------------------------
            // refseq accession:  identity & product only

            if( xr.getDbAc() != null && xr.getDbAc().equals( "MI:0481" ) ){

                //<secondaryRef db="refseq" dbAc="MI:0481" 
                //              id="NP_689853" refType="identity" 
                //              refTypeAc="MI:0356" version="3"/>
                
                //<xref ac="NP_689853.3" ns="RefSeq" type="instance-of"
                //      typeAc="0006" typeNs="dxf"/>

                String ac = xr.getId();
                
                if( xr.getVersion() != null ) {

                    String ver = xr.getVersion();
                    try {
                        ver.replaceAll( "\\D", "" );
                    } catch( PatternSyntaxException psx  ){
                        // should not happen
                    }

                    ac = ac + "." + ver;
                }

                if( xr.getRefTypeAc() == null
                    || xr.getRefTypeAc().equals( "MI:0356" ) ){ // ident

                    edu.ucla.mbi.dxf14.XrefType nXref = DipDxfUtil
                        .createXref( "refseq", ac,
                                     "dxf", "dxf:0006", "instance-of" );
                    
                    dxfNode.getXrefList().getXref().add( nXref );
                    continue;
                }

                if( xr.getRefTypeAc() != null
                    && xr.getRefTypeAc().equals( "MI:0251" ) ){ // product

                    edu.ucla.mbi.dxf14.XrefType nXref = DipDxfUtil
                        .createXref( "refseq", ac,
                                     "dxf", "dxf:0022", "encoded-by" );
                    
                    dxfNode.getXrefList().getXref().add( nXref );
                    continue;
                }
            }


            //------------------------------------------------------------------
            // entrez gene accession:  identity & product only

            if( xr.getDbAc() != null && xr.getDbAc().equals( "MI:0481" ) ){

                //<secondaryRef db="entrez gene/locuslink" dbAc="MI:0477"
                //              id="7157" refType="gene product"
                //              refTypeAc="MI:0251"/>


                //<xref ac="7157" ns="entrezgene" 
                //      type="encoded-by" typeAc="dxf:0022" typeNs="dxf"/>

                String ac = xr.getId();
                if( xr.getVersion() != null ) {
                    ac = ac + "." +  xr.getVersion();
                }

                if( xr.getRefTypeAc() == null
                    || xr.getRefTypeAc().equals( "MI:0356" ) ){ // ident

                    edu.ucla.mbi.dxf14.XrefType nXref = DipDxfUtil
                        .createXref( "entrezgene", ac,
                                     "dxf", "dxf:0006", "instance-of" );
                    
                    dxfNode.getXrefList().getXref().add( nXref );
                    continue;
                }

                if( xr.getRefTypeAc() != null
                    && xr.getRefTypeAc().equals( "MI:0251" ) ){ // product

                    edu.ucla.mbi.dxf14.XrefType nXref = DipDxfUtil
                        .createXref( "entrezgene", ac,
                                     "dxf", "dxf:0022", "encoded-by" );

                    dxfNode.getXrefList().getXref().add( nXref );
                    continue;
                }
            }

            
            //------------------------------------------------------------------
            // skip 

            if( xr.getDbAc() != null 
                && ( xr.getDbAc().equals( "MI:0448" ) // GO
                     || xr.getDbAc().equals( "MI:0449" ) // interpro
                     || xr.getDbAc().equals( "MI:0460" ) // pdb
                     ) 
                ){

                continue;
            }

            //------------------------------------------------------------------
            // anything else

            String ac = xr.getId();
            if( xr.getVersion() != null ) {
                ac = ac + "." +  xr.getVersion().replaceAll( "\\D", "" );
            }

            String ns = xr.getDb();
            String rtAc = xr.getRefTypeAc();
            String rtName = xr.getRefType(); 

            
            if( ac != null && ns != null ) {
                if( rtAc != null && rtName != null ){

                    edu.ucla.mbi.dxf14.XrefType nXref = DipDxfUtil
                        .createXref( ns, ac, "psi-mi", rtAc, rtName );
                    dxfNode.getXrefList().getXref().add( nXref );
                    continue;
                }
                
                edu.ucla.mbi.dxf14.XrefType nXref = DipDxfUtil
                    .createXref( ns, ac, "dxf", "dxf:0054", "annotated-with" );
                dxfNode.getXrefList().getXref().add( nXref );
                continue;
            }


        }
        return dxfNode;
    }   
}
