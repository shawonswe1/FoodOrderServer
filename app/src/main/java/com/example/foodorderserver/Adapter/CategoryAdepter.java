package com.example.foodorderserver.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderserver.R;
import com.example.foodorderserver.model.AddCategory;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdepter extends RecyclerView.Adapter<CategoryAdepter.MyViewHolder> {

    private Context context;
    private List<AddCategory> addCategoryList;
    private OnItemClickListener mListener;

    public CategoryAdepter(Context context , List<AddCategory> addCategoryList) {
        this.context = context;
        this.addCategoryList = addCategoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cateegory_list,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder , int position) {

        AddCategory addCategory = addCategoryList.get(position);
        holder.textView.setText(addCategory.getImageName());
        Picasso.with(context).load(addCategory.getImageUrl())
                .placeholder(R.drawable.picture)
                .fit().centerCrop().into(holder.imageView);

        Glide.with(context).load(addCategory.getImageUrl())
                .fitCenter()
                .centerCrop()
                .placeholder(R.drawable.picture)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return addCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        TextView textView;
        ImageView imageView;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.cardCategoryNameId);
            imageView = itemView.findViewById(R.id.cardCategoryViewId);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View view) {

            if (mListener != null)
            {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu , View view , ContextMenu.ContextMenuInfo contextMenuInfo) {

            contextMenu.setHeaderTitle("Selece Action");
            contextMenu.setHeaderIcon(R.drawable.action);
            MenuItem update = contextMenu.add(Menu.NONE,1,1,"Update");
            MenuItem delete = contextMenu.add(Menu.NONE,2,2,"Delete");

            update.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION)
            {
                switch (menuItem.getItemId())
                {
                    case 1:
                        mListener.update(position);
                        return true;
                    case 2:
                        mListener.delete(position);
                        return true;
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(int position);

        void update(int position);

        void delete(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

}
