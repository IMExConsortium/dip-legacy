package edu.ucla.mbi.dip;

/* =============================================================================
 # $Id:: DipDxfDataSrcUtil.java 1886 2011-10-09 18:20:16Z lukasz               $
 # Version: $Rev:: 1886                                                        $
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

public class DipDxfDataSrcUtil{
    
    static edu.ucla.mbi.dxf14.ObjectFactory 
	dof = new edu.ucla.mbi.dxf14.ObjectFactory();

    //--------------------------------------------------------------------------
    // DataSrc DXF representation
    //---------------------------

    public static edu.ucla.mbi.dxf14.NodeType 
	dip2dxf(edu.ucla.mbi.dip.DipDataSrc dds, 
		long id, DxfLevel mode) {
	
	Log log = LogFactory.getLog(DipDxfUtil.class);
	
	edu.ucla.mbi.dxf14.NodeType dxfNode = null;
	int _id=1;

	if(dds==null){
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
	    
	// set node type to 'link'
	//------------------------
	
	dxfType.setNs("dxf");
	dxfType.setAc("dxf:0016");
	dxfType.setName("data-source");
	    
	dxfNode.setId(_id++);
	dxfNode.setNs("dip");
	dxfNode.setAc(dds.getAccession());
	    
	dxfNode.setType(dxfType);
	    
	log.debug( "DipDxfDataSrcUtil:   label   = " 
                   + dds.getAccession() );
	log.debug( "DipDxfDataSrcUtil:   name    = " 
                   + dds.getAuthors().replaceAll("\\s*,.+$","") 
                   + dds.getYear() );

	// set label
	//----------
	dxfNode.setLabel(dds.getAccession());
	
	// set name/description
	//---------------------
	    
	if( dds.getAccession()!=null &&
            dds.getAuthors().length()>0){
	    dxfNode.setName( dds.getAuthors()
                             .replaceAll( "\\s*\\S+\\s*,.+$", "" ) 
                             + dds.getYear() );
	}
	
	// create attribute list
	//----------------------

	dxfNode.setAttrList(dof.createNodeTypeAttrList());
	List<edu.ucla.mbi.dxf14.AttrType>
	    atl=dxfNode.getAttrList().getAttr();
	
	log.debug("DipDxfUtil:   adding attributes");

	// - title
	
	atl.add(DipDxfUtil.createAttr("dip","dip:0004","title",
						dds.getTitle()));
	
	// -authors

	String authList=dds.getAuthors();

	if(mode==DxfLevel.STUB){  // stub only

	    // truncate if needed 
	
	}

	atl.add(DipDxfUtil.createAttr("dip","dip:0010","authors",
                                      authList));
	
	// -author-year
	atl.add(DipDxfUtil.createAttr("dip","dip:0027","author-year",
				      dds.getLabel()));
	
	
	if(mode.compareTo(DxfLevel.BASE)>=0){  // base or more

	    // -journal title
	    if(dds.getJname()!=null && dds.getJname().length()>0){
                atl.add(DipDxfUtil.createAttr("dip","dip:0009","journal-title",
                                              dds.getJname()));
            }
	    
	    // -volume

	    atl.add(DipDxfUtil.createAttr("dip","dip:0011","volume",
					  dds.getVolume()));
	    // -issue
	    atl.add(DipDxfUtil.createAttr("dip","dip:0012","issue",
					  dds.getIssue()));
	    // -pages
	    atl.add(DipDxfUtil.createAttr("dip","dip:0015","pages",
					  dds.getPages()));

	    // -year
	    atl.add(DipDxfUtil.createAttr("dip","dip:0013","year",
					  Integer.toString(dds.getYear())));
	}

	if( mode.compareTo(DxfLevel.FULL)>=0){ //full or more

	    // - abstract
	    
	    if(dds.getAbstract()!=null && dds.getAbstract().length()>0){
		atl.add(DipDxfUtil.createAttr("dip","dip:0014","abstract",
					      dds.getAbstract()));
	    }
	}

	if(mode.compareTo(DxfLevel.DEEP)==0){ // deep
	    
	    
	}
	
	// create xref list
	//-----------------

	dxfNode.setXrefList(dof.createNodeTypeXrefList());
	List<edu.ucla.mbi.dxf14.XrefType>
	    xrl=dxfNode.getXrefList().getXref();
	
	log.debug( "DipDxfDataSrcUtil:   adding xrefs");
	
	// -pubmed
	//--------
	
	if( dds.getPmid() != null ){
	    xrl.add( DipDxfUtil
                     .createXref( "pubmed", dds.getPmid().replaceAll("\\s",""),
                                  "dxf", "dxf:0009", "identical-to") );
	}
        
        if( dds.getImexSrc() >= 0 ){
            
            XrefType pbx = DipDxfDataSrcUtil
                .createPublishedByXref( dds.getImexSrc() );

            XrefType cbx = DipDxfDataSrcUtil
                .createCuratedByXref( dds.getImexSrc() );
            
            if( pbx != null && cbx != null) {
                xrl.add( pbx );
                xrl.add( cbx );
            }
        
        }

	if( mode.compareTo( DxfLevel.DEEP ) >= 0 ){ // deep
	    
	}
        
        log.debug( "DipDxfDataSrcUtil: done:" + dxfNode );
	return dxfNode;
    }
    
    //--------------------------------------------------------------------------
    // ImexSrc xref
    //-------------

    private static XrefType createImexSrcXref( String ns, String ac, String name, 
                                               int imexSrc ){
        
        if( imexSrc < 0 ) return null;
        
        String sImexSrc = null;
        String sImexSrcAc = null;

        switch( imexSrc )
            {
            case 0: sImexSrc="dip"; sImexSrcAc="MI:0465"; break;
            case 1: sImexSrc="dip"; sImexSrcAc="MI:0465"; break;
            case 2: sImexSrc="intact"; sImexSrcAc="MI:0469"; break;
            case 3: sImexSrc="mint"; sImexSrcAc="MI:0471"; break;
            default: 
            }
        
        if( sImexSrc != null ){
            return DipDxfUtil.createXref( sImexSrc, sImexSrcAc,
                                          ns, ac, name );
        } 

        return null;
    }

    //--------------------------------------------------------------------------

    public static XrefType createCuratedByXref( int imexSrc ){
        return DipDxfDataSrcUtil
            .createImexSrcXref( "dxf", "dxf:0055", "curated-by", imexSrc );
    }

    public static XrefType createPublishedByXref( int imexSrc ){
        return DipDxfDataSrcUtil
            .createImexSrcXref( "dxf", "dxf:0040", "published-by", imexSrc );
    }
    
}
