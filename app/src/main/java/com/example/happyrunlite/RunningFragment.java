package com.example.happyrunlite;



import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;


public class RunningFragment extends Fragment implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {
    DBManager m_dbmanager = null;
    View m_view;
    boolean m_brunning = false;
    double m_distance = 0.0;
    private RecordFragment m_recordfragment;
    // ---------------- mapview code -----------------
    private static final String LOG_TAG = "MainActivity";

    private MapView mMapView;


    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }
    
    MapPolyline m_polyline = new MapPolyline();
    MapPoint.GeoCoordinate formercoord;

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) { // GPS가 업데이트 되면 이 함수가 호출됩니다.
        if(m_brunning == true) {


            m_polyline.setTag(1000);
            m_polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline 컬러 지정.
            MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
            // Polyline 좌표 지정.
            m_polyline.addPoint(MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude));
            mapView.addPolyline(m_polyline);
            if(formercoord != null) {
                m_distance += distance(mapPointGeo.latitude, mapPointGeo.longitude, formercoord.latitude, formercoord.longitude,"meter");
            }
            formercoord = mapPointGeo;


            Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        }
    }


    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }




    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(getActivity(), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    getActivity().finish();


                }else {

                    //Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(getActivity(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }



    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    // -------------------------------------------------



    private SQLiteDatabase init_database() {

        SQLiteDatabase db = null ;
        File file = new File(getActivity().getFilesDir(), "happyrun.db") ;

        System.out.println("PATH : " + file.toString()) ;
        try {
            db = SQLiteDatabase.openOrCreateDatabase(file, null) ;
        } catch (SQLiteException e) {
            e.printStackTrace() ;
        }

        if (db == null) {
            System.out.println("DB creation failed. " + file.getAbsolutePath()) ;
        }

        return db ;
    }

    private void init_tables() {
        m_dbmanager = DBManager.getInstance(getContext());
    }

    private void save_values(){
        SQLiteDatabase db = m_dbmanager.getWritableDatabase() ;
        TextView timetext = (TextView) m_view.findViewById(R.id.lbltime);
        String strtime = timetext.getText().toString();
        TextView distancetext = (TextView) m_view.findViewById(R.id.lbldistance);
        String strdist = distancetext.getText().toString();
        String strrecord = strtime + " , " + strdist;

        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        String nowTime = sdfNow.format(date);

        String sqlInsert = "INSERT INTO HAPPYRUNDB " +
                    "(RECORD , DATE) VALUES (" +
                    "'" + strrecord + "'," +
                    "'" + nowTime + "'" + ")" ;

            System.out.println(sqlInsert) ;

            db.execSQL(sqlInsert) ;
        }


    static int runcount = 0;
    static TimerTask tt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_view = inflater.inflate(R.layout.runningpage, container, false);
        // ------------- DB code ------------------------
        init_tables();





        // ---------------- mapview code -----------------
        mMapView = (MapView)m_view.findViewById(R.id.map_view);
        //mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setCurrentLocationEventListener(this);

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        // ------------------------------------------------
        final TextView lblrun_time = (TextView) m_view.findViewById(R.id.lbltime);
        tt = timerTaskMaker();
        try{
            final Button BtnStart = (Button) m_view.findViewById(R.id.btnStart);
            final Button BtnStop = (Button) m_view.findViewById(R.id.btnStop);
            BtnStart.setEnabled(true); // 초기화
            BtnStop.setEnabled(false);
            BtnStart.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) { // 달리기 시작버튼 이벤트 처리
                    m_brunning = true;
                    BtnStart.setEnabled(false);
                    BtnStop.setEnabled(true);
                    runcount = 0;
                    m_distance = 0.0;
                    try{
                        tt = timerTaskMaker();
                        final Timer timer = new Timer();
                        timer.schedule(tt,1000,1000);
                    } catch(Exception e) {
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String exceptionAsStrting = sw.toString();

                        Log.e("Timer Error Detected!!!", exceptionAsStrting);
                    }

                }
            });
            BtnStop.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) { // 달리기 종료버튼 이벤트 처리
                    m_brunning = false;
                    BtnStart.setEnabled(true);
                    BtnStop.setEnabled(false);
                    tt.cancel();
                    // ---- DB저장 -------------
                    save_values();
                    ((MainActivity)getActivity()).refresh();
                }
            });
            throw new Exception();
        }catch(Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsStrting = sw.toString();

            // Log.e("Button Error Detected", exceptionAsStrting);
        }

        return m_view;
    }
    public TimerTask timerTaskMaker(){
        TimerTask tempTask = new TimerTask(){
            @Override
            public void run(){
                runcount++;
                int timeidx = runcount;
                int hour = 0; int min = 0; int sec = 0;
                if (timeidx >= 3600) {
                    hour = timeidx / 3600;
                    timeidx %= 60;
                }
                if(timeidx >= 60) {
                    min = timeidx / 60;
                    timeidx %= 60;
                }
                sec = timeidx;

                String strRecord = String.format("경과시간 - %02d : %02d : %02d", hour,min,sec);
                final TextView lblrun_time = (TextView) getView().findViewById(R.id.lbltime);
                lblrun_time.setText(strRecord);
                String strDistance = String.format("거리 : %.2f m", m_distance);
                final TextView lbldistance = (TextView) getView().findViewById(R.id.lbldistance);
                lbldistance.setText(strDistance);
            }
        };
        return tempTask;
    }
        /**
         * 두 지점간의 거리 계산
         *
         * @param lat1 지점 1 위도
         * @param lon1 지점 1 경도
         * @param lat2 지점 2 위도
         * @param lon2 지점 2 경도
         * @param unit 거리 표출단위
         * @return
         */
        private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;

            if (unit == "kilometer") {
                dist = dist * 1.609344;
            } else if(unit == "meter"){
                dist = dist * 1609.344;
            }

            return (dist);
        }
        // This function converts decimal degrees to radians
        private double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }
        // This function converts radians to decimal degrees
        private double rad2deg(double rad) {
            return (rad * 180 / Math.PI);
        }


}
