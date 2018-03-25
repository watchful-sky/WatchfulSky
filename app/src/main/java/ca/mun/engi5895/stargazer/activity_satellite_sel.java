package ca.mun.engi5895.stargazer;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import org.orekit.errors.OrekitException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class activity_satellite_sel extends AppCompatActivity {

    private TextView mTextMessage;
    private ListView listView;
    private ProgressBar progressBar;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapterList;
    private ArrayAdapter<String> favoriteList;
    private static ArrayList<Object> selectedSats = new ArrayList<Object>();
    private static ArrayList<Object> favoriteSats = new ArrayList<Object>();
    private static String TLE1;
    private static String TLE2;
    private static Entity currentEntity = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_satellites);
                    try {
                        getSatsCreate();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    listView.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:

                    Context context = activity_satellite_sel.this;
                    Favorites fav = new Favorites(context);
                    adapterList = fav.getFavorites();

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            context,
                            android.R.layout.simple_list_item_1,
                            list);
                    //se the adapter
                    listView.setAdapter(favoriteList);
                    listView.setVisibility(View.INVISIBLE);
                    return true;

                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_nearby);




                    listView.setVisibility(View.INVISIBLE);
                    //progressBar.setProgress(1);
                    //progressBar.setVisibility(View.VISIBLE);
                    //progressBar.setActivated(true);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satellite_sel);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /*
        try {
            getSatsCreate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        listView = (ListView) findViewById(R.id.lvid2);
    }

    public void getSatsCreate() throws IOException {

        //open file stations.txt
        FileInputStream stream = openFileInput("stations.txt");
        InputStreamReader sreader = new InputStreamReader(stream);
        BufferedReader breader = new BufferedReader(sreader);

        // StringBuilder sb = new StringBuilder();

        String line;
        int lineNumber = 0;

        //While the next line exists, check to see if it is the 0th line or every 3rd line (satellite names)
        //If it is a name, add it to the list ArrayList
        while ((line = breader.readLine()) != null) {
            if ((lineNumber % 3 == 0) || (lineNumber == 0)) {
                list.add(line);
            }
            lineNumber++;
        }
        //Needed to convert it to a ListView
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                list);
        //se the adapter
        listView.setAdapter(arrayAdapter);
        //close streams
        breader.close();
        sreader.close();
        stream.close();

        //Class that handles clicking on a list item
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position); //Gets clicked option as java object
                selectedSats.add(o);
                System.out.println(o.toString()); //Output to console as string
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                //listView.setVisibility(listView.GONE); //Hide the list cause its no longer needed

                //Updates textview to the picked satellite name. Used for testing.
                //outSat = (TextView) findViewById(R.id.textView2);
                //outSat.setText(o.toString());
                //outSat.setVisibility(View.VISIBLE);


                //Start the re-parsing of the text file for the TLE data for chosen satellite
                FileInputStream stream1 = null;
                try {
                    stream1 = openFileInput("stations.txt"); //openFileInput auto opens from getFilesDir() directory
                    // getFilesDir() is directory of internal app storage
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                InputStreamReader sreader1 = new InputStreamReader(stream1);
                BufferedReader breader1 = new BufferedReader(sreader1);

                String line1;
                String TLE1 = new String();
                String TLE2 = new String();

                //Read each lne of file, if its equal to the one chosen from the list, update TLE strings and break loop
                try {
                    while ((line1 = breader1.readLine()) != null) {
                        if (line1.equals(o.toString())) { //If the current line is the one we chose from the list
                            TLE1 = breader1.readLine();
                            TLE2 = breader1.readLine();

                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    currentEntity = new Entity(TLE1, TLE2);
                } catch (OrekitException e) {
                    e.printStackTrace();
                }

                //Creating new entity

            }

        };
        listView.setOnItemClickListener(mMessageClickedHandler);
    }


    public static ArrayList<Entity> getSelectedSat() { //ArrayList<String> getSelectedSat(){
        ArrayList<Entity> list = null;
        if (currentEntity != null) {
            list.add(currentEntity);
        }


        return list;
    }

    public static ArrayList<Object> getFavoriteSats(){return favoriteSats;}

    public static void removeFavSat() {
        boolean found = false;

        for (int i = 0; i < favoriteSats.size(); i++){

        }

        if(found == false){
            //create dialog box
        }
    }

    public static void clearSatsList() {
        selectedSats.clear();
    }

    public void geoGo(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
