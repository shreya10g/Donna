package com.example.shrey.donna;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private List<Message> messages;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myText;
        public TextView donnaText;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            myText = (TextView) v.findViewById(R.id.my_content);
            donnaText = (TextView) v.findViewById(R.id.donna_content);
        }
    }
    public void add(int position, Message msg) {
        messages.add(position, msg);
        notifyItemInserted(position);
    }
    public void remove(int position) {
        messages.remove(position);
        notifyItemRemoved(position);
    }
    public MessageAdapter(List<Message> myDataset) {
        messages = myDataset;
    }
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.my_message, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Message msg = messages.get(position);
        boolean user = msg.getUser();
        final String name = msg.getText();
        if(user){
            holder.myText.setText(name);
            holder.donnaText.setVisibility(View.INVISIBLE);
        }
        else{
            holder.donnaText.setText(name);
            holder.myText.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }
}
