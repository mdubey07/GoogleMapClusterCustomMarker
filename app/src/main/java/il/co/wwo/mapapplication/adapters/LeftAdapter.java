package il.co.wwo.mapapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import il.co.wwo.mapapplication.R;
import il.co.wwo.mapapplication.models.PostResponse;

public class LeftAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private PostViewHolder viewHolder;
    private Context context;
    private ArrayList<PostResponse> listItems = new ArrayList<>();

    public LeftAdapter(Context _context) {
        super();
        this.context = _context;
    }
    public void setListItems(ArrayList<PostResponse> list){

        this.listItems = list;

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_marker_style_2, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        if(listItems.size() > 0){
            PostResponse post = listItems.get(position);
            holder.txtTitle.setText(post.getTitle());
            Picasso.get().load(post.getThumbnail()).into(holder.image);
            holder.txtDistance.setText(String.valueOf(post.getDistance()) + "km");
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}
