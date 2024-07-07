# https://wiki.openstreetmap.org/wiki/Map_features
# documentation with tags

import overpy

def checkWay(latitude, longitude):
    radius = 100
    api = overpy.Overpass()

    # Checking all way tags, excluding tags that aren't passable for vehicles

    query = f"""
        [out:json];
        (
            way(around:{radius}, {latitude}, {longitude})
            [highway!=footway]
            [highway!=bridleway]
            [highway!=steps]
            [highway!=corridor]
            [highway!=path]
            [highway!=via_ferrata]
            [!aerialway]
            [!aeroway]
            [!railway]
            [!waterway]
        ;);
        out;
    """

    result = api.query(query)

    if result.ways:
        print("Location is on a way.")
        return True
    else:
        print("Location is not on a way.")
        return False

#latitude = 50
#longitude = 50

checkWay(latitude, longitude)
