package edu.upenn.cis455.crawler;
import java.util.Comparator;

public class DomainComparator implements Comparator<Domain>
{
	final int DOMAIN_THRESHOLD = 50;
	@Override
	public int compare(Domain x, Domain y)
	{
		
		if(x.count > DOMAIN_THRESHOLD && y.count < DOMAIN_THRESHOLD)
			return -1;
		
		if(y.count > DOMAIN_THRESHOLD && x.count < DOMAIN_THRESHOLD)
			return 1;		
		
		if (x.accessTime < y.accessTime)
		{
			return 1;
		}
		if (x.accessTime > y.accessTime)
		{
			return -1;
		}
		return 0;
	}
}