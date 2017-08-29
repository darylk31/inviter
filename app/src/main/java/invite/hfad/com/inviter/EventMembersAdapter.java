package invite.hfad.com.inviter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Daryl on 8/15/2017.
 */

public class EventMembersAdapter extends RecyclerView.Adapter<EventMembersAdapter.ViewHolder> {


    private ArrayList<User> user_list;
    private int admin_num;
    private Context context;



    public EventMembersAdapter(ArrayList<User> user_list,
                               int admin_num,
                               Context context){
        this.user_list = user_list;
        this.admin_num = admin_num;
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
        if (position <= admin_num - 1) {
            displayname.setText(user_list.get(position).getDisplayname() + " (Admin)");}
        else {
            displayname.setText(user_list.get(position).getDisplayname());}

        Glide.with(context)
                .load(user_list.get(position).getPhotoUrl())
                .into(imageView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileDialogBox profileDialogBox = new ProfileDialogBox((EventViewPager) context, user_list.get(position).getUsername());
                profileDialogBox.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return user_list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
