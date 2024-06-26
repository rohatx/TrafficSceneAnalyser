import overpy

def query_bridges_nearby(latitude, longitude):
    radius = 100
    
    # Api is Overpass von OpenStreetMaps
    api = overpy.Overpass()

    # Query Anfrage an Overpass Server 
    # Look for tag "bridge", excluding the tags other bridge tags that won't be underridable
    query = f"""
        [out:json];
        (
            way(around:{radius}, {latitude}, {longitude})
                ["bridge"]
                [bridge!=boardwalk]
                [bridge!=low_water_crossing]
                [bridge!=movable]  
        ;);
        /*added by auto repair*/
        (._;>;);
        /*end of auto repair*/
        out;
        """
    # Checke ob im Result bridge tags vorhanden sind
    result = api.query(query)

    if not result.ways:
       # print("result is empty")
        return False
    if result.ways:
       # print("result has bridges")
        return True

value = query_bridges_nearby(37.819912, -122.478661)
print(value)