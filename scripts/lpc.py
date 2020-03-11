import sys

from http.client import HTTPSConnection, HTTPResponse
import json
import re
import ssl
#import dxf as DXP
#import dippy as DP
import time

from requests import Session
from requests.auth import HTTPBasicAuth

#import pymex.psimi

import zeep
from zeep import Client as zClient, Settings as zSettings
from zeep.transports import Transport

from lxml import etree as ET

dxfns = { 'dxf': 'http://dip.doe-mbi.ucla.edu/services/dxf15' }

url = 'http://10.1.2.2:8080/dip-legacy/dxf?wsdl'

print(url)

zsettings = zSettings( strict=False, xml_huge_tree=True,
                       raw_response=True )
        
zsession = Session()
zsession.verify = False

zclient = zClient(url,
                  settings=zsettings,                                
                  transport=Transport( session=zsession) )

dxfactory = zclient.type_factory('ns1')
ssfactory = zclient.type_factory('ns0')
        
print(zclient)

try:                                    
    r_query = zclient.service.query( 'P60010', 'foo', 'mif25' )
    print( r_query )

except Exception as e:
    print( type(e) )
                 
