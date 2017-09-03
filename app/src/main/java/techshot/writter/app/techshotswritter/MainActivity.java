package techshot.writter.app.techshotswritter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.Calendar;
import java.util.List;

import utils.FirebaseHandler;
import utils.NewsArticle;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_EXTRA_IMAGE5 = 5;
    NewsArticle newsArticle =new NewsArticle();

    Uri newsImageUri ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_EXTRA_IMAGE5) {

                List<Uri> mSelectedProfileImage = Matisse.obtainResult(data);
                 newsImageUri = mSelectedProfileImage.get(0);
                Log.d("Matisse", "mSelected: " + newsImageUri.getPath());
                ImageView imageView = (ImageView) findViewById(R.id.main_newsImage_imageView);
                imageView.setImageURI(newsImageUri);
                Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();




            }
        }
    }


    public void onImageClick(View view) {
        Matisse.from(MainActivity.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_EXTRA_IMAGE5);

    }

    public void onUploadClick(View view) {

        if (createNewsArticle()){


            new FirebaseHandler().uploadNewsArticleImage(newsArticle, newsImageUri, new FirebaseHandler.OnNewsArticleUploadListener() {
                @Override
                public void onNewsArticleUpload(boolean isSuccessful) {
                    Toast.makeText(MainActivity.this, "News Uploaded", Toast.LENGTH_SHORT).show();
                    clearEditText();

                }

                @Override
                public void onNewsArticleImageUpload(boolean isSuccessful) {

                    Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void clearEditText() {
        EditText editText = (EditText)findViewById(R.id.main_newsTitle_editText);
        editText.setText(null);
        editText =(EditText)findViewById(R.id.main_newsDescription_editText);
        editText.setText(null);
        editText =(EditText)findViewById(R.id.main_newsTag_editText);
        editText.setText(null);
        editText =(EditText)findViewById(R.id.main_newsSource_editText);
        editText.setText(null);
        editText =(EditText)findViewById(R.id.main_newsSourceLink_editText);
        editText.setText(null);
    }

    private boolean createNewsArticle() {

        EditText editText = (EditText)findViewById(R.id.main_newsTitle_editText);
        String string =editText.getText().toString().trim();

        if (string.length() <5){
            return false;
        }else{
            newsArticle.setNewsArticleTitle(string);
        }


        editText =(EditText)findViewById(R.id.main_newsDescription_editText);
        string =editText.getText().toString().trim();

        if (string.length() <20){
            return false;
        }else{
            newsArticle.setNewsArticleDescription(string);
        }

        editText =(EditText)findViewById(R.id.main_newsTag_editText);
        string =editText.getText().toString().trim();

        if (string.isEmpty()){
            return false;
        }else{
            newsArticle.setNewsArticleTag(string);
        }

        editText =(EditText)findViewById(R.id.main_newsSource_editText);
        string =editText.getText().toString().trim();

        if (string.isEmpty()){
            return false;
        }else{
            newsArticle.setNewsArticleSource(string);
        }

        editText =(EditText)findViewById(R.id.main_newsSourceLink_editText);
        string =editText.getText().toString().trim();

        if (string.isEmpty()){
            return false;
        }else{
            newsArticle.setNewsArticleSourceLink(string);
        }


        newsArticle.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        newsArticle.setPushNotification(true);



        return true;
    }

}
