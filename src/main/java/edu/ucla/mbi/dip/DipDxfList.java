package edu.ucla.mbi.dip;

/* =============================================================================
 # $Id:: DipDxfList.java 2191 2012-04-24 00:47:31Z lukasz                      $
 # Version: $Rev:: 2191                                                        $
 #==============================================================================
 #                                                                             $
 #  DipDxfList  - retrieves lists of dip records                               $
 #                                                                             $
 #    NOTES:   should return ttl info ?                                        $
 #                                                                             $
 #=========================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import edu.ucla.mbi.dip.*;
import edu.ucla.mbi.dxf14.*;
import edu.ucla.mbi.orm.*;

public class DipDxfList{
    
    static edu.ucla.mbi.dxf14.ObjectFactory dxfFactory
	= new edu.ucla.mbi.dxf14.ObjectFactory();
    
    public static List<XrefType> 
	getNodeList( String ns, String ac, String match, String detail ){
	
	Log log = LogFactory.getLog(DipDxf.class);
	log.debug("DipDxf: getNodeList called");
	
	List<DipNode> ndlist=new ArrayList<DipNode>();

	ac=ac.replaceAll("\\s","");
	
	log.info( "DipDxf:  Node Requested: ac=\"" + ac 
                  + "\" (ns=\"" + ns + "\")"
                  + " detail->" + detail + " match->" + match );
	
	if( ns.equals("dip") && ac.length()>0 ){
	    int nInd = (ac.toUpperCase()).lastIndexOf("N");
	    long dAcc = Long.valueOf( ac.replaceAll("[^0-9]", "" ) );
	    
	    if(dAcc>0 && nInd==ac.length()-1){
  
		// NODE ID: return self xref
		//--------------------------
		log.debug("DipDxf:  Getting DIP Node: ac=\"" + ac 
                          + "\"  (id=\"" + dAcc + "\")" );
		
		DipNode dipNode = DipDAO.node().find( dAcc );
		log.debug("DipDxf: dipNode "+ dipNode );
		
		if( dipNode != null ){
		    
		    List nl = new ArrayList();
		    nl.add( dipNode.getId() );
		    
		    List<Link> ll 
                        = DipDAO.link().findByNodeIdListMatch( nl );
		    
		    if( ll!=null ){
			for( Iterator lli = ll.iterator(); lli.hasNext(); ){
			    
			    Link cl = (Link) lli.next();

			    if(cl.getNodeCount()==1){
				// self-interactions
				for( Iterator lni = cl.getLnode()
                                         .iterator(); lni.hasNext(); ){
				    DipNode cn = ((LNode)lni.next()).getNode();
				    if( !ndlist.contains(cn) ){
					ndlist.add(cn);
				    }
				}
			    } else {
				// others
				for( Iterator lni = cl.getLnode()
                                         .iterator(); lni.hasNext(); ){
				    DipNode cn = ((LNode)lni.next()).getNode();
				    if( !ndlist.contains(cn) 
                                        && !cn.equals(dipNode) ){
					ndlist.add(cn);
				    }
				}
			    }
			}
		    }
		}
	    } 

	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("E")==ac.length()-1 ){

		// EDGE ID: return linked nodes
                //-----------------------------
		
                Link dipLink = DipDAO.link().find(dAcc);
		log.debug( "DipDxf: dipLink " + dipLink );

		if( dipLink!=null ){
		    for( Iterator lni = dipLink.getLnode()
                             .iterator(); lni.hasNext(); ){
			DipNode cn = ((LNode)lni.next()).getNode();
			if( !ndlist.contains(cn) ){
			    ndlist.add(cn);
			}
		    }
		}
	    }

	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("X")==ac.length()-1 ){
		
		// EDGE ID: return nodes reported for the evidence
                //------------------------------------------------
		
                Evidence dipEvid = DipDAO.evidence().find(dAcc);
		log.debug("DipDxf: dipEvid " + dipEvid);

		if( dipEvid!=null ){
		    for( Iterator lni = dipEvid.getLink().getLnode()
                             .iterator(); lni.hasNext(); ){
			DipNode cn = ((LNode)lni.next()).getNode();
                        if( !ndlist.contains(cn) ){
			    ndlist.add(cn);
			}
		    }
		}
	    }

	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("S")==ac.length()-1 ){

		// SOURCE ID: return nodes reported in the source
                //-----------------------------------------------
		
                DipDataSrc dipSrc = DipDAO.dipdatasrc().find(dAcc);
		log.debug( "DipDxf: dipLink " + dipSrc );
		
		if( dipSrc!=null ){
		    List<Evidence> evl = (List<Evidence>) DipDAO.evidence()
                        .findByDataSrc(dipSrc);
                    
		    for( Iterator<Evidence> evi = evl
                             .iterator(); evi.hasNext(); ){
			Evidence ev = evi.next();
			for(Iterator lni = ev.getLink().getLnode()
                                .iterator(); lni.hasNext(); ){
			    DipNode cn = ((LNode)lni.next()).getNode();
			    if( !ndlist.contains(cn) ){
				ndlist.add(cn);
			    }
			}
		    }
                }
	    }
	}
	
	List<XrefType> xlist = new ArrayList<XrefType>();
	if( ndlist.size()>0 ){
	    for( Iterator<DipNode> ndli = ndlist
                     .iterator(); ndli.hasNext(); ){
		DipNode cn = ndli.next();
		
		xlist.add( DipDxfUtil.createXref( "dip", cn.getAccession(),
                                                  "dxf", "dxf:0009",
                                                  "identical-to") );
	    }	       
	}
	return xlist;
    }

    //--------------------------------------------------------------------------
    // link list
    //----------

    public static List<XrefType> 
	getLinkList(String ns, String ac, String match, String detail){
	
	Log log = LogFactory.getLog( DipDxf.class );

        List<Link> lnlist = new ArrayList<Link>();
	ac=ac.replaceAll( "\\s", "" );
	
	log.info( "DipDxf:  Link Requested: ac=\"" 
                  + ac + "\"  (ns=\"" + ns + "\")"
                  + " detail->" + detail +" match->" + match );
	
	if( ns.equals("dip") && ac.length()>0 ){
	    int nInd = (ac.toUpperCase()).lastIndexOf("N");
	    long dAcc = Long.valueOf( ac.replaceAll("[^0-9]","") );
	    
	    if( dAcc>0 && nInd==ac.length()-1 ){
                
		// NODE ID: return all links
		//--------------------------
		log.debug( "DipDxf:  Getting DIP Node: ac=\"" 
                          + ac + "\"  (id=\"" + dAcc + "\")" );
		
		List nn = new ArrayList<Long>();
		nn.add( new Long(dAcc) );


                if( match == null || match.equals("") ||
                    match.equalsIgnoreCase("EXACT") ) {

                    lnlist = DipDAO.link().findByNodeIdListMatch( nn );
                    
                } else {
                    if( match.equalsIgnoreCase("EX0") ||
                        match.equalsIgnoreCase("EX0+")){
                        
                        // self link only
                        //---------------

                        lnlist = DipDAO.link().findByNodeIdListMatch( nn, 1 );
                        
                    } else {
                        if( match.toUpperCase().startsWith("EX1") ||
                            match.toUpperCase().startsWith("EX2") ){
                            
                            lnlist = DipDAO.link().findByNodeIdListMatch( nn, 2 );
                        }
                    }
                }
                
                if( match.equalsIgnoreCase("EX1+") &&
                    lnlist != null && lnlist.size()>0 ){

                    // add between direct
                    //-------------------
                    
                    List nidl = new ArrayList();
                    
                    for( Iterator<Link> pni = lnlist.iterator(); 
                         pni.hasNext(); ){
                        Link cl = pni.next();
                        for( Iterator<LNode> iln = cl.getLnode().iterator();
                             iln.hasNext(); ){

                            DipNode cn = iln.next().getNode();
                            if( !nidl.contains( cn.getId() ) ){
                                nidl.add( cn.getId() );
                                log.debug("ID(1)="+cn.getId());
                            }
                        }
                    }

                    List plist = DipDAO.link().findByNodeIdInList( nidl, 2, 2);
                    
                    log.info("EX1+ size=" + plist.size());

                    for( Iterator<Link> pni = plist.iterator(); 
                         pni.hasNext(); ){
                        Link cl = pni.next(); 
                        
                        if( !lnlist.contains(cl) ){
                            lnlist.add(cl);
                            log.debug("EX1+ ID=" + cl.getId() );
                        }
                    }
                    
                } else {
                
                    if( match.toUpperCase().startsWith("EX2") ){
                        
                        // two edges away
                        
                        List<DipNode> nList = new ArrayList<DipNode>();
                        
                        for( Iterator<Link> lni = lnlist.iterator(); 
                             lni.hasNext(); ){
                           
                            Link cl = lni.next();
                            
                            for( Iterator<LNode> iln = cl.getLnode().iterator();
                                 iln.hasNext(); ){

                                DipNode cn = iln.next().getNode();
                                if( !nList.contains( cn ) ){
                                    nList.add( cn );

                                    log.debug("EX2 NID=" + cl.getId() );
                                }
                            }
                        }
                        
                        lnlist = DipDAO.link().findByNodeIdInList( nList, 2 );
                        log.info("EX2 size=" + lnlist.size() );
                    }
                    
                    if( match.equalsIgnoreCase("EX2+") ){

                        // add between links
                        // -----------------

                        
                        
                    }
                }

		log.info( "DipDxf: dipLinks " + lnlist.size() );
                
		for( Iterator<Link> lni = lnlist
                         .iterator(); lni.hasNext(); ){
		    Link cl = lni.next();
		    
		    if( !lnlist.contains( cl ) ){
			lnlist.add( cl );
		    }
		}
	    } 

	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("E")==ac.length()-1 ){
		
		// EDGE ID: return self
                //----------------------
		
                Link dipLink = DipDAO.link().find(dAcc);
		log.debug( "DipDxf: dipLink "+dipLink);

		if( dipLink!=null && !lnlist.contains(dipLink) ){
		    lnlist.add( dipLink );
		}
	    }

	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("X")==ac.length()-1 ){

		// EVIDENCE ID: links supported by a given evidence
                //---------------------------------------------
		
                Evidence dipEvid = DipDAO.evidence().find(dAcc);
		log.debug( "DipDxf: dipEvid " + dipEvid);

		if( dipEvid!=null && !lnlist.contains( dipEvid.getLink() ) ){
		    lnlist.add( dipEvid.getLink() );
		}

		// links inferred from a given evidence
		//-------------------------------------

		List iflist = DipDAO.inference()
                    .findByGroundsId( dipEvid.getId() );

		for( Iterator ifi = iflist.iterator(); ifi.hasNext(); ){
                    Inference ci = (Inference)ifi.next();
		    
                    if( !lnlist.contains(ci.getLink()) ){
                        lnlist.add( ci.getLink() );
                    }
                }
		

	    }

	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("S")==ac.length()-1 ){
                
		// SOURCE ID: return nodes reported in the source
                //-----------------------------------------------
		
                DipDataSrc dipSrc = DipDAO.dipdatasrc().find(dAcc);
		log.debug( "DipDxf: dipLink " + dipSrc);
		
		if( dipSrc!=null ){
		    List<Evidence> evl = (List<Evidence>) DipDAO.evidence()
                        .findByDataSrc(dipSrc);
                    
		    for( Iterator<Evidence> evi = evl
                             .iterator(); evi.hasNext(); ){
			Evidence ev = evi.next();

			// links supported by a given evidence
			//-------------------------------------
			
			if( !lnlist.contains( ev.getLink() ) ){
			    lnlist.add( ev.getLink() );
			}

			// links inferred from a given evidence
			//-------------------------------------

		    }
                }
	    }
	}
	
	List<XrefType> xlist = new ArrayList<XrefType>();
	if( lnlist.size()>0 ){
	    for( Iterator<Link> lnli = lnlist.iterator(); lnli.hasNext(); ){
		Link cl = lnli.next();
		
		xlist.add( DipDxfUtil.createXref( "dip", cl.getAccession(),
                                                  "dxf", "dxf:0009",
                                                  "identical-to" ) );
	    }	       
	}
	return xlist;
    }

    //--------------------------------------------------------------------------
    // evidence list
    //--------------

    public static List<XrefType> 
	getEvidenceList( String ns, String ac, String match, String detail ){
	
	Log log = LogFactory.getLog(DipDxf.class);
	log.debug( "DipDxf: getNodeList called" );
	
	List<Evidence> evlist = new ArrayList<Evidence>();

	ac = ac.replaceAll("\\s","");
	
	log.debug( "DipDxf:  Evidence Requested: ac=\"" 
                  + ac + "\"  (ns=\"" + ns + "\") detail->"+detail );
	
	if( ns.equals("dip") && ac.length()>0 ){
	    int nInd = (ac.toUpperCase()).lastIndexOf("N");
	    long dAcc = Long.valueOf( ac.replaceAll( "[^0-9]", "" ) );
	    
	    if( dAcc>0 && nInd==ac.length()-1 ){
  
		// NODE ID: return evidence 
                // supporting all links with a given node
		//--------------------------------------------------------------
                
		log.debug( "DipDxf:  Getting DIP Node: ac=\"" 
                          + ac + "\"  (id=\"" + dAcc + "\")" );
		
		List nn = new ArrayList<Long>();
		nn.add( new Long(dAcc) );
		List<Link> lnlist = DipDAO.link().findByNodeIdListMatch(nn);
		log.debug( "DipDxf: dipLinks " + lnlist.size() );
		
		for( Iterator<Link> lni = lnlist
                         .iterator(); lni.hasNext(); ){
		    Link cl = lni.next();
		    List evl = DipDAO.evidence().findByLinkId( cl.getId() );

		    for( Iterator evi = evl.iterator(); evi.hasNext(); ){
			Evidence ev = (Evidence)evi.next();
			if( !evlist.contains(ev) ){
			    evlist.add(ev);
			}
		    }
		}
	    }
            
	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("E")==ac.length()-1 ){
		
		// EDGE ID: evidence supporting a given edge
                //------------------------------------------
		
                Link dipLink = DipDAO.link().find( dAcc );
		log.debug( "DipDxf: dipLink " + dipLink );
		
		if( dipLink!=null ){
                    
		    // direct evidence
		    
		    List evl = DipDAO.evidence().findByLinkId( dipLink.getId() );
		    for( Iterator evi = evl.iterator(); evi.hasNext(); ){
                        Evidence ev = (Evidence) evi.next();
                        if( !evlist.contains(ev) ){
                            evlist.add(ev);
                        }
                    }

		    // inference grounds

		    List ifl = DipDAO.inference()
                        .findByLinkId( dipLink.getId() );
                    for(Iterator ifi = ifl.iterator(); ifi.hasNext(); ){
                        Inference ci = (Inference) ifi.next();
                        if( !evlist.contains( ci.getGrounds() ) ){
                            evlist.add( ci.getGrounds() );
                        }
                    }
		}
	    }

	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("X")==ac.length()-1 ){
		
		// EVIDENCE ID: self
                //------------------
		
                Evidence dipEvid = DipDAO.evidence().find(dAcc);
		log.debug( "DipDxf: dipEvid " + dipEvid );
                
		if( dipEvid!=null && !evlist.contains(dipEvid) ){
		    evlist.add(dipEvid);
		}

                // links inferred from a given evidence
		//-------------------------------------
		
		
	    }

	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("S")==ac.length()-1 ){
		
		// SOURCE ID: return nodes reported in the source
                //--------------------------------------------------------------		

                DipDataSrc dipSrc = DipDAO.dipdatasrc().find(dAcc);
		log.debug( "DipDxf: dipLink " + dipSrc );
		
		if( dipSrc!=null ){
		    List evl = DipDAO.evidence().findByDataSrc(dipSrc);

		    for( Iterator evi=evl.iterator(); evi.hasNext(); ){
			Evidence ev = (Evidence) evi.next();
			
			if( !evlist.contains(ev) ){
			    evlist.add(ev);
			}
		    }
                }
	    }
	}
	
	List<XrefType> xlist = new ArrayList<XrefType>();
	if( evlist.size()>0 ){
	    for( Iterator<Evidence> evli = evlist.iterator(); evli.hasNext(); ){
		Evidence ce = evli.next();
		
		xlist.add( DipDxfUtil.createXref( "dip", ce.getAccession(),
                                                  "dxf", "dxf:0009",
                                                  "identical-to" ));
	    }	       
	}
	return xlist;
    }


    //--------------------------------------------------------------------------
    // source list
    //------------

    public static List<XrefType> 
	getSourceList( String ns, String ac, String match, String detail ){
	
	Log log = LogFactory.getLog(DipDxf.class);
	log.debug("DipDxf: getNodeList called");

       	List<DataSrc> dslist = new ArrayList<DataSrc>();
	ac=ac.replaceAll("\\s","");
	
	log.debug( "DipDxf:  Evidence Requested: ac=\"" 
                  + ac + "\"  (ns=\"" + ns + "\") detail->"+detail);
	
	if( ns.equals("dip") && ac.length()>0 ){
	    int nInd = (ac.toUpperCase()).lastIndexOf("N");
	    long dAcc = Long.valueOf(ac.replaceAll("[^0-9]",""));
	    
	    if( dAcc>0 && nInd==ac.length()-1 ){
                
		// NODE ID: return sources of all evidence 
                // supporting all links with a given node
		//--------------------------------------------------------------
                
		log.debug( "DipDxf:  Getting DIP Node: ac=\"" 
                          + ac + "\"  (id=\"" + dAcc + "\")");
		
		List nn = new ArrayList<Long>();
		nn.add( new Long(dAcc) );
		List<Link> lnlist = DipDAO.link().findByNodeIdListMatch(nn);
		log.debug( "DipDxf: dipLinks " + lnlist.size() );
		
		for( Iterator<Link> lni = lnlist.iterator(); lni.hasNext(); ){
		    Link cl = lni.next();
		    List evl = DipDAO.evidence().findByLinkId( cl.getId() );

		    for( Iterator evi=evl.iterator(); evi.hasNext(); ){
			Evidence ev = (Evidence)evi.next();
			if( !dslist.contains( ev.getDataSrc()) ){
			    dslist.add( ev.getDataSrc() );
			}
		    }
		}
	    }
	    
	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("E")==ac.length()-1 ){
		
		// EDGE ID: sources of all evidence supporting a given edge
                //--------------------------------------------------------------		

                Link dipLink = DipDAO.link().find(dAcc );
		log.debug( "DipDxf: dipLink " + dipLink );
		
		if( dipLink!=null ){
		    List evl = DipDAO.evidence()
                        .findByLinkId( dipLink.getId() );
		    for( Iterator evi=evl.iterator(); evi.hasNext(); ){
                        Evidence ev = (Evidence)evi.next();
                        if( !dslist.contains(ev.getDataSrc()) ){
                            dslist.add( ev.getDataSrc() );
                        }
                    }
		}
	    }

	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("X")==ac.length()-1 ){

		// EVIDENCE ID: source of the evidence
                //--------------------------------------------------------------
		
                Evidence dipEvid = DipDAO.evidence().find( dAcc );
		log.debug( "DipDxf: dipEvid " + dipEvid);

		if( dipEvid!=null && !dslist.contains( dipEvid.getDataSrc()) ){
		    dslist.add( dipEvid.getDataSrc() );
		}

	    }

	    if( dAcc>0 && (ac.toUpperCase()).lastIndexOf("S")==ac.length()-1 ){
		
		// SOURCE ID: self
                //----------------
		
                DipDataSrc dipSrc = DipDAO.dipdatasrc().find( dAcc );
		log.debug( "DipDxf: dipLink " + dipSrc );
		
		if( dipSrc!=null && !dslist.contains( dipSrc ) ){
		    dslist.add( dipSrc );
		}
	    }
	}
	
	List<XrefType> xlist = new ArrayList<XrefType>();
	if( dslist.size()>0 ){
	    for( Iterator<DataSrc> dsli = dslist.iterator(); dsli.hasNext(); ){
		DataSrc cs = dsli.next();

		xlist.add( DipDxfUtil.createXref( "dip", cs.getAccession(),
                                                  "dxf", "dxf:0009",
                                                  "identical-to" ) );
	    }	       
	}
	return xlist;
    }

}

