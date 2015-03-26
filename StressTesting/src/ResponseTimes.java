import java.util.ArrayList;
import java.util.Iterator;

public class ResponseTimes
{
    private ArrayList<ResponseTime> times = new ArrayList<ResponseTime>();

    public ResponseTimes()
    {
        super();
        
        setTimes(new ArrayList<ResponseTime>());
    }

    public void add(ResponseTime responseTime)
    {
        getTimes().add(responseTime);
    }

    public long getTotal()
    {
        return times.size();
    }
    
    public Iterator<ResponseTime> getResponseTimes()
    {
        return getTimes().iterator();
    }
    
    public long getRequestAverage()
    {
        long average;
        ResponseTime nextResponseTime;
        long totalTotals;

        average = 0L;
        nextResponseTime = null;
        totalTotals = 0L;
        
        for(int i = 0; i < getTimes().size(); i++)
        {
            nextResponseTime = getTimes().get(i);
            totalTotals += nextResponseTime.getRequest();
        }
        
        average = totalTotals / getTotal();
        
        return average;
    }
    
    public long getResponseAverage()
    {
        long average;
        ResponseTime nextResponseTime;
        long totalTotals;

        average = 0L;
        nextResponseTime = null;
        totalTotals = 0L;
        
        for(int i = 0; i < getTimes().size(); i++)
        {
            nextResponseTime = getTimes().get(i);
            totalTotals += nextResponseTime.getResponse();
        }
        
        average = totalTotals / getTotal();
        
        return average;
    }
    
    public long x()
    {
        long total;
        ResponseTime nextResponseTime;
        
        total = 0;
        nextResponseTime = null;
        
        for(int i = 0; i < getTimes().size(); i++)
        {
            nextResponseTime = getTimes().get(i);
            total += nextResponseTime.getRequest();
        }
        
        return total;
    }
    
    public long y()
    {
        long total;
        ResponseTime nextResponseTime;
        
        total = 0;
        nextResponseTime = null;
        
        for(int i = 0; i < getTimes().size(); i++)
        {
            nextResponseTime = getTimes().get(i);
            total += nextResponseTime.getResponse();
        }
        
        return total;
    }
    
    public double z()
    {
        double total;
        ResponseTime nextResponseTime;
        
        total = 0;
        nextResponseTime = null;
        
        for(int i = 0; i < getTimes().size(); i++)
        {
            nextResponseTime = getTimes().get(i);
            total += nextResponseTime.getRequest();
            total += nextResponseTime.getResponse();
        }
        
        total /= 1000;
        total = getTimes().size() / total;
        
        return total;
        
    }

    /*
    public long getAverage()
    {
        long average;
        Long nextTime;
        long totalTotals;

        average = 0L;
        nextTime = null;
        totalTotals = 0L; 

        for(int i = 0; i < getTimes().size(); i++)
        {
            nextTime = getTimes().get(i);
            totalTotals += nextTime.longValue();
        }

        average = totalTotals / getTotal();

        return average;
    }
    */

    /*
    public long getHighest()
    {
        long highest;
        Long nextTime;

        highest = 0L;
        nextTime = null;
        highest = 0L;

        for(int i = 0; i < getTimes().size(); i++)
        {
            nextTime = getTimes().get(i);

            if(nextTime.longValue() > highest)
            {
                highest = nextTime.longValue();
            }
        }

        return highest;
    }
    */
    
    private ArrayList<ResponseTime> getTimes()
    {
        return this.times;
    }
    
    private void setTimes(ArrayList<ResponseTime> times)
    {
        this.times = times;
    }
}