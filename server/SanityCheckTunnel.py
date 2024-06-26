# https://wiki.openstreetmap.org/wiki/Map_features
# documentation with tags

import overpy

def query_tunnels_nearby(latitude, longitude):
    radius = 100
    
    # Api is Overpass von OpenStreetMaps
    api = overpy.Overpass()

    # Query Anfrage an Overpass Server 
    # Look for tag "tunnel", excluding the tags that won't be underridable
    query = f"""
        [out:json];
        (
            way(around:{radius}, {latitude}, {longitude})
                ["tunnel"]
                [tunnel!=canal]
                [tunnel!=flooded]
                [tunnel!=culvert]
                [tunnel!=avalanche_protector]  
        ;);
        /*added by auto repair*/
        (._;>;);
        /*end of auto repair*/
        out;
        """
    # Checke ob im Result tunnel tags vorhanden sind
    result = api.query(query)

    if not result.ways:
        #print("result is empty")
        return False
    if result.ways:
        #print("result has tunnel")
        return True

value = query_tunnels_nearby(37.81960328096007, -122.47864979760193)
print(value)
             