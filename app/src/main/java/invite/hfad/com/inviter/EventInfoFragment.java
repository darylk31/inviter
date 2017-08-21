package invite.hfad.com.inviter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class EventInfoFragment extends Fragment {

    private String id;
    private String event_string;
    private View view;
    private TextView event_location;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id = getArguments().getString("event_id");
        return inflater.inflate(R.layout.fragment_event_info, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        view = getView();
        setEventPicture();
        try {
            SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(this.getContext());
            SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
            Cursor cursor = event_db.rawQuery("SELECT * FROM EVENTS WHERE EID LIKE '" + id + "';", null);
            cursor.moveToLast();
            TextView event_name = (TextView) view.findViewById(R.id.tv_eventpagename);
            this.event_string = cursor.getString(4);
            event_name.setText(event_string);
            TextView event_date = (TextView) view.findViewById(R.id.tv_eventpagedate);
            TextView event_time = (TextView) view.findViewById(R.id.tv_eventpagetime);
            String event_day = cursor.getString(2);
            //TODO: Select just date.
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(event_day);
                String output_day = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                String output_month = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date);
                String output_year = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date);
                String output_time = new SimpleDateFormat("KK:mm a", Locale.ENGLISH).format(date);
                event_date.setText(output_month + " " + output_day + ", " + output_year);
                event_time.setText(output_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            event_location = (TextView) view.findViewById(R.id.tv_eventpageloc);
            event_location.setText(cursor.getString(6));
            TextView event_description = (TextView) view.findViewById(R.id.tv_eventpagedescrip);
            event_description.setText(cursor.getString(5));
            cursor.close();
            event_db.close();

        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this.getContext(), "Error: Event unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        onEventOptions();
        onEventInvite();

        event_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(event_location.getText() != null){
                    String map = "http://maps.google.co.in/maps?q=" + event_location.getText();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(map)));
                }



            }
        });

        TextView tv_members = (TextView) view.findViewById(R.id.tv_eventinfomembers);
        tv_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("event_id", id);
                EventMembersFragment eventMembersFragment = new EventMembersFragment();
                eventMembersFragment.setArguments(args);
                FragmentManager fm = getFragmentManager();
                //FragmentTransaction ft = fm.beginTransaction();ft.replace(R.id.eventinfo_container, eventMembersFragment, "member");
                //ft.addToBackStack("member");
                //ft.commit();
            }
        });

        arrowAnimation();
    }

    private void setEventPicture() {
        final ImageView EventPictureView = (ImageView) view.findViewById(R.id.event_image);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("events/" + id + ".jpg");

        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(EventInfoFragment.this)
                            .load(task.getResult())
                            .into(EventPictureView);
                }
                else
                    Glide.with(EventInfoFragment.this)
                        .load(R.drawable.event_camera)
                            .dontAnimate()
                            .into(EventPictureView);
            }
        });


        EventPictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(R.array.Picture_Options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 1);
                                break;
                            case 1:
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
        }
        if (resultCode == RESULT_OK) {
            final ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setMessage("Uploading image...");
            dialog.show();
            Uri selectedimg = data.getData();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            UploadTask task = storageRef.child("events/" + id + ".jpg").putFile(selectedimg);
            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    setEventPicture();
                    dialog.dismiss();
                }
            });
        }
    }


    public void onEventOptions() {
        getView().findViewById(R.id.tv_eventinfooptions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventOptionsDialogFragment optionsDialogFragment = new EventOptionsDialogFragment();
                Bundle args = new Bundle();
                args.putString("id",id);
                optionsDialogFragment.setArguments(args);
                android.app.FragmentManager fm = getActivity().getFragmentManager();
                optionsDialogFragment.show(fm,"dialog");
            }
        });

    }

    private void onEventInvite() {
        getView().findViewById(R.id.tv_eventinfoinvite);
    }

    private void arrowAnimation(){
        final ImageView arrows = (ImageView) view.findViewById(R.id.eventinfo_arrows);
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(1000);
        fadeOut.setRepeatCount(1);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                arrows.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        arrows.setAnimation(fadeOut);
    }
}
