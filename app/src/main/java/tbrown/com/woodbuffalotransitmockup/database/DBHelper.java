package tbrown.com.woodbuffalotransitmockup.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "new.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "MyActivity";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


        // you can use an alternate constructor to specify a database location
        // (such as a folder on the sd card)
        // you must ensure that this folder is available and you have permission
        // to write to it
        //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
    }

    private Cursor queryRoutes() {
        // Queries the routes table returning a cursor pointing to all bus routes

        SQLiteDatabase db = getReadableDatabase(); // Create open database in read mode

        // Setup query parameters
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder(); // class to help build queries
        String sqlTables = "routes"; // search the table called routes
        qb.setTables(sqlTables);
        String [] sqlSelect = {"route_id", "route_long_name"}; // return route_id and name from query
        String orderBy = "route_id ASC";

        // Run query
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, orderBy);
        c.moveToFirst();
        checkCursor(c); // indicates details of query in log under MyActivity TAG

        db.close();
        return c;
    }

    public String[] getAllRoutes() {
        // Returns a String Array of all bus routes where
        // each elmement contains the id and name of a route
        Cursor queryRoutes = queryRoutes();
        String[] result = DBUtils.queryToAllRoutes(queryRoutes);
        return result;
    };


    public Cursor queryStopsbyRoute(int routeId) {
        // Returns
        SQLiteDatabase db = getReadableDatabase();

        // Build query
        String query =
                "SELECT DISTINCT stops.stop_id, stops.stop_name FROM stops " +
                        "INNER JOIN " +
                        "(SELECT stop_id AS my_stops FROM stop_times WHERE trip_id " +
                        "IN (SELECT trip_id FROM trips WHERE route_id = " + routeId + ")) the_stops " +
                "ON the_stops.my_stops = stops.stop_id" +
                " ORDER BY stops.stop_name";
        Cursor c = db.rawQuery(query,null);
        Log.i("MyActivity", query);
        c.moveToFirst();
        checkCursor(c); // indicates details of query in log under MyActivity TAG
        db.close();
        return c;
    }

    public String[] getStopsByRoute(int routeId) {
        Cursor queryStops = queryStopsbyRoute(routeId);
        return DBUtils.queryToAllRoutes(queryStops);
    }

    public Cursor queryTimesByStop(int routeId,String serviceId,int directionId,int stopId) {
        // Return departure times at a given stop based on the route, time of week (service_id)
        // and direction of service (direction_id)

        SQLiteDatabase db = getReadableDatabase();

        // Build query
        String query = "SELECT * FROM " +
                "(SELECT stop_times.stop_id AS my_stops, stop_times.departure_time AS my_times FROM stop_times " +
                "INNER JOIN " +
                "(SELECT trip_id AS trips FROM trips WHERE route_id = " + routeId +
                " AND service_id =" + serviceId +
                //" AND direction_id =" + directionId +
                ")" +
                " my_trips" +
                " ON stop_times.trip_id = my_trips.trips)" +
                " WHERE my_stops =" + stopId +
                " ORDER BY my_times ";

        Log.i("MyActivity", query);

        // Run query
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        checkCursor(c); // indicates details of query in log under MyActivity TAG

        db.close();
        return c;
    }

    public String[] getTimes(int routeId,String serviceId,int directionId,int stopId) {
        Cursor queryTimes = queryTimesByStop(routeId,serviceId,directionId,stopId);
        checkCursor(queryTimes);
        return DBUtils.queryToTimes(queryTimes);
    }

//    public Cursor getTripsByRoute(int routeId) {
//
//        SQLiteDatabase db = getReadableDatabase();
//        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
//
//        String [] sqlSelect = {"trip_id"};
//        String sqlTables = "trips";
//        String where = "route_id = " + routeId;
//
//        qb.setTables(sqlTables);
//        Cursor c = qb.query(db, sqlSelect, where, null, null, null, null);
//        db.close();
//        c.moveToFirst();
//        checkCursor(c); // indicates details of query in log under MyActivity TAG
//        return c;
//    }

     /*   public Cursor getTripsGivenRoute(int routeId) {
        // Returns cursor pointing to all trips associated with given route_id
        SQLiteDatabase db = getReadableDatabase();

        // Setup query parameters
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String [] sqlSelect = {"trip_id"};
        String sqlTables = "trips";
        String where = "route_id = " + routeId;
        qb.setTables(sqlTables);

        // Run query
        Cursor c = qb.query(db, sqlSelect, where, null, null, null, null);
        c.moveToFirst();
        checkCursor(c); // indicates details of query in log under MyActivity TAG

        db.close();
        return c;
    }

    public Cursor getStopsbyTrips(String[] tripIDs) {
        // Returns all stops associated with a list of trips

        SQLiteDatabase db = getReadableDatabase();

        String trips = arrayToString(tripIDs); // convert trip_id array into string for use in query

        // Setup query
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String [] sqlSelect = {"stop_id"};
        String sqlTables = "trips";
        qb.setTables(sqlTables);

        // Run query
        Cursor c = qb.query(db, sqlSelect, trips, null, null, null, null);
        c.moveToFirst();
        checkCursor(c); // indicates details of query in log under MyActivity TAG
        db.close();
        return c;
    }*/



    private void checkCursor(Cursor c) {
        Log.i(TAG, "Validating the cursor");
        if (c.getCount()<1) {
            Log.i(TAG,"Query returned zero results.");
        } else {
            Log.i(TAG,"Query was successful.");
            Log.i(TAG,"" + c.getCount() + " rows returned...");
            Log.i(TAG, "" + c.getColumnCount() + " columns returned...");
        }
    }

    private String arrayToString(String[] array) {
        // Converts string array to a single string, each value seperated by comma

        String result = "=";

        for (int i = 0; i < array.length; i++) {
            result = result + array[i] + ", ";
        }
        result = result.substring(0,result.length()); // remove excess comma as last character
        return result.substring(0,result.length()-1);
    }

    private String queryToString(Cursor c) {
      String result = "";
      int noRows = c.getCount();

        for (int i = 0; i < c.getCount(); i++) {
            result = result + c.getString(0) + ", ";
            c.moveToNext();
        }
        return result;
    };
}
