package edu.ucla.mbi.dip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.text.NumberFormat;

import edu.ucla.mbi.dip.*;
import edu.ucla.mbi.dxf14.*;
import edu.ucla.mbi.orm.*;

import org.compass.core.*;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;

public class DipDxfRecord {
    
    static NodeDAO nDAO = null;
    static LNodeDAO lnDAO = null;
    static OrganismDAO oDAO = null;
    static CvtDAO cvtDAO = null;
    static CvTermDAO cvTermDAO = null;
    static LinkDAO lDAO = null;
    static EvidDAO xDAO = null;
    static InferDAO infDAO = null;
    static DataSrcDAO sDAO = null;
    static ImexSRecDAO imexDAO = null;
    static DipSRecDAO dipSRecDAO = null;

    static DipDataSrcDAO ddsDAO = null;
    static CrossRefDAO crDAO = null;
    static CountsDAO dcDAO = null;

    static edu.ucla.mbi.dxf14.ObjectFactory dof
        = new edu.ucla.mbi.dxf14.ObjectFactory();
    
    //--------------------------------------------------------------------------

    public static edu.ucla.mbi.dxf14.DatasetType
        getNode( String ns, String ac, String detail ) {
        
        Log log = LogFactory.getLog( DipDxfRecord.class );
        log.debug( "getNode called" );
        log.debug( "ns= " + ns + " ac=" + ac + " det=" + detail );

        DatasetType res = dof.createDatasetType();
        
        if( ns == null || ac == null ){ return res; }
        
        if( nDAO == null ) {
            log.debug( "DipSOAP:   Creating NodeDAO..." );
            nDAO = new NodeDAO();
            log.debug( "DipSOAP:   ...done" );
        }
        
        DipNode dnode = null;
        
        if( ns.equals("dip") ){
            dnode = nDAO.find( new Integer( ac.replaceAll( "[^0-9]", "" ) ) );
        }       
        if( ns.equals( "refseq") ){
            List dnl = nDAO.findByRefSeq( ac );
            
            if( dnl != null ){
                for( Iterator i = dnl.iterator(); i.hasNext(); ){
                    DipNode cn = (DipNode) i.next();
                    res.getNode()
                        .add( DipDxfUtil
                              .dip2dxf( cn, 1L,
                                        DxfLevel.fromString( detail ) ) );
                }
            }
        }       
        
        if( ns.toLowerCase().equals( "uniprot" ) 
            || ns.toLowerCase().equals("uniprotkb") ){
            
            log.debug("findByUniProt: up="+ac);
            
            List dnl = nDAO.findByUniProt( ac );
            
            if( dnl != null ){
                
                log.debug("findByUniProt: count=" + dnl.size());
                
                for( Iterator i = dnl.iterator(); i.hasNext(); ){
                    DipNode cn = (DipNode) i.next();
                    res.getNode()
                        .add( DipDxfUtil
                              .dip2dxf( cn, 1L,
                                        DxfLevel.fromString( detail ) ) );
                }
            }
        }
        
        if( dnode != null ) {
            res.getNode()
                .add( DipDxfUtil.dip2dxf( dnode, 1L, 
                                          DxfLevel.fromString( detail ) ) );
        }
        log.debug( "getNode: DONE\n" );
        return res;
    }
    
    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.DatasetType
        getNodeBySequence( String sequence, String detail ){
        	
        Log log = LogFactory.getLog( DipDxfRecord.class );
        log.debug( "getNode called" );

        DatasetType res = dof.createDatasetType();

        if( sequence == null ){ return res; }
        
        try{
            sequence = sequence.replaceAll(" ","");
        }catch(Exception ex){
            // should not happen
        }
        
        if( sequence.length() == 0 ){ return res; }
        
        if(sequence.length() > 32){
            log.debug( "seq= " + sequence.substring( 0, 32 ) );
        } else {
            log.debug( "seq= " + sequence );
        }
        log.debug( "det=" + detail );
        
        if( nDAO == null ) {
            log.debug( "DipSOAP:   Creating NodeDAO..." );
            nDAO = new NodeDAO();
            log.debug( "DipSOAP:   ...done" );
        }
        
        DipNode dnode = null;
        
        List dnl = nDAO.findBySequence( sequence );
        
        if( dnl != null ){
            for( Iterator i = dnl.iterator(); i.hasNext(); ){
                DipNode cn = (DipNode) i.next();
                res.getNode()
                    .add( DipDxfUtil
                          .dip2dxf( cn, 1L,
                                    DxfLevel.fromString( detail ) ) );
            }
        }       
        
        log.debug( "getNode(by Sequence): DONE\n" );
        return res;
    }

    //------------------------------------------------------------------------------
    
    
    public static edu.ucla.mbi.dxf14.DatasetType
        getNodeRange( long fr, long to, String detail ) {
        
        Log log = LogFactory.getLog( DipDxfRecord.class );
        log.debug( "getNodeRange called" );
        log.debug( "from= " + fr + " to=" + to + " det=" + detail );
        
        DatasetType res = dof.createDatasetType();
        
        if( nDAO == null ) {
            log.debug( "DipSOAP:   Creating NodeDAO..." );
            nDAO = new NodeDAO();
            log.debug( "DipSOAP:   ...done" );
        }
        
        List<DipNode> nodeList = nDAO.findRange( fr, to );
        
        if( nodeList == null || nodeList.size() == 0 ){
            log.debug( "getNodeRange: DONE(no hits)\n" );
            return res;
        }
        
        long id = 0;
        
        for( Iterator<DipNode> ni = nodeList.iterator(); ni.hasNext(); ){
            
            res.getNode()
                .add( DipDxfUtil.dip2dxf( ni.next(), id++,
                                          DxfLevel.fromString( detail ) ) );
        }
        
        log.debug( "getNodeRange: DONE\n" );
        return res;
    }

    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.DatasetType
        getLink( String ns, String ac, String detail ) {
        
        Log log = LogFactory.getLog( DipDxfRecord.class );
        log.debug( "getLink called" );
        log.debug( "ns= " + ns + " ac=" + ac + " det=" + detail );
        
        DatasetType res = dof.createDatasetType();
        
        if( lDAO == null ) {
            log.debug( "DipSOAP:   Creating LinkDAO..." );
            lDAO = new LinkDAO();
            log.debug( "DipSOAP:   ...done" );
        }
        
        Link dlink = lDAO.find( new Integer( ac.replaceAll( "[^0-9]", "" ) ) );
        
        if( dlink != null ) {
            res.getNode()
                .add( DipDxfUtil.dip2dxf( dlink, 1L, 
                                          DxfLevel.fromString( detail ) ) );
        }
        log.debug( "getLink: DONE\n" );
        
        return res;
    }
    
    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.DatasetType
        getLinkRange( long fr, long to, String detail ) {
        
        Log log = LogFactory.getLog( DipDxfRecord.class );
        log.debug( "getLinkRange called" );
        log.debug( "from= " + fr + " to=" + to + " det=" + detail );
        
        DatasetType res = dof.createDatasetType();
        
        if( lDAO == null ) {
            log.debug( "DipSOAP:   Creating LinkDAO..." );
            lDAO = new LinkDAO();
            log.debug( "DipSOAP:   ...done" );
        }
        
        List<Link> linkList = lDAO.findRange( fr, to );
        
        if( linkList == null || linkList.size() == 0 ){
            log.debug( "getLinkRange: DONE(no hits)\n" );
            return res;
        }
        
        long id = 0;
        
        for( Iterator<Link> li = linkList.iterator(); li.hasNext(); ){
            
            res.getNode()
                .add( DipDxfUtil.dip2dxf( li.next(), id++,
                                          DxfLevel.fromString( detail ) ) );
        }
        
        log.debug( "getLinkRange: DONE\n" );
        return res;
    }
    
    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.DatasetType
        getEvidence( String ns, String ac, String detail ) {
        
        Log log = LogFactory.getLog( DipDxfRecord.class );
        log.debug( "getEvidence called" );
        log.debug( "ns= " + ns + " ac=" + ac + " det=" + detail );
        
        DatasetType res = dof.createDatasetType();
        
        if( xDAO == null ) {
            log.debug( "DipSOAP:   Creating EvidenceDAO..." );
            xDAO = new EvidDAO();
            log.debug( "DipSOAP:   ...done" );
        }
        
        Evidence devid = null;
        
        if( ns.equalsIgnoreCase( "dip" ) ){                
            devid = xDAO.find( new Integer( ac.replaceAll( "[^0-9]", "" ) ) );
        } else {
            List evidList = xDAO.findByImex254( ac ); 
            
            if( evidList != null && evidList.size() > 0 ){
                devid = (Evidence) evidList.get(0); 
            }
        }
        
        if( devid != null ) {
            res.getNode()
                .add( DipDxfEvidUtil.dip2dxf( devid, 1L,
                                              DxfLevel.fromString( detail ) ) );
        }
        log.debug( "getEvidence: DONE\n" );
        
        return res;
    }
    
    //--------------------------------------------------------------------------
        
    public static edu.ucla.mbi.dxf14.DatasetType
        getSource( String ns, String ac, String detail ) {
	
        Log log = LogFactory.getLog( DipDxfRecord.class );
        log.debug( "getSource called" );
        log.debug( "ns= " + ns + " ac=" + ac + " det=" + detail );
        
        DatasetType res = dof.createDatasetType();
        if(ns == null || ac == null){  return res; }
        
        if( ddsDAO == null ) {
            log.debug( "DipSOAP:   Creating DipDataSrcDAO..." );
            ddsDAO = new DipDataSrcDAO();
            log.debug( "DipSOAP:   ...done" );
        }
        
        DipDataSrc dsrc = null;
        
        if( ns.equals("dip") ){
            dsrc = ddsDAO.find( new Integer( ac.replaceAll( "[^0-9]", "" ) ) );
        }
        
        if( ns.equals("pubmed") ){
            dsrc = ddsDAO. findByPmid( ac );
        }
        
        if( dsrc != null ) {
            
            res.getNode().add( DipDxfDataSrcUtil
                               .dip2dxf( dsrc, 1L,
                                         DxfLevel.fromString( detail ) ) );
        }
        log.debug( "getSource: DONE\n" );

        return res;
    }

    //--------------------------------------------------------------------------
    
    public static edu.ucla.mbi.dxf14.DatasetType
        getSourceRange( long fr, long to, String detail ) {
        
        Log log = LogFactory.getLog( DipDxfRecord.class );
        log.debug( "getSourceRange called" );
        log.debug( "from= " + fr + " to=" + to + " det=" + detail );
        
        DatasetType res = dof.createDatasetType();
        
        if( ddsDAO == null ) {
            log.debug( "DipSOAP:   Creating DipDataSrcDAO..." );
            ddsDAO = new DipDataSrcDAO();
            log.debug( "DipSOAP:   ...done" );
        }
        
        List<DipDataSrc> ddsList = ddsDAO.findRange( fr, to );
        
        if( ddsList == null || ddsList.size() == 0 ){
            log.debug( "getSourceRange: DONE(no hits)\n" );
            return res;
        }
        
        long id = 0;
        
        for( Iterator<DipDataSrc> dsi = ddsList.iterator(); dsi.hasNext(); ){
            
            res.getNode().add( DipDxfDataSrcUtil
                               .dip2dxf( dsi.next(), id++,
                                         DxfLevel.fromString( detail ) ) );
        }
        
        log.debug( "getSourceRange: DONE\n" );
        return res;
    }
    
    //--------------------------------------------------------------------------
    
    /*

	if( query != null ) {
	    try {
		CompassSession searchsession = 
                    DipContext.getCompass().openSession();
                CompassTransaction stx = searchsession.beginTransaction();

                CompassHits hits = searchsession.find(query);
                log.debug(" Found [" + hits.getLength() + "]");
		
                
		edu.ucla.mbi.dxf14.NodeType resNode= dof.createNodeType();
		res.getNode().add(resNode);
		    
		resNode.setLabel("Text Search Report");

		TypeDefType res_type = dof.createTypeDefType();
		res_type.setNs("dxf");
		res_type.setAc("dxf:0026");
		res_type.setName("search-report");

		resNode.setType(res_type);
		   
		NodeType.AttrList al= dof.createNodeTypeAttrList();
		resNode.setAttrList(al);
		    
		AttrType at= dof.createAttrType();
		at.setName("text-query-string");
		    
		AttrType.Value atv= dof.createAttrTypeValue();
		at.setValue(atv);
		atv.setValue(query);

		al.getAttr().add(at);
		
		if(hits.getLength()>0){
		    
		    if(nDAO==null){
			log.debug("DipSOAP:   Creating NodeDAO...");
			nDAO=new NodeDAO();
			log.debug("DipSOAP:   ...done");
		    }
		    

		    edu.ucla.mbi.dxf14.NodeType.PartList 
			part_list= dof.createNodeTypePartList();
		    resNode.setPartList(part_list);
		    
		    NumberFormat scoreFormat = NumberFormat.getInstance();
		    scoreFormat.setMaximumFractionDigits(3);
		    
		    for (int i = 0; i < hits.getLength(); i++) {
			
			Object value = hits.data(i);
			Resource resource = hits.resource(i);
			float score=hits.score(i);

			if( DipContext.getIndexReport() ) {
			    Property[] pa = resource.getProperties();
			    log.debug("Query: resource alias=" + 
                                     resource.getAlias());

			    for(int j=0; j<pa.length;j++){
				log.debug("Query:  property=" + 
                                         pa[j].getName()+
					 " value=" + 
                                         pa[j].getStringValue());
			    }
			}
			edu.ucla.mbi.dxf14.NodeType.PartList.Part
			    part= dof.createNodeTypePartListPart();
			part_list.getPart().add(part);

			TypeDefType part_type = dof.createTypeDefType();
			part_type.setNs("dxf");
			part_type.setAc("dxf:0027");
			part_type.setName("search-result");
			part.setType(part_type);

			PartType.AttrList hit_attr_list = 
                            dof.createPartTypeAttrList();
			part.setAttrList(hit_attr_list);
			
			AttrType hit_attr= dof.createAttrType();
			hit_attr.setName("search-score");

			AttrType.Value hit_attr_value = 
                            dof.createAttrTypeValue();
			hit_attr.setValue(hit_attr_value);
			hit_attr_value.setValue(scoreFormat.format(score));
			hit_attr_list.getAttr().add(hit_attr);

			edu.ucla.mbi.dxf14.NodeType result_node=
			    result_node= dof.createNodeType();
			part.setNode(result_node);
			String cac="";
			
			if(resource.getAlias().equals("node")){
			    cac="DIP-"+resource.getId()+"N";
			    TypeDefType ctp = dof.createTypeDefType();
			    ctp.setNs("dxf");
			    ctp.setAc("dxf:0003");
			    ctp.setName("protein");
			    result_node.setType(ctp);			    
			}
			if(resource.getAlias().equals("link")){
			    cac="DIP-"+resource.getId()+"E";
			    TypeDefType ctp = dof.createTypeDefType();
			    ctp.setNs("dxf");
			    ctp.setAc("dxf:0004");
			    ctp.setName("link");
			    result_node.setType(ctp);
			}
			if(resource.getAlias().equals("dipdatasrc")){
			    cac="DIP-"+resource.getId()+"S";
			    TypeDefType ctp = dof.createTypeDefType();
			    ctp.setNs("dxf");
			    ctp.setAc("dxf:0016");
			    ctp.setName("data-source");
			    result_node.setType(ctp);
			}
			if(resource.getAlias().equals("evidence")){
			    cac="DIP-"+resource.getId()+"X";
			    TypeDefType ctp = dof.createTypeDefType();
			    ctp.setNs("dxf");
			    ctp.setAc("dxf:0015");
			    ctp.setName("evidence");
			    result_node.setType(ctp);
			}
			result_node.setNs("dip");
			result_node.setAc(cac);
			result_node.setLabel(cac);
			
			NodeType.XrefList xref_list = 
                            dof.createNodeTypeXrefList();
			result_node.setXrefList(xref_list);

			XrefType cxref=dof.createXrefType();
			cxref.setNs("dip");
			cxref.setAc(cac);
			cxref.setTypeNs("dxf");
			cxref.setTypeAc("dxf:0009");
			cxref.setType("identical-to");
			xref_list.getXref().add(cxref);
		    }
                    
		    hits.close();
		    
		    stx.commit();
		    searchsession.close();
		}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
	}
	return res;
    }
    */
}
