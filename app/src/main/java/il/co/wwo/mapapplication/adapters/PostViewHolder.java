package il.co.wwo.mapapplication.adapters;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import il.co.wwo.mapapplication.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    LinearLayout itemContainer;
    CircleImageView image;
    TextView txtDistance, txtTitle ;

    PostViewHolder(@NonNull View itemView){
        super(itemView);
        itemContainer = itemView.findViewById(R.id.item);
        image = itemView.findViewById(R.id.user_dp);
        txtDistance = itemView.findViewById(R.id.kmaway);
        txtTitle = itemView.findViewById(R.id.post_name);
    }
}
