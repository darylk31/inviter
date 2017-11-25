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

    String cityName = "";
    String stateName = "";
    String countryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        mDatabaseReference = Utils.getDatabase().getReference();
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
                                getCityRegion();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            System.out.println("PROMOTION ACTIVITY GETLAST LOCATION FAILED");
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
            protected void populateViewHolder(PromotionViewHolder viewHolder, Promotion promotion, int position) {
            View cardView = viewHolder.cardView;
            System.out.println("Promotion activity detected promotion: " + promotion.getPromotionName());
            if(promotion.getpromotionEndDate() != null){
                try {
                    if(new SimpleDateFormat("yyyy-MM-dd").parse(promotion.getpromotionEndDate()).before(Utils.yesterday())){
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(promotion.getPromotionName() != null) {
                viewHolder.setPromotionName(promotion.getPromotionName());
            }
            if(promotion.getPromotionDetails() != null){
                viewHolder.setPromotionDescription(promotion.getPromotionDetails());
            }
            if(promotion.getPromotionRequirement() != null){
                viewHolder.setPromotionRequirement(promotion.getPromotionRequirement());
            }
            if(promotion.getpromotionStartDate() != null) {
                viewHolder.setPromotionStartDate(promotion.getpromotionStartDate());
                System.out.println(promotion.getpromotionStartDate());
            }
            if(promotion.getpromotionEndDate() != null) {
                viewHolder.setPromotionEndDate(promotion.getpromotionEndDate());
                System.out.println(promotion.getpromotionEndDate());
            }
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"Open up more details page on promotion",Toast.LENGTH_LONG).show();
                }
            });
            }
        };
        promotionRecycler.setAdapter(promotionRecyclerAdapter);
    }
    public static class PromotionViewHolder extends RecyclerView.ViewHolder{
        View cardView;
        TextView promotionName;
        TextView promotionDescription;
        TextView promotionStartDate;
        TextView promotionEndDate;
        TextView promotionRequirement;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            promotionName = cardView.findViewById(R.id.card_title);
            promotionDescription = cardView.findViewById(R.id.card_text);
            promotionStartDate = cardView.findViewById(R.id.promotion_card_start_date);
            promotionEndDate = cardView.findViewById(R.id.promotion_card_end_date);
        }
        public void setPromotionName(String name){
            promotionName.setText(name);
        }
        public void setPromotionDescription (String description) { promotionDescription.setText(description);}
        public void setPromotionEndDate(String promotionEndDate) {
            this.promotionEndDate.setText(promotionEndDate);
        }
        public void setPromotionStartDate(String startDate){
            this.promotionStartDate.setText(startDate);
        }
        public void setPromotionRequirement(String requirement) {
            this.promotionRequirement.setText("Required people: " + requirement);
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
