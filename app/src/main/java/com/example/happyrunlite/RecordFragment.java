package com.example.happyrunlite;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


public class RecordFragment extends Fragment {
    public class RecordData{
        private String record;
        private String date;

        public RecordData(String record, String date){
            this.record = record;
            this.date = date;
        }

        public String getRecord(){
            return this.record;
        }
        public String getDate(){
            return this.date;
        }
    }
    ArrayList<RecordData> recordDataList;

    View m_view;
    DBManager m_dbmanager = null;
    ListViewAdapter mylistviewadapter;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("onCreateView RecordFragment");
        m_view = inflater.inflate(R.layout.recordpage, container, false);

        context = container.getContext();
        m_dbmanager = DBManager.getInstance(getContext());

        this.InitializeRecordData();

        final ListView listView = (ListView) m_view.findViewById(R.id.listView);
        mylistviewadapter = new ListViewAdapter(getActivity(), recordDataList);

        listView.setAdapter(mylistviewadapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // 아이템이 선택되었을때의 이벤트를 정의합니다.
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popup= new PopupMenu(getActivity().getBaseContext(), view);//view는 오래 눌러진 뷰를 가리킨다
                getActivity().getMenuInflater().inflate(R.menu.menu_listview, popup.getMenu());

                final int index = position;

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.delete: // 리스트 뷰에서 삭제 루틴 작성
                                Log.e("DEBUG", "index: " + index);
                                recordDataList.remove(index);
                                String strdate = ((TextView)m_view.findViewById(R.id.date)).getText().toString();
                                m_dbmanager.delete_values(strdate);
                                Log.e("DEBUG", strdate);
                                ((MainActivity)getActivity()).refresh();
                                Toast.makeText(context, "정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                                break;

                        }

                        return false;
                    }
                });

                popup.show();

                return false;
            }
        });
        return m_view;
    }
    @Override
    public void onResume(){
        Log.e("DEBUG", "onResume of RecordFragment");
        InitializeRecordData();
        super.onResume();
    }


    public void InitializeRecordData()
    {
        recordDataList = new ArrayList<RecordData>();
        // 데이터베이스 불러와서 여기에서 Add하기
        load_values();
    }

    // --------------- DB 처리함수 -----------------
    private void load_values(){
        Cursor cursor = m_dbmanager.load_db_cursor();

            for(int i = 0; i< cursor.getCount(); i++) {
                cursor.moveToNext();
                    // record (TEXT) 값 가져오기
                String record = cursor.getString(0) ;
                    // date (TEXT) 값 가져오기
                String data = cursor.getString(1) ;

                recordDataList.add(new RecordData(record, data));

            }

        }
}
