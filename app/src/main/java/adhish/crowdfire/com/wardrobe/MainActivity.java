package adhish.crowdfire.com.wardrobe;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import adhish.crowdfire.com.wardrobe.wardrobe_utility.DatabaseHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ViewPager.OnPageChangeListener {


    int like=0;
    MyPageAdapter pageAdapter;
    MyPageAdapter pageAdapter_bottoms;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    DatabaseHandler databaseHandler;
    FloatingActionButton fabTop,fabLike,fabBottom,fabShuffle;


    int flag=1;//1 for top 0 for bottom
    Random r;

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wardrobe");
        getSupportActionBar().setElevation(10);
        databaseHandler=new DatabaseHandler(MainActivity.this);

        r = new Random();


        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        final ViewPager pager =
                (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        fabTop=(FloatingActionButton)findViewById(R.id.fabAddTop);
        fabShuffle=(FloatingActionButton)findViewById(R.id.fabShuffle);
        fabBottom=(FloatingActionButton)findViewById(R.id.fabAddBottom);
        fabLike = (FloatingActionButton) findViewById(R.id.fabLike);



        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

        startAt6();

        List<Fragment> fragments_bottoms = getFragmentsBottoms();
        pageAdapter_bottoms = new MyPageAdapter(getSupportFragmentManager(), fragments_bottoms);
        final ViewPager pager_bottoms =
                (ViewPager)findViewById(R.id.viewpager_bottom);
        pager_bottoms.setAdapter(pageAdapter_bottoms);


        fabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pager.getAdapter().getCount()>0 && pager_bottoms.getAdapter().getCount()>0) {
                    fabLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_24dp));

                    like = 1;

                    databaseHandler.addFav(pager.getCurrentItem(), pager_bottoms.getCurrentItem());
                    Snackbar.make(view, "Liked! Combination saved", Snackbar.LENGTH_LONG)
                            .show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Add top and bottoms both to make it favorite",Toast.LENGTH_LONG).show();
                }
            }
        });


        fabTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=1;
                selectImage();
            }
        });
        fabBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=0;
                selectImage();
            }
        });

        fabShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getAdapter().getCount()>0 && pager_bottoms.getAdapter().getCount()>0) {
                    int topRand = r.nextInt(pager.getAdapter().getCount());
                    int bottomRand = r.nextInt(pager_bottoms.getAdapter().getCount());
                    pager.setCurrentItem(topRand);
                    pager_bottoms.setCurrentItem(bottomRand);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Insufficient items to shuffle",Toast.LENGTH_LONG).show();
                }
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                like = 0;
                fabLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline_24dp));
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });

        pager_bottoms.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                like=0;
                fabLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline_24dp));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            } else if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bytes);




        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] byteArray = bytes .toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        if(flag==1) {
            databaseHandler.addTop(byteArray);
        }else
        {
            databaseHandler.addBottom(byteArray);
        }


        Toast.makeText(getApplicationContext(),"Added",Toast.LENGTH_SHORT).show();
        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager =
                (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);



        List<Fragment> fragments_bottoms = getFragmentsBottoms();
        pageAdapter_bottoms = new MyPageAdapter(getSupportFragmentManager(), fragments_bottoms);
        ViewPager pager_bottoms =
                (ViewPager)findViewById(R.id.viewpager_bottom);
        pager_bottoms.setAdapter(pageAdapter_bottoms);
    }


    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;


        System.out.println(selectedImagePath.toString());

        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();


        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        if(flag==1) {
            databaseHandler.addTop(byteArray);
        }else
        {
            databaseHandler.addBottom(byteArray);
        }
        Toast.makeText(getApplicationContext(),"Added",Toast.LENGTH_SHORT).show();
        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager =
                (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);



        List<Fragment> fragments_bottoms = getFragmentsBottoms();
        pageAdapter_bottoms = new MyPageAdapter(getSupportFragmentManager(), fragments_bottoms);
        ViewPager pager_bottoms =
                (ViewPager)findViewById(R.id.viewpager_bottom);
        pager_bottoms.setAdapter(pageAdapter_bottoms);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dash) {
            // Open Dashboard
        } else if (id == R.id.nav_likes) {
            Toast.makeText(MainActivity.this, "Coming soon..", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void startAt6() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 20;

        /* Set the alarm to start at 6:00 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 00);

        /* Repeating on every 24 hours interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 60 * 24, pendingIntent);
    }
    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();



        try {
                List<byte[]> byteList = databaseHandler.getTop();
            for (int i=0; i<byteList.size(); i++) {
                byte[] bytes=byteList.get(i);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                fList.add(MyFragment.newInstance(bitmap));
                System.out.println(byteList.get(i));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return fList;
    }

    private List<Fragment> getFragmentsBottoms() {
        List<Fragment> fList = new ArrayList<Fragment>();



        try {
            List<byte[]> byteList = databaseHandler.getBottom();
            for (int i=0; i<byteList.size(); i++) {
                byte[] bytes=byteList.get(i);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                fList.add(MyFragment.newInstance(bitmap));
                System.out.println(byteList.get(i));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return fList;
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        like=savedInstanceState.getInt("like");
        if(like==1)
        {
            fabLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_24dp));
        }
        else
        {
            fabLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline_24dp));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("like",like);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int pos) {


    }


}
