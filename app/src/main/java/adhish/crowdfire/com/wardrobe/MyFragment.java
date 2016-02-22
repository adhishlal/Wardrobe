package adhish.crowdfire.com.wardrobe;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Adhish on 13/11/2015.
 */
public class MyFragment extends Fragment {

    public static final String EXTRA_IMAGE="EXTRA_IMAGE";


    public static final MyFragment newInstance(Bitmap backImage)
    {
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(1);
        bdl.putParcelable(EXTRA_IMAGE, backImage);

        f.setArguments(bdl);
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bitmap backImage=getArguments().getParcelable(EXTRA_IMAGE);

        View v = inflater.inflate(R.layout.pageritem, container, false);
        ImageView ivBack=(ImageView)v.findViewById(R.id.ivClothImage);
        ivBack.setImageBitmap(backImage);

        return v;
    }

}
