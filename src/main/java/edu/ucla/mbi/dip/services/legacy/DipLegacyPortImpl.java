package edu.ucla.mbi.dip.services.legacy;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.ucla.mbi.legacy.dip.*;
import edu.ucla.mbi.dxf14.*;

import edu.ucla.mbi.services.legacy.dip.*;

import edu.ucla.mbi.dip.*;

@WebService( serviceName = "DipLegacyService", 
             portName = "legacyPort", 
             endpointInterface = "edu.ucla.mbi.legacy.dip.DipLegacyPort", 
             targetNamespace = "http://mbi.ucla.edu/services/legacy/dip",
             name = "DipLegacyPortType",
             wsdlLocation = "/WEB-INF/wsdl/DipLegacyService.wsdl" )

public class DipLegacyPortImpl implements DipLegacyPort {

    public void initialize(){  
        Log log = LogFactory.getLog( DipLegacyPortImpl.class );        
        log.info( "DipLegacyPortImpl: Initializing" );
    }
    
    public java.util.List<edu.ucla.mbi.dxf14.XrefType>
        getLinkList( String ns, String ac,
                     String match, String detail, String format ){


        List<XrefType>  res = null;

        if ( ns != null && ns.equals( "dip" ) ) {
            res = DipDxfList.getLinkList( "dip", ac, match, detail );
        }

        return res;
    }
    
    public java.lang.String
        getDipSRec( edu.ucla.mbi.services.legacy.dip.GetDipSRec request ){
        
        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getCounts( String ns, String ac,
                   String match, String detail, String format ){
        
        DatasetType res = null;

        if ( ns != null && ns.equals( "taxid" ) ) {
            res = DipDxfStatistics.getCounts( "taxid", ac, "full" );
        } else {
            res = DipDxfStatistics.getCounts( "psi-mi", "MI:0465", "full" );
        }
        
        if( res != null ) {
            return res.getNode();
        }
        
        return null;
    }
    
    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getEvidence( String ns, String ac,
                     String match, String detail, String format ){

        DatasetType res = null;

        if( ns != null && ( ns.equalsIgnoreCase( "dip" ) ||
                            ns.equalsIgnoreCase( "imex" ) ) 
            ){
            
            res = DipDxfRecord.getEvidence( ns , ac, detail );
        }
        
        if( res != null ) {
            return res.getNode();
        }

        return null;
    }
    
    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getSourceRange( long fr, long to, String detail, String format){

        Log log = LogFactory.getLog( DipLegacyPortImpl.class );
        log.debug("DipSOAP:  Sources Requested: from:" + fr + " to:" + to);
        
        if(detail== null || detail.equals("") ){
            detail="base";
        }
        
        DatasetType sourceDS = DipDxfRecord.getSourceRange( fr, to, detail );
        
        if( sourceDS != null && sourceDS.getNode().size() > 0 ) {
            return sourceDS.getNode();
        }

        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        query( java.lang.String query,
               java.lang.String detail,
               java.lang.String format ){

        Log log = LogFactory.getLog( DipLegacyPortImpl.class );
        log.info( "DipLegacyPortImpl: query:" + query );
        
        DatasetType res = DipDxfQuery.queryLC( query, detail );
        if( res != null ){
            return res.getNode();
        }
        
        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getNode( String ns, String ac,
                 String sequence, String match,
                 String detail, String format ){

        DatasetType res = null;
                
        Log log = LogFactory.getLog( DipLegacyPortImpl.class );
        
        if( ns != null && ac != null ){
            res = DipDxfRecord.getNode( ns, ac, detail );
        } else {
            if( sequence != null  ){
                res = DipDxfRecord.getNodeBySequence( sequence, detail );
            }
        }
        if( res != null ){
            return res.getNode();
        }

        return null;
    }
    
    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getLinkBounds( String detail, String format ){
                DatasetType ds = DipDxf.getLinkBounds();
        if(ds != null ){
            return ds.getNode();
        }

        return null;
    }
    
    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        setEvidence( edu.ucla.mbi.dxf14.DatasetType dataset,
                     java.lang.String mode ){

        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.XrefType>
        getEvidenceList( String ns, String ac,
                         String match, String detail, String format ){

        List<XrefType>  res = null;
        
        if ( ns != null && ns.equals( "dip" ) ) {
            res = DipDxfList.getEvidenceList( "dip", ac, "", detail );
        }
        
        return res;
    }
    
    public java.lang.String
        dropImexSRec( edu.ucla.mbi.services.legacy.dip.DropImexSRec request ){

        return null;
    }

    public edu.ucla.mbi.dxf14.DatasetType getCrossRefList(
        edu.ucla.mbi.services.legacy.dip.GetCrossRef request ){

        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getLink( java.lang.String ns,
                 java.lang.String ac,
                 java.lang.String match,
                 java.lang.String detail,
                 java.lang.String format ){

        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.XrefType>
        getSourceList( String ns, String ac,
                       String match, String detail, String format ){

        List<XrefType>  res = null;

        if ( ns != null && ns.equals( "dip" ) ) {
            res = DipDxfList.getSourceList( "dip", ac, "", detail );
        }
        return res;
    }

    public java.util.List<edu.ucla.mbi.dxf14.XrefType>
        getNodeList( String ns, String ac,
                     String match, String detail, String format ){
        
        List<XrefType>  res = null;
        
        if ( ns != null && ns.equals( "dip" ) ) {
            res = DipDxfList.getNodeList( "dip", ac, "", detail );
        }
        return res;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        matchNode( DatasetType dataset,
                   String match, String detail, String format ){

        DatasetType res = DipDxf.matchNode( dataset, match, detail );
        if( res != null ){
            return res.getNode();
        }

        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getLinkRange( long fr, long to,
                      String detail, String format ){


        Log log = LogFactory.getLog( DipLegacyPortImpl.class );
        log.debug("DipSOAP:  Links Requested: from:" + fr + " to:" + to);
        
        if(detail== null || detail.equals("") ){
            detail="base";
        }

        DatasetType linkDS = DipDxfRecord.getLinkRange( fr, to, detail );
        
        if( linkDS != null && linkDS.getNode().size() > 0 ) {
            return linkDS.getNode();
        }

        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        setLink( edu.ucla.mbi.dxf14.DatasetType dataset,
                 java.lang.String mode ){

        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getSource( String ns, String ac,
                   String match, String detail, String format ){
        
        DatasetType res = null;

        if ( ns != null && (ns.equals( "dip" ) || ns.equals( "pubmed" ) )) {
            res = DipDxfRecord.getSource( ns, ac, detail );
        }
        
        if( res != null ) {
            return res.getNode();
        }

        return null;
    }
    
    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getSourceBounds( String detail, String format ){

        DatasetType ds = DipDxf.getSourceBounds();
        if(ds != null ){
            return ds.getNode();
        }
        
        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getNodeBounds( String detail, String format ){

        DatasetType ds = DipDxf.getNodeBounds();
        if(ds != null ){
            return ds.getNode();
        }

        return null;
    }
    
    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        setNode( edu.ucla.mbi.dxf14.DatasetType dataset,
                 java.lang.String mode ){

        return null;
    }
    
    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        setSource( edu.ucla.mbi.dxf14.DatasetType dataset,
                   java.lang.String mode ){

        return null;
    }

    public java.lang.String
        setImexSRec( SetImexSRec request ){

        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getLinksByNodeSet( DatasetType dataset,
                           String match, String detail, String format ){

        Log log = LogFactory.getLog( DipLegacyPortImpl.class );

        log.debug("Dataset: " + dataset);
        log.debug("Detail: " + detail);
        log.debug("Format: " + format );
        
        DatasetType linkDS = DipDxf.getLinksByNodeSet( dataset, 
                                                       match, detail );
        
        if( linkDS != null && linkDS.getNode().size() > 0 ) {
            return linkDS.getNode();
        }
        
        return null;
    }
    
    public java.lang.String
        getImexSRec( GetImexSRec request ){
        
        String ns = request.getNs();
        String ac = request.getAc();
        String format = request.getFormat();

        Log log = LogFactory.getLog( DipLegacyPortImpl.class );

        if( ns == null || ns.equals("") ){
            ns= "dip";
        }
        
        if( format == null || format.equals("") ||
            (format != null && format.equals("mif25") ) ){
            format= "mif254";
        }
        
        log.debug( "getImexSRec: NS=" + ns + " AC=" + ac );
        log.debug( "getImexSRec: Format=" + format );

        String isr = DipDxf.getImexSRec( ns, ac );
        return isr; 
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        transform( edu.ucla.mbi.dxf14.DatasetType dataset,
                   java.lang.String detail,
                   java.lang.String format,
                   java.lang.String expand,
                   edu.ucla.mbi.services.legacy.dip.OperationType operation,
                   java.lang.String limit ){

        return null;
    }
    
    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        dropSource( java.lang.String ns,
                    java.lang.String ac ){

        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        getNodeRange( long fr, long to,
                      String detail, String format ){

        Log log = LogFactory.getLog( DipLegacyPortImpl.class );
        log.debug("DipSOAP:  Nodes Requested: from:" + fr + " to:" + to);
        
        if(detail== null || detail.equals("") ){
            detail="base";
        }

        DatasetType nodeDS = DipDxfRecord.getNodeRange( fr, to, detail );
        
        if( nodeDS != null && nodeDS.getNode().size() > 0 ) {
            return nodeDS.getNode();
        }
        
        return null;
    }

    public java.util.List<edu.ucla.mbi.dxf14.NodeType>
        dropEvidence( java.lang.String ns,
                      java.lang.String ac ){
        return null;
    }
}
