package rick.mobliesafe.blackcontact;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.mock.MockContext;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlackNumberDaoTest {
private Context   context;
    @Before
    public void setUp() throws Exception {
       context= InstrumentationRegistry.getTargetContext();

    }

    @Test
    public void add() {
        BlackNumberDao dao =new BlackNumberDao(context);
        BlackContactInfo info =new BlackContactInfo();
        info.phoneNumber=1350000000+"";
        info.contactName="zhangsan";
        info.mode=1;
        dao.add(info);
    }

    @Test
    public void getBlackContactMode() {
        BlackNumberDao dao = new BlackNumberDao(context);
        int mode=dao.getBlackContactMode(1350000000+"");
        Log.i("TestBlackNumberDao",mode+"");
    }
}