package com.corral.firebase.shailshah.bakingapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;

import com.corral.firebase.shailshah.bakingapp.provider.BakingAppContractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by shailshah on 10/25/17.
 */

public class OpenBakingJsonUtils {
    public static String strSeparator = "__,__";

    public static ContentValues[] getSimpleMovieDataFromJson(final Context context) throws JSONException {


        URL bakingUrl = NetworkUtils.buildUrl();


        try {
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(bakingUrl);
           // JSONObject bakingObject = new JSONObject(jsonWeatherResponse);

            JSONArray array = new JSONArray(jsonWeatherResponse);
            ContentValues BakingContentValue[] = new ContentValues[array.length()];
            for (int i =0 ; i< array.length() ; i++)
            {
                ContentValues bakingValues = new ContentValues();
                JSONObject object = array.getJSONObject(i);

                String name = object.getString("name");
                int id = object.getInt("id");


                JSONArray ingredients = object.getJSONArray("ingredients");
                String[] quantity = new String[ingredients.length()];
                String[] meaasure = new String[ingredients.length()];
                String[] ingre = new String[ingredients.length()];
                String[] widgetInfo = new String[ingredients.length()];
                for (int j = 0 ; j< ingredients.length() ; j++)
                {
                    JSONObject indegrientObject = ingredients.getJSONObject(j);


                    quantity[j] = indegrientObject.getString("quantity");
                    meaasure[j] = indegrientObject.getString("measure");
                    ingre[j] = indegrientObject.getString("ingredient");
                    widgetInfo[j] = quantity[j] + " " + meaasure[j]+  " " + ingre[j] + System.lineSeparator();
                    // Log.v(OpenBakingJsonUtils.class.getSimpleName(),"The Quantity are : " + quantity[j]);
                    //Log.v(OpenBakingJsonUtils.class.getSimpleName(),"The measure are : " + meaasure[j]);
                   // Log.v(OpenBakingJsonUtils.class.getSimpleName(),"The ingredient are : " + ingre[j]);

                }

                JSONArray stepsArray = object.getJSONArray("steps");
                String[] Stepid = new String[stepsArray.length()];
                String[] shortDescription = new String[stepsArray.length()];
                String[] description = new String[stepsArray.length()];
                String[] videoUrl = new String[stepsArray.length()];
                String thumbnailFromVideo = new String();
                String[] thumbnail_URL = new String[stepsArray.length()];
                String thumbnails = new String();
                String bitmap2 = null;

                for (int j = 0; j< stepsArray.length(); j++)
                {
                    JSONObject stepsObject = stepsArray.getJSONObject(j);
                    Stepid[j] = stepsObject.getString("id");
                    shortDescription[j] = stepsObject.getString("shortDescription");
                    description[j] = stepsObject.getString("description");
                    videoUrl[j] = stepsObject.getString("videoURL");
                    if (stepsObject.getString("thumbnailURL").equals(""))
                    {
                        thumbnail_URL[j] = " ";

                    }
                    else
                    {
                        thumbnail_URL[j] = stepsObject.getString("thumbnailURL");
                    }




                }
                thumbnailFromVideo = videoUrl[videoUrl.length-1];
                Bitmap bitmap = null;
                MediaMetadataRetriever mediaMetadataRetriever = null ;
                try {
                    mediaMetadataRetriever = new MediaMetadataRetriever();
                    if (Build.VERSION.SDK_INT >= 14)
                        // no headers included
                        mediaMetadataRetriever.setDataSource(thumbnailFromVideo, new HashMap<String, String>());
                    else
                        mediaMetadataRetriever.setDataSource(thumbnailFromVideo);
                    //   mediaMetadataRetriever.setDataSource(videoPath);
                    bitmap = mediaMetadataRetriever.getFrameAtTime(15000000);
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (mediaMetadataRetriever != null)
                        mediaMetadataRetriever.release();
                }






                String servings = object.getString("servings");
                String image = null ;
                image = object.getString("image");


                Log.v(OpenBakingJsonUtils.class.getSimpleName(), "Thumbnails are :::::: "  + OpenBakingJsonUtils.convertArrayToString(thumbnail_URL));





                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_BAKERY_ID,id);
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_NAME,name);
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_QUANTITY,OpenBakingJsonUtils.convertArrayToString(quantity));
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_MEASURE,OpenBakingJsonUtils.convertArrayToString(meaasure));
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_INGREDIENT, OpenBakingJsonUtils.convertArrayToString(ingre));
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_STEPS_ID,OpenBakingJsonUtils.convertArrayToString(Stepid));
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_SHORT_DESCRIPTION,OpenBakingJsonUtils.convertArrayToString(shortDescription));
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_DESCRIPTION,OpenBakingJsonUtils.convertArrayToString(description));
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_VIDEO_URL,OpenBakingJsonUtils.convertArrayToString(videoUrl));
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_THUMBNAIL_URL, convertBitmaptoByte(bitmap));
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_THUMBNAIL,OpenBakingJsonUtils.convertArrayToString(thumbnail_URL));
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_SERVINGS,servings);
                bakingValues.put(BakingAppContractor.BakeryEntry.COLIMN_IMAGE_URL,image);
                bakingValues.put(BakingAppContractor.BakeryEntry.COLUMN_WIDGET_INFO,OpenBakingJsonUtils.convertArrayToString(widgetInfo));
                BakingContentValue[i] = bakingValues;

            }
            return BakingContentValue;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        }


    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }
    public static byte[] convertBitmaptoByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap convertByteToBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }



}

