package edu.gatech.seclass.peersupport;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Comparator;
        import java.util.Date;

class GroupContactsComparator implements Comparator<String> {
    public int compare(String s1, String s2)
    {
        try {
            Date d1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(s1.split("`")[2]);
            Date d2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(s2.split("`")[2]);

            if (d1.before(d2))
                return 1;
            else if (d1.after(d2))
                return -1;
            else
                return 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

