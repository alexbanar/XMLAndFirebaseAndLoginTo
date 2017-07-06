package alex.ness.edu.xmlandfirebaseandloginto;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A fragment retresenting a list of Ynet Items
 */
public class YnetArticleFragment extends Fragment  implements YnetDataSource.OnYnetArrivedListener {

    private RecyclerView recyclerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recyclerView = (RecyclerView)
                inflater.inflate(R.layout.fragment_ynetarticle/*contains RecyclerView XML*/, container, false);


        YnetDataSource.getYnet(this);

        //to Recycler give Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return recyclerView;
    }

    @Override
    public void onYnetArrived(final List<YnetDataSource.Ynet> data, final Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e == null) {
                    recyclerView.setAdapter(new YnetAdapter(data, getActivity()));
                    //Toast.makeText(getContext(), data.toString(), Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    static class YnetAdapter extends RecyclerView.Adapter<YnetAdapter.YnetViewHolder> {
        //Properties:
        private List<YnetDataSource.Ynet> data;
        private Context context;
        private LayoutInflater inflater;
        //Constructor:
        public YnetAdapter(List<YnetDataSource.Ynet> data, Context context) {
            this.data = data;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        //createViewsHolders
        @Override
        public YnetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflate a single item -> View
            View v = inflater.inflate(R.layout.ynet_item, parent, false);
            //return a new ViewHolder
            return new YnetViewHolder(v);
        }

        //bind  the viewholders to the data
        @Override
        public void onBindViewHolder(YnetViewHolder holder, int position) {
            YnetDataSource.Ynet ynet = data.get(position);
            holder.tvTitle.setText(ynet.getTitle());
            holder.tvContent.setText(ynet.getDescription());
            Picasso.with(context).load(ynet.getImage()).into(holder.ivThumbnail);
        }

        //getCount()
        @Override
        public int getItemCount() {
            return data.size();
        }



        //View Holder: holds the views
        class YnetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView ivThumbnail;
            TextView tvTitle;
            TextView tvContent;

            public YnetViewHolder(View v) {
                super(v);
                ivThumbnail = (ImageView) v.findViewById(R.id.ivThumbnail);
                tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                tvContent = (TextView) v.findViewById(R.id.tvContent);

                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (context instanceof AppCompatActivity){
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    //method of ViewHolder
                    int position = getAdapterPosition();
                    YnetDataSource.Ynet ynet = data.get(position);

                    YnetDetailFragment detailFragment = YnetDetailFragment.newInstance(ynet.getLink());

                    fm.beginTransaction().
                            replace(R.id.frame, detailFragment).
                            addToBackStack("details").
                            commit();
                }
            }
        }
    }
}


