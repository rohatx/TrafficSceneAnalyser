import overpy

def query_brigdes_nearby(latitude, longitude, radius):

    #API ist Overpass von OpenStreetMaps
    api = overpy.Overpass()

    # Query-Anfrage an den Overpass Server
    query = f"""
        [out:json];
        (
         way(around:{radius}, {latitude}, {longitude})["bridge"];
        );
        /*added by auto repair*/
        (._;>;);
        /*end of auto repair*/
        out;
        """
    #Checke, ob im Result bridge tags vorhanden sind
    result = api.query(query)
    #print(result.ways)
    if not result.ways:
        print("result is empty")
        return False
    if result.ways:
        #print("result has bridges")
        return True

value = query_brigdes_nearby(37.81713298488588, -122.47837630605842, 100)
print(value)

