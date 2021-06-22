package il.ac.hit.gamersarc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {

    Context context;
    List<IntroItem> list;

    public IntroViewPagerAdapter(Context context, List<IntroItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.intro,null);

        ImageView introImage = view.findViewById(R.id.introImage);
        TextView introDescription1 = view.findViewById(R.id.introDescription1);
        TextView introDescription2 = view.findViewById(R.id.introDescription2);
        TextView introDescription3 = view.findViewById(R.id.introDescription3);
        TextView introTitle = view.findViewById(R.id.introTitle);

        introTitle.setText(list.get(position).getTitle());
        introImage.setImageResource(list.get(position).getImage());
        introDescription1.setText(list.get(position).getDescription1());
        introDescription2.setText(list.get(position).getDescription2());
        if(list.get(position)==list.get(1)){
            introDescription3.setText(list.get(position).getDescription3());
        }
        else
            introDescription3.setText("");




        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
