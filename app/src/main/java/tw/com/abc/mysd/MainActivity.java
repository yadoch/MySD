package tw.com.abc.mysd;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    //用來記錄使用者是否按同意
    private boolean isPermissionOK;
    private File sdroot,approot;
    private SQLiteDatabase db;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                // 要判斷的條件(網路,外部儲存裝置....)
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            // no
            ActivityCompat.requestPermissions(this,
                    // 要判斷的條件(網路,外部儲存裝置....)
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }else {
            isPermissionOK = true;
            init();
        }

    }

    private void init(){
        if (!isPermissionOK) {
            finish();
        }else{
            go();
        }
        //Log.i("brad", "start");
    }

    private void  go(){
        sdroot= Environment.getExternalStorageDirectory();
        approot=new File(sdroot,"Android/data/"+getPackageName()+"/");

        if(!approot.exists()){
            approot.mkdirs(); // 跟目錄不存在時會完整建立
        }
        tv= (TextView) findViewById(R.id.tv);

        MyDBHelper dbHelper=new MyDBHelper(this,"brad",null,1);
        db =dbHelper.getReadableDatabase();
    }

    public void test1(View view){
        File file1= new File(sdroot,"file1");
        File file2=new File(approot,"file2");

        try {
            FileOutputStream fout1 =new FileOutputStream(file1);
            FileOutputStream fout2 =new FileOutputStream(file2);

            fout1.write("I am File1".getBytes());
            fout2.write("I am File2".getBytes());

            fout1.flush();fout2.flush();
            fout1.close();fout2.close();
            Toast.makeText(this,"ok",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void test2(View view){
        //Log.i("geoff","test2");
        File file1=new File(sdroot,"file1");
        File file2=new File(approot,"file2");

        int temp1;
        StringBuilder sb =new StringBuilder();
        try {
            FileInputStream fin1 = new FileInputStream(file1);
            FileInputStream fin2 = new FileInputStream(file2);

            while ( ( temp1 = fin1.read() ) != -1){   // 如果 fin1.read() != -1 迴圈才結束
                sb.append((char)temp1);  //temp1 為bytes 透過轉型成char,再透過sb 存檔案內容
             //   Log.i("geoff",sb.toString());
            }
            fin1.close();
            fin2.close();

            Toast.makeText(this,sb.toString(),Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void test3(View view){
        // insert
        ContentValues values= new ContentValues();
        values.put("cname","brad");
        values.put("tel","1234");
        values.put("birthday","1999-01-01");
        db.insert("cust",null,values);
        test6(null);
    }
    public void test4(View view){
        // delete form cust where _id = 2 and cname ='brad'
        db.delete("cust","_id=? and cname =?",new String[]{"2","brad"});
        test6(null);
    }
    public void test5(View view){
        // update cust set cname ='peter',tel='456' where _id=4
        ContentValues values=new ContentValues();
        values.put("cname","peter");
        values.put("tel","456");

        db.update("cust",values,"_id=?",new String[]{"4"});
        test6(null);
    }
    public void test6(View view){
        //select * form cust
        Cursor cursor = db.query("cust",null,null,null,null,null,null);
       // Get cust count
        int count = cursor.getCount();


        Log.i("geoff","Count:"+count);
        tv.setText("Count:"+count+"\n");

        while (cursor.moveToNext()){
            String id = cursor.getString(0);
            String cname=cursor.getString(1);
            String tel=cursor.getString(2);
            String birthday=cursor.getString(3);
            tv.append(id+":"+cname+":"+tel+":"+birthday+"\n");
        }

        cursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isPermissionOK = true;

            }
            init();
        }
    }
}
