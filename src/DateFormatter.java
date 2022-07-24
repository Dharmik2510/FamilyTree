import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//Class whose method will return the formatted date.
public class DateFormatter
{
    /**Method that will check if date passed as an argument is formatted or not*
     * @param
     * date_last_updated - date passed as an argument
     * @return
     * String date - formatted date
     * */
    public static String getFormattedDate(String date_last_updated)
    {
        //SimpleDateFormat when day,month,year are given
        SimpleDateFormat completeFormatter = new SimpleDateFormat("yyyy-MM-dd");

        //SimpleDateFormat when month,year are given
        SimpleDateFormat onlyYearMonth = new SimpleDateFormat("yyyy-MM");

        //SimpleDateFormat when only year is given
        SimpleDateFormat onlyYear = new SimpleDateFormat("yyyy");

        //check if null or not
        if(date_last_updated==null || date_last_updated.isEmpty())
        {
            return null;
        }

        Date formatedDate = null;

        try
        {
            formatedDate = completeFormatter.parse(date_last_updated);
        } catch (ParseException e) {
            try {
                formatedDate = onlyYearMonth.parse(date_last_updated);
            } catch (ParseException parseException) {
                try {
                   formatedDate= onlyYear.parse(date_last_updated);
                } catch (ParseException exception) {
                    System.out.println("please enter valid date format");
                }
            }
        }

        //Initializing calendar instance
        Calendar calendar = Calendar.getInstance();
        if (formatedDate != null) {
            calendar.setTime(formatedDate);
        }
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);

    }
}
