package com.wishbook.catalog.home.adapters;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.home.contacts.add.Fragment_Buyer_Rating;
import com.wishbook.catalog.home.models.BuyerQuestions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyerRatingAdapter extends RecyclerView.Adapter<BuyerRatingAdapter.ViewHolder> {



    Fragment frag;
    private List<BuyerQuestions> data;

    public BuyerRatingAdapter(List<BuyerQuestions> data, Fragment frag) {
        this.data = data;
        this.frag=frag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_buyer_rating, parent, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.itemView.setAlpha(0.4f);
        holder.question.setText(data.get(position).getQuestions());
        holder.question_summary.setText(data.get(position).getQuestion_summary());
        holder.option1.setText(data.get(position).getOptions(0));
        holder.option2.setText(data.get(position).getOptions(1));
        holder.option3.setText(data.get(position).getOptions(2));
        holder.option4.setText(data.get(position).getOptions(3));
        switch (data.get(position).getAns()){
            case 1: holder.option1.setChecked(true);break;
            case 2: holder.option2.setChecked(true);break;
            case 3: holder.option3.setChecked(true);break;
            case 4: holder.option4.setChecked(true);break;
        }


        holder.options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(frag instanceof Fragment_Buyer_Rating) {
                    BuyerQuestions save = data.get(holder.getAdapterPosition());
                    switch (checkedId){
                        case R.id.option1: save.setAns(1);break;
                        case R.id.option2: save.setAns(2);break;
                        case R.id.option3: save.setAns(3);break;
                        case R.id.option4: save.setAns(4);break;
                    }
                    data.set(holder.getAdapterPosition(),save);
                    ((Fragment_Buyer_Rating) frag).onItemClicked(holder.getAdapterPosition());
                }
            }
        });



    }

    public int getans(int question_no){
        int i=question_no;
        return data.get(i).getAns();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.question)
        TextView question;
        @BindView(R.id.question_summary)
        TextView question_summary;
        @BindView(R.id.option1)
        RadioButton option1;
        @BindView(R.id.option2)
        RadioButton option2;
        @BindView(R.id.option3)
        RadioButton option3;
        @BindView(R.id.option4)
        RadioButton option4;

        @BindView(R.id.options)
        RadioGroup options;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



}