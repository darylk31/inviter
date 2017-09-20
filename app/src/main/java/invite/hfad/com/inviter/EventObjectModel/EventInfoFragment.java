package invite.hfad.com.inviter.EventObjectModel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserAreaActivity;
import invite.hfad.com.inviter.UserDatabaseHelper;
import invite.hfad.com.inviter.Utils;

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
            SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(getActivity().getApplicationContext());
            SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
            Cursor cursor = event_db.rawQuery("SELECT * FROM EVENTS WHERE EID LIKE '" + id + "';", null);
            cursor.moveToLast();
            TextView event_name = (TextView) view.findViewById(R.id.tv_eventpagename);
            this.event_string = cursor.getString(4);
            event_name.setText(event_string);
            TextView event_date = (TextView) view.findViewById(R.id.tv_eventpagedate);
            TextView event_time = (TextView) view.findViewById(R.id.tv_eventpagetime);
            String event_day = cursor.getString(2);
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(event_day);
                String output_day = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                String output_month = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date);
                String output_year = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date);
                event_date.setText(output_month + " " + output_day + ", " + output_year);
                String output_time = new SimpleDateFormat("KK:mm a", Locale.ENGLISH).format(date);
                event_time.setText(output_time);
            } catch (ParseException e) {
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(event_day);
                    String output_day = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                    String output_month = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date);
                    String output_year = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date);
                    event_date.setText(output_month + " " + output_day + ", " + output_year);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
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
        onInvite();

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
                Intent intent = new Intent(getActivity(), EventMembersActivity.class);
                intent.putExtra("event_id", id);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        arrowAnimation();
    }

    private void setEventPicture() {
        final ImageView EventPictureView = view.findViewById(R.id.event_image);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("events/" + id + ".jpg");
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(EventInfoFragment.this)
                            .load(task.getResult())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setAspectRatio(1,1)
                                        .start(getActivity());
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
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Uploading image...");
                dialog.show();
                Uri resultUri = result.getUri();
                File photoPath = new File(resultUri.getPath());

                try {
                    Bitmap photo_bitmap = new Compressor(getActivity().getApplicationContext())
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(photoPath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] photoByteArray = baos.toByteArray();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    UploadTask uploadtask = storageRef.child("events/" + id + ".jpg").putBytes(photoByteArray);
                    uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            setEventPicture();
                            Toast.makeText(getActivity().getApplicationContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                } catch (IOException e) {
                    dialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "Error uploading image, please try again.", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity().getApplicationContext(), "Error uploading image, please try again.", Toast.LENGTH_SHORT).show();
            }
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

    private void onInvite(){
        getView().findViewById(R.id.tv_eventinfoinvite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditEventSelectContacts.class)
                        .putExtra("event_id", id)
                        .putExtra("event_name", event_string)
                );

            }
        });
    }

    private void arrowAnimation(){
        final ImageView arrows = (ImageView) view.findViewById(R.id.eventinfo_arrows);
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(700);
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
