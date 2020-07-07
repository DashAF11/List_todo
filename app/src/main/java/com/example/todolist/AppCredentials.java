package com.example.todolist;


import android.app.Activity;
import android.content.SharedPreferences;

public class AppCredentials {

    public static final String TAG = "oxstren " + AppCredentials.class.getSimpleName();
    public static final String APPLICATION_ID = "com.actofit.androidwearV2";
    private SharedPreferences spfFreestyle, spfUser;
    private Activity activity;
    //private DatabaseReference projectRef;

    public AppCredentials(Activity activity) {
        this.activity = activity;
//        spfUser = activity.getSharedPreferences(SPF_USER, Context.MODE_PRIVATE);
//        spfFreestyle = activity.getSharedPreferences(SPF_FREESTYLE, Context.MODE_PRIVATE);
//        projectRef = FirebaseDatabase.getInstance().getReference().child(CHILD_APP)
//                .child(CHILD_ANDROID).child(CHILD_PROJECT_NAME);
    }

////    public void checkFirebase(final String CHILD_BUILD_TYPE) {
////        if (new ConnectionUtility().isInternetAvailable()) {
////            projectRef.child(CHILD_BUILD_TYPE)
////                    .addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////
////                            // ====== Disable App ======
////                            boolean isDisabled = dataSnapshot.child(CHILD_DISABLE_APP).getValue(Boolean.class);
////                            int disableAppCode = dataSnapshot.child(CHILD_DISABLE_APP_VERSION).getValue(Integer.class);
////                            if (isDisabled) {
////                                spfUser.edit().putBoolean(KEY_DISABLE_APP, true).apply();
////                                spfUser.edit().putInt(KEY_DISABLE_APP_VERSION, disableAppCode).apply();
////                            } else {
////                                spfUser.edit().putBoolean(KEY_DISABLE_APP, false).apply();
////                                new SendMessageAsync(PATH_DISABLE_APP).execute("false");
////                            }
////                            //Log.d(TAG, "onDataChange: " + isDisabled + ": " + disableAppCode);
////
////                            // ====== API ======
////                            String instance = dataSnapshot.child(CHILD_API).getValue(String.class);
////                            spfFreestyle.edit().putString(CHILD_API, instance).apply();
////                            int fr = dataSnapshot.child(CHILD_FRAME_RATE).getValue(Integer.class);
////                            spfFreestyle.edit().putString(KEY_FRAME_RATE, String.valueOf(fr)).apply();
////
////                            int frameDivisor;
////                            /**
////                             * Control frame rate by:
////                             * 20fps => 5.
////                             * 25fps => 4.
////                             * 50fps => 2.
////                             */
////                            switch (fr) {
////                                case 20:
////                                    frameDivisor = 5;
////                                    break;
////
////                                case 25:
////                                    frameDivisor = 4;
////                                    break;
////
////                                case 50:
////                                    frameDivisor = 2;
////                                    break;
////
////                                default:
////                                    frameDivisor = 5;
////                            }
////                            //frameDivisor = 2;
////                            spfFreestyle.edit().putInt(KEY_FRAME_DIVISOR, frameDivisor).apply();
////                            new SendMessageAsync(PATH_FRAME_DIVISOR).execute(String.valueOf(frameDivisor));
////                            //Log.d(TAG, "onDataChange: " + CHILD_BUILD_TYPE + ": " + fr + " -> " + 100 + "/" + fr + "=" + (100 / fr) + " frameDivisor==> " + frameDivisor);
////
////                            // ====== Survey ======
////                            boolean isSurveyRequired = dataSnapshot.child(CHILD_SURVEY).getValue(Boolean.class);
////                            int surveyCount = dataSnapshot.child(CHILD_SURVEY_COUNT).getValue(Integer.class);
////                            spfFreestyle.edit().putBoolean(KEY_IS_SURVEY_REQUIRED, isSurveyRequired).apply();
////                            spfFreestyle.edit().putInt(KEY_SURVEY_COUNT, surveyCount).apply();
////
////                            // ====== Force Update ======
////                            int appId = dataSnapshot.child(CHILD_APP_VERSION_CODE).getValue(Integer.class);
////                            String appVersion = dataSnapshot.child(CHILD_APP_VERSION_NAME).getValue(String.class);
////                            boolean isForceUpdateRequired = dataSnapshot.child(CHILD_FORCE_UPDATE).getValue(Boolean.class);
////                            if (isForceUpdateRequired) {
////                                spfUser.edit().putInt(KEY_APP_VERSION_CODE, appId).apply();
////                                spfUser.edit().putString(KEY_APP_VERSION_NAME, appVersion).apply();
////                                //checkAppVersion(appId, appVersion);
////                            } else {
////                                spfUser.edit().putInt(KEY_APP_VERSION_CODE,
////                                        BuildConfig.VERSION_CODE).apply();
////                                spfUser.edit().putString(KEY_APP_VERSION_NAME,
////                                        BuildConfig.VERSION_NAME).apply();
////                            }
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
////        }
//    }

//    public void handleDisableApp() {
//        if (BuildConfig.VERSION_CODE <= spfUser
//                .getInt(KEY_DISABLE_APP_VERSION, VALUE_DISABLE_APP_VERSION)) {
//            new AlertDialog.Builder(activity)
//                    //.setTitle("")
//                    .setMessage("This version of the app has been disabled. Kindly " +
//                            "update to a new version or contact us at support@actofit.com")
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            gotoPlayStore();
//                        }
//                    })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            activity.finish();
//                        }
//                    })
//                    .setCancelable(false)
//                    .show();
//            new SendMessageAsync(PATH_DISABLE_APP).execute("true");
//        }
//    }
//
//    // =========================================================
//    private void checkAppVersion(int appID, String appVersion) {
//        if (BuildConfig.VERSION_CODE < appID) {
//            new AlertDialog.Builder(activity)
//                    .setTitle("Update")
//                    .setMessage("New Version : " + appVersion + " is Available !!")
//                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            gotoPlayStore();
//                        }
//                    })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            activity.finish();
//                        }
//                    })
//                    .setCancelable(false)
//                    .show();
//        }
//    }
//
//    private void gotoPlayStore() {
//        try {
//            activity.finish();
//            Intent intent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("market://details?id=" + APPLICATION_ID));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            activity.startActivity(intent);
//        } catch (Exception e) { //android.content.ActivityNotFoundException anfe) {
//            Intent intent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id=" + APPLICATION_ID));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            activity.startActivity(intent);
//            e.printStackTrace();
//        }
//    }

}