/**
 * DipSOAPBindingImpl.java
 */

package edu.ucla.mbi.dip;

import edu.ucla.mbi.dxf14.*;
 
import java.util.*;
import java.io.*;
import org.w3c.dom.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.*;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;

import javax.xml.parsers.*;

public class DXF{

    public static DatasetType reconcile(DatasetType  dxf){
       	return dxf;
    } 

    public static List<NodeType> findEqNode(DatasetType  dxf, NodeType nd){

        List ndLst = new ArrayList();       

	List<NodeType> nds0= dxf.getNode();
        Log log = LogFactory.getLog( DXF.class );
        log.debug("    ---findEqNode -> "+ nd.getAc());  

	for(int n0i= 0; n0i<nds0.size(); n0i++){

	    NodeType cn0= nds0.get(n0i);

	    log.debug("     testing -> "+ cn0.getAc()); 

	    List eql = getEqv(cn0,nd);
	    
            log.debug("     eql.size() -> "+ eql.size());
	    if(eql.size()>0){
		ndLst.addAll(eql);
	    }
	}
        log.debug("    ---findEqNode: done with ->"+ nd.getAc()+"\n");
	return ndLst;
    } 
    
    public static List<NodeType> getEqv(NodeType tst, NodeType ref){
	
	List<NodeType> cl = new ArrayList<NodeType>();
        Log log = LogFactory.getLog( DXF.class );
	log.debug("  getEqv -> "+ ref.getAc());  

	if(tst.getAc().equals(ref.getAc()) &&
	   tst.getNs().toLowerCase().equals(ref.getNs().toLowerCase())){

	    log.debug("    getEqv: direct match...");
	    cl.add(tst);
	} else {

	    //<node ns="pubmed" id="3" ac="9420330">
	    // <type ns="dxf" name="data-source" ac="dxf:0016"/>
	    // <label>pubmed:9420330</label>
	    // <xrefList>
            //  <xref typeNs="dxf" typeAc="dxf:0019" type="describes" ns="uniprot" ac="A2QWI7">
            //    <node ns="uniprot" id="1" ac="A2QWI7">
            //       <type ns="dxf" name="protein" ac="dxf:0003"/>
            //       <label>A2QWI7_ASPNG</label>
            //       <name>Complex: teh mediator complex is a subcomplex of the RNA polymerase II holoenzyme</name>
            //       <xrefList>
            //        <xref typeNs="dxf" typeAc="dxf:0007" type="produced-by" ns="TaxId" ac="5061">
	    //         <node ns="TaxId" id="2" ac="5061">
	    //          <type ns="dxf" name="organism" ac="dxf:0017"/>
	    //          <label>Aspergillus niger</label>
	    //          <name/>
	    //         </node>
	    //        </xref>
	    //        <xref typeNs="dxf" typeAc="dxf:0006" type="instance-of" ns="uniprot" ac="A2QWI7"/>
	    //       </xrefList>
	    //    </node>
            //  </xref>
            //</node>

            log.debug("    getEqv:  no direct match... going over children");

	    // check if tst is datasource
            //---------------------------

            if(tst.getType().getNs().equals("dxf") &&   
	       tst.getType().getAc().equals("dxf:0016")){   
		
		log.debug("    getEqv:  descending data source xrefs...");

		// yes -> lok for nodes within xrefs and test theese...
		//-----------------------------------------------------

		if(tst.getXrefList()!=null && tst.getXrefList().getXref().size()>0){
		    log.debug("    getEqv: xrefs nonempty");
		    
		    List<XrefType> txl=tst.getXrefList().getXref();

		    for(int txi= 0; txi<txl.size(); txi++){
			
			XrefType cx=txl.get(txi);
			NodeType cn=cx.getNode();
			
			if(cn!=null){

			    log.debug("    getEqv:  found: "+cn.getAc());
		    
			    if(cn!=null){
				List<NodeType> eql = DXF.getEqv(cn, ref);
				cl.addAll(eql);

				// NOTE: reset xref ns/ac to eql node ns/ac...
				// NOTE: should be done outside...

				if(eql.size()>0){
				    cx.setAc(eql.get(0).getAc());
				    cx.setNs(eql.get(0).getNs());
				}
			    }
			}
		    }
		}
	    } else {

                log.debug("    getEqv:  testing ref Xrefs: "+ref.getAc()); 

		if(ref.getXrefList()!=null && ref.getXrefList().getXref().size()>0){ 
		    List<XrefType> xl=ref.getXrefList().getXref();
		
		    for(int xi= 0; xi<xl.size(); xi++){
			XrefType cx=xl.get(xi);
			
			if(cx.getAc().equals(tst.getAc()) &&
			   cx.getNs().toLowerCase().equals(tst.getNs().toLowerCase())){

                            log.debug("    getEqv: tst-> "+tst.getAc()+" xref-> "+cx.getAc());
			    cl.add(tst);
			    log.debug("    getEqv: xref match...");
			    break;
			}
		    }
		}
	    }
	}
        log.debug("   getEqv done: "+cl.size());
	return cl;
    }
}

    /*
    public Document transform(Document request)
        throws java.rmi.RemoteException {
        Log log = LogFactory.getLog( this.getClass() );
        log.debug("DipSOAP: transform called");

	// String match = getSingleParameter(request, "match" ,"exact");
        String retTP = getSingleParameter(request, "return","base");
        String expTP = getSingleParameter(request, "expand","0");

	int mode=0;
	if(retTP.equals("stub")){ mode=0; }
	if(retTP.equals("base")){ mode=1; }
	if(retTP.equals("mit" )){ mode=2; }

	List ndList= new ArrayList();

	Document doc= null;
	
	//org.w3c.dom.Node nodeSet = getSingleNode(request,"dataset");

	DatasetType reqSet
	    = req2dts(getSingleNode(request,"dataset"),logger);

	int cn=0;
        long maxId=-1;

	// go over data source nodes 
        // 
        //   - id pmid nodes present look/add proteins 
        //     & interactions
        
        for(Iterator i=reqSet.getNode().iterator();i.hasNext();){

            NodeType dxfNode= (NodeType)i.next();

            log.debug("DipSOAP:    ------------------------ ");
            log.debug("DipSOAP:    node["+cn+"]");
            log.debug("DipSOAP:    node(label): "+dxfNode.getLabel());

            if(dxfNode.getId()>maxId){ maxId=dxfNode.getId();}
            cn++;

	    // test if data source
            //--------------------

            if(dxfNode.getType().getNs().equals("dxf") &&
               dxfNode.getType().getAc().equals("dxf:0016")){
        

		// search for node
                //----------------
                DipNode dipNode = null;

                String qns = dxfNode.getNs();
                String qac = dxfNode.getAc();


                // by PMID accession
                //------------------

                if(qns.toLowerCase().equals("pubmed") &&
                   !qac.equals("")){

                    int dAcc=Integer.valueOf(qac.replaceAll("[^0-9]",""));

                    if(dAcc>0){

                        log.debug("DipSOAP:  Getting DIP Node by PMID: ac=\""+
                                     qac+"\"  (id=\""+dAcc+"\")");

                        if(nDAO==null){
                            log.debug("DipSOAP:   Creating NodeDAO...");
                            nDAO=new NodeDAO();
                            log.debug("DipSOAP:   ...done");
                        }
                        
			List ndl= nDAO.findByXref(qns,qac);

			if(ndl.size()>0){
			    for(Iterator ni=ndl.iterator();ni.hasNext();){
				DipNode cDipNode= (DipNode) ni.next();

                                // append node as xref 
                                //--------------------

                                log.debug("DipSOAP:  node found: "+cDipNode.getAccession());  

				//<ns1:xref ac="9606" ns="TaxId" 
                                //          type="produced-by" 
                                //          typeAc="dxf:0007" typeNs="dxf">

				dxfAddXref(dxfNode,
					   "dip",cDipNode.getAccession(),   // ns , ac
                                           "dxf","dxf:0019","describes",    // typeNs,typeAc,type
					   dip2dxf(cDipNode,maxId++,mode));        

			    }
			}

                        
			if(lDAO==null){
                            log.debug("DipSOAP:   Creating LinkDAO...");
                            lDAO=new LinkDAO();
                            log.debug("DipSOAP:   ...done");
                        }
                        

			log.debug("DipSOAP:   trying findByPubMedId: "+qac);

			List lnl= lDAO.findByPubMedId(qac);

			if(lnl.size()>0){
			    for(Iterator li=lnl.iterator();li.hasNext();){
				Link cLink= (Link) li.next();

                                // append link 
                                //------------
				
				dxfAddXref(dxfNode,
                                           "dip",cLink.getAccession(),   // ns , ac
                                           "dxf","dxf:0019","describes",    // typeNs,typeAc,type
                                           dip2dxf(cLink,maxId++,mode));				
				
				log.debug("DipSOAP:  link found: "+cLink.getAccession());  
			    }
			}
                        
		    }
		}
	    }
	}    

   
        // go over protein nodes
        //   - if dip id present look for node, read in from db, replace
        //     or remove, if not known
               
	// go over nodes
	//--------------

	for(Iterator i=reqSet.getNode().iterator();i.hasNext();){
	    
	    NodeType dxfNode= (NodeType)i.next();
	    
	    log.debug("DipSOAP:    ------------------------ "); 
	    log.debug("DipSOAP:    node["+cn+"]");
	    log.debug("DipSOAP:    node(label): "+dxfNode.getLabel()); 
	    
	    if(dxfNode.getId()>maxId){ maxId=dxfNode.getId();}
	    cn++;
	    
	    // test if protein 
	    //----------------

	    if(dxfNode.getType().getNs().equals("dxf") &&
	       dxfNode.getType().getAc().equals("dxf:0003")){
		
		//String ndLabel=dxfNode.getLabel();
		//String ndName =dxfNode.getName();

		// search for node
		//----------------
		DipNode dipNode = null;

		String qns = dxfNode.getNs();
		String qac = dxfNode.getAc();


		// by DIP accession
		//-----------------

		if(qns.toLowerCase().equals("dip") &&
		   !qac.equals("")){
		    
		    int nInd=(qac.toUpperCase()).lastIndexOf("N");
		    int dAcc=Integer.valueOf(qac.replaceAll("[^0-9]",""));

		    if(dAcc>0 && nInd==qac.length()-1){
  
			log.debug("DipSOAP:  Getting DIP Node: ac=\""+qac+"\"  (id=\""+dAcc+"\")");
			
			if(nDAO==null){
			    log.debug("DipSOAP:   Creating NodeDAO...");
			    nDAO=new NodeDAO();
			    log.debug("DipSOAP:   ...done");
			}
			dipNode= nDAO.find(dAcc);
		    } 
		}

		// by RefSeq
		//----------
     
		if(dipNode==null && qns.toLowerCase().equals("refseq") &&
		   !qac.equals("")){
     
		    log.debug("DipSOAP:  Getting DIP Node: RefSeq=\""+qac+"\"");

		    if(nDAO==null){
			log.debug("DipSOAP:   Creating NodeDAO...");
			nDAO=new NodeDAO();
			log.debug("DipSOAP:   ...done");
		    }

		    List ndl= nDAO.findByRefSeq(qac);

		    if(ndl.size()>0){
			dipNode= (DipNode) ndl.get(0);
		    }
		}

		// by UniProt
		//-----------
		
		if(dipNode==null && qns.toLowerCase().equals("uniprot")){

		    log.debug("DipSOAP:  Getting DIP Node: UniProt=\""+qac+"\"");
		    
		    if(nDAO==null){
			log.debug("DipSOAP:   Creating NodeDAO...");
			nDAO=new NodeDAO();
			log.debug("DipSOAP:   ...done");
		    }
		    
		    List ndl= nDAO.findByUniProt(qac);
		    if(ndl.size()>0){
			dipNode= (DipNode) ndl.get(0);
		    }
		}
		
		// by other xref
		//--------------

		if(dipNode==null){

		    log.debug("DipSOAP:  Getting DIP Node: "+qns+"=\""+qac+"\"");

		    if(nDAO==null){
			log.debug("DipSOAP:   Creating NodeDAO...");
			nDAO=new NodeDAO();
			log.debug("DipSOAP:   ...done");
		    }

		    List ndl= nDAO.findByXref(qns,qac);

		    if(ndl.size()>0){
			dipNode= (DipNode) ndl.get(0);
		    }
		}
		
		if(dipNode!=null){

		    // ammend the node with DIP-derived info if one got found
		    //--------------------------------------------------------
		    
		    dxfMod(dxfNode,dipNode,mode,qns,qac);
		} else {
		    
		}
	    }
	    
	    // res.getNode().add(dip2dxf(nNd,_id++,1));
	}
	

	// update maxId

	for(Iterator i=reqSet.getNode().iterator();i.hasNext();){
            NodeType dxfNode= (NodeType)i.next();
	    if(dxfNode.getId()>maxId){ maxId=dxfNode.getId();}
	}

	
        // go over edges
        //   - if vertices missing add
	
	for(Iterator i=reqSet.getNode().iterator();i.hasNext();){

            NodeType dxfNode= (NodeType)i.next();
	    
            log.debug("DipSOAP:    ------------------------ ");
            log.debug("DipSOAP:    node["+cn+"]");
            log.debug("DipSOAP:    node(label): "+dxfNode.getLabel());

            String ndTest=null;

            cn++;

            // test if link
            //-------------

	    if(dxfNode.getType().getNs().equals("dxf") &&
	       dxfNode.getType().getAc().equals("dxf:0004")){

                //String ndLabel=dxfNode.getLabel();
                //String ndName =dxfNode.getName();

		edu.ucla.mbi.dip.Link dipLink = null;

                
		String qns = dxfNode.getNs();
		String qac = dxfNode.getAc();

 
                // test if DIP interaction
		//------------------------

                if(qns.toLowerCase().equals("dip") &&
		   !qac.equals("")){
		    
		    // search by DIP accession
		    //------------------------
		    
		    int lInd=(qac.toUpperCase()).lastIndexOf("E");
		    int dAcc=Integer.valueOf(qac.replaceAll("[^0-9]",""));
		    
		    if(dAcc>0 && lInd==qac.length()-1){

			log.debug("DipSOAP:  Getting DIP Link: "+
				    "ac=\""+qac+"\"  (id=\""+dAcc+"\")");

			if(lDAO==null){
			    log.debug("DipSOAP:   Creating LinkDAO...");
			    lDAO=new LinkDAO();
			    log.debug("DipSOAP:   ...done");
			}
			dipLink= lDAO.find(dAcc);
		    }
		}
		
                // test if IMEx interaction
		//-------------------------
		
                if(dipLink== null && qns.toLowerCase().equals("imex") &&
		   !qac.equals("")){
		    
		    // search by IMEx accession
		    //-------------------------
		    
		    int lInd=(qac.toUpperCase()).lastIndexOf("IM-");
		    long dAcc=Long.valueOf(qac.replaceAll("[^0-9]",""));
		    
		    if(dAcc>0){
			
			log.debug("DipSOAP:  Getting DIP Link (by IMEx Id): "+
				    "ac=\""+qac+"\"  (id=\""+dAcc+"\")");

			if(lDAO==null){
			    log.debug("DipSOAP:   Creating LinkDAO...");
			    lDAO=new LinkDAO();
			    log.debug("DipSOAP:   ...done");
			}

                        List linkLst= lDAO.findByImexId(dAcc);

                        if(linkLst!=null && linkLst.size()>0){
			    dipLink = (Link)linkLst.get(0);
			}
		    }
		}
		if(dipLink!=null){

		    // ammend the node with DIP-derived info if one got found
		    //--------------------------------------------------------
		    
		    maxId=dxfMod(dxfNode,dipLink,mode,maxId,qns,qac);
		}
	    }
	}

        // if expand 
        // - go over protein nodes and add edges to all the nodes


	if(expTP.equals("1")){
	
	    log.debug("\nDipSOAP:    Expanding: level->"+expTP+"\n"); 

	    List expList = new ArrayList();  // expansion buffer

	    for(Iterator i=reqSet.getNode().iterator();i.hasNext();){
	    
		NodeType dxfNode= (NodeType) i.next();
	    
		log.debug("DipSOAP:    ------------------------ "); 
		log.debug("DipSOAP:    node["+cn+"]");
		log.debug("DipSOAP:    node(label): "+dxfNode.getLabel()); 
		
		if(dxfNode.getId()>maxId){ 
		    maxId=dxfNode.getId();
		}
		cn++;
	   
		// test if protein 
		//----------------

		if(dxfNode.getType().getNs().equals("dxf") &&
		   dxfNode.getType().getAc().equals("dxf:0003")){
		    
		    // by DIP accession
		    //-----------------
		
		    if(dxfNode.getNs().toLowerCase().equals("dip") &&
		       !dxfNode.getAc().equals("")){
		    
			int nInd=(dxfNode.getAc().toUpperCase()).lastIndexOf("N");
			int dAcc=Integer.valueOf(dxfNode.getAc().replaceAll("[^0-9]",""));

			if(dAcc>0 && nInd==dxfNode.getAc().length()-1){
  
			    log.debug("DipSOAP:  Getting DIP Node: ac=\""+
					dxfNode.getAc()+
					"\"  (id=\""+dAcc+"\")");
			
			    if(nDAO==null){
				log.debug("DipSOAP:   Creating NodeDAO...");
				nDAO=new NodeDAO();
				log.debug("DipSOAP:   ...done");
			    }

			    // search for node
			    //----------------

			    DipNode dipNode = nDAO.find(dAcc);

			    if(dipNode!=null){
			
				if(lDAO==null){
				    log.debug("DipSOAP:   Creating LinkDAO...");
				    lDAO=new LinkDAO();
				    log.debug("DipSOAP:   ...done");
				}

				logger.debug("DipSOAP:    got node... looking for edges\n");

				List ndId = new ArrayList();
				ndId.add(new Long(dipNode.getId()));
				
				List lList2=lDAO.findByNodeIdListMatch(ndId,2);
			    
				if(lList2!=null && lList2.size()>0){
				
				    for(Iterator li=lList2.iterator();li.hasNext();){
					Link  cl= (Link) li.next();				    
					expList.add(cl);
					// reqSet.getNode().add(dip2dxf(cl,maxId++,mode));
				    }
				}

				List lList1=lDAO.findByNodeIdListMatch(ndId,1);
				
				if(lList1!=null && lList1.size()>0){
				
				    for(Iterator li=lList1.iterator();li.hasNext();){
					Link  cl= (Link) li.next();
					expList.add(cl);
					// reqSet.getNode().add(dip2dxf(cl,maxId++,mode));
				    }
				}
			    }
			}   
			log.debug("DipSOAP:   processed  node...\n");
		    }		
		}
	    }
	
	    log.debug("\nDipSOAP:    Expansion buffer collected:");

	    if(expList!=null && expList.size()>0){
		log.debug("DipSOAP:     buffer size:"+expList.size());

		for(Iterator li=expList.iterator();li.hasNext();){
		    Link  cl= (Link) li.next();
		    //expList.add(cl);
		    log.debug("DipSOAP:     "+cl);
		    
		    reqSet.getNode().add(dip2dxf(cl,maxId++,mode));
		}
	    }
	
	    log.debug("\nDipSOAP:    Expanding DONE\n");
	}
	
	reqSet=DXF.reconcile(reqSet);   

	log.debug("DipSOAP: DONE:transform");
	return dts2doc(reqSet,logger);
    }
*/
