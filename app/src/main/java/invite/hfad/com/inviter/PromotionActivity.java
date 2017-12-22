package invite.hfad.com.inviter;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PromotionActivity extends AppCompatActivity {

    private TextView noPromotionText;
    private RecyclerView promotionRecycler;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<Promotion, PromotionViewHolder> promotionRecyclerAdapter;

    private DatabaseReference mDatabaseReference;

    private FusedLocationProviderClient mFusedLocationClient;
    private Toolbar myToolbar;
    String cityName = "";
    String stateName = "";
    String countryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        mDatabaseReference = Utils.getDatabase().getReference();
        //Set up custom toolbar
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        promotionRecycler = findViewById(R.id.promotion_recycler);
        noPromotionText = findViewById(R.id.tvNoPromotion);
        promotionRecycler.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        promotionRecycler.setLayoutManager(linearLayoutManager);
        //Check for last known location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                        Geocoder geocoder = new Geocoder(PromotionActivity.this, Locale.getDefault());
                            try {
                                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                cityName = addressList.get(0).getLocality().trim();
                                stateName = addressList.get(0).getAdminArea().trim();
                                countryName = addressList.get(0).getCountryName().trim();
                                System.out.println("Promotion Activity: " + cityName + " " + stateName + " " + countryName);
                                getCityRegion();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            noPromotionText.setText("Location not detected");
                            System.out.println("PROMOTION ACTIVITY GET LAST LOCATION FAILED");
                        }
                    }
                });
    }

    private void getCityRegion(){
        //Use city to find what the city region is
        mDatabaseReference.child(Utils.REGION_DATABASE).child(countryName).child(stateName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getKey().equalsIgnoreCase(cityName)){
                        String region = (String) ds.getValue();
                        myToolbar.setTitle(region);
                        checkForPromotionText(region);
                        downloadPromotions(region);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void downloadPromotions(String region){
        promotionRecyclerAdapter = new FirebaseRecyclerAdapter<Promotion, PromotionViewHolder>(
                Promotion.class,
                R.layout.promotion_item_layout,
                PromotionViewHolder.class,
                mDatabaseReference.child(Utils.PROMOTION_DATABASE).child(countryName).child(stateName).child(region).limitToLast(50)) {
            @Override
            protected void populateViewHolder(final PromotionViewHolder viewHolder, Promotion promotion, int position) {
            View cardView = viewHolder.cardView;
            System.out.println("Promotion activity detected promotion: " + promotion.getName());
            if(promotion.getEndDate() != null){
                try {
                    if(new SimpleDateFormat("yyyy-MM-dd").parse(promotion.getEndDate()).before(Utils.yesterday())){
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            viewHolder.setId(promotion.getId());
            if(promotion.getName() != null) {
                viewHolder.setName(promotion.getName());
            }
            if(promotion.getDescription() != null){
                viewHolder.setDescription(promotion.getDescription());
            }
            if(promotion.getRequirementNumber() != null){
                viewHolder.setRequirementNumber(promotion.getRequirementNumber());
            }
            if(promotion.getStartDate() != null) {
                viewHolder.setStartDate(promotion.getStartDate());
                System.out.println(promotion.getStartDate());
            }
            if(promotion.getEndDate() != null) {
                viewHolder.setEndDate(promotion.getEndDate());
                System.out.println(promotion.getEndDate());
            }
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"Open up more details page on promotion." + viewHolder.getId(),Toast.LENGTH_LONG).show();
                }
            });
            }
        };
        promotionRecycler.setAdapter(promotionRecyclerAdapter);
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder{
        View cardView;
        TextView Name;
        TextView StartDate;
        TextView EndDate;
        TextView RequirementNumber;
        TextView Code;
        TextView WebsiteUrl;
        TextView LogoUrl;
        TextView Description;
        TextView promotionViews;
        TextView promotionUsed;
        String Id;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            Name = cardView.findViewById(R.id.card_title);
            Description = cardView.findViewById(R.id.card_text);
            StartDate = cardView.findViewById(R.id.promotion_card_start_date);
            EndDate = cardView.findViewById(R.id.promotion_card_end_date);
        }
        public void setName(String name){
            Name.setText(name);
        }
        public void setDescription (String description) { Description.setText(description);}
        public void setEndDate(String EndDate) {
            this.EndDate.setText(EndDate);
        }
        public void setStartDate(String startDate){
            this.StartDate.setText(startDate);
        }
        public void setRequirementNumber(String requirement) {
            this.RequirementNumber.setText("Required people: " + requirement);
        }

        public void setId(String Id) {
            this.Id = Id;
        }
        public String getId(){
            return Id;
        }
    }

    /**
     * Checks firebase database to see if promotion exists given city and month.
     * Display text to no promotion if no promotion exists
      */
    private void checkForPromotionText(String region){
        mDatabaseReference.child(Utils.PROMOTION_DATABASE).child(countryName).child(stateName).child(region).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()){
                    noPromotionText.setVisibility(View.VISIBLE);
                } else{
                    noPromotionText.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
