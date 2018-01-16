package ai_drones;

import misc.Request;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SearchRequestAnalysisTest
{
	public static void main(String[] args)
	{
		List<Request> requests = new ArrayList<>();
		requests.add(new Request(null, "smartphone"));
		requests.add(new Request(null, "smartphone"));
		requests.add(new Request(null, "toast"));
		requests.add(new Request(null, "snartpgone"));
		requests.add(new Request(null, "tosst"));
		requests.add(new Request(null, "boat"));
		requests.add(new Request(null, "boat"));

		try {
			new SearchRequestAnalysis().runIteration(requests, true);
		}catch (Exception e)
		{
			e.printStackTrace();

			throw new RuntimeException(e);
		}
	}
}
