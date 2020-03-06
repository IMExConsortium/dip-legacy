package edu.ucla.mbi.dip;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.*;
import java.util.*;

class NamespaceContextImpl
    implements NamespaceContext{

    Log log = LogFactory.getLog(NamespaceContextImpl.class);
    
    public String uri;
    public String prefix;

    public String getUri(){
        return uri;
    }

    public void setUri(String uri){
        this.uri=uri;
    }

    public String getNamespaceURI(String prefix){
        log.debug("getNamespaceURI: prefix "+uri);
        return uri;
    }

    public String getPrefix(String uri){
        log.debug("getNamespaceURI: uri "+uri);
        return prefix;
    }

    public Iterator getPrefixes(String uri){
        log.debug("getPrefixes: uri "+uri);
        return null;
    }

    public void setPrefix(String prefix){
        this.prefix=prefix;
    }
}
