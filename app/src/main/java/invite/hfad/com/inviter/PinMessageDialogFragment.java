package invite.hfad.com.inviter;


import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.CLIPBOARD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PinMessageDialogFragment extends DialogFragment {

    private String message;
    private String event_id;
    private String message_id;

    private Button unpin_button;
    private Button copy_button;
    private Button remove_button;

    private View rootView;

    private DatabaseReference mFirebseDatabaseReference;

    private DialogInterface.OnDismissListener onDismissListener;

    public PinMessageDialogFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event_id = getArguments().getString("id");
        message_id = getArguments().getString("message_id");
        //setOnDismissListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.fragment_pin_message_dialog, container, false);
        ButtonClickListener();
        return rootView;
    }


    private void ButtonClickListener(){
        copy_button =(Button) rootView.findViewById(R.id.copy_button);
        unpin_button = (Button) rootView.findViewById(R.id.unpin_button);
        copy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", message);
                clipboard.setPrimaryClip(clip);
                getDialog().dismiss();
                Toast.makeText(getActivity(), "Copied Message", Toast.LENGTH_SHORT).show();
            }
        });
        unpin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebseDatabaseReference = Utils.getDatabase().getReference();
                mFirebseDatabaseReference.child(Utils.EVENT).child(event_id).child(Utils.PIN).child(message_id).removeValue();
                getDialog().dismiss();
                Toast.makeText(getActivity(),"Message unpinned",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }


}
