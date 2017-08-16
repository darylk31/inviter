package invite.hfad.com.inviter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Daryl on 8/15/2017.
 */

public class EventMembersAdapter extends RecyclerView.Adapter<EventMembersAdapter.ViewHolder> {

    private String[] userNames;
    private String[] displayNames;
    private String[] displayPictures;
    private String creator;
    private Context context;



    public EventMembersAdapter(String[] userNames,
                               String[] displayNames,
                               String[] displayPictures,
                               String creator,
                               Context context){
        this.userNames = userNames;
        this.displayNames = displayNames;
        this.displayPictures = displayPictures;
        this.creator = creator;
        this.context = context;
    }

    @Override
    public EventMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(EventMembersAdapter.ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;
        TextView displayname = (TextView) cardView.findViewById(R.id.tv_eventMembersName);
        CircleImageView imageView = (CircleImageView) cardView.findViewById(R.id.civ_eventMembers);
        displayname.setText(displayNames[position]);
        Glide.with(context)
                .load(displayPictures[position])
                .into(imageView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileDialogBox profileDialogBox = new ProfileDialogBox((EventViewPager) context, userNames[position]);
                profileDialogBox.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userNames.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
