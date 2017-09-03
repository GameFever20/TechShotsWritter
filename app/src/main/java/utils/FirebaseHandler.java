package utils;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

/**
 * Created by bunny on 09/08/17.
 */

public class FirebaseHandler {

    private FirebaseDatabase mDatabase;

    public FirebaseHandler() {
        mDatabase = FirebaseDatabase.getInstance();

    }


    public void downloadNewsArticle(final String newsArticleID, final OnNewsArticleListener onNewsArticleListener) {

        DatabaseReference myRef = mDatabase.getReference().child("newsArticle/" + newsArticleID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                NewsArticle newsArticle = dataSnapshot.getValue(NewsArticle.class);

                if (newsArticle != null) {
                    newsArticle.setNewsArticleID(dataSnapshot.getKey());
                }
                onNewsArticleListener.onNewsArticle(newsArticle, true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onNewsArticleListener.onNewsArticle(null, false);
            }
        });


    }

    public void downloadNewsArticleList(int limitTo, final OnNewsArticleListener onNewsArticleListener) {
        DatabaseReference myRef = mDatabase.getReference().child("newsArticle/");

        Query myref2 = myRef.limitToLast(limitTo);
        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<NewsArticle> newsArticleArrayList = new ArrayList<NewsArticle>();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    NewsArticle newsArticle = snapshot.getValue(NewsArticle.class);
                    if (newsArticle != null) {
                        newsArticle.setNewsArticleID(snapshot.getKey());
                    }
                    newsArticleArrayList.add(newsArticle);

                }
                onNewsArticleListener.onNewsArticleList(newsArticleArrayList, true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onNewsArticleListener.onNewsArticleList(null, false);

            }
        });
    }

    public void uploadNewsArticle(NewsArticle newsArticle, final OnNewsArticleUploadListener onNewsArticleUploadListener) {


        DatabaseReference myRef = mDatabase.getReference().child("newsArticle/" + newsArticle.getNewsArticleID());
        myRef.setValue(newsArticle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onNewsArticleUploadListener.onNewsArticleUpload(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onNewsArticleUploadListener.onNewsArticleUpload(false);
            }
        });


    }

    public void uploadNewsArticleImage(final NewsArticle newsArticle, Uri uri, final OnNewsArticleUploadListener onNewsArticleUploadListener) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        DatabaseReference myRef = mDatabase.getReference().child("newsArticle/");
        newsArticle.setNewsArticleID(myRef.push().getKey());

        StorageReference riversRef = storageRef.child("newsArticleImage/" + newsArticle.getNewsArticleID() + "/" + "main");
        UploadTask uploadTask = riversRef.putFile(uri);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                onNewsArticleUploadListener.onNewsArticleImageUpload(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.


                try {
                    newsArticle.setNewsArticleImageLink(taskSnapshot.getDownloadUrl().getPath());
                    onNewsArticleUploadListener.onNewsArticleImageUpload(true);

                } catch (Exception e) {

                }

                //finished uploading image now upload newsarticle
                uploadNewsArticle(newsArticle, onNewsArticleUploadListener);

            }
        });

    }


    public interface OnNewsArticleListener {
        void onNewsArticleList(ArrayList<NewsArticle> newsArticleArrayList, boolean isSuccessful);

        void onNewsArticle(NewsArticle newsArticle, boolean isSuccessful);
    }

    public interface OnNewsArticleUploadListener {
        void onNewsArticleUpload(boolean isSuccessful);

        void onNewsArticleImageUpload(boolean isSuccessful);

    }


}
