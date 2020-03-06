package edu.ucla.mbi.dip;

/* =============================================================================
 # $Id:: DipDxfUtil.java 1908 2011-10-25 16:48:17Z lukasz                      $
 # Version: $Rev:: 1908                                                        $
 #==============================================================================
 #                                                                             $
 #  DipDxfUtil - dip objects->DXF1.4 conversion                                $
 #                                                                             $
 #    NOTES:                                                                   $
 #                                                                             $
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 
import java.util.*;
import javax.xml.bind.*;

import edu.ucla.mbi.dip.*;
import edu.ucla.mbi.dxf14.*;

public class DipDxfUtil{
    
    static edu.ucla.mbi.dxf14.ObjectFactory 
	dof = new edu.ucla.mbi.dxf14.ObjectFactory();

    //--------------------------------------------------------------------------
    // DipNode DXF representation
    //---------------------------
    
    public static edu.ucla.mbi.dxf14.NodeType
	dip2dxf( DipNode dipNode, long id, DxfLevel mode ){
	return DipDxfUtil.dip2dxf( dipNode, id, 
                                   mode, "", "" );  // self as query ns/ac
    }
    
    //--------------------------------------------------------------------------

    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf( DipNode dipNode, long id, 
                 DxfLevel mode, String qns, String qac ) {

	Log log = LogFactory.getLog( DipDxfUtil.class );
	edu.ucla.mbi.dxf14.NodeType dxfNode = null;
	
	if( dipNode!=null ){     
	    
	    log.debug( "dip2dxf(node): AC=" + dipNode.getAccession() );  
            
	    // create new DXF node & node type elements
	    //-----------------------------------------
		    
	    dxfNode = dof.createNodeType();
		
	    // set node type 
	    //--------------
            
	    edu.ucla.mbi.dxf14.TypeDefType 
                dxfType = dof.createTypeDefType();
	    
	    CvType cvt = dipNode.getCvType();
	    
	    dxfType.setNs( cvt.getNs() );
	    dxfType.setAc( cvt.getAc() );
	    dxfType.setName( cvt.getName() );
	    
	    dxfNode.setId( id );
	    dxfNode.setNs( "dip" );
	    dxfNode.setAc( dipNode.getAccession() );
	    
	    dxfNode.setType(dxfType);
	    
	    log.debug( "label=" + dipNode.getLabel() );
	    log.debug( "name =" + dipNode.getName() );
	    
	    // set label
	    //----------
	      
	    dxfNode.setLabel( dipNode.getLabel() );
		
	    // create xref list
	    //-----------------
	    dxfNode.setXrefList( dof.createNodeTypeXrefList() );
	    
	    // add xrefs
	    //----------
	    
	    //   Taxon Id/Organism
	    //--------------------
	    log.debug( "taxid ="+dipNode.getTaxId());
	    
	    XrefType oXref = DipDxfUtil
                .createXref( "TaxId", 
                             String.valueOf( dipNode.getTaxId() ),
                             "dxf", "dxf:0007", "produced-by" );
	    
	    if( mode.compareTo( DxfLevel.STUB )>0 ){ // all but STUB mode
		
		edu.ucla.mbi.dxf14.NodeType dxfOrganism = null;
		dxfOrganism = dof.createNodeType();
		
		edu.ucla.mbi.dxf14.TypeDefType
		    dxfOrgType = dof.createTypeDefType();
                
		dxfOrgType.setNs( "dip" );
		dxfOrgType.setAc( "dip:0301" );
		dxfOrgType.setName( "organism" );
		
		dxfOrganism.setType( dxfOrgType );
		
		dxfOrganism.setNs( "TaxId" );
		dxfOrganism.setAc( String.valueOf( dipNode.getTaxId() ) );
		dxfOrganism.setId( 123 );
		dxfOrganism.setLabel( dipNode.getOrganism().getName() );
		dxfOrganism.setName( dipNode.getOrganism().getCommonName() );
		
		// set organism mode
		//------------------
		
		oXref.setNode( dxfOrganism );
	    }
	    
	    log.debug( "adding taxid..." );
		
	    dxfNode.getXrefList().getXref().add(oXref);
	    
	    if( mode.compareTo( DxfLevel.STUB )>0 ){ // all but STUB mode
		    
		// set name/description
		//---------------------
		dxfNode.setName( dipNode.getName() );
		
		// xrefs to refseq/uniprot
		//------------------------
		
		String up = dipNode.getUniProt();
		String rs = dipNode.getRefSeq();
		String eg = dipNode.getEntrezGene();
		
		if( up!=null && up.length()>0 ){
		    
		    XrefType upXref = DipDxfUtil
                        .createXref( "uniprot", up,
                                     "dxf", "dxf:0006","instance-of" );
		    
		    log.debug( "adding uniprot: " + up );
		    dxfNode.getXrefList().getXref().add( upXref );
		}
		
		if( rs!=null && rs.length()>0 ){
		    
		    XrefType rsXref = DipDxfUtil
                        .createXref( "refseq", rs,
                                     "dxf", "dxf:0006","instance-of" );
		    
		    log.debug( "adding refseq: " + rs );
		    dxfNode.getXrefList().getXref().add( rsXref );
		}

		if( eg!=null && eg.length()>0 ){
		    
		    XrefType egXref = DipDxfUtil
                        .createXref( "entrezgene", eg,
                                     "dxf", "dxf:0022","encoded-by" );
		    
		    log.debug( "adding entrezgene: " + eg );
		    dxfNode.getXrefList().getXref().add( egXref );
		}
		
		String seq = dipNode.getSequence();
		
		if( seq!=null && seq.length()>0 ){
		    
		    dxfNode.setAttrList(dof.createNodeTypeAttrList());
		    AttrType seqAtt = DipDxfUtil
                        .createAttr( "dip", "dip:0008",
                                     "sequence", seq);
		    
		    dxfNode.getAttrList().getAttr().add( seqAtt );
		}
	    }
	    
	    if( mode.compareTo( DxfLevel.MIT )==0 ){  //mit
		
		// MiTap 
		//------
                
		Set xs= dipNode.getXrefs();
		
		if( xs!=null && xs.size()>0 ){
		    
		    for( Iterator xi=xs.iterator(); xi.hasNext(); ){ 
			
			NodeCrossRef xc = (NodeCrossRef) xi.next(); 
			
			XrefType xcXref = DipDxfUtil
                            .createXref( xc.getSrcDb().getName(), 
                                         xc.getAcc(),
                                         "dxf", "dxf:0006","instance-of");
			
			log.debug( "adding xref: " + xc.getAcc() );
			dxfNode.getXrefList().getXref().add( xcXref );
		    }
		}
	    }
	    
	    if( !qns.equals("") && !qac.equals("") ){
		XrefType xcXref = DipDxfUtil
                    .createXref( qns, qac,
                                 "dxf", "dxf:0019","queried-as" );
		
		log.debug( "adding queried-as xref: " + qac );
		dxfNode.getXrefList().getXref().add( xcXref );    
	    }
	}
	return dxfNode;
    }
    
    //--------------------------------------------------------------------------
    // Counts (DipNode) DXF representation
    //------------------------------------

    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf( edu.ucla.mbi.dip.CountsFull cnt, edu.ucla.mbi.dip.DipNode nd, 
                 long id, DxfLevel mode ) {
	
	Log log = LogFactory.getLog(DipDxfUtil.class);
	
	edu.ucla.mbi.dxf14.NodeType dxfNode = null;
	int _id=1;

	if( cnt == null ) {
	    return null;
	}
	
	// create empty result dataset
	//----------------------------

	//DatasetType res =  dof.createDatasetType();
	    
	// create new DXF node
	//--------------------
	    
	dxfNode= dof.createNodeType();
	    
	edu.ucla.mbi.dxf14.TypeDefType   
	    dxfType= dof.createTypeDefType();
	    
	// set node type to 'protein'
	//---------------------------
	
	dxfType.setNs("dxf");
	dxfType.setAc("dxf:0003");
	dxfType.setName("protein");
	dxfNode.setNs("dip");
	dxfNode.setAc( nd.getAccession() );
	dxfNode.setLabel( nd.getLabel() ); // FIX ME

	// create taxid xref
	//------------------

	dxfNode.setXrefList(dof.createNodeTypeXrefList());

	XrefType xXref =
	    DipDxfUtil.createXref("TaxId", String.valueOf(nd.getOrganism().getTaxId()),
				  "dxf", "dxf:0007","produced-by");
	dxfNode.getXrefList().getXref().add(xXref);
	
	dxfNode.setId(_id++);	    
	dxfNode.setType(dxfType);
		
	// create attribute list
	//----------------------

	dxfNode.setAttrList(dof.createNodeTypeAttrList());
	List<edu.ucla.mbi.dxf14.AttrType>
	    atl=dxfNode.getAttrList().getAttr();
	
	log.debug("DipDxfUtil:   adding attributes");

	// partners
	//---------

	Integer pb=cnt.getDetail().get("partner-binary-count");
	if(pb!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0021","partner-binary-count",
					  String.valueOf(pb)));
	}
	
	Integer pc=cnt.getDetail().get("partner-multi-count");
	if(pc!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0022","partner-multi-count",
					  String.valueOf(pc)));
	}
	
	Integer  pa=cnt.getDetail().get("partner-assembly-count");
	if(pa!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0000","partners-assembly-count",
					  String.valueOf(pa)));
	}
	
	// interactions
	//-------------

	Integer ib=cnt.getDetail().get("interaction-binary-count");
	if(ib!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0023","interaction-binary-count",
					  String.valueOf(ib)));
	}

	Integer im=cnt.getDetail().get("interaction-multi-count");
	if(ib!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0024","interaction-multi-count",
					  String.valueOf(im)));
	}

	// evidence
	//---------
	
	Integer ec=cnt.getDetail().get("evidence-covalent-count");
	if(ec!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0000","evidence-covalent-count",
					  String.valueOf(im)));
	}
	
	Integer ed=cnt.getDetail().get("evidence-direct-count");
	if(ed!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0000","evidence-direct-count",
					  String.valueOf(ed)));
	}
	
	Integer ep=cnt.getDetail().get("evidence-physical-count");
	if(ep!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0000","evidence-physical-count",
					  String.valueOf(ep)));
	}
	
	Integer ea=cnt.getDetail().get("evidence-assembly-count");
	if(ea!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0000","evidence-assembly-count",
					  String.valueOf(ea)));
	}
	
	// sources
	//--------

	Integer sc=cnt.getDetail().get("source-count");
        if(sc!=null){
            atl.add(DipDxfUtil.createAttr("dip","dip:0020","source-count",
                                          String.valueOf(sc)));
        }
	

        log.debug("DipDxfUtil: done: "+dxfNode.getAc());
	return dxfNode;

    }
    
    /* Counts (Link) DXF representation */
    
    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf(edu.ucla.mbi.dip.CountsFull cnt, 
		edu.ucla.mbi.dip.Link ln, 
		long id, DxfLevel mode) {
	
	Log log = LogFactory.getLog(DipDxfUtil.class);
	
	edu.ucla.mbi.dxf14.NodeType dxfNode = null;
	int _id=1;

	if(cnt==null){
	    return null;
	}
	
	// create empty result dataset
	//----------------------------

	//DatasetType res =  dof.createDatasetType();
	    
	// create new DXF node
	//--------------------
	    
	dxfNode= dof.createNodeType();
	    
	edu.ucla.mbi.dxf14.TypeDefType   
	    dxfType= dof.createTypeDefType();
	    
	// set node type to 'protein'
	//---------------------------
	
	dxfType.setNs("dxf");
	dxfType.setAc("dxf:0004");
	dxfType.setName("link");
	dxfNode.setType(dxfType);

	dxfNode.setId(_id++);	    
	dxfNode.setNs("dip");
	dxfNode.setAc(ln.getAccession());
	dxfNode.setLabel(ln.getAccession());
       
	// create attribute list
	//----------------------

	dxfNode.setAttrList(dof.createNodeTypeAttrList());
	List<edu.ucla.mbi.dxf14.AttrType>
	    atl=dxfNode.getAttrList().getAttr();
	
	log.debug("DipDxfUtil:   adding attributes");

	// evidence
	//---------

	Integer ea=cnt.getDetail().get("evidence-count");
	if(ea!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0018","evidence-count",
					  String.valueOf(ea)));
	}
	
	Integer ei=cnt.getDetail().get("evidence-imex-count");
	if(ei!=null){
	    atl.add(DipDxfUtil.createAttr("dip","dip:0025","evidence-imex-count",
					  String.valueOf(ei)));
	}
	
        log.debug("DipDxfUtil: done:"+dxfNode);
	return dxfNode;

    }
    

    //--------------------------------------------------------------------------
    // List<Counts> DXF representation
    //--------------------------------
    
    public static edu.ucla.mbi.dxf14.NodeType
        dip2dxf( List<Counts> countList, edu.ucla.mbi.dip.Organism org,
                 long id, DxfLevel mode ) {
         
        Log log = LogFactory.getLog(DipDxfUtil.class);
	
        edu.ucla.mbi.dxf14.NodeType dxfNode = null;
        int _id=1;

        if( countList == null ) {
            return null;
        }	
	    
        // create new DXF node
        //--------------------
        
        dxfNode = dof.createNodeType();
	    
        // set node type to 'data-source-report'
        //-------------------------------------
         
        edu.ucla.mbi.dxf14.TypeDefType dxfType = dof.createTypeDefType();
        dxfType.setNs( "dxf" );
        dxfType.setAc( "dxf:0044" );
        dxfType.setName("data-source-report");
         
        dxfNode.setNs( "psi-mi" );
        dxfNode.setAc( "MI:0465" );
        dxfNode.setLabel( "DIP" );
        dxfNode.setName( "The Database of Interacting Proteins" );
        
        if( org != null ) {
            
            // create taxid xref
            //------------------
            
            if(dxfNode.getXrefList() == null ) {
                dxfNode.setXrefList( dof.createNodeTypeXrefList() );
            }
            
            XrefType oXref = DipDxfUtil
                .createXref( "TaxId", String.valueOf( org.getTaxId() ) ,
                             "dxf", "dxf:0024","describes");
            
            NodeType orgNode = dof.createNodeType();
            orgNode.setId(1);
            orgNode.setNs( "TaxId" );
            orgNode.setAc( String.valueOf( org.getTaxId() ) );
            orgNode.setLabel( org.getName() );
            orgNode.setName( org.getCommonName() );
            
            TypeDefType orgType = dof.createTypeDefType();
            orgType.setNs( "dip" );
            orgType.setAc( "dip:0301" );
            orgType.setName( "organism" );
            
            orgNode.setType( orgType );
            oXref.setNode( orgNode );
            dxfNode.getXrefList().getXref().add( oXref );
        }
        
        dxfNode.setId( _id++ );	    
        dxfNode.setType(dxfType);
		
        XrefType xXref = DipDxfUtil.createXref( "psi-mi", "MI:0465" ,
                                                "dxf", "dxf:0024","describes");

        // NOTE: de-hardwire !!!!!!!!!!

        XrefType yXref = DipDxfUtil.createXref( "psi-mi", "MI:0465" ,
                                                "dxf", "dxf:0040","published-by");

        
        if(dxfNode.getXrefList() == null ) {
            dxfNode.setXrefList( dof.createNodeTypeXrefList() );
        }
        dxfNode.getXrefList().getXref().add(xXref);
        dxfNode.getXrefList().getXref().add(yXref);
        
        for( Iterator<Counts> ci = countList.iterator(); ci.hasNext(); ) {
            Counts cc = ci.next();
            NodeType countsNode = DipDxfUtil.dip2dxf( cc, org, _id++, mode ); 
            
            NodeType.PartList.Part dxfReportPart 
                = dof.createNodeTypePartListPart();
            dxfReportPart.setId(_id++);
            dxfReportPart.setName("data source report");
            
            edu.ucla.mbi.dxf14.TypeDefType dxfRPType = dof.createTypeDefType();
            dxfRPType.setNs( "dxf" );
            dxfRPType.setAc( "dxf:0044" );
            dxfRPType.setName( "data-source-report" );
            dxfReportPart.setType( dxfRPType );
            dxfReportPart.setNode( countsNode );
            
            if(dxfNode.getPartList() == null ) {
                dxfNode.setPartList( dof.createNodeTypePartList() );
            }
            
            dxfNode.getPartList().getPart().add( dxfReportPart );
        }

        return dxfNode;
     }

    //--------------------------------------------------------------------------
    //  Counts DXF representation
    //---------------------------

    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf(edu.ucla.mbi.dip.Counts cnt, edu.ucla.mbi.dip.Organism org, 
		long id, DxfLevel mode) {
	
	Log log = LogFactory.getLog(DipDxfUtil.class);
	
	edu.ucla.mbi.dxf14.NodeType dxfNode = null;
	int _id=1;

	if(cnt==null){
	    return null;
	}
	
	// create empty result dataset
	//----------------------------

	// DatasetType res =  dof.createDatasetType();
	    
	// create new DXF node
	//--------------------
	    
	dxfNode = dof.createNodeType();
	    
        // set node type 
	//--------------
        
        TypeDefType dxfType= dof.createTypeDefType();

        dxfType.setNs("dxf");
        dxfType.setAc("dxf:0044");
        dxfType.setName("data-source-report");
        dxfNode.setNs("psi-mi");
        dxfNode.setAc("MI:0465");
        dxfNode.setLabel("DIP");
        dxfNode.setName("The Database of Interacting Proteins");
	
        if( org != null ) {
             
            // create taxid xref
            //------------------
            
            if(dxfNode.getXrefList() == null ) {
                dxfNode.setXrefList( dof.createNodeTypeXrefList() );
            }
            
            XrefType oXref = DipDxfUtil
                .createXref( "TaxId", String.valueOf( org.getTaxId() ) ,
                             "dxf", "dxf:0024","describes");
            
            NodeType orgNode = dof.createNodeType();
            orgNode.setId(1);
            orgNode.setNs( "TaxId" );
            orgNode.setAc( String.valueOf( org.getTaxId() ) );
            orgNode.setLabel( org.getName() );
            orgNode.setName( org.getCommonName() );
            
            TypeDefType orgType = dof.createTypeDefType();
            orgType.setNs( "dip" );
            orgType.setAc( "dip:0301" );
            orgType.setName( "organism" );
            
            orgNode.setType( orgType );
            oXref.setNode( orgNode );
            dxfNode.getXrefList().getXref().add( oXref );
        }
        
	dxfNode.setId(_id++);	    
	dxfNode.setType(dxfType);

        if(dxfNode.getXrefList() == null ) {
            dxfNode.setXrefList(dof.createNodeTypeXrefList());
        }

        XrefType xXref = DipDxfUtil.createXref( "psi-mi", "MI:0465" ,
                                                "dxf", "dxf:0024","describes");
        dxfNode.getXrefList().getXref().add( xXref );

	// create attribute list
	//----------------------

	dxfNode.setAttrList(dof.createNodeTypeAttrList());

	List<edu.ucla.mbi.dxf14.AttrType>
	    atl=dxfNode.getAttrList().getAttr();
	
	log.debug("DipDxfUtil:   adding attributes");

        log.info("DipDxfUtil: ProcessingStatusId="+cnt.getProcessingStatusId()+" ImexSrcId="+cnt.getImexSrcId());

	// - processing status
	
        if ( cnt.getProcessingStatusId() == 1 ) {
            atl.add(DipDxfUtil.createAttr("dip","dip:0048","record-processing-status",
                                          "dip","dip:0046","final"));
        } 

        if ( cnt.getProcessingStatusId() == 97 ) {
            atl.add(DipDxfUtil.createAttr("dip","dip:0048","record-processing-status",
                                          "dip","dip:0045","provisional"));
        } 


        if( cnt.getImexSrcId() == 0 || cnt.getImexSrcId() == 1) { // DIP
            //if( dxfNode.getXrefList() == null ) {
            //    dxfNode.setXrefList(dof.createNodeTypeXrefList());
            //}
            XrefType sXref =
                DipDxfUtil.createXref("psi-mi", "MI:0465",
                                      "dxf", "dxf:0040","published-by");
            dxfNode.getXrefList().getXref().add( sXref );

        }

        if( cnt.getImexSrcId() == 2 ) { // IntAct
            //if( dxfNode.getXrefList() == null ) {
            //    dxfNode.setXrefList(dof.createNodeTypeXrefList());
            // }
            XrefType sXref =
                DipDxfUtil.createXref("psi-mi", "MI:0469",
                                      "dxf", "dxf:0040","published-by");
            dxfNode.getXrefList().getXref().add( sXref );

        }

        if( cnt.getImexSrcId() == 3 ) { // MINT
            //if( dxfNode.getXrefList() == null ) {
            //    dxfNode.setXrefList(dof.createNodeTypeXrefList());
            //}
            XrefType sXref =
                DipDxfUtil.createXref("psi-mi", "MI:0471",
                                      "dxf", "dxf:0040","published-by");
            dxfNode.getXrefList().getXref().add( sXref );
        }

        // - proteins
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0016","protein-count",
				      String.valueOf(cnt.getProteinCount())));
	
	// - interactions
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0017","interaction-count",
				      String.valueOf(cnt.getInteractionCount())));
	
	// - evidence
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0018","evidence-count",
				      String.valueOf(cnt.getEvidenceCount())));
	// - imex evidence
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0049","imex-evidence-count",
				      String.valueOf(cnt.getImexEvidenceCount())));
	// - inference
	
	atl.add( DipDxfUtil.createAttr("dip","dip:0041","inference-count",
                                       String.valueOf(cnt.getAutoInferenceCount())));
	// - author inference
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0042","author-inference-count",
				      String.valueOf(cnt.getAuthorInferenceCount())));
	
	// - sources/papers
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0020","source-count",
				      String.valueOf(cnt.getSourceCount())));

	atl.add(DipDxfUtil.createAttr("dip","dip:0050","imex-source-count",
				      String.valueOf(cnt.getImexSourceCount())));
	
	if( cnt.getTaxid() == 0 ) {
	    // species count: only when database stats
            log.debug("species-count:" + cnt.getSpeciesCount() );
	    atl.add(DipDxfUtil.createAttr( "dip", "dip:0019", "species-count",
                                           String.valueOf( cnt.getSpeciesCount() ) ) );
	}

        log.debug("DipDxfUtil: done:"+dxfNode);
	return dxfNode;
    }
    
    /*
    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf(edu.ucla.mbi.dip.CountsFull cnt, edu.ucla.mbi.dip.DipNode nde, 
		long id, DxfLevel mode) {
	
	Log log = LogFactory.getLog(DipDxfUtil.class);
	
	edu.ucla.mbi.dxf14.NodeType dxfNode = null;
	int _id=1;

	if(cnt==null){
	    return null;
	}
	
	// create empty result dataset
	//----------------------------

	DatasetType res =  dof.createDatasetType();
	    
	// create new DXF node
	//--------------------
	    
	dxfNode= dof.createNodeType();
	    
	edu.ucla.mbi.dxf14.TypeDefType   
	    dxfType= dof.createTypeDefType();
	    
	// set node type to 'database/species/etc'
	//----------------------------------------
	
	if(cnt.getTaxid()==0){
	    dxfType.setNs("dxf");
	    dxfType.setAc("dxf:0016");
	    dxfType.setName("data-source");
	    dxfNode.setNs("psi-mi");
	    dxfNode.setAc("MI:0465");
	    dxfNode.setLabel("DIP");
	    dxfNode.setName("The Database of Interacting Proteins");
	} else {
	    dxfType.setNs("dip");
	    dxfType.setAc("dip:0301");
	    dxfType.setName("organism");
	    dxfNode.setNs("TaxId");
	    dxfNode.setAc(String.valueOf(cnt.getTaxid()));
	    //dxfNode.setLabel(org.getName()); 
	    //dxfNode.setName(org.getCommonName());
	    
	    // create taxid xref
	    //------------------

	    dxfNode.setXrefList(dof.createNodeTypeXrefList());

	    XrefType xXref =
		DipDxfUtil.createXref("TaxId", String.valueOf(cnt.getTaxid()),
				      "dxf", "dxf:0009","identical-to");
	    dxfNode.getXrefList().getXref().add(xXref);
	}
	
	dxfNode.setId(_id++);	    
	dxfNode.setType(dxfType);
		
	// create attribute list
	//----------------------

	dxfNode.setAttrList(dof.createNodeTypeAttrList());
	List<edu.ucla.mbi.dxf14.AttrType>
	    atl=dxfNode.getAttrList().getAttr();
	
	log.debug("DipDxfUtil:   adding attributes");

	// - proteins
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0016","protein-count",
				      String.valueOf(cnt.getProteinCount())));
	
	// - interactions
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0017","interaction-count",
				      String.valueOf(cnt.getInteractionCount())));
	
	// - evidence
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0018","evidence-count",
				      String.valueOf(cnt.getEvidenceCount())));
	
	// - sources/papers
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0020","source-count",
				      String.valueOf(cnt.getSourceCount())));
	
	if(cnt.getTaxid()==0){
	    // species count: only when database stats

	    atl.add(DipDxfUtil.createAttr("dip","dip:0019","species-count",
					  String.valueOf(cnt.getSpeciesCount())));
	}

        log.debug("DipDxfUtil: done:"+dxfNode);
	return dxfNode;
    }
    */
    
    //--------------------------------------------------------------------------
    // Link DXF representation 
    //------------------------
    
    public static edu.ucla.mbi.dxf14.NodeType 
        dip2dxf( edu.ucla.mbi.dip.Link dipLink, long id, DxfLevel mode) {
        
	return dip2dxf( dipLink, null, id, mode );
    }
    
    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf(edu.ucla.mbi.dip.Link dipLink, 
		List<edu.ucla.mbi.dip.CvType> linkType,
		long id, DxfLevel mode) {
	
	Log log = LogFactory.getLog( DipDxfUtil.class );

	edu.ucla.mbi.dxf14.NodeType dxfNode = null;
	
	int _id=1;

	if( dipLink != null ){
	      
	    // create empty result dataset
	    //----------------------------
	    
	    // DatasetType res = dof.createDatasetType();
	    
	    // create new DXF node
	    //--------------------
	    
	    dxfNode= dof.createNodeType();
	    
	    edu.ucla.mbi.dxf14.TypeDefType   
		dxfType= dof.createTypeDefType();
	    
	    // set node type to 'link'
	    //------------------------
	    
	    dxfType.setNs("dxf");
	    dxfType.setAc("dxf:0004");
	    dxfType.setName("link");
	    
	    dxfNode.setId(_id++);
	    dxfNode.setNs("dip");
	    dxfNode.setAc(dipLink.getAccession());
	    
	    dxfNode.setType(dxfType);
	    
	    log.debug("DipDxfUtil:   label = " + dipLink.getAccession());
	    log.debug("DipDxfUtil:   name  = " + dipLink.getName());
	    log.debug("DipDxfUtil:   dnc   = " + dipLink.getDNC());
	    
	    if(dipLink.getQuality()!=null){
		log.debug( "DipDxfUtil:   quality = " 
                           + dipLink.getQuality().getName());
	    }
	    log.debug( "DipDxfUtil:   #lnodes = " 
                       + dipLink.getLnode().size());
	    
	    // set label
	    //----------
	    dxfNode.setLabel(dipLink.getAccession());
	    
	    // set name/description
	    //---------------------
	    
	    if(dipLink.getName()!=null &&
	       dipLink.getName().length()>0){
		dxfNode.setName(dipLink.getName());
	    }
	    
	    if( dipLink.getEvidence() != null &&
	        dipLink.getEvidence().size() > 0 ){
                
                //--------------------------------------------------------------
		// create evidence xref list
		//--------------------------
		
		dxfNode.setXrefList( dof.createNodeTypeXrefList() );
                
		for( Iterator i = dipLink.getEvidence().iterator(); 
                     i.hasNext(); ){
		    
		    Evidence iX= (Evidence)i.next();
		    
		    log.debug("DipDxfUtil:     adding: " + iX.getAccession());
                    
		    XrefType xXref = DipDxfUtil
                        .createXref( "dip", iX.getAccession(),
                                     "dxf", "dxf:0008", "supported-by" );
		    
		    if( mode.compareTo( DxfLevel.SHRT )>=0 ){
			edu.ucla.mbi.dxf14.NodeType evNode
			    = DipDxfEvidUtil.dip2dxf( iX, 1, mode, false);
			xXref.setNode( evNode );
		    }
		    dxfNode.getXrefList().getXref().add( xXref );
		}
	    }

            log.debug( "DipDxfUtil: inference(s)=" + dipLink.getInference() );
            
	    if( dipLink.getInference() != null &&
                dipLink.getInference().size() > 0 ){
		
                log.debug( "DipDxfUtil: inference(s) #" 
                          + dipLink.getInference().size() );

                //--------------------------------------------------------------
		// create inference xref list (if needed)
		//---------------------------------------
		
		if( dxfNode.getXrefList() == null ){
		    dxfNode.setXrefList( dof.createNodeTypeXrefList() );
		}
		
		for( Iterator i = dipLink.getInference().iterator();
                     i.hasNext(); ){
		    
		    Inference iInf = (Inference)i.next();
		    Evidence iX = iInf.getGrounds();
		    
		    log.debug("DipDxfUtil:     adding: "+iX.getAccession());
		    
                    // NOTE: possibly modify type to "entailed-by" ? LS

		    XrefType xXref = DipDxfUtil
                        .createXref( "dip", iX.getAccession(),
                                     "dxf", "dxf:0008", "supported-by" );  
		    
		    if( mode.compareTo( DxfLevel.SHRT ) >=0 ){
			edu.ucla.mbi.dxf14.NodeType evNode
			    = DipDxfEvidUtil.dip2dxf( iInf, 1, mode, false );
			xXref.setNode( evNode );
		    }
		    dxfNode.getXrefList().getXref().add( xXref );
		}
	    }
            
	    if( linkType!=null ||
                (dipLink.getQuality()!=null && 
                 dipLink.getQuality().getName().equals("t"))
                ){
		
		// create attribute list
		//----------------------
		
		dxfNode.setAttrList(dof.createNodeTypeAttrList());
		
		log.debug("DipDxfUtil:   adding attributes");
		
                List<AttrType> atl = dxfNode.getAttrList().getAttr(); 
                
		// add attributes:
		//----------------
		
		//  - core
		
		if( dipLink.getQuality()!=null ){
		    
		    if(dipLink.getQuality().getName().startsWith("t")){
			AttrType qstAtt =
			    DipDxfUtil.createAttr("dip", "dip:0304",
						  "quality-status",
						  "dip","dip:0305","core");
			atl.add(qstAtt);
			
			AttrType qscAtt =
			    DipDxfUtil.createAttr("dip", "dip:0026",
						  "quality-score","0");
			atl.add(qscAtt);
		    }
                                                                           
		    if(dipLink.getQuality().getName().startsWith("f")){
			AttrType qstAtt =
			    DipDxfUtil.createAttr("dip", "dip:0304",
						  "quality-status",
						  "dip","dip:0025","non-core");
			atl.add(qstAtt);

			AttrType qscAtt =
			    DipDxfUtil.createAttr("dip", "dip:0026",
						  "quality-score","-3");
			atl.add(qscAtt);
		    }
		}
		
		
                // NOTE: depreciated ?

		if( linkType != null ){
		    for(Iterator<CvType> lti=linkType.iterator();lti.hasNext();){
			CvType cct=lti.next();
			AttrType ltAtt =
			    DipDxfUtil.createAttr("dip", "dip:0001","link-type",
						  cct.getNs(),cct.getAc(),cct.getName());
			atl.add(ltAtt);
			
		    }
		}
		
                // NOTE: add cvIntCoreType ?
                
                log.debug( "CvIntType: " + dipLink.getCvIntType() );

                if( dipLink.getCvIntType() != null ){
                    CvTerm itp= dipLink.getCvIntType();

                    log.debug( "CvNs: " + itp.getCvNs() + " CvId: " + itp.getCvId() + " Name: " + itp.getName() );

                    AttrType att =
                        DipDxfUtil.createAttr( "dip", "dip:0001", "link-type",
                                               itp.getCvNs(), itp.getCvId(),
                                               itp.getName() );
                    atl.add(att);
                }

                if( dipLink.getEvidenceCount() > 0 ){
                    atl.add( DipDxfUtil
                             .createAttr( "dip", "dip:0018", "evidence-count",
                                          String.valueOf( dipLink.getEvidenceCount())));
                }
                
                if( dipLink.getImexEvidenceCount() > 0 ){
                    atl.add( DipDxfUtil
                             .createAttr( "dip", "dip:0049", "imex-evidence-count",
                                          String.valueOf( dipLink.getImexEvidenceCount())));
                }
                
                if( dipLink.getHtpEvidenceCount() > 0 ){
                    atl.add( DipDxfUtil
                             .createAttr( "dip", "dip:0051", "htp-evidence-count",
                                          String.valueOf( dipLink.getHtpEvidenceCount())));
                }
                
                if( dipLink.getInferenceCount() > 0 ){
                    atl.add( DipDxfUtil
                             .createAttr( "dip", "dip:0041", "inference-count",
                                          String.valueOf( dipLink.getInferenceCount())));
                }

                if( dipLink.getHtpInferenceCount() > 0 ){
                    atl.add( DipDxfUtil
                             .createAttr( "dip", "dip:0052", "htp-inference-count",
                                          String.valueOf( dipLink.getHtpInferenceCount())));
                }

	    }
	    
	    // add linked nodes
	    //-----------------
            
	    if(dipLink.getLnode().size()>0){
		
		if( dxfNode.getPartList()==null ){
		    dxfNode.setPartList(dof.createNodeTypePartList());
		}
		
		log.debug("DipDxfUtil:    building node list...");
		
		int  _pid=1;

		for(Iterator i = dipLink.getLnode().iterator();i.hasNext();){
		    
		    LNode iN= (LNode)i.next();
		    
		    DipNode dipNode=iN.getNode();
		    
		    log.debug("DipDxfUtil:     adding: "+iN.getAccession());
		    
		    edu.ucla.mbi.dxf14.NodeType.PartList.Part 
			dxfIPart = dof.createNodeTypePartListPart();
		    
		    //edu.ucla.mbi.dxf14.PartType  
		    //    dxfIPart = dof.createPartType();
		    
		    DxfLevel ndMode = mode.compareTo(DxfLevel.BASE)>0 ? DxfLevel.BASE : mode;
		    
		    dxfIPart.setNode(DipDxfUtil.dip2dxf(dipNode,_id++,ndMode));
		    dxfIPart.setId(_id++);
		    
		    // set lnode type to 'linked-node'
		    //-------------------------------
		    
		    edu.ucla.mbi.dxf14.TypeDefType   
			iNType = new edu.ucla.mbi.dxf14.TypeDefType();
		    
		    iNType.setNs("dxf");
		    iNType.setAc("dxf:0010");
		    iNType.setName("linked-node");
		    
		    dxfIPart.setType(iNType);
		    
		    if(iN.getLabel()!=null && iN.getLabel().length()>0){
			dxfIPart.setName(iN.getLabel()+"{A}");
		    } else{
			dxfIPart.setName(iN.getNode().getLabel()+"{A}");
		    }
		    dxfNode.getPartList().getPart().add(dxfIPart);
		}
	    }
	}
        log.debug("DipDxfUtil: done:"+dxfNode);
	return dxfNode;
    }

    public static long dxfAddXref(edu.ucla.mbi.dxf14.NodeType dxfNode,
				  String ns, String ac, 
				  String typeNs, String typeAc, String type) {
            
	return DipDxfUtil.dxfAddXref(dxfNode,
				      ns, ac, 
				      typeNs, typeAc, type, null);
    } 

    public static long dxfAddXref(edu.ucla.mbi.dxf14.NodeType dxfNode,
				  String ns, String ac, 
				  String typeNs, String typeAc, String type,
				  edu.ucla.mbi.dxf14.NodeType dxfXrefNode) {

	Log log = LogFactory.getLog(DipDxfUtil.class);
	
	// add xref to the target node
	//----------------------------
	    
	// create xref list
	//-----------------
	
	if(dxfNode.getXrefList()==null){
	    dxfNode.setXrefList(dof.createNodeTypeXrefList());
	}
	
	// create xref
	//------------
	
	XrefType nXref = DipDxfUtil.createXref(ns, ac, typeNs, typeAc,type);
	
	if(dxfXrefNode!=null){
	       
	    // set target node 
	    //----------------
	    
	    nXref.setNode(dxfXrefNode);  
	    log.debug("DipDxfUtil:    dxfAddXref:  xref node added...");
	    
	}
	
	log.debug("DipDxfUtil:    dxfAddXref:  xref added...");
	dxfNode.getXrefList().getXref().add(nXref);
	
	return 1L;
    }
 
    
    //    dxfAddXref(dxfNode,
    //	       "pubmed",String.valueOf(dAcc),   // ns , ac
    //	       "dxf","dxf:0019","describes",    // typeNs,typeAc,type
    //	       dAccdip2dxf(cDipNode,maxId++,mode));


    
    public static long dxfMod(edu.ucla.mbi.dxf14.NodeType dxfNode, 
			      edu.ucla.mbi.dip.Link dipLink, 
			      DxfLevel mode, long maxId) {
	
	return dxfMod(dxfNode,dipLink,mode,maxId,"","");
    }

    public static long dxfMod(edu.ucla.mbi.dxf14.NodeType dxfNode, 
			      edu.ucla.mbi.dip.Link dipLink, 
			      DxfLevel mode, long maxId,
			      String qns, String qac) {
	
	Log log = LogFactory.getLog(DipDxfUtil.class);
	long _id=maxId;
	
	if(dipLink!=null){
	    
	    // reset node type to 'link'
	    //-------------------------
	    
	    edu.ucla.mbi.dxf14.TypeDefType   
		dxfType= dof.createTypeDefType();
		
	    dxfType.setNs("dxf");
	    dxfType.setAc("dxf:0004");
	    dxfType.setName("link");
	    
	    // dxfNode.setId(_id++);
	    dxfNode.setNs("dip");
	    dxfNode.setAc(dipLink.getAccession());
	    
	    dxfNode.setType(dxfType);
	    
	    log.debug("DipDxfUtil:   label   = "+dipLink.getAccession());
	    log.debug("DipDxfUtil:   name    = "+dipLink.getName());
	    log.debug("DipDxfUtil:   dnc     = "+dipLink.getDNC());
	    
	    if(dipLink.getQuality()!=null){
		log.debug("DipDxfUtil:   quality = "
			  + dipLink.getQuality().getName());
	    }
	    log.debug("DipDxfUtil:   #lnodes = "
		      + dipLink.getLnode().size());
	    
	    // set label
	    //----------
	    dxfNode.setLabel(dipLink.getAccession());
	    
	    // set name/description
	    //---------------------
	    
	    if(dipLink.getName()!=null &&
	       dipLink.getName().length()>0){
		dxfNode.setName(dipLink.getName());
	    }		
	    
	    if(dipLink.getEvidence()!=null &&
	       dipLink.getEvidence().size()>0){
		
		// create (evidence) xref list
		//----------------------------
		
		dxfNode.setXrefList(dof.createNodeTypeXrefList());
		
		for(Iterator i = dipLink.getEvidence().iterator();i.hasNext();){
		    
		    Evidence iX= (Evidence)i.next();
		    
		    log.debug("DipDxfUtil:     adding: "+iX.getAccession());
		    
		    XrefType xXref =
			DipDxfUtil.createXref("dip", iX.getAccession(),
					      "dxf", "dxf:0008","supported-by");
		    
		    if(mode.compareTo(DxfLevel.FULL)>0){
			edu.ucla.mbi.dxf14.NodeType evNode
			    = DipDxfEvidUtil.dip2dxf(iX, 222, DxfLevel.SHRT, false);
			xXref.setNode(evNode);
		    }
		    dxfNode.getXrefList().getXref().add(xXref);
		}
	    }
	    
	    if(dipLink.getQuality()!=null && 
	       dipLink.getQuality().getName().equals("t")){
		
		// create attribute list
		//----------------------
		
		dxfNode.setAttrList(dof.createNodeTypeAttrList());
		
		log.debug("DipDxfUtil:   adding attributes");
		
		// add attributes:
		//----------------
		
		//  - core
		
		AttrType qstAtt =
		    DipDxfUtil.createAttr("dip", "dip:0304",
					  "quality-status",
					  "dip","dip:0304","core");
		dxfNode.getAttrList().getAttr().add(qstAtt);
	    }
	    
	    
	    // re linked nodes
	    //-----------------
            
	    if(dipLink.getLnode().size()>0){
		
		dxfNode.setPartList(dof.createNodeTypePartList());
		
		log.debug("DipDxfUtil:    building node list...");
		
		for(Iterator i = dipLink.getLnode().iterator();i.hasNext();){
		    
		    LNode iN= (LNode)i.next();
		    
		    DipNode dipNode=iN.getNode();
		    
		    log.debug("DipDxfUtil:     adding: "+iN.getAccession());
		    
		    edu.ucla.mbi.dxf14.NodeType.PartList.Part
			dxfIPart = dof.createNodeTypePartListPart();
		    
		    //			edu.ucla.mbi.dxf14.PartType
		    //                            dxfIPart = objFactory.createPartType();
		    
		    dxfIPart.setNode(DipDxfUtil.dip2dxf(dipNode,_id++,DxfLevel.SHRT));  // LS ???
		    dxfIPart.setId(_id++);
		    
		    // set lnode type to 'linked-node'
		    //-------------------------------
		    
		    edu.ucla.mbi.dxf14.TypeDefType   
			iNType = new edu.ucla.mbi.dxf14.TypeDefType();
		    
		    iNType.setNs("dxf");
		    iNType.setAc("dxf:0010");
		    iNType.setName("linked-node");
		    
		    dxfIPart.setType(iNType);
		    
		    if(iN.getLabel()!=null && iN.getLabel().length()>0){
			dxfIPart.setName(iN.getLabel()+"{A}");
		    } else{
			dxfIPart.setName(iN.getNode().getLabel()+"{A}");
		    }
		    dxfNode.getPartList().getPart().add(dxfIPart);
		}
	    }
	    
	    if(!qns.equals("") && !qac.equals("")){
		if(dxfNode.getXrefList()==null){
		    dxfNode.setXrefList(dof.createNodeTypeXrefList());
		}
		
		XrefType qXref =
		    DipDxfUtil.createXref(qns, qac,
					  "dxf", "dxf:0019","queried-as");
		dxfNode.getXrefList().getXref().add(qXref);
	    }
	}
	return _id++;
    }

    
    /*
    public static edu.ucla.mbi.dxf14.NodeType
	dip2dxf(DipNode dipNode, long id, DxfLevel mode){
	return DipDxfUtil.dip2dxf(dipNode, id, mode,"","");  // self as query ns/ac
    }
    
    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf(DipNode dipNode, long id, 
		DxfLevel mode, String qns, String qac) {

	Log log = LogFactory.getLog(DipDxfUtil.class);
	edu.ucla.mbi.dxf14.NodeType dxfNode = null;
	
	if(dipNode!=null){     
	    
	    log.debug("DipDxfUtil:   dip2dxf(node)");  
	    log.debug("DipDxfUtil:    AC="+dipNode.getAccession());  
	    
	    // create new DXF node & node type elements
	    //-----------------------------------------
		    
	    dxfNode = dof.createNodeType();
		
	    // set node type 
	    //--------------
		
	    edu.ucla.mbi.dxf14.TypeDefType 
		    dxfType= dof.createTypeDefType();
	    
	    CvType cvt = dipNode.getCvType();
	    
	    dxfType.setNs(cvt.getNs());
	    dxfType.setAc(cvt.getAc());
	    dxfType.setName(cvt.getName());
	    
	    dxfNode.setId(id);
	    dxfNode.setNs("dip");
	    dxfNode.setAc(dipNode.getAccession());
	    
	    dxfNode.setType(dxfType);
	    
	    log.debug("DipDxfUtil:   label="+dipNode.getLabel());
	    log.debug("DipDxfUtil:   name ="+dipNode.getName());
	    
	    // set label
	    //----------
	      
	    dxfNode.setLabel(dipNode.getLabel());
		
	    // create xref list
	    //-----------------
	    dxfNode.setXrefList(dof.createNodeTypeXrefList());
	    
	    // add xrefs
	    //----------
	    
	    //   Taxon Id/Organism
	    //--------------------
	    log.debug("DipDxfUtil:   taxid ="+dipNode.getTaxId());
	    
	    XrefType oXref = 
		DipDxfUtil.createXref("TaxId", 
				      String.valueOf(dipNode.getTaxId()),
				      "dxf", "dxf:0007","produced-by");
	    
	    if(mode.compareTo(DxfLevel.STUB)>0){ // all but STUB mode
		
		edu.ucla.mbi.dxf14.NodeType dxfOrganism = null;
		dxfOrganism = dof.createNodeType();
		
		edu.ucla.mbi.dxf14.TypeDefType
		    dxfOrgType= dof.createTypeDefType();

		dxfOrgType.setNs("dip");
		dxfOrgType.setAc("dip:0301");
		dxfOrgType.setName("organism");
		
		dxfOrganism.setType(dxfOrgType);
		
		dxfOrganism.setNs("TaxId");
		dxfOrganism.setAc(String.valueOf(dipNode.getTaxId()));
		dxfOrganism.setId(123);
		dxfOrganism.setLabel(dipNode.getOrganism().getName());
		dxfOrganism.setName(dipNode.getOrganism().getCommonName());
		
		// set organism mode
		//------------------
		
		oXref.setNode(dxfOrganism);
	    }
	    
	    log.debug("DipDxfUtil:    adding taxid...");
		
	    dxfNode.getXrefList().getXref().add(oXref);
	    
	    if(mode.compareTo(DxfLevel.STUB)>0){ // all but STUB mode
		    
		// set name/description
		//---------------------
		dxfNode.setName(dipNode.getName());
		
		// xrefs to refseq/uniprot
		//------------------------
		
		String up= dipNode.getUniProt();
		String rs= dipNode.getRefSeq();
		String eg= dipNode.getEntrezGene();
		
		if(up!=null && up.length()>0){
		    
		    XrefType upXref = 
			DipDxfUtil.createXref("uniprot", up,
					      "dxf", "dxf:0006","instance-of");
		    
		    log.debug("DipDxfUtil:    adding uniprot: "+up);
		    dxfNode.getXrefList().getXref().add(upXref);
		}
		
		if(rs!=null && rs.length()>0){
		    
		    XrefType rsXref = 
			DipDxfUtil.createXref("refseq", rs,
					      "dxf", "dxf:0006","instance-of");
		    
		    log.debug("DipDxfUtil:    adding refseq: "+rs);
		    dxfNode.getXrefList().getXref().add(rsXref);
		}

		if(eg!=null && eg.length()>0){
		    
		    XrefType egXref = 
			DipDxfUtil.createXref("entrezgene", eg,
					      "dxf", "dxf:0022","encoded-by");
		    
		    log.debug("DipDxfUtil:    adding entrezgene: "+eg);
		    dxfNode.getXrefList().getXref().add(egXref);
		}
		
		String seq= dipNode.getSequence();
		
		if(seq!=null && seq.length()>0){
		    
		    dxfNode.setAttrList(dof.createNodeTypeAttrList());
		    AttrType seqAtt =
			DipDxfUtil.createAttr("dip", "dip:0008",
					      "sequence",
					      seq);
		    
		    dxfNode.getAttrList().getAttr().add(seqAtt);
		}
	    }
	    
	    if(mode.compareTo(DxfLevel.MIT)==0){  //mit
		
		// MiTap 
		//------
 
		Set xs= dipNode.getXrefs();
		
		if(xs!=null && xs.size()>0){
		    
		    for(Iterator xi=xs.iterator();xi.hasNext();){ 
			
			NodeCrossRef xc=(NodeCrossRef)xi.next(); 
			
			XrefType xcXref =
			    DipDxfUtil.createXref(xc.getSrcDb().getName(), 
						  xc.getAcc(),
						  "dxf", "dxf:0006","instance-of");
			
			log.debug("DipDxfUtil:    adding xref: "+xc.getAcc());
			dxfNode.getXrefList().getXref().add(xcXref);
		    }
		}
	    }
	    
	    if(!qns.equals("") && !qac.equals("")){
		XrefType xcXref =
		    DipDxfUtil.createXref(qns, qac,
					  "dxf", "dxf:0019","queried-as");
		
		log.debug("DipDxfUtil:    adding queried-as xref: "+qac);
		dxfNode.getXrefList().getXref().add(xcXref);    
	    }
	}
	return dxfNode;
    }
    */

    public static void dxfMod(edu.ucla.mbi.dxf14.NodeType dxfNode, 
			      DipNode dipNode, DxfLevel mode) {
	DipDxfUtil.dxfMod(dxfNode, dipNode, mode,"","");
    }
    
    public static void dxfMod(edu.ucla.mbi.dxf14.NodeType dxfNode, 
			      DipNode dipNode, DxfLevel mode,
			      String qns, String qac) {

	Log log = LogFactory.getLog(DipDxfUtil.class);
	if(dipNode!=null){     
	    
	    // set node type 
	    //--------------
	    
	    edu.ucla.mbi.dxf14.TypeDefType 
		dxfType= dof.createTypeDefType();
	    
	    CvType cvt = dipNode.getCvType();
	    
	    dxfType.setNs(cvt.getNs());
	    dxfType.setAc(cvt.getAc());
	    dxfType.setName(cvt.getName());
	    
	    //dxfNode.setId(id);
	    dxfNode.setNs("dip");
	    dxfNode.setAc(dipNode.getAccession());
	    
	    dxfNode.setType(dxfType);
	    
	    log.debug("DipDxfUtil:   label="+dipNode.getLabel());
	    log.debug("DipDxfUtil:   name ="+dipNode.getName());
	    
	    // set label
	    //----------
	    
	    dxfNode.setLabel(dipNode.getLabel());
	    
	    // create xref list
	    
	    dxfNode.setXrefList(dof.createNodeTypeXrefList());
	    
	    // add xrefs
	    //----------
	    
	    //   Taxon Id/Organism
	    //--------------------
	    log.debug("DipDxfUtil:   taxid ="+dipNode.getTaxId());
	    
	    XrefType oXref = 
		DipDxfUtil.createXref("TaxId", 
				      String.valueOf(dipNode.getTaxId()),
				      "dxf", "dxf:0007","produced-by");
	    
	    if(mode.compareTo(DxfLevel.STUB)>0){ // all but STUB mode
		
		edu.ucla.mbi.dxf14.NodeType dxfOrganism = null;
		dxfOrganism = dof.createNodeType();
		
		edu.ucla.mbi.dxf14.TypeDefType
		    dxfOrgType= dof.createTypeDefType();
		
		dxfOrgType.setNs("dip");
		dxfOrgType.setAc("dip:0301");
		dxfOrgType.setName("organism");
		
		dxfOrganism.setType(dxfOrgType);
		
		dxfOrganism.setNs("TaxId");
		dxfOrganism.setAc(String.valueOf(dipNode.getTaxId()));
		dxfOrganism.setId(123);
		dxfOrganism.setLabel(dipNode.getOrganism().getName());
		dxfOrganism.setName(dipNode.getOrganism().getCommonName());
		
		// set organism mode
		//------------------
		
		oXref.setNode(dxfOrganism);
	    }
	    
	    log.debug("DipDxfUtil:    adding taxid...");
	    
	    dxfNode.getXrefList().getXref().add(oXref);
  
	    if(mode.compareTo(DxfLevel.STUB)>0){ // all but STUB mode
		
		// set name/description
		//---------------------
		dxfNode.setName(dipNode.getName());
		
		// xrefs to refseq/uniprot
		//------------------------
		
		String up= dipNode.getUniProt();
		String rs= dipNode.getRefSeq();
		
		if(up!=null && up.length()>0){
		    
		    XrefType upXref = 
			DipDxfUtil.createXref("UniProt", up,
					      "dxf", "dxf:0006","instance-of");
		    
		    log.debug("DipDxfUtil:    adding uniprot: "+up);
		    dxfNode.getXrefList().getXref().add(upXref);
		}
		
		if(rs!=null && rs.length()>0){
		    
		    XrefType rsXref = 
			DipDxfUtil.createXref("RefSeq", rs,
					      "dxf", "dxf:0006","instance-of");
		    
		    log.debug("DipDxfUtil:    adding refseq: "+rs);
		    dxfNode.getXrefList().getXref().add(rsXref);
		}
		
		String seq= dipNode.getSequence();

		if(seq!=null && seq.length()>0){
		    
		    dxfNode.setAttrList(dof.createNodeTypeAttrList());
		    AttrType seqAtt =
			DipDxfUtil.createAttr("dip", "dip:0008",
					      "sequence",
					      seq);
		    
		    dxfNode.getAttrList().getAttr().add(seqAtt);
		}
	    }
	    
	    if(!qns.equals("") && !qac.equals("")){
		if(dxfNode.getXrefList()==null){
		    dxfNode.setXrefList(dof.createNodeTypeXrefList());
		}
		
		XrefType qXref =
		    DipDxfUtil.createXref(qns, qac,
					  "dxf", "dxf:0019","queried-as");
		dxfNode.getXrefList().getXref().add(qXref);
	    }
	}	
    }


    //--------------------------------------------------------------------------
    // xref 
    //-----
        
    public static XrefType createXref(String ns, String ac,
				      String tNs, String tAc, String tName){
	
	XrefType xref = dof.createXrefType();
	
	xref.setNs(ns);
	xref.setAc(ac);
	
	xref.setTypeNs(tNs);
	xref.setTypeAc(tAc);
	xref.setType(tName);

       	return xref;
    }
    
    //--------------------------------------------------------------------------
    // CV attribute
    //-------------
    
    public static AttrType createAttr( String aNs, String aAc, String aName,
                                       String vNs, String vAc, String value ) {
	
	AttrType attr = dof.createAttrType();
	
	attr.setNs( aNs );
	attr.setAc( aAc );
	attr.setName( aName );
	
	AttrType.Value 
	    val = dof.createAttrTypeValue();
		
	if( vNs!=null && vAc!=null && 
            vNs.length()>0 && vAc.length()>0 ) {
	    
	    val.setNs( vNs );
	    val.setAc( vAc );
	    val.setValue( value );
	}
	
	attr.setValue(val);
	return attr;
    }

    //--------------------------------------------------------------------------
    // free-valued attribute
    //----------------------
  
    public static AttrType createAttr(String aNs, String aAc, String aName,
				      String value){
		
	AttrType attr = dof.createAttrType();
	
	attr.setNs(aNs);
	attr.setAc(aAc);
	attr.setName(aName);
	
	AttrType.Value 
	    val = dof.createAttrTypeValue();
	
	if(value!=null && value.length()>0){
	    val.setValue(value);
	}
	
	attr.setValue(val);
	return attr;
    }
}
