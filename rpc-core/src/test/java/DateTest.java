import com.cxl.rpc.util.DateUtil;

import java.util.Date;

public class DateTest {
    public static void main(String[] args) {
        String s = DateUtil.formatDate(DateUtil.addDays(new Date(), 1));
        String format = DateUtil.format(DateUtil.addDays(new Date(), 1), DateUtil.getDatetimeFormat());
        System.out.println(format);
        Date date = DateUtil.parseDateTime(format);
        Date date1 = DateUtil.parseDate(s);
        System.out.println("date1  "+date1);
        System.out.println("date  "+date);
        System.out.println(s);
        System.out.println(format);
        Date date2=new Date();
        System.out.println(date2.getTime());
    }
}
