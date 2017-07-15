package com.dietbuddy.tralice.mapsapplication;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

public class activity_recycler_view extends AppCompatActivity {

    private ActionBar mActionBar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        int Images[] = {R.drawable.pasta,R.drawable.soup,R.drawable.cucumber_soup,R.drawable.french_bean_salad,R.drawable.potato
        };
        Card[] mDataset ={new Card(Images[0],"Garden Pasta Salad",R.string.pasta),
                new Card(Images[1], "Chicken & Spinach Soup with Fresh Pesto",R.string.chicken_soup),
                new Card(Images[2], "Creamy Cucumber Soup",R.string.cucumber),
                new Card(Images[3], "French Bean Salad",R.string.french),
                new Card(Images[4], "Tomato, Carrots and Asparagus Smoothie",R.string.tomato),
        };

        mRecyclerView= (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter=new MyAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        mActionBar = getSupportActionBar();

        //Tool Bar Boom Button
        assert mActionBar != null;
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View actionBar = mInflater.inflate(R.layout.customappbar, null);
        TextView mTitleTextView = (TextView) actionBar.findViewById(R.id.title_text);
        mTitleTextView.setText("HEALTHY RECEPIES");
        mActionBar.setCustomView(actionBar);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) actionBar.getParent()).setContentInsetsAbsolute(0,0);

        BoomMenuButton rightBmb = (BoomMenuButton) actionBar.findViewById(R.id.action_bar_right_bmb);

        rightBmb.setButtonEnum(ButtonEnum.Ham);
        rightBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        rightBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);
        int images[]={R.drawable.ic_burn_calories,R.drawable.ic_bmi_calculator,R.drawable.ic_recepe_finder,R.drawable.ic_records};
        String normalText[]={"Burn Calories","BMI Calculator","Find Recepe","History"};
        for (int i = 0; i < rightBmb.getPiecePlaceEnum().pieceNumber(); i++)
            rightBmb.addBuilder(new HamButton.Builder().normalImageRes(images[i]).normalText(normalText[i]).
                    shadowEffect(true).listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    if (index == 1) {
                        Intent i = new Intent(activity_recycler_view.this, bmi_activity.class);
                        startActivity(i);

                    } else if (index == 3) {
                        Intent i = new Intent(activity_recycler_view.this, history.class);
                        startActivity(i);
                    } else if (index == 2) {
                        Intent i = new Intent(activity_recycler_view.this, activity_recycler_view.class);
                        startActivity(i);
                    } else if (index == 0) {
                        Intent i = new Intent(activity_recycler_view.this, MapsActivity.class);
                        startActivity(i);
                    }
                }
            }));

    }
}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    public static final String EXTRA_MESSAGE_DESCRIPTION_TITLE="abcd";
    public static final String EXTRA_MESSAGE_IMAGE="0";
    public static final String EXTRA_MESSAGE_DESCRIPTION="abc";
    private Card [] mDataset;


    public MyAdapter(Card[] mDataset){
        this.mDataset=mDataset;

    }
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardview = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        ViewHolder vh = new ViewHolder(cardview);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);
        Card card = mDataset[position];
        holder.bindCard(card);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case

        public ImageView imageView;
        public TextView description;

        public Card card;

        private static final String CARD_KEY = "CARD";

        public ViewHolder(View v) {
            super(v);

            imageView= (ImageView)v.findViewById(R.id.foodImage);
            description=(TextView)v.findViewById(R.id.food_description);
            v.setOnClickListener(this);

        }


        public void bindCard(Card card) {
            this.card = card;
            imageView.setImageResource(card.getImage());
            description.setText(card.getDescription_title());
        }

        @Override
        public void onClick(View v) {

            Bundle bun=new Bundle();
            bun.putString(EXTRA_MESSAGE_DESCRIPTION_TITLE,card.getDescription_title());
            bun.putInt(EXTRA_MESSAGE_IMAGE,card.getImage());
            bun.putInt(EXTRA_MESSAGE_DESCRIPTION,card.getDescription());

            Intent i=new Intent(v.getContext(),ParallaxToolbarScrollViewActivity.class);
            i.putExtras(bun);
            v.getContext().startActivity(i);

        }
    }
}
class Card {
    private String description_title;
    private int image;
    private int description;

    public Card(int image,String description_title, int description){
        this.image=image;
        this.description=description;
        this.description_title=description_title;
    }

    public String getDescription_title() {
        return description_title;
    }

    public void setDescription_title(String description_title) {
        this.description_title = description_title;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int  description) {
        this.description= description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}


