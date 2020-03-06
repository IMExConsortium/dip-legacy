package edu.ucla.mbi.dip;

/*==============================================================================
 * $Id:: DipDxfImex.java 1863 2011-09-13 17:44:26Z lukasz                      $
 * Version: $Rev:: 1863                                                        $
 *------------------------------------------------------------------------------
 *                                                                             $
 *  DipDxfImex  - imex record processing                                       $
 *                                                                             $
 *    NOTES:                                                                   $
 *                                                                             $
 *=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.regex.*;
import java.io.*;

import edu.ucla.mbi.dip.*;
import edu.ucla.mbi.dxf14.*;
import edu.ucla.mbi.orm.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;

public class DipDxfImex{

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
    static CountsDAO dcDAO = null;

    static edu.ucla.mbi.dxf14.ObjectFactory dxfFactory
	= new edu.ucla.mbi.dxf14.ObjectFactory();

    //--------------------------------------------------------------------------
        
    public static String setImexSRec( String ns, String ac, 
                                      String imex, String mif, 
                                      String imex254, String mif254 ){
        
	Log log = LogFactory.getLog( DipDxfImex.class );
        log.debug( "DipDxfImex: setImexSRec called" );
	log.debug( "DipDxfImex: ac=\""+ac+"\"  (ns=\""+ns+"\")");
	log.debug( "DipDxfImex: imex="+imex+"  imex254="+imex254);
        
        if( (imex.length() > 0 || imex254.length() > 0 ) &&
            ac.length() > 0 && ns.equals("dip") ) {

            if ( mif254 != null && mif254.length() > 0 ) {

                // mif254: final version - no conversion
                //--------------------------------------

                log.debug( "DipDxfImex: 254 -> verbatim insertion");
                try{
                    StringReader sr = new StringReader( mif254 );
                    InputSource xmlIsSource
                        = new org.xml.sax.InputSource( sr );
                
                    DocumentBuilderFactory
                        dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse( xmlIsSource );
                    doc.getDocumentElement().normalize();
                
                    int imexSrc = DipDxfImex.getImexSrc( doc );
                    log.debug("DipDxfImex: imexSrc=" + imexSrc );

                    if( imexSrc > 0 ) {
                        log.debug( "DipDxfImex: mif254=" 
                                   + mif254.substring( 0,64) );
                        if( imexDAO == null ) {
                            imexDAO = new ImexSRecDAO();
                        }

                        ac = ac.replaceAll("\\D","");

                        ImexSRec isr = new ImexSRec();
                        isr.setEvidId( new Long( ac ) );
                        isr.setDts( imexSrc );
                        isr.setImex254( imex254 );
                                
                        isr.setMif( mif254 );
                        isr.setMif254( mif254 );
                        log.debug( "imexSrc: mif254 submitted" );
                        imexDAO.saveOrUpdate( isr );
                        
                        log.debug( "DipDxfImex: setImexSRec : DONE(verbatim)" );
                        return mif254;
                    }
                } catch( Exception ex ) {
                    log.debug("imexSrc: " + ex );
                }
            }

            if ( mif != null && mif.length() > 0 ) {

                // mif: legacy version (AKA: curator-generated) -  convert
                //--------------------------------------------------------
                log.debug( "DipDxfImex: legacy->254 convertion");

                try {
                    StringReader sr = new StringReader( mif );
                    InputSource xmlIsSource
                        = new org.xml.sax.InputSource( sr );
                    
                    DocumentBuilderFactory
                        dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse( xmlIsSource );
                    doc.getDocumentElement().normalize();
                    
                    if( doc!= null ) {
                        String mif254v = DipDxfImex.to254( doc );
                        int imexSrc = DipDxfImex.getImexSrc( doc ); 
                        
                        log.debug("DipDxfImex: imexSrc=" + imexSrc );
			
                        if( mif254v != null && mif254v.length() > 0  &&
                            imexSrc > 0 ) {
                            
                            log.debug( "DipDxfImex: mif254=" 
                                       + mif254v.substring( 0,64) );
                            
                            if( imexDAO == null ) {
                                imexDAO = new ImexSRecDAO();
                            }
                            
                            ac = ac.replaceAll("\\D","");
                            
                            ImexSRec isr = new ImexSRec();
                            isr.setEvidId( new Long( ac ) );
                            isr.setDts( imexSrc );
                            isr.setImex254( imex254 );
                            
                            isr.setMif( mif );
                            isr.setMif254( mif254v );
                            log.debug( "imexSrc: mif254 submitted" );
                            imexDAO.saveOrUpdate( isr );
                            return mif254v;
                        }
                    }
                } catch( Exception ex ) {
                    log.debug("imexSrc: " + ex );                        
                }
            }
        }
            
	log.debug( "DipDxfImex: setImexSRec : DONE(empty)\n" );
	return null; 
    }
    
    public static String to254( Document doc ) {
        
        Log log = LogFactory.getLog( DipDxfImex.class );

        try {
            
            log.debug( "Root element " + 
                      doc.getDocumentElement().getNodeName() );

            // root element conversion
            //------------------------------------------------------------------

            //<entrySet xmlns="net:sf:psidev:mi" 
            //    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
            //    xsi:schemaLocation="net:sf:psidev:mi 
            //     http://psidev.sourceforge.net/mi/rel25/src/MIF25.xsd"
            //    level="2" version="5" minorVersion="3">
            
            // convert into:

            //<entrySet xmlns="http://psi.hupo.org/mi/mif"
            //    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
            //    xsi:schemaLocation="http://psi.hupo.org/mi/mif 
            //     http://psidev.sourceforge.net/mi/rel25/src/MIF254.xsd" 
            //     level="2" version="5" minorVersion="4">
            
            String namespace = "http://psi.hupo.org/mi/mif";
            String schemaLocation 
                = "http://psidev.sourceforge.net/mi/rel25/src/MIF254.xsd";
            doc.getDocumentElement().setAttribute( "xmlns", namespace );
            doc.getDocumentElement().setAttribute( "xsi:schemaLocation",
                                                   namespace + " " + 
                                                   schemaLocation );
            doc.getDocumentElement().setAttribute( "minorVersion", "4" );
            
            // source conversion
            //------------------------------------------------------------------
            // releaseDate -> current, set curationDate
            // set provider id (prid);
            // 

            String curationDate = "";               
            String importDate = "";               
            String prid = "";
                
            NodeList nodeLst = doc.getElementsByTagName( "source" );
            for( int s = 0; s < nodeLst.getLength(); s++ ) {
                Node fstNode = nodeLst.item( s );
                if( fstNode.getNodeType() == Node.ELEMENT_NODE ) {
                     
                    // process date
                    //-------------

                    Date now = new Date();
                    SimpleDateFormat
                        dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                    
                    String releaseDate = dateFormat.format( now );
		    importDate = releaseDate;

                    curationDate = 
                        ((Element) fstNode).getAttribute("releaseDate");
                        
                    if( curationDate.equals( "" ) ) {
                        curationDate = releaseDate;
                    } else {
                            
                    }
                        
                    log.debug( "curationDate: " + curationDate + 
                              " releaseDate: " + releaseDate );
                        
                    ((Element) fstNode).setAttribute( "releaseDate", 
                                                      releaseDate );
                    
                    // get imex provider
                    //------------------
                    
                    //<xref>
                    // <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0465" 
                    //    refType="primary-reference" refTypeAc="MI:0358"/>
                    //</xref>
                    
                    NodeList cl = fstNode.getChildNodes() ;
                    Element xref = null;
                    
                    for( int ci = 0; ci < cl.getLength(); ci++ ) {
                        Node cn = cl.item( ci );
                        if( cn.getNodeType() == Node.ELEMENT_NODE &&
                            ((Element) cn).getNodeName().equals("xref") ) {                                    
                            xref = (Element) cn;
                        }
                    }
                    
                    if( xref != null ) {
                        NodeList rl = xref.getChildNodes(); 
                        for( int ri = 0; ri < rl.getLength(); ri++ ) {
                            Node cr = rl.item( ri );
                            if( cr.getNodeType() == Node.ELEMENT_NODE ) {
                                Element ref = (Element) cr;
                                
                                if( ref.getAttribute("refTypeAc")
                                    .equals("MI:0358") ) {
                                    prid = ref.getAttribute("id");
                                }
                            }
                        }
                    }
                    log.debug( "imex provider=" +  prid );
                }
            }
                
            // interaction conversion
            //------------------------------------------------------------------
            // get imexId -> convert to imex-primary 
            //
                
            String imexPrimary = "";

            NodeList intLst = doc.getElementsByTagName( "interaction" );
            for( int s = 0; s < intLst.getLength(); s++ ) {
                Node fstNode = intLst.item( s );
                if( fstNode.getNodeType() == Node.ELEMENT_NODE ) {
                    Element i10 = (Element) fstNode;
                        
                    // get primary
                    //------------
                        
                    String iid = i10.getAttribute("imexId");
                    try { // sanity tests  
                        iid = iid.replaceAll("IM-IM-", "IM-"); 
                        iid = iid.replaceAll("--", "-"); 
                        
                        imexPrimary =  iid.replaceAll("-[\\d+]$","");
                        if( imexPrimary.equals("IM-") ) {
                            imexPrimary = iid;
                        }
                        i10.setAttribute( "imexId", iid );
                        log.debug( "imexPrimary=" + imexPrimary );
                        
                    } catch( PatternSyntaxException pse ) {
                            // should not happen
                    }
                        
                    // set imex-source
                    //----------------
                    
                    //<interaction>
                    // <xref>
                    //  <primaryRef refType="imex-source" dbAc="MI:0488"
                    //     refTypeAc="MI:0973" db="psi-mi"  id="MI:0465"/>
                    //  <secondaryRef refType="identity" dbAc="MI:0465"  
                    //     refTypeAc="MI:0356" db="dip" id="DIP-102254E"/>
                    // </xref>
                    
                    Element xref = null;
                    NodeList xl = i10.getChildNodes();
                    for( int xi = 0; xi < xl.getLength(); xi++ ) {
                        Node xn = xl.item( xi );
                        if( xn.getNodeType() == Node.ELEMENT_NODE &&
                            ((Element) xn).getNodeName().equals("xref") ) {
                            xref = (Element) xn;
                        }
                    }
                    
                    if( xref != null ) {
                        boolean skipImexSource = false; 
                        NodeList prLst = xref.getChildNodes();
                            
                        for( int xi = 0; xi < prLst.getLength(); xi++ ) {
                            Node xn = prLst.item( xi );
                            if( xn.getNodeType() == Node.ELEMENT_NODE &&
                                ((Element) xn).getNodeName().equals( "primaryRef" ) 
                                ) {
                                Element el = (Element) xn;
                                if( ! el.getAttribute("refTypeAc")
                                    .equals("MI:0973") ) {
                                    Element sec = buildSecondaryRef( el );     
                                    xref.appendChild( sec );
                                    xn.getParentNode().removeChild( xn );
                                } else {
                                    skipImexSource = true;
                                }
                            }
                        }
                        
                        if( !skipImexSource ) {
                            Element prm 
                                = buildPrimaryRef( doc, prid,
                                                   "psi-mi", "MI:0488",
                                                   "imex-source", "MI:0973" 
                                                   );
                            xref.insertBefore( prm, xref.getFirstChild() );
                        }
                        
                    }
                        

                    // set imex-primary
                    //-----------------
                        
                    //<experimentDescription id="34995">
                    // ...
                    // <xref>
                    //  <primaryRef db="imex" dbAc="MI:0670" id="IM-8310" 
                    //    refType="imex-primary" refTypeAc="MI:0662"/>
                    //  <secondaryRef db="dip" dbAc="MI:0465" id="DIP-131934X"
                    //    refType="identity" refTypeAc="MI:0356"/>
                    // </xref>
                                            
                    NodeList edl 
                        = i10.getElementsByTagName( "experimentDescription" );
                    for( int edi = 0; edi < edl.getLength(); edi++ ) {
                        Node edn = edl.item( edi );
                        if( edn.getNodeType() == Node.ELEMENT_NODE &&
                            ((Element) edn).getNodeName()
                            .equals("experimentDescription") ) {
                            Element edes = (Element) edn;
                            Element edxref = null;
                            
                            if( edes != null ) {
                                NodeList exl = edes.getChildNodes();
                                for( int exi = 0; exi < exl.getLength(); exi++ 
                                     ) {
                                    Node xn = exl.item( exi );
                                    if( xn.getNodeType() == Node.ELEMENT_NODE &&
                                        ((Element) xn).getNodeName().equals("xref") 
                                        ) {
                                        edxref = (Element) xn;
                                    }
                                }
                            }

                            if( edxref != null ) {
                                
                                boolean skipImexPrimary = false;

                                NodeList prLst = edxref.getChildNodes();
                                
                                for( int xi = 0; xi < prLst.getLength(); xi++ 
                                     ) {
                                    Node xn = prLst.item( xi );
                                    if( xn.getNodeType() == Node.ELEMENT_NODE &&
                                        ((Element) xn).getNodeName().equals("primaryRef")
                                        ) {
                                        Element el = (Element) xn;
                                        if( ! el.getAttribute("refTypeAc")
                                            .equals("MI:0662") ) {
                                            Element sec = buildSecondaryRef( el );
                                            edxref.appendChild( sec );
                                            xn.getParentNode().removeChild( xn );
                                        } else {
                                            skipImexPrimary = true;
                                        }
                                    }
                                }

                                if( !skipImexPrimary ) {
                                    Element prm
                                        = buildPrimaryRef( doc, imexPrimary,
                                                           "imex", "MI:0670",
                                                           "imex-primary", "MI:0662"
                                                           );
                                    edxref.insertBefore( prm, 
                                                         edxref.getFirstChild() );
                                }
                            }
                        }
                    }
                }
            }
            
                
            // uniprot/refseq versioning
            //------------------------------------------------------------------

            NodeList prLst = doc.getElementsByTagName( "primaryRef" );
            
            for( int s = 0; s < prLst.getLength(); s++ ) {
                Node fstNode = prLst.item( s );
                if( fstNode.getNodeType() == Node.ELEMENT_NODE ) {
                    Element pre = (Element) fstNode;
                    if( pre.getAttribute("dbAc").equals("MI:0486") ||
                        pre.getAttribute("dbAc").equals("MI:0481") ) {
                        versionRef( pre );
                    }   
                }
            }
            
            NodeList srLst = doc.getElementsByTagName( "secondaryRef" );
            
            for( int s = 0; s < srLst.getLength(); s++ ) {
                Node fstNode = srLst.item( s );
                if( fstNode.getNodeType() == Node.ELEMENT_NODE ) {
                    Element pre = (Element) fstNode;
                    if( pre.getAttribute("dbAc").equals("MI:0486") ||
                        pre.getAttribute("dbAc").equals("MI:0481") ) {
                        versionRef( pre );
                    }   
                }
            }

            
            // attribute update
            //------------------------------------------------------------------
            // add dip:curation-date
            //  version = 1 
            //  'full coverage'(MI:0957) = only protein-protein interactions
            //  'imex curation'(MI:0959)
            
            NodeList entLst = doc.getElementsByTagName( "entry" );
            for( int s = 0; s < entLst.getLength(); s++ ) {
                Node fstNode = entLst.item( s );
                if( fstNode.getNodeType() == Node.ELEMENT_NODE ) {
                    Element entry = (Element) fstNode;
                    
                    if( entry != null ) {
                        
                        List<Element> ell 
                            = getChildElementsByName( entry,
                                                      "attributeList" );
                        if( ell == null ) {
                            Element el = entry.getOwnerDocument()
                                .createElement( "attributeList" );
                            entry.appendChild( el );
                            
                            ell = getChildElementsByName( entry,
                                                          "attributeList" );
                        }

                        log.debug( "atll size=" + ell.size() + "\n");


                        for( Iterator<Element> ei = ell.iterator();
                             ei.hasNext(); ) {
                            
                            Element al = ei.next();
                            log.debug( "atl="+ al );
                            log.debug( "atl owner=" + al.getOwnerDocument() );
                            al.appendChild( buildAttribute( al.getOwnerDocument(),
                                                            "dip:curation-date",
                                                            null,
                                                            curationDate ) );

                            al.appendChild( buildAttribute( al.getOwnerDocument(),
                                                            "dip:import-date",
                                                            null,
                                                            importDate ) );
                            
                            al.appendChild( buildAttribute( al.getOwnerDocument(),
                                                            "version",
                                                            null,
                                                            "1" ) );
                            
                            al.appendChild( buildAttribute( al.getOwnerDocument(),
                                                            "full coverage",
                                                            "MI:0957",
                                                            "only protein-protein interactions" ) );
                            
                            al.appendChild( buildAttribute( al.getOwnerDocument(),
                                                            "imex curation",
                                                            "MI:0959",
                                                            null) );                                                                        
                        }
                    }
                }
            }
            
            //--------------------------------------------------------------
            // serialize to string
            //--------------------

            DOMSource domSource = new DOMSource( doc );
            Writer sw = new StringWriter();                                
            StreamResult streamResult = new StreamResult( sw ); 
            
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.transform( domSource, streamResult );
            
            log.debug( "SW:\n" + sw.toString() + "\n");
        
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.debug( "DipDxfImex: to254 error");
        }
        
        return null;       
    }


    //--------------------------------------------------------------------------
    
    public static int getImexSrc( Document doc ) {
        
        Log log = LogFactory.getLog( DipDxfImex.class );
        try {
            
            // get provider id (prid)
            //------------------------------------------------------------------
                        
            String prid = "";
                
            NodeList nodeLst = doc.getElementsByTagName( "source" );
            for( int s = 0; s < nodeLst.getLength(); s++ ) {
                Node fstNode = nodeLst.item( s );
                if( fstNode.getNodeType() == Node.ELEMENT_NODE ) {
                    
                    // get imex provider
                    //------------------
                    
                    //<xref>
                    // <primaryRef db="psi-mi" dbAc="MI:0488" id="MI:0465" 
                    //    refType="primary-reference" refTypeAc="MI:0358"/>
                    //</xref>
                    
                    NodeList cl = fstNode.getChildNodes() ;
                    Element xref = null;
                    
                    for( int ci = 0; ci < cl.getLength(); ci++ ) {
                        Node cn = cl.item( ci );
                        if( cn.getNodeType() == Node.ELEMENT_NODE &&
                            ((Element) cn).getNodeName().equals("xref") ) {                                    
                            xref = (Element) cn;
                        }
                    }
                    
                    if( xref != null ) {
                        NodeList rl = xref.getChildNodes(); 
                        for( int ri = 0; ri < rl.getLength(); ri++ ) {
                            Node cr = rl.item( ri );
                            if( cr.getNodeType() == Node.ELEMENT_NODE ) {
                                Element ref = (Element) cr;
                                
                                if( ref.getAttribute("refTypeAc")
                                    .equals("MI:0358") ) {
                                    prid = ref.getAttribute("id");
                                }
                            }
                        }
                    }
                    log.debug( "imex provider=" +  prid );
                }
            }
            
            //------------------------------------------------------------------

            int imexSrc = 1; // default DIP
            if( prid != null && prid.equals("MI:0465") ) {
                imexSrc = 1;  // DIP
            }
            if( prid != null && prid.equals("MI:0469") ) {
                imexSrc = 2;  // IntAct
            }
            
            if( prid != null && prid.equals("MI:0471") ) {
                imexSrc = 3; // MINT
            }
            
            if( prid != null && prid.equals("MI:0463") ) {
                imexSrc = 4; // GRID
            }
            
            if( prid != null && prid.equals("MI:0903") ) {
                imexSrc = 5; // MPIDB 
            }

            return imexSrc;
            
        } catch (Exception e) {
            e.printStackTrace();
            log.debug( "DipDxfImex: getImexScr error");
        }

        return 0;         
    }
    
    
    //--------------------------------------------------------------------------
    //

    private static List<Element> getChildElementsByName( Node node, 
                                                         String name ) {
        List res = null; 
        
        NodeList childLst = node.getChildNodes();
        if( childLst != null ) {
            for( int ci = 0; ci < childLst.getLength(); ci++ ) {
                Node cn = childLst.item( ci );
                if( cn.getNodeType() == Node.ELEMENT_NODE &&
                    ((Element) cn).getNodeName().equals( name ) ) {
                    
                    if( res == null ) {
                        res = new ArrayList<Element>();
                    } 
                    res.add( (Element) cn );
                }
            }
        }
        return res;
    }

    //--------------------------------------------------------------------------    
    //<attribute name="dip:key-assigner">production</attribute>
    
    private static Element buildAttribute( Document doc, String name,
                                           String nameAc, String value ) {

        Element el = doc.createElement("attribute");
        el.setAttribute( "name", name );
        if( nameAc != null ) {
            el.setAttribute( "nameAc", nameAc );
        }
        if( value != null ) {
            el.appendChild( doc.createTextNode( value ) ); 
        }
        return el;
    }
    
    //--------------------------------------------------------------------------
    //<primaryRef refType="imex-source" refTypeAc="MI:0973" 
    //            db="psi-mi" dbAc="MI:0488" id="MI:0465"/>
    
    private static Element buildPrimaryRef( Document doc, String id,
                                            String db, String dbAc, 
                                            String refType, String refTypeAc ) {
        
        Element el = doc.createElement("primaryRef"); 

        el.setAttribute( "id", id );
        el.setAttribute( "db", db );
        el.setAttribute( "dbAc", dbAc );
        
        if( refType != null ) {
            el.setAttribute( "refType", refType );
        }
        if( refTypeAc!=null ) {
            el.setAttribute( "refTypeAc", refTypeAc );
        }
        
        return el;
    }

    //--------------------------------------------------------------------------

    private static Element buildPrimaryRef( Element ref ) {
        
        Document doc = ref.getOwnerDocument() ;
        
        String id = ref.getAttribute("id");
        String db =  ref.getAttribute("db");
        String dbAc = ref.getAttribute("dbAc");
        String refType = ref.getAttribute("refType");
        String refTypeAc = ref.getAttribute("refTypeAc");

        return buildSecondaryRef( doc, id, db, dbAc, refType, refTypeAc );
    }
    
    //--------------------------------------------------------------------------
    //<secondaryRef refType="imex-source" refTypeAc="MI:0973" 
    //              db="psi-mi" dbAc="MI:0488" id="MI:0465"/>
    
    private static Element buildSecondaryRef( Document doc, String id,
                                       String db, String dbAc, 
                                       String refType, String refTypeAc ) {
        
        Element el = doc.createElement( "secondaryRef" ); 
        
        el.setAttribute( "id", id );
        el.setAttribute( "db", db );
        el.setAttribute( "dbAc", dbAc );
        
        if( refType != null ) {
            el.setAttribute( "refType", refType );
        }
        if( refTypeAc!=null ) {
            el.setAttribute( "refTypeAc", refTypeAc );
        }
        
        return el;
    }

    //--------------------------------------------------------------------------

    private static Element buildSecondaryRef( Element ref ) {
        
        Document doc = ref.getOwnerDocument() ;

        String id = ref.getAttribute("id");
        String db =  ref.getAttribute("db");
        String dbAc = ref.getAttribute("dbAc");
        String refType = ref.getAttribute("refType");
        String refTypeAc = ref.getAttribute("refTypeAc");
        
        return buildSecondaryRef( doc,id,db,dbAc,refType,refTypeAc );
    }
    
    //--------------------------------------------------------------------------
    // <primaryRef db="uniprotkb" dbAc="MI:0481" id="NP_60010.111" 
    //    refType="identity" refTypeAc="MI:0356" 
    // version="81"/>

    private static Element versionRef( Element el ) {
        
        String version = el.getAttribute( "version" );

        if( ! version.equals("") ) {
            return el;
        }

        String id = el.getAttribute( "id" );
        
        if( id.lastIndexOf(".") > 0 ) {
            version = id.substring( id.lastIndexOf(".") + 1 );
            id = id.substring( 0, id.lastIndexOf(".") );
        }
        
        if ( ! version.equals( "" ) ) {
            el.setAttribute( "version", version );
            el.setAttribute( "id", id );
        }
        
        return el;
    }
}
