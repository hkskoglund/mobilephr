package hks.itprojects.healthcollector.backgroundTasks;

public class QueryBloodPressuresInBackground implements Runnable
	{
		public QueryBloodPressuresInBackground()
			{
				(new Thread(this)).start();
			}
		

	public void run()
		{
			
		}

	}
