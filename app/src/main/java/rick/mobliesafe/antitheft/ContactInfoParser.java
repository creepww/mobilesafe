package rick.mobliesafe.antitheft;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class ContactInfoParser {
    public static List<ContactInfo>getSystemContact(Context context){
        ContentResolver resolver = context.getContentResolver();
        //1.查询raw_contacts表,把联系人的id取出来
        Uri uri =Uri.parse("content://com.android.contacts/raw_contacts");
        Uri datauri = Uri.parse("content://com.android.contacts/data");
        List<ContactInfo> infos = new ArrayList<ContactInfo>();
        Cursor cursor = resolver.query(uri,new String[] {"contact_id"},null,null,null);
        while (cursor.moveToNext()){
            String id = cursor.getString(0);
            if (id!=null){
                System.out.println("联系人id"+id);
                ContactInfo info = new ContactInfo();
                info.id = id;
                //2.根据联系人的id，查询data表，把这个id的数据提取出来
                //系统api查询data表的时候，不是真正的查询data表，而是查询data表的试图
                Cursor dataCursor=resolver.query(datauri,new String[] {"data1","mimetype"},"raw_contact_id=?",
                        new String[] {id},null);
                while (dataCursor.moveToNext()){
                    String data1 =dataCursor.getString(0);
                    String mimetype=dataCursor.getString(1);
                    if ("vnd.android.cursor.item/name".equals(mimetype)){
                        System.out.println("姓名="+data1);
                        info.name=data1;
                    }else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                        System.out.println("电话="+data1);
                        info.phone=data1;
                    }
                }
                infos.add(info);
                dataCursor.close();
            }
        }
        cursor.close();
        return infos;
    }
}
