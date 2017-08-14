package invite.hfad.com.inviter;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import static android.content.Context.CLIPBOARD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatDialogFragment extends DialogFragment{

    private String message;
    private String event_id;

    private Button pin_button;
    private Button copy_button;
    private Button remove_button;

    private View view;

    public ChatDialogFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = getArguments().getString("message");
        event_id = getArguments().getString("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_chat_dialog, container, false);
        ButtonClickListener();
        return view;
    }


    private void ButtonClickListener(){
        copy_button =(Button) view.findViewById(R.id.copy_button);
        pin_button = (Button) view.findViewById(R.id.pin_button);
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
    }

}
