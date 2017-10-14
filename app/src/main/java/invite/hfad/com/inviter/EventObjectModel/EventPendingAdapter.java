package invite.hfad.com.inviter.EventObjectModel;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import invite.hfad.com.inviter.DialogBox.ProfileDialogBox;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;

/**
 * Created by Daryl on 8/22/2017.
 */

public class EventPendingAdapter extends RecyclerView.Adapter<EventPendingAdapter.ViewHolder> {

    private ArrayList<User> userArrayList;
    private Context context;

    public EventPendingAdapter(ArrayList<User> userArrayList, Context context){
        this.userArrayList = userArrayList;
        this.context = context;
    }

    @Override
    public EventPendingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item, parent, false);
        return new EventPendingAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(EventPendingAdapter.ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;
        TextView displayname = (TextView) cardView.findViewById(R.id.tv_eventMembersName);
        CircleImageView imageView = (CircleImageView) cardView.findViewById(R.id.civ_eventMembers);
        displayname.setText(userArrayList.get(position).getDisplayname() + " (Pending)");

        Glide.with(context)
                .load(userArrayList.get(position).getPhotoUrl())
                .into(imageView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileDialogBox profileDialogBox = new ProfileDialogBox((EventMembersActivity) context, userArrayList.get(position).getUsername());
                profileDialogBox.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
